package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantOrderRoutingKeyEnum;
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
class MerchantOrderEventSubscriberTests {
    private final static String EXCHANGE_NAME = "merchantorder";
    @InjectMocks
    MerchantOrderEventSubscriber merchantOrderEventSubscriber;
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
    MerchantOrderUpdateDTO merchantOrderUpdateDTO;
    int id;
    Instant merchantOrderDate = Instant.now();
    String email;
    int orderId;
    int merchantId;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        id = 1;
        email = "merchantnumber1@gmail.com";
        orderId = 1;
        merchantId = 1;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(id, merchantOrderDate, MerchantOrderStatusEnum.PENDING, email, orderId, merchantId, userDTO);

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
            Thread.sleep(100);

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

    void handleDeliverySkeleton(Envelope envelope, MerchantOrderEvent merchantOrderEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert OrderEvent to JSON
        String merchantOrderEventJson = merchantOrderEvent.toJson();

        byte[] body = merchantOrderEventJson.getBytes(StandardCharsets.UTF_8);

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

    void handleDelivery_UpdateOrder(String routingKey, MerchantOrderUpdateDTO merchantOrderUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        MerchantOrderEvent merchantOrderEvent = new MerchantOrderEvent(merchantOrderUpdateDTO, EventTypeEnum.UPDATE);

        handleDeliverySkeleton(envelope, merchantOrderEvent);
    }

    void handleDelivery_DeleteOrder(String routingKey, MerchantOrderDTO merchantOrderDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        MerchantOrderEvent merchantOrderEvent = new MerchantOrderEvent(merchantOrderDTO, EventTypeEnum.DELETE);

        handleDeliverySkeleton(envelope, merchantOrderEvent);
    }

    @Test
    void test_HandleDelivery_FullCancelOrder() throws IOException, InterruptedException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_FULL_CANCEL.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        handleDelivery_UpdateOrder(routingKey, merchantOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RejectOrder() throws IOException, InterruptedException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_REJECTED.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        handleDelivery_UpdateOrder(routingKey, merchantOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ApproveOrder() throws IOException, InterruptedException {
        // Arrange
        routingKey = MerchantOrderRoutingKeyEnum.MERCHANT_ORDER_APPROVED.getMerchantOrderKey();
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);

        handleDelivery_UpdateOrder(routingKey, merchantOrderUpdateDTO);

        // Cleanup
        subscriptionThread.interrupt();
    }
}
