package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.OrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.OrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
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
public class OrderEventSubscriber {

    private static final String EXCHANGE_NAME = "order";

    @Autowired
    private OrderService orderService;

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

        // bind the queue to the exchange for the order events
        channel.queueBind(queueName, EXCHANGE_NAME, "order.*");

        // create a consumer and start consuming messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                String eventType = envelope.getRoutingKey().substring(envelope.getRoutingKey().lastIndexOf(".") + 1);
                String originService = envelope.getRoutingKey().substring(0, envelope.getRoutingKey().indexOf("."));
                System.out.println("Received event '" + eventType + "' from service '" + originService + "' with message '" + message + "'");

                // parse the message as an OrderEvent
                if (eventType.equals(OrderRoutingKeyEnum.ORDER_CREATED.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_FULL_CANCEL.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_REJECTED.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_APPROVED.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_SHIPPED.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_DELIVERED.getKey()) ||
                        eventType.equals(OrderRoutingKeyEnum.ORDER_DELETED.getKey())) {
                    OrderEvent event = OrderEvent.fromJson(message);
                    try {
                        handleOrderEvent(eventType, event);
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

    private void handleOrderEvent(String eventType, OrderEvent event) throws Exception {
        // handle the order event
        boolean isEvent = true;
        if (OrderRoutingKeyEnum.ORDER_CREATED.getKey().equals(eventType)) {
            orderService.createOrder(
                    OrderCreateDTO.builder()
                            .id(event.getId())
                            .orderDate(event.getOrderDate())
                            .orderStatus(event.getOrderStatus())
                            .customerId(event.getCustomerId())
                            .email(event.getEmail())
                            .orderItems(event.getOrderItems())
                            .totalPrice(event.getTotalPrice())
                            .payment(event.getPaymentDTO())
                            .merchantId(event.getMerchantId())
                            .address(event.getAddressDTO())
                            .userDTO(event.getUserDTO())
                            .build()
            );
        } else if (OrderRoutingKeyEnum.ORDER_FULL_CANCEL.getKey().equals(eventType)) {
            orderService.fullCancelOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getId())
                            .orderDate(event.getOrderDate())
                            .orderStatus(event.getOrderStatus())
                            .email(event.getEmail())
                            .userDTO(event.getUserDTO())
                            .build()
            );
        } else if (OrderRoutingKeyEnum.ORDER_REJECTED.getKey().equals(eventType)) {
            orderService.rejectOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getId())
                            .orderDate(event.getOrderDate())
                            .orderStatus(event.getOrderStatus())
                            .email(event.getEmail())
                            .userDTO(event.getUserDTO())
                            .build()
            );
        } else if (OrderRoutingKeyEnum.ORDER_APPROVED.getKey().equals(eventType)) {
            orderService.approveOrder(event.getId(),
                    OrderUpdateDTO.builder()
                            .id(event.getId())
                            .orderDate(event.getOrderDate())
                            .orderStatus(event.getOrderStatus())
                            .email(event.getEmail())
                            .userDTO(event.getUserDTO())
                            .build()
            );
        } else if (OrderRoutingKeyEnum.ORDER_SHIPPED.getKey().equals(eventType)) {
            orderService.shipOrder(null, event.getId());
        } else if (OrderRoutingKeyEnum.ORDER_DELIVERED.getKey().equals(eventType)) {
            orderService.deliverOrder(null, event.getId());
        } else if (OrderRoutingKeyEnum.ORDER_DELETED.getKey().equals(eventType)) {
            orderService.deleteOrder(0, event.getId());
        } else {
            throw new Exception("Not a valid event type");
        }
    }

    @Bean
    @Async
    public void mainOrderSubscription() throws IOException, TimeoutException {
        this.start();
    }

}
