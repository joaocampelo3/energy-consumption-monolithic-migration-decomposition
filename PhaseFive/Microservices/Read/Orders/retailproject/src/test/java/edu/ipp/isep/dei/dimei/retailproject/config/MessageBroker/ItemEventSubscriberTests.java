package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.events.ItemEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ItemRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ItemEventSubscriberTests {
    private final static String EXCHANGE_NAME = "item";
    @InjectMocks
    ItemEventSubscriber itemEventSubscriber;
    @Mock
    ItemService itemService;
    @Mock
    RabbitMQHost rabbitMQHost;
    @Mock
    ConnectionFactory connectionFactory;
    @Mock
    Connection connection;
    @Mock
    Channel channel;
    @Mock
    AMQP.Queue.DeclareOk declareOk;
    Thread subscriptionThread;
    String routingKey;
    Item item;
    ItemDTO itemDTO;
    ItemUpdateDTO itemUpdateDTO;
    boolean isEvent = true;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException, InvalidQuantityException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);

        subscriptionThread = new Thread(() -> {
            try {
                itemEventSubscriber.mainItemSubscription();
            } catch (IOException | TimeoutException e) {
                fail("Should not throw exception");
            }
        });

        item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Description")
                .price(12.0)
                .quantityInStock(new StockQuantity(10))
                .build();

        itemDTO = new ItemDTO(item);
        itemUpdateDTO = new ItemUpdateDTO(item);
    }

    @Test
    void test_MainItemSubscription_Success() throws IOException, TimeoutException {
        when(connection.createChannel()).thenReturn(channel);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("test-queue");

        // Act & Assert
        assertDoesNotThrow(() -> {
            // Start the subscription in a separate thread since it contains an infinite loop
            subscriptionThread.start();

            // Give it a small amount of time to start
            Thread.sleep(100);

            // Interrupt the thread to stop the infinite loop
            subscriptionThread.interrupt();
        });

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare("item", "topic");
        verify(channel).queueDeclare();
        verify(channel).queueBind(anyString(), eq("item"), eq("item.*"));
        verify(channel).basicConsume(anyString(), eq(true), any(Consumer.class));
    }

    @Test
    void test_MainItemSubscription_ThrowsIOException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new IOException("Connection failed"));

        // Act & Assert
        assertThrows(IOException.class, () -> itemEventSubscriber.mainItemSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    @Test
    void test_MainItemSubscription_ThrowsTimeoutException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new TimeoutException("Connection timeout"));

        // Act & Assert
        assertThrows(TimeoutException.class, () -> itemEventSubscriber.mainItemSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkeleton(Envelope envelope, ItemEvent itemEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert ItemOrderEvent to JSON
        String itemEventJson = itemEvent.toJson();

        byte[] body = itemEventJson.getBytes(StandardCharsets.UTF_8);

        when(connection.createChannel()).thenReturn(channel);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("test-queue");

        // Start subscription in a separate thread
        subscriptionThread.start();

        // Give it time to initialize
        Thread.sleep(100);

        // Get the Consumer instance
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(channel).basicConsume(anyString(), eq(true), consumerCaptor.capture());
        Consumer consumer = consumerCaptor.getValue();

        // Act
        consumer.handleDelivery(consumerTag, envelope, properties, body);
    }

    void handleDelivery_DeleteItem(String routingKey, ItemDTO itemDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        ItemEvent itemEvent = new ItemEvent(itemDTO, EventTypeEnum.DELETE);

        handleDeliverySkeleton(envelope, itemEvent);
    }

    void handleDelivery_UpdateItem(String routingKey, ItemUpdateDTO itemUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        ItemEvent itemEvent = new ItemEvent(itemUpdateDTO, EventTypeEnum.UPDATE);

        handleDeliverySkeleton(envelope, itemEvent);
    }

    void handleDelivery_CreateItem(String routingKey, ItemDTO itemDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        ItemEvent itemEvent = new ItemEvent(itemDTO, EventTypeEnum.CREATE);

        handleDeliverySkeleton(envelope, itemEvent);
    }

    @Test
    void test_HandleDelivery_CreateItem() {
        // Arrange
        routingKey = ItemRoutingKeyEnum.ITEM_CREATED.getItemKey();

        assertDoesNotThrow(() -> handleDelivery_DeleteItem(routingKey, itemDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_AddItemStock() {
        // Arrange
        routingKey = ItemRoutingKeyEnum.ITEM_ADD_STOCK.getItemKey();

        assertDoesNotThrow(() -> handleDelivery_UpdateItem(routingKey, itemUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RemoveItemStock() {
        // Arrange
        routingKey = ItemRoutingKeyEnum.ITEM_REMOVE_STOCK.getItemKey();

        assertDoesNotThrow(() -> handleDelivery_UpdateItem(routingKey, itemUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeleteItem() {
        // Arrange
        routingKey = ItemRoutingKeyEnum.ITEM_DELETED.getItemKey();

        assertDoesNotThrow(() -> handleDelivery_DeleteItem(routingKey, itemDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }
}
