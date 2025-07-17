package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.ShippingOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ShippingOrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ShippingOrderEventSubscriber {

    private static final String EXCHANGE_NAME = "shippingorder";
    private static final boolean isEvent = true;

    @Autowired
    private RabbitMQHost rabbitMQHost;
    @Autowired
    @Lazy
    private OrderService orderService;

    public void start() throws IOException, TimeoutException {

        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // create the exchange and queue
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        // bind the queue to the exchange for the item events
        channel.queueBind(queueName, EXCHANGE_NAME, "shippingorder.*");

        // create a consumer and start consuming messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                String eventType = envelope.getRoutingKey().substring(envelope.getRoutingKey().lastIndexOf(".") + 1);
                String originService = envelope.getRoutingKey().substring(0, envelope.getRoutingKey().indexOf("."));
                System.out.println("Received event '" + eventType + "' from service '" + originService + "' with message '" + message + "'");

                // parse the message as a ShippingOrderEvent
                if (eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_CREATED.getKey()) || eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_APPROVED.getKey()) || eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELIVERED.getKey()) ||
                        eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELETED.getKey()) || eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_REJECTED.getKey()) || eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_SHIPPED.getKey()) ||
                        eventType.equals(ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_FULL_CANCEL.getKey())) {

                    ShippingOrderEvent event = ShippingOrderEvent.fromJson(message);
                    try {
                        handleShippingOrderEvent(eventType, event);
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

    private void handleShippingOrderEvent(String eventType, ShippingOrderEvent event) throws Exception {
        // handle the item event
        if (ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_APPROVED.getKey().equals(eventType)) {
            orderService.approveOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else if (ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_FULL_CANCEL.getKey().equals(eventType)) {
            orderService.fullCancelOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else if (ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_REJECTED.getKey().equals(eventType)) {
            orderService.rejectOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getOrderId())
                            .build(),
                    isEvent);
        } else if (ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_SHIPPED.getKey().equals(eventType)) {
            orderService.shipOrder(null, event.getOrderId(), isEvent);
        } else if (ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELIVERED.getKey().equals(eventType)) {
            orderService.deliverOrder(null, event.getOrderId(), isEvent);
        } else {
            throw new Exception("Not a valid event type");
        }
    }

    @Bean
    @Async
    public void mainShippingOrderSubscription() throws IOException, TimeoutException {
        this.start();
    }

}
