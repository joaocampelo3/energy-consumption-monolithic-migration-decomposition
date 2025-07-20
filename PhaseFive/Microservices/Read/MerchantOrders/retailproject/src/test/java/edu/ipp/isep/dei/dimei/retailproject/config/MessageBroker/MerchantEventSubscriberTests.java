package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.MerchantRoutingKeyEnum;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantOrderService;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantService;
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
class MerchantEventSubscriberTests {
    private final static String EXCHANGE_NAME = "merchant";
    @InjectMocks
    MerchantEventSubscriber merchantEventSubscriber;
    @Mock
    MerchantService merchantService;
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
    UserDTO userDTO;
    AddressDTO addressDTO;
    MerchantDTO merchantDTO;
    boolean isEvent = true;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);

        subscriptionThread = new Thread(() -> {
            try {
                merchantEventSubscriber.mainMerchantSubscription();
            } catch (IOException | TimeoutException e) {
                fail("Should not throw exception");
            }
        });

        userDTO = UserDTO.builder()
                .userId(1)
                .email("testEmail@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantDTO = MerchantDTO.builder()
                .id(1)
                .name("testName")
                .email("testEmail@gmail.com")
                .addressId(addressDTO.getId())
                .userDTO(userDTO)
                .addressDTO(addressDTO)
                .build();
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
        verify(channel).exchangeDeclare("merchant", "topic");
        verify(channel).queueDeclare();
        verify(channel).queueBind(anyString(), eq("merchant"), eq("merchant.*"));
        verify(channel).basicConsume(anyString(), eq(true), any(Consumer.class));
    }

    @Test
    void test_MainOrderSubscription_ThrowsIOException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new IOException("Connection failed"));

        // Act & Assert
        assertThrows(IOException.class, () -> merchantEventSubscriber.mainMerchantSubscription());

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
        assertThrows(TimeoutException.class, () -> merchantEventSubscriber.mainMerchantSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkelleton(Envelope envelope, MerchantEvent merchantEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert MerchantOrderEvent to JSON
        String merchantEventJson = merchantEvent.toJson();

        byte[] body = merchantEventJson.getBytes(StandardCharsets.UTF_8);

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

    void handleDelivery_DeleteMerchant(String routingKey, MerchantDTO merchantDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        MerchantEvent merchantEvent = new MerchantEvent(merchantDTO, EventTypeEnum.DELETE);

        handleDeliverySkelleton(envelope, merchantEvent);
    }

    @Test
    void test_HandleDelivery_DeleteMerchant() {
        // Arrange
        routingKey = MerchantRoutingKeyEnum.MERCHANT_DELETED.getMerchantKey();

        assertDoesNotThrow(() -> handleDelivery_DeleteMerchant(routingKey, merchantDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }
}
