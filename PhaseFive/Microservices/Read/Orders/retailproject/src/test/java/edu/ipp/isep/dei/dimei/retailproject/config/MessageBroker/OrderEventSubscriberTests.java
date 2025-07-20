package edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker;

import com.rabbitmq.client.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
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
import edu.ipp.isep.dei.dimei.retailproject.events.enums.OrderRoutingKeyEnum;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderEventSubscriberTests {
    private final static String EXCHANGE_NAME = "order";
    @InjectMocks
    OrderEventSubscriber orderEventSubscriber;
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
    boolean isEvent;
    Order order;
    OrderCreateDTO orderCreateDTO;
    OrderUpdateDTO orderUpdateDTO;
    OrderDTO orderDTO;
    Instant orderDate = Instant.now();
    List<ItemQuantity> itemQuantities = new ArrayList<>();
    Payment payment;
    double price;
    UserDTO userDTO;
    AddressDTO addressDTO;
    String routingKey;
    Thread subscriptionThread;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException, InvalidQuantityException {
        when(rabbitMQHost.getFactory()).thenReturn(connectionFactory);
        when(connectionFactory.newConnection()).thenReturn(connection);
        isEvent = true;
        price = 12.0;
        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

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
                .paymentDateTime(orderDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        Order order = new Order(1, orderDate, OrderStatusEnum.PENDING, 1, itemQuantities, payment);
        orderCreateDTO = new OrderCreateDTO(order, "testEmail@gmail.com", addressDTO, userDTO);
        orderUpdateDTO = new OrderUpdateDTO(order, "testEmail@gmail.com");
        orderDTO = new OrderDTO(order);

        subscriptionThread = new Thread(() -> {
            try {
                orderEventSubscriber.mainOrderSubscription();
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
            Thread subscriptionThread = new Thread(() -> {
                try {
                    orderEventSubscriber.mainOrderSubscription();
                } catch (IOException | TimeoutException e) {
                    fail("Should not throw exception");
                }
            });
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
        verify(channel).exchangeDeclare(EXCHANGE_NAME, "topic");
        verify(channel).queueDeclare();
        verify(channel).queueBind(anyString(), eq(EXCHANGE_NAME), eq("order.*"));
        verify(channel).basicConsume(anyString(), eq(true), any(Consumer.class));
    }

    @Test
    void test_MainOrderSubscription_ThrowsIOException() throws IOException, TimeoutException {
        // Arrange
        when(connectionFactory.newConnection()).thenThrow(new IOException("Connection failed"));

        // Act & Assert
        assertThrows(IOException.class, () -> orderEventSubscriber.mainOrderSubscription());

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
        assertThrows(TimeoutException.class, () -> orderEventSubscriber.mainOrderSubscription());

        // Verify
        verify(rabbitMQHost).getFactory();
        verify(connectionFactory).newConnection();
        verifyNoInteractions(channel);
    }

    void handleDeliverySkeleton(Envelope envelope, OrderEvent orderEvent) throws IOException, InterruptedException {
        // Arrange
        String consumerTag = "testConsumerTag";
        AMQP.BasicProperties properties = new AMQP.BasicProperties();

        // Convert OrderEvent to JSON
        String orderEventJson = orderEvent.toJson();

        byte[] body = orderEventJson.getBytes(StandardCharsets.UTF_8);

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

    void handleDelivery_CreateOrder(String routingKey, OrderCreateDTO orderCreateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        OrderEvent orderEvent = new OrderEvent(orderCreateDTO, EventTypeEnum.CREATE);

        handleDeliverySkeleton(envelope, orderEvent);
    }

    void handleDelivery_UpdateOrder(String routingKey, OrderUpdateDTO orderUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        OrderEvent orderEvent = new OrderEvent(orderUpdateDTO, EventTypeEnum.UPDATE);

        handleDeliverySkeleton(envelope, orderEvent);
    }

    void handleDelivery_DeleteOrder(String routingKey, OrderUpdateDTO orderUpdateDTO) throws IOException, InterruptedException {
        // Arrange
        Envelope envelope = new Envelope(1L, false, EXCHANGE_NAME, routingKey);
        OrderEvent orderEvent = new OrderEvent(orderUpdateDTO, EventTypeEnum.DELETE);

        handleDeliverySkeleton(envelope, orderEvent);
    }

    @Test
    void test_HandleDelivery_CreateOrder() throws IOException, InterruptedException, InvalidQuantityException, BadPayloadException, NotFoundException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_CREATED.getOrderKey();
        when(orderService.createOrder(orderCreateDTO, isEvent)).thenReturn(orderDTO);

        handleDelivery_CreateOrder(routingKey, orderCreateDTO);

        // Assert
        verify(orderService).createOrder(any(OrderCreateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_FullCancelOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_FULL_CANCEL.getOrderKey();
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        when(orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent)).thenReturn(orderUpdateDTO);

        handleDelivery_UpdateOrder(routingKey, orderUpdateDTO);

        // Assert
        verify(orderService).fullCancelOrder(anyInt(), any(OrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_RejectOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException, InvalidQuantityException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_REJECTED.getOrderKey();
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        when(orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent)).thenReturn(orderUpdateDTO);

        handleDelivery_UpdateOrder(routingKey, orderUpdateDTO);

        // Assert
        verify(orderService).rejectOrder(anyInt(), any(OrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ApproveOrder() throws IOException, InterruptedException, WrongFlowException, BadPayloadException, NotFoundException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_APPROVED.getOrderKey();
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);
        when(orderService.approveOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent)).thenReturn(orderUpdateDTO);

        handleDelivery_UpdateOrder(routingKey, orderUpdateDTO);

        // Assert
        verify(orderService).approveOrder(anyInt(), any(OrderUpdateDTO.class), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_ShipOrder() throws WrongFlowException, NotFoundException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_SHIPPED.getOrderKey();
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.SHIPPED);
        doNothing().when(orderService).shipOrder(null, orderUpdateDTO.getId(), isEvent);

        assertDoesNotThrow(() -> handleDelivery_UpdateOrder(routingKey, orderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeliveredOrder() throws WrongFlowException, NotFoundException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_DELIVERED.getOrderKey();
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.DELIVERED);
        doNothing().when(orderService).deliverOrder(null, orderUpdateDTO.getId(), isEvent);

        assertDoesNotThrow(() -> handleDelivery_UpdateOrder(routingKey, orderUpdateDTO));

        // Cleanup
        subscriptionThread.interrupt();
    }

    @Test
    void test_HandleDelivery_DeleteOrder() throws IOException, InterruptedException, NotFoundException {
        // Arrange
        routingKey = OrderRoutingKeyEnum.ORDER_DELETED.getOrderKey();
        when(orderService.deleteOrder(0, orderUpdateDTO.getId(), isEvent)).thenReturn(orderDTO);

        handleDelivery_DeleteOrder(routingKey, orderUpdateDTO);

        // Assert
        verify(orderService).deleteOrder(anyInt(), anyInt(), eq(isEvent));

        // Cleanup
        subscriptionThread.interrupt();
    }

}
