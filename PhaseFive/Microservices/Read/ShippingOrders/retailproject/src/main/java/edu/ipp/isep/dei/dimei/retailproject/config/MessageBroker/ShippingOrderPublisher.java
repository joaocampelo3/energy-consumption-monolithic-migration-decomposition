package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.events.ShippingOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ShippingOrderRoutingKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ShippingOrderPublisher {
    private final static String EXCHANGE_NAME = "shippingorder";
    private static final Logger logger = Logger.getLogger(ShippingOrderPublisher.class.getName());

    @Autowired
    private RabbitMQHost rabbitMQHost;

    public void publishEvent(ShippingOrderEvent shippingOrderEvent) {
        try {
            String routingKey = determineRoutingKey(shippingOrderEvent);
            mainPublish(shippingOrderEvent, routingKey);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error publishing event", e);
        }
    }

    private String determineRoutingKey(ShippingOrderEvent shippingOrderEvent) {
        return switch (shippingOrderEvent.getEventTypeEnum()) {
            case CREATE -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_CREATED.getShippingOrderKey();
            case DELETE -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELETED.getShippingOrderKey();
            default ->  switch (shippingOrderEvent.getStatus()) {
                case CANCELLED -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_FULL_CANCEL.getShippingOrderKey();
                case REJECTED -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_REJECTED.getShippingOrderKey();
                case APPROVED -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_APPROVED.getShippingOrderKey();
                case SHIPPED -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_SHIPPED.getShippingOrderKey();
                case DELIVERED -> ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELIVERED.getShippingOrderKey();
                default -> null;
            };
        };
    }

    private void mainPublish(ShippingOrderEvent shippingOrderEvent, String routingKey) throws Exception {
        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // declare the exchange for the item events
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // publish the event
        String message = shippingOrderEvent.toJson();
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Sent event '" + routingKey + "' with message '" + message + "'");

        // close the channel and connection
        channel.close();
        connection.close();
    }
}