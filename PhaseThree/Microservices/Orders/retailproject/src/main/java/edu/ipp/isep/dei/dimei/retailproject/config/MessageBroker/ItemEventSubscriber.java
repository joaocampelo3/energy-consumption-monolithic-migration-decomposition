package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.ItemEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ItemRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
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
public class ItemEventSubscriber {

    private static final String EXCHANGE_NAME = "item";

    @Autowired
    private ItemService itemService;

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
        channel.queueBind(queueName, EXCHANGE_NAME, "item.*");

        // create a consumer and start consuming messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                String eventType = envelope.getRoutingKey().substring(envelope.getRoutingKey().lastIndexOf(".") + 1);
                String originService = envelope.getRoutingKey().substring(0, envelope.getRoutingKey().indexOf("."));
                System.out.println("Received event '" + eventType + "' from service '" + originService + "' with message '" + message + "'");

                // parse the message as a ItemEvent
                if (eventType.equals(ItemRoutingKeyEnum.ITEM_CREATED.getKey()) || eventType.equals(ItemRoutingKeyEnum.ITEM_ADD_STOCK.getKey()) || eventType.equals(ItemRoutingKeyEnum.ITEM_REMOVE_STOCK.getKey()) || eventType.equals(ItemRoutingKeyEnum.ITEM_DELETED.getKey())) {
                    ItemEvent event = ItemEvent.fromJson(message);
                    try {
                        handleItemEvent(eventType, event);
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

    private void handleItemEvent(String eventType, ItemEvent event) throws Exception {
        // handle the item event
        if (ItemRoutingKeyEnum.ITEM_CREATED.getKey().equals(eventType)) {
            itemService.createItem(
                    ItemDTO.builder()
                            .id(event.getId())
                            .itemName(event.getName())
                            .sku(event.getSku())
                            .itemDescription(event.getDescription())
                            .price(event.getPrice())
                            .quantityInStock(event.getQuantity())
                            .build()
            );
        } else if (ItemRoutingKeyEnum.ITEM_ADD_STOCK.getKey().equals(eventType)) {
            itemService.addItemStock(event.getId(),
                    ItemUpdateDTO.builder()
                            .id(event.getId())
                            .sku(event.getSku())
                            .price(event.getPrice())
                            .quantityInStock(event.getQuantity())
                            .build()
            );
        } else if (ItemRoutingKeyEnum.ITEM_REMOVE_STOCK.getKey().equals(eventType)) {
            itemService.removeItemStock(event.getId(),
                    ItemUpdateDTO.builder()
                            .id(event.getId())
                            .sku(event.getSku())
                            .price(event.getPrice())
                            .quantityInStock(event.getQuantity())
                            .build()
            );
        } else if (ItemRoutingKeyEnum.ITEM_DELETED.getKey().equals(eventType)) {
            itemService.deleteItem(event.getId());
        } else {
            throw new Exception("Not a valid event type");
        }
    }

    @Bean
    @Async
    public void mainItemSubscription() throws IOException, TimeoutException {
        this.start();
    }

}
