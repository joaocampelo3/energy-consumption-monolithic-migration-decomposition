package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.ShippingOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ShippingOrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
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
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ShippingOrderEventSubscriberTests {
    private final static String EXCHANGE_NAME = "shippingorder";
    @InjectMocks
    ShippingOrderEventSubscriber shippingOrderEventSubscriber;
    @Mock
    OrderService orderService;
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
    ShippingOrderDTO shippingOrderDTO;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    Instant shippingOrderDate = Instant.now();
    ShippingOrderStatusEnum shippingOrderStatus;
    AddressDTO addressDTO;
    int orderId;
    int merchantOrderId;
    String email;
    UserDTO userDTO;
    int id;
    boolean isEvent = true;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException, InvalidQuantityException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        id = 1;
        shippingOrderStatus = ShippingOrderStatusEnum.PENDING;
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        orderId = 1;
        merchantOrderId = 1;
        email = "johndoe1234@gmail.com";

        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);

        shippingOrderDTO = new ShippingOrderDTO(1, shippingOrderDate, ShippingOrderStatusEnum.PENDING, addressDTO.getId(), orderId, merchantOrderId, email);
        shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(1, shippingOrderDate, ShippingOrderStatusEnum.PENDING, addressDTO.getId(), orderId, merchantOrderId, userDTO.getUserId(), userDTO);

        subscriptionThread = new Thread(() -> {
            try {
                shippingOrderEventSubscriber.mainShippingOrderSubscription();
            } catch (IOException | TimeoutException e) {
                fail("Should not throw exception");
            }
        });
    }

    @Test
    void test_MainShippingOrderSubscription_Success() throws IOException, TimeoutException {
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
        verify(channel).exchangeDeclare("shippingorder", "topic");
        verify(channel).queueDeclare();
        verify(channel).queueBind(anyString(), eq("shippingorder"), eq("shippingorder.*"));
        verify(channel).basicConsume(anyString(), eq(true), any(Consumer.class));
    }

    @Test
    void test_MainShippingOrderSubscription_ThrowsIOException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new IOException("Connection failed"));

        // Act & Assert
        assertThrows(IOException.class, () -> shippingOrderEventSubscriber.mainShippingOrderSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    @Test
    void test_MainShippingOrderSubscription_ThrowsTimeoutException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new TimeoutException("Connection timeout"));

        // Act & Assert
        assertThrows(TimeoutException.class, () -> shippingOrderEventSubscriber.mainShippingOrderSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkeleton(Envelope envelope, ShippingOrderEvent shippingOrderEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert ShippingOrderEvent to JSON
        String shippingOrderEventJson = shippingOrderEvent.toJson();

        byte[] body = shippingOrderEventJson.getBytes(StandardCharsets.UTF_8);

        when(connection.createChannel()).thenReturn(channel);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("test-queue");

        // Start subscription in a separate thread
        subscriptionThread.start();

        // Give it time to initialize
        Thread.sleep(1000);

        // Get the Consumer instance
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(channel).basicConsume(anyString(), eq(true), consumerCaptor.capture());
        Consumer consumer = consumerCaptor.getValue();

        // Act
        consumer.handleDelivery(consumerTag, envelope, properties, body);
    }

    void handleDelivery_UpdateShippingOrder(String routingKey, ShippingOrderUpdateDTO shippingOrderUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        ShippingOrderEvent orderEvent = new ShippingOrderEvent(shippingOrderUpdateDTO, EventTypeEnum.UPDATE);

        handleDeliverySkeleton(envelope, orderEvent);
    }

    @Test
    void test_HandleDelivery_FullCancelShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_FULL_CANCEL.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RejectShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_REJECTED.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ApproveShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_APPROVED.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ShipShippingOrder() {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_SHIPPED.getShippingOrderKey();
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);

        assertDoesNotThrow(() -> handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeliveredShippingOrder() {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELIVERED.getShippingOrderKey();
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);

        assertDoesNotThrow(() -> handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }
}
