package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
@EnableAsync
public class MerchantEventSubscriber {

    private static final String EXCHANGE_NAME = "merchant";
    private static final boolean isEvent = true;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private RabbitMQHost rabbitMQHost;

    public void start() throws IOException, TimeoutException {

        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // create the exchange and queue
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // bind the queue to the exchange for the item events
        channel.queueBind(queueName, EXCHANGE_NAME, "merchant.*");

        // create a consumer and start consuming messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                String eventType = envelope.getRoutingKey().substring(envelope.getRoutingKey().lastIndexOf(".") + 1);
                String originService = envelope.getRoutingKey().substring(0, envelope.getRoutingKey().indexOf("."));
                System.out.println("Received event '" + eventType + "' from service '" + originService + "' with message '" + message + "'");

                // parse the message as a MerchantEvent
                if (eventType.equals(MerchantRoutingKeyEnum.MERCHANT_CREATED.getKey()) || eventType.equals(MerchantRoutingKeyEnum.MERCHANT_UPDATED.getKey()) || eventType.equals(MerchantRoutingKeyEnum.MERCHANT_DELETED.getKey())) {
                    MerchantEvent event = MerchantEvent.fromJson(message);
                    try {
                        handleMerchantEvent(eventType, event);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        channel.basicConsume(queueName, true, consumer);

        // keep the subscriber running
        System.out.println("Waiting for events...");
        while (true) {
        }
    }

    private void handleMerchantEvent(String eventType, MerchantEvent event) throws Exception {
        // handle the item event
        if (MerchantRoutingKeyEnum.MERCHANT_CREATED.getKey().equals(eventType)) {
            merchantService.createMerchant(
                    MerchantDTO.builder()
                            .id(event.getId())
                            .name(event.getName())
                            .email(event.getEmail())
                            .build(),
                    isEvent
            );
        } else if (MerchantRoutingKeyEnum.MERCHANT_UPDATED.getKey().equals(eventType)) {
            merchantService.updateMerchant(event.getId(),
                    MerchantDTO.builder()
                            .id(event.getId())
                            .name(event.getName())
                            .email(event.getEmail())
                            .build(),
                    isEvent
            );
        } else if (MerchantRoutingKeyEnum.MERCHANT_DELETED.getKey().equals(eventType)) {
            merchantService.deleteMerchant(event.getId(), isEvent);
        } else {
            throw new Exception("Not a valid event type");
        }
    }

    @Bean
    @Async
    public void mainMerchantSubscription() throws IOException, TimeoutException {
        this.start();
    }

}
