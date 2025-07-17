package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantOrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
@EnableAsync
public class MerchantOrderEventSubscriber {

    private static final String EXCHANGE_NAME = "merchantorder";
    private static final boolean isEvent = true;

    private final RabbitMQHost rabbitMQHost;
    private final OrderService orderService;

    public MerchantOrderEventSubscriber(RabbitMQHost rabbitMQHost, @Lazy OrderService orderService) {
        this.rabbitMQHost = rabbitMQHost;
        this.orderService = orderService;
    }

    public void start() throws IOException, TimeoutException {

        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // create the exchange and queue
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // bind the queue to the exchange for the item events
        channel.queueBind(queueName, EXCHANGE_NAME, "merchantorder.*");

        // create a consumer and start consuming messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                String eventType = envelope.getRoutingKey().substring(envelope.getRoutingKey().lastIndexOf(".") + 1);
                String originService = envelope.getRoutingKey().substring(0, envelope.getRoutingKey().indexOf("."));
                System.out.println("Received event '" + eventType + "' from service '" + originService + "' with message '" + message + "'");

                // parse the message as a MerchantOrderEvent
                if (eventType.equals(MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_APPROVED.getKey()) ||
                        eventType.equals(MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_FULL_CANCEL.getKey()) ||
                        eventType.equals(MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_REJECTED.getKey())) {

                    MerchantOrderEvent event = MerchantOrderEvent.fromJson(message);
                    try {
                        handleMerchantOrderEvent(eventType, event);
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

    private void handleMerchantOrderEvent(String eventType, MerchantOrderEvent event) throws Exception {
        // handle the item event
        if (MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_APPROVED.getKey().equals(eventType)) {
            orderService.approveOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else if (MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_FULL_CANCEL.getKey().equals(eventType)) {
            orderService.fullCancelOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else if (MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_REJECTED.getKey().equals(eventType)) {
            orderService.rejectOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else {
            throw new Exception("Not a valid event type");
        }
    }

    @Bean
    @Async
    public void mainMerchantOrderSubscription() throws IOException, TimeoutException {
        this.start();
    }

}
