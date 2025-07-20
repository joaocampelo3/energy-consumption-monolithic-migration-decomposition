package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantOrderRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantOrderService;
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
class MerchantOrderEventSubscriberTests {
    private final static String EXCHANGE_NAME = "merchantorder";
    @InjectMocks
    MerchantOrderEventSubscriber merchantOrderEventSubscriber;
    @Mock
    MerchantOrderService merchantOrderService;
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
    Merchant merchant;
    MerchantOrder merchantOrder;
    MerchantOrderDTO merchantOrderDTO;
    MerchantOrderUpdateDTO merchantOrderUpdateDTO;
    Instant merchantOrderDate = Instant.now();
    boolean isEvent = true;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);

        merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(1)
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(merchantOrderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(1)
                .orderId(1)
                .merchant(merchant)
                .build();

        merchantOrderDTO = new MerchantOrderDTO(merchantOrder, "testEmail@gmail.com");
        merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(merchantOrder, "testEmail@gmail.com");

        subscriptionThread = new Thread(() -> {
            try {
                merchantOrderEventSubscriber.mainMerchantOrderSubscription();
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
            Thread.sleep(300);

            // Interrupt the thread to stop the infinite loop
            subscriptionThread.interrupt();
        });

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare("merchantorder", "topic");
        verify(channel).queueDeclare();
        verify(channel).queueBind(anyString(), eq("merchantorder"), eq("merchantorder.*"));
        verify(channel).basicConsume(anyString(), eq(true), any(Consumer.class));
    }

    @Test
    void test_MainOrderSubscription_ThrowsIOException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new IOException("Connection failed"));

        // Act & Assert
        assertThrows(IOException.class, () -> merchantOrderEventSubscriber.mainMerchantOrderSubscription());

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
        assertThrows(TimeoutException.class, () -> merchantOrderEventSubscriber.mainMerchantOrderSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkelleton(Envelope envelope, MerchantOrderEvent merchantOrderEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert MerchantOrderEvent to JSON
        String merchantOrderEventJson = merchantOrderEvent.toJson();

        byte[] body = merchantOrderEventJson.getBytes(StandardCharsets.UTF_8);

        when(connection.createChannel()).thenReturn(channel);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("test-queue");

        // Start subscription in a separate thread
        subscriptionThread.start();

        // Give it time to initialize
        Thread.sleep(300);

        // Get the Consumer instance
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(channel).basicConsume(anyString(), eq(true), consumerCaptor.capture());
        Consumer consumer = consumerCaptor.getValue();

        // Act
        consumer.handleDelivery(consumerTag, envelope, properties, body);
    }

    void handleDelivery_UpdateMerchantOrder(String routingKey, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        MerchantOrderEvent orderEvent = new MerchantOrderEvent(merchantOrderUpdateDTO, EventTypeEnum.UPDATE);

        handleDeliverySkelleton(envelope, orderEvent);
    }

    void handleDelivery_DeleteMerchantOrder(String routingKey, MerchantOrderDTO merchantOrderDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        MerchantOrderEvent orderEvent = new MerchantOrderEvent(merchantOrderDTO, EventTypeEnum.DELETE);

        handleDeliverySkelleton(envelope, orderEvent);
    }

    @Test
    void test_HandleDelivery_FullCancelMerchantOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_FULL_CANCEL.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        when(merchantOrderService.fullCancelMerchantOrder(merchantOrderUpdateDTO.getId(), MerchantOrderUpdateDTO.builder().id(merchantOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(merchantOrderUpdateDTO);

        handleDelivery_UpdateMerchantOrder(routingKey, merchantOrderUpdateDTO);

        // Assert
        verify(merchantOrderService).fullCancelMerchantOrder(anyInt(), any(MerchantOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RejectMerchantOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_REJECTED.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);
        when(merchantOrderService.rejectMerchantOrder(merchantOrderDTO.getId(), MerchantOrderUpdateDTO.builder().id(merchantOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(merchantOrderUpdateDTO);

        handleDelivery_UpdateMerchantOrder(routingKey, merchantOrderUpdateDTO);

        // Assert
        verify(merchantOrderService).rejectMerchantOrder(anyInt(), any(MerchantOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ApproveMerchantOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_APPROVED.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        when(merchantOrderService.approveMerchantOrder(merchantOrderDTO.getId(), MerchantOrderUpdateDTO.builder().id(merchantOrderUpdateDTO.getId()).build(), isEvent)).thenReturn(merchantOrderUpdateDTO);

        handleDelivery_UpdateMerchantOrder(routingKey, merchantOrderUpdateDTO);

        // Assert
        verify(merchantOrderService).approveMerchantOrder(anyInt(), any(MerchantOrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ShipMerchantOrder() throws WrongFlowException, NotFoundException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_SHIPPED.getMerchantOrderKey();
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.SHIPPED);
        merchantOrder.setStatus(MerchantOrderStatusEnum.SHIPPED);
        when(merchantOrderService.shipMerchantOrder(null, merchantOrderUpdateDTO.getId(), isEvent)).thenReturn(merchantOrder);

        assertDoesNotThrow(() -> handleDelivery_UpdateMerchantOrder(routingKey, merchantOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeliveredMerchantOrder() throws WrongFlowException, NotFoundException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_DELIVERED.getMerchantOrderKey();
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.DELIVERED);
        merchantOrder.setStatus(MerchantOrderStatusEnum.DELIVERED);
        when(merchantOrderService.deliverMerchantOrder(null, merchantOrderUpdateDTO.getId(), isEvent)).thenReturn(merchantOrder);

        assertDoesNotThrow(() -> handleDelivery_UpdateMerchantOrder(routingKey, merchantOrderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeleteMerchantOrder() {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_DELETED.getMerchantOrderKey();
        doNothing().when(merchantOrderService).deleteMerchantOrderByOrderId(merchantOrderDTO.getId());

        assertDoesNotThrow(() -> handleDelivery_DeleteMerchantOrder(routingKey, merchantOrderDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }
}
