package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.events.OrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.OrderRoutingKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Component
public class Publisher {
    private final static String EXCHANGE_NAME = "order";
    private static final Logger logger = Logger.getLogger(Publisher.class.getName());

    @Autowired
    private RabbitMQHost rabbitMQHost;

    public void publishEvent(OrderEvent orderEvent) {
        try {
            String routingKey = determineRoutingKey(orderEvent);
            mainPublish(orderEvent, routingKey);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error publishing event", e);
        }
    }

    private String determineRoutingKey(OrderEvent orderEvent) {
        return switch (orderEvent.getEventTypeEnum()) {
            case CREATE -> OrderRoutingKeyEnum.ORDER_CREATED.getOrderKey();
            case DELETE -> OrderRoutingKeyEnum.ORDER_DELETED.getOrderKey();
            default -> switch (orderEvent.getOrderStatus()) {
                case CANCELLED -> OrderRoutingKeyEnum.ORDER_FULL_CANCEL.getOrderKey();
                case REJECTED -> OrderRoutingKeyEnum.ORDER_REJECTED.getOrderKey();
                case APPROVED -> OrderRoutingKeyEnum.ORDER_APPROVED.getOrderKey();
                case SHIPPED -> OrderRoutingKeyEnum.ORDER_SHIPPED.getOrderKey();
                case DELIVERED -> OrderRoutingKeyEnum.ORDER_DELIVERED.getOrderKey();
                default -> null;
            };
        };
    }

    private void mainPublish(OrderEvent orderEvent, String routingKey) throws Exception {
        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // declare the exchange for the item events
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // publish the event
        String message = orderEvent.toJson();
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Sent event '" + routingKey + "' with message '" + message + "'");

        // close the channel and connection
        channel.close();
        connection.close();
    }
}