package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.Publisher;
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
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.ipp.isep.dei.dimei.retailproject.services.OrderService.BADPAYLOADEXCEPTIONMESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ItemService itemService;
    @Mock
    PaymentService paymentService;
    @Mock
    ItemQuantityService itemQuantityService;
    @Mock
    Publisher publisher;
    double price;
    OrderDTO orderDTO1;
    OrderDTO orderDTO2;
    List<OrderDTO> orderDTOS = new ArrayList<>();
    OrderUpdateDTO orderUpdateDTO;
    AddressDTO shippingAddressDTO;
    AddressDTO merchantAddressDTO;
    Order order1;
    Order order2;
    Order newOrder1;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
    UserDTO adminUserDTO;
    Item item;
    Item itemUpdated;
    ItemQuantity itemQuantity1;
    ItemQuantity itemQuantity2;
    List<ItemQuantity> itemQuantityList1 = new ArrayList<>();
    List<ItemQuantity> itemQuantityList2 = new ArrayList<>();
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantityDTO itemQuantityDTO2;
    ItemUpdateDTO itemUpdateDTO1;
    ItemDTO itemDTO1;
    ItemDTO itemDTO2;
    ItemDTO itemDTO1Updated;
    List<Order> orders = new ArrayList<>();
    Instant currentDateTime = Instant.now();
    OrderCreateDTO orderCreateDTO;
    Payment payment;
    PaymentDTO paymentDTO;
    Order order1Updated;
    int categoryId;
    int merchantId;
    boolean isEvent = false;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        price = 12.0;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        merchantUserDTO = UserDTO.builder()
                .userId(2)
                .email("merchant_email@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        adminUserDTO = UserDTO.builder()
                .userId(3)
                .email("admin_email@gmail.com")
                .role(RoleEnum.ADMIN)
                .build();

        shippingAddressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantAddressDTO = AddressDTO.builder()
                .id(2)
                .street("Different Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        categoryId = 1;
        merchantId = 1;

        item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(1)
                .quantityInStock(new StockQuantity(10))
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        itemUpdated = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 Desc")
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        itemQuantity1 = ItemQuantity.builder()
                .id(1)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();

        itemQuantityDTO1 = new ItemQuantityDTO(itemQuantity1);

        itemQuantity2 = ItemQuantity.builder()
                .id(2)
                .quantityOrdered(new OrderQuantity(1))
                .item(item)
                .price(price)
                .build();

        itemQuantityDTO2 = new ItemQuantityDTO(itemQuantity2);

        itemUpdateDTO1 = new ItemUpdateDTO(item.getId(), item.getSku(), item.getPrice(), item.getQuantityInStock().getQuantity(), userDTO);

        itemDTO1 = new ItemDTO(item);
        itemDTO2 = new ItemDTO(item);

        itemDTO1Updated = new ItemDTO(item);
        itemDTO1Updated.setQuantityInStock(itemDTO1Updated.getQuantityInStock() - 1);

        itemQuantityList1.add(itemQuantity1);

        newOrder1 = Order.builder()
                .id(0)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        order1 = Order.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        itemQuantityList2.add(itemQuantity2);

        order2 = Order.builder()
                .id(2)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList2)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        orders.add(order1);
        orders.add(order2);

        orderDTO1 = new OrderDTO(order1);
        orderDTO2 = new OrderDTO(order2);
        orderDTOS.add(orderDTO1);
        orderDTOS.add(orderDTO2);

        shippingAddressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        payment = Payment.builder()
                .id(1)
                .amount(1)
                .paymentDateTime(currentDateTime)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();

        paymentDTO = new PaymentDTO(payment);

        orderCreateDTO = OrderCreateDTO.builder()
                .orderDate(currentDateTime)
                .customerId(userDTO.getUserId())
                .email(userDTO.getEmail())
                .orderItems(order1.getItemQuantities().stream().map(ItemQuantityDTO::new).toList())
                .totalPrice(1)
                .payment(paymentDTO)
                .merchantId(merchantId)
                .address(shippingAddressDTO)
                .userDTO(userDTO)
                .build();

        order1Updated = Order.builder()
                .id(1)
                .orderDate(currentDateTime)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantityList1)
                .payment(Payment.builder()
                        .id(1)
                        .amount(1)
                        .paymentDateTime(currentDateTime)
                        .paymentMethod(PaymentMethodEnum.CARD)
                        .status(PaymentStatusEnum.ACCEPTED)
                        .build())
                .build();

        orderUpdateDTO = new OrderUpdateDTO(order1.getId(), order1.getOrderDate(), order1.getStatus(), userDTO.getEmail(), userDTO);
    }

    @Test
    void test_GetAllOrders() {
        // Define the behavior of the mock
        when(orderRepository.findAll()).thenReturn(orders);

        // Call the service method that uses the Repository
        List<OrderDTO> result = orderService.getAllOrders();
        List<OrderDTO> expected = orderDTOS;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrders() {
        // Define the behavior of the mock
        when(orderRepository.findByUserId(userDTO.getUserId())).thenReturn(orders);

        // Call the service method that uses the Repository
        List<OrderDTO> result = orderService.getUserOrders(userDTO);
        List<OrderDTO> expected = orderDTOS;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findByUserId(userDTO.getUserId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateOrder() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        itemUpdateDTO1.setQuantityInStock(itemUpdateDTO1.getQuantityInStock() - 1);
        itemUpdated.getQuantityInStock().setQuantity((itemUpdated.getQuantityInStock().getQuantity() - 1));
        when(itemService.getItemDTO(itemQuantityDTO1.getId())).thenReturn(itemDTO1);
        when(paymentService.createPayment(orderCreateDTO.getPayment())).thenReturn(payment);
        when(itemQuantityService.createItemQuantity(itemQuantityDTO1)).thenReturn(itemQuantity1);
        when(itemService.removeItemStock(itemQuantityDTO1.getItemId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty(), userDTO))).thenReturn(itemDTO1Updated);
        when(orderRepository.save(newOrder1)).thenReturn(order1);

        // Call the service method that uses the Repository
        OrderDTO result = orderService.createOrder(orderCreateDTO, isEvent);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(itemService, atLeastOnce()).getItemDTO(itemQuantityDTO1.getId());
        verify(paymentService, atLeastOnce()).createPayment(orderCreateDTO.getPayment());
        verify(itemQuantityService, atLeastOnce()).createItemQuantity(itemQuantityDTO1);
        verify(itemService, atLeastOnce()).removeItemStock(itemQuantity1.getItem().getId(), new ItemUpdateDTO(itemQuantityDTO1.getItemId(), itemQuantityDTO1.getItemSku(), itemQuantityDTO1.getPrice(), item.getQuantityInStock().getQuantity() - itemQuantityDTO1.getQty(), userDTO));
        verify(orderRepository, atLeastOnce()).save(newOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(userDTO, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(adminUserDTO, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserOrderByMerchant() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(orderRepository.findById(id)).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.getUserOrder(merchantUserDTO, id);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteOrder() throws NotFoundException {
        // Define the behavior of the mock
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderDTO result = orderService.deleteOrder(userDTO.getUserId(), order1.getId(), false);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        item.getQuantityInStock().setQuantity(item.getQuantityInStock().getQuantity() + 1);
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);

        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailCancelledOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderIDAndCancelledOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrderByOrderId(userDTO, orderUpdateDTO.getId(), false);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrder() throws InvalidQuantityException, WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrderByOrderId(userDTO, orderUpdateDTO.getId(), false);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailRejectedOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailDifferentOrderIDAndRejectedOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.approveOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveOrderFailDifferentOrderID() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.approveOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrderFailApprovedOrder() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.approveOrder(orderUpdateDTO.getId(), orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrderFailDifferentOrderIDAndApprovedOrder() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, false);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ShipOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.APPROVED);
        order1Updated.setStatus(OrderStatusEnum.SHIPPED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);


        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.shipOrder(userDTO, orderUpdateDTO.getId(), false));

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
    }

    @Test
    void test_DeliverOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.SHIPPED);
        order1Updated.setStatus(OrderStatusEnum.SHIPPED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.deliverOrder(userDTO, orderUpdateDTO.getId(), false));

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
    }

}
