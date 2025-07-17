package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.MerchantOrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MerchantOrderPublisherTests {
    private final static String EXCHANGE_NAME = "merchantorder";
    @InjectMocks
    MerchantOrderPublisher merchantOrderPublisher;
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

    UserDTO userDTO;
    Instant currentDateTime = Instant.now();
    MerchantOrder merchantOrder;
    MerchantOrderDTO merchantOrderDTO;
    MerchantOrderEvent merchantOrderEvent;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();
        merchantOrder = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(userDTO.getUserId()).orderId(1).merchantId(1).build();
        merchantOrderDTO = MerchantOrderDTO.builder().id(1).merchantOrderDate(currentDateTime).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).customerId(merchantOrder.getUserId()).email(userDTO.getEmail()).orderId(1).merchantId(merchantOrder.getMerchantId()).build();
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    void test_PublishEventSkelleton(MerchantOrderDTO merchantOrderDTO, EventTypeEnum eventTypeEnum) throws IOException, TimeoutException {
        merchantOrderEvent = new MerchantOrderEvent(merchantOrderDTO, eventTypeEnum);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> merchantOrderPublisher.publishEvent(merchantOrderEvent));

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare(EXCHANGE_NAME, "topic");
        //verify(channel).basicPublish(anyString(), anyString(), eq(null), any());
    }

    @Test
    void test_PublishEvent_Create() throws IOException, TimeoutException {
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.CREATE);
    }

    @Test
    void test_PublishEvent_Delete() throws IOException, TimeoutException {
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.DELETE);
    }

    @Test
    void test_PublishEvent_FullCancel() throws IOException, TimeoutException {
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Rejected() throws IOException, TimeoutException {
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Approved() throws IOException, TimeoutException {
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Shipped() throws IOException, TimeoutException {
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.SHIPPED);
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Delivered() throws IOException, TimeoutException {
        merchantOrderDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.DELIVERED);
        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Nothing() throws IOException, TimeoutException {
        doNothing().when(channel).basicPublish(anyString(), eq(null), eq(null), any());

        test_PublishEventSkelleton(merchantOrderDTO, EventTypeEnum.UPDATE);

        verify(channel).basicPublish(anyString(), eq(null), eq(null), any());
    }
}
