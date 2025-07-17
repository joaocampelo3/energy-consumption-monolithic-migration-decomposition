package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.ShippingOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.ShippingOrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService;
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
    ShippingOrderService shippingOrderService;
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
    ShippingOrder shippingOrder;
    ShippingOrderDTO shippingOrderDTO;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    Instant shippingOrderDate = Instant.now();
    boolean isEvent = true;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);

        shippingOrder = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(shippingOrderDate)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(1)
                .orderId(1)
                .merchantOrderId(1)
                .userId(1)
                .build();

        shippingOrderDTO = new ShippingOrderDTO(shippingOrder, "testEmail@gmail.com");
        shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(shippingOrder);

        subscriptionThread = new Thread(() -> {
            try {
                shippingOrderEventSubscriber.mainShippingOrderSubscription();
            } catch (IOException | TimeoutException e) {
                fail("Should not throw exception");
            }
        });
    }

    @Test
    void test_MainOrderSubscription_Success() throws IOException, TimeoutException {
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
    void test_MainOrderSubscription_ThrowsIOException() throws IOException, TimeoutException {
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
    void test_MainOrderSubscription_ThrowsTimeoutException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new TimeoutException("Connection timeout"));

        // Act & Assert
        assertThrows(TimeoutException.class, () -> shippingOrderEventSubscriber.mainShippingOrderSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkelleton(Envelope envelope, ShippingOrderEvent shippingOrderEvent) throws IOException, InterruptedException {
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
        Thread.sleep(100);

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

        handleDeliverySkelleton(envelope, orderEvent);
    }

    void handleDelivery_DeleteShippingOrder(String routingKey, ShippingOrderDTO shippingOrderDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        ShippingOrderEvent orderEvent = new ShippingOrderEvent(shippingOrderDTO, EventTypeEnum.DELETE);

        handleDeliverySkelleton(envelope, orderEvent);
    }

    @Test
    void test_HandleDelivery_FullCancelShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_FULL_CANCEL.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        when(shippingOrderService.fullCancelShippingOrder(shippingOrderUpdateDTO.getId(), ShippingOrderUpdateDTO.builder().id(shippingOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(shippingOrderUpdateDTO);

        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Assert
        verify(shippingOrderService).fullCancelShippingOrder(anyInt(), any(ShippingOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RejectShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_REJECTED.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        when(shippingOrderService.rejectShippingOrder(shippingOrderDTO.getId(), ShippingOrderUpdateDTO.builder().id(shippingOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(shippingOrderUpdateDTO);

        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Assert
        verify(shippingOrderService).rejectShippingOrder(anyInt(), any(ShippingOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ApproveShippingOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_APPROVED.getShippingOrderKey();
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        when(shippingOrderService.approveShippingOrder(shippingOrderDTO.getId(), ShippingOrderUpdateDTO.builder().id(shippingOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(shippingOrderUpdateDTO);

        handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO);

        // Assert
        verify(shippingOrderService).approveShippingOrder(anyInt(), any(ShippingOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ShipShippingOrder() throws WrongFlowException, NotFoundException, BadPayloadException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_SHIPPED.getShippingOrderKey();
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        when(shippingOrderService.shippedShippingOrder(
                shippingOrderUpdateDTO.getId(),
                ShippingOrderUpdateDTO.builder().id(shippingOrderUpdateDTO.getId()).build(),
                isEvent
        )).thenReturn(shippingOrderUpdateDTO);

        assertDoesNotThrow(() -> handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeliveredShippingOrder() throws WrongFlowException, NotFoundException, BadPayloadException {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELIVERED.getShippingOrderKey();
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        when(shippingOrderService.deliveredShippingOrder(
                shippingOrderUpdateDTO.getId(),
                ShippingOrderUpdateDTO.builder().id(shippingOrderUpdateDTO.getId()).build(),
                isEvent
        )).thenReturn(shippingOrderUpdateDTO);

        assertDoesNotThrow(() -> handleDelivery_UpdateShippingOrder(routingKey, shippingOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeleteShippingOrder() {
        // Arrange
        routingKey = ShippingOrderRoutingKeyEnum.SHIPPING_ORDER_DELETED.getShippingOrderKey();
        doNothing().when(shippingOrderService).deleteShippingOrderByOrderId(shippingOrderDTO.getId(), isEvent);

        assertDoesNotThrow(() -> handleDelivery_DeleteShippingOrder(routingKey, shippingOrderDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }
}
