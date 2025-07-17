package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantOrderRoutingKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MerchantOrderPublisher {
    private final static String EXCHANGE_NAME = "merchantorder";
    private static final Logger logger = Logger.getLogger(MerchantOrderPublisher.class.getName());

    @Autowired
    private RabbitMQHost rabbitMQHost;

    public void publishEvent(MerchantOrderEvent merchantOrderEvent) {
        try {
            String routingKey = determineRoutingKey(merchantOrderEvent);
            mainPublish(merchantOrderEvent, routingKey);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error publishing event", e);
        }
    }

    private String determineRoutingKey(MerchantOrderEvent merchantOrderEvent) {
        return switch (merchantOrderEvent.getEventTypeEnum()) {
            case CREATE -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_CREATED.getMerchantOrderKey();
            case DELETE -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_DELETED.getMerchantOrderKey();
            default -> switch (merchantOrderEvent.getStatus()) {
                case CANCELLED -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_FULL_CANCEL.getMerchantOrderKey();
                case REJECTED -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_REJECTED.getMerchantOrderKey();
                case APPROVED -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_APPROVED.getMerchantOrderKey();
                case SHIPPED -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_SHIPPED.getMerchantOrderKey();
                case DELIVERED -> MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_DELIVERED.getMerchantOrderKey();
                default -> null;
            };
        };
    }

    private void mainPublish(MerchantOrderEvent merchantOrderEvent, String routingKey) throws Exception {
        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // declare the exchange for the item events
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // publish the event
        String message = merchantOrderEvent.toJson();
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Sent event '" + routingKey + "' with message '" + message + "'");

        // close the channel and connection
        channel.close();
        connection.close();
    }
}