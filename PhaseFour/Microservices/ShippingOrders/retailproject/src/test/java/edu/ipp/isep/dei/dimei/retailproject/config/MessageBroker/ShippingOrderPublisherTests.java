package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.ShippingOrderEvent;
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
class ShippingOrderPublisherTests {
    private final static String EXCHANGE_NAME = "shippingorder";
    @InjectMocks
    ShippingOrderPublisher shippingOrderPublisher;
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
    ShippingOrder shippingOrder;
    ShippingOrderDTO shippingOrderDTO;
    ShippingOrderEvent shippingOrderEvent;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();
        shippingOrder = ShippingOrder.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .status(ShippingOrderStatusEnum.PENDING)
                .shippingAddressId(1)
                .orderId(1)
                .merchantOrderId(1)
                .userId(userDTO.getUserId())
                .build();

        shippingOrderDTO = ShippingOrderDTO.builder()
                .id(1)
                .shippingOrderDate(currentDateTime)
                .shippingOrderStatus(ShippingOrderStatusEnum.PENDING)
                .addressId(1)
                .orderId(1)
                .merchantOrderId(1)
                .email(userDTO.getEmail())
                .build();

        shippingOrderEvent = new ShippingOrderEvent(shippingOrderDTO, EventTypeEnum.UPDATE);
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    void test_PublishEventSkelleton(ShippingOrderDTO shippingOrderDTO, EventTypeEnum eventTypeEnum) throws IOException, TimeoutException {
        shippingOrderEvent = new ShippingOrderEvent(shippingOrderDTO, eventTypeEnum);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> shippingOrderPublisher.publishEvent(shippingOrderEvent));

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare(EXCHANGE_NAME, "topic");
        //verify(channel).basicPublish(anyString(), anyString(), eq(null), any());
    }

    @Test
    void test_PublishEvent_Create() throws IOException, TimeoutException {
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.CREATE);
    }

    @Test
    void test_PublishEvent_Delete() throws IOException, TimeoutException {
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.DELETE);
    }

    @Test
    void test_PublishEvent_FullCancel() throws IOException, TimeoutException {
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Rejected() throws IOException, TimeoutException {
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Approved() throws IOException, TimeoutException {
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.APPROVED);
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Shipped() throws IOException, TimeoutException {
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Delivered() throws IOException, TimeoutException {
        shippingOrderDTO.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Nothing() throws IOException, TimeoutException {
        doNothing().when(channel).basicPublish(anyString(), eq(null), eq(null), any());

        test_PublishEventSkelleton(shippingOrderDTO, EventTypeEnum.UPDATE);

        verify(channel).basicPublish(anyString(), eq(null), eq(null), any());
    }
}
