package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantRoutingKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Component
public class MerchantPublisher {
    private final static String EXCHANGE_NAME = "merchant";
    private static final Logger logger = Logger.getLogger(MerchantPublisher.class.getName());

    @Autowired
    private RabbitMQHost rabbitMQHost;

    public void publishEvent(MerchantEvent merchantEvent) {
        try {
            String routingKey = determineRoutingKey(merchantEvent);
            mainPublish(merchantEvent, routingKey);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error publishing event", e);
        }
    }

    private String determineRoutingKey(MerchantEvent merchantEvent) {
        return switch (merchantEvent.getEventTypeEnum()) {
            case CREATE -> MerchantRoutingKeyEnum.MERCHANT_CREATED.getMerchantKey();
            case DELETE -> MerchantRoutingKeyEnum.MERCHANT_DELETED.getMerchantKey();
            case UPDATE -> MerchantRoutingKeyEnum.MERCHANT_UPDATED.getMerchantKey();
        };
    }

    private void mainPublish(MerchantEvent merchantEvent, String routingKey) throws Exception {
        // create a connection to the RabbitMQ server
        ConnectionFactory factory = rabbitMQHost.getFactory();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // declare the exchange for the item events
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // publish the event
        String message = merchantEvent.toJson();
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Sent event '" + routingKey + "' with message '" + message + "'");

        // close the channel and connection
        channel.close();
        connection.close();
    }
}