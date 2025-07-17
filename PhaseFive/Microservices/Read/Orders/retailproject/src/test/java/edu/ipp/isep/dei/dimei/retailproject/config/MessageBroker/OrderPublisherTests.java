package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.events.OrderEvent;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderPublisherTests {
    private final static String EXCHANGE_NAME = "order";
    @InjectMocks
    OrderPublisher orderPublisher;
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
    double price;
    List<ItemQuantity> itemQuantities = new ArrayList<>();
    Payment payment;
    Order order;
    OrderDTO orderDTO;
    OrderEvent orderEvent;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException, InvalidQuantityException {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();
        price = 12.0;
        Item item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Description")
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .build();
        itemQuantities = new ArrayList<>();
        ItemQuantity itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();
        itemQuantities.add(itemQuantity1);
        double totalPrice = itemQuantities.stream().mapToDouble(value -> value.getItem().getPrice() * value.getQuantityOrdered().getQuantity()).sum();

        payment = Payment.builder()
                .id(1)
                .amount(totalPrice)
                .paymentDateTime(currentDateTime)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();
        order = new Order(1, currentDateTime, OrderStatusEnum.PENDING, userDTO.getUserId(), itemQuantities, payment);
        orderDTO = new OrderDTO(order);
        orderEvent = new OrderEvent(orderDTO, EventTypeEnum.UPDATE);
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    void test_PublishEventSkeleton(OrderDTO orderDTO, EventTypeEnum eventTypeEnum) throws IOException, TimeoutException {
        orderEvent = new OrderEvent(orderDTO, eventTypeEnum);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderPublisher.publishEvent(orderEvent));

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();
        verify(channel).exchangeDeclare(EXCHANGE_NAME, "topic");
        //verify(channel).basicPublish(anyString(), anyString(), eq(null), any());
    }

    @Test
    void test_PublishEvent_Create() throws IOException, TimeoutException {
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.CREATE);
    }

    @Test
    void test_PublishEvent_Delete() throws IOException, TimeoutException {
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.DELETE);
    }

    @Test
    void test_PublishEvent_FullCancel() throws IOException, TimeoutException {
        orderDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Rejected() throws IOException, TimeoutException {
        orderDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Approved() throws IOException, TimeoutException {
        orderDTO.setOrderStatus(OrderStatusEnum.APPROVED);
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Shipped() throws IOException, TimeoutException {
        orderDTO.setOrderStatus(OrderStatusEnum.SHIPPED);
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Delivered() throws IOException, TimeoutException {
        orderDTO.setOrderStatus(OrderStatusEnum.DELIVERED);
        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);
    }

    @Test
    void test_PublishEvent_Nothing() throws IOException, TimeoutException {
        doNothing().when(channel).basicPublish(anyString(), eq(null), eq(null), any());

        test_PublishEventSkeleton(orderDTO, EventTypeEnum.UPDATE);


        verify(channel).basicPublish(anyString(), eq(null), eq(null), any());
    }
}
