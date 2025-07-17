package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.OrderPublisher;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
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
    MerchantOrderService merchantOrderService;
    @Mock
    ShippingOrderService shippingOrderService;
    @Mock
    OrderPublisher publisher;
    OrderDTO orderDTO1;
    OrderDTO orderDTO2;
    List<OrderDTO> orderDTOS = new ArrayList<>();
    OrderUpdateDTO orderUpdateDTO;
    AddressDTO shippingAddressDTO;
    AddressDTO merchantAddressDTO;
    Order order1;
    Order order2;
    Merchant merchant;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
    UserDTO adminUserDTO;
    MerchantOrder merchantOrder1;
    List<Order> orders = new ArrayList<>();
    Instant currentDateTime = Instant.now();
    OrderCreateDTO orderCreateDTO;
    Order order1Updated;
    MerchantOrderDTO merchantOrderDTO1;
    ShippingOrder shippingOrder1;
    ShippingOrderDTO shippingOrderDTO1;
    boolean isEvent;

    @BeforeEach
    void beforeEach() {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();

        merchantUserDTO = UserDTO.builder().userId(2).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        adminUserDTO = UserDTO.builder().userId(3).email("admin_email@gmail.com").role(RoleEnum.ADMIN).build();

        shippingAddressDTO = AddressDTO.builder().id(1).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        merchantAddressDTO = AddressDTO.builder().id(2).street("Different Avenue").zipCode("10128").city("New York").country("USA").build();

        merchant = Merchant.builder().id(1).name("Merchant 1").email("merchant1_email@gmail.com").addressId(merchantAddressDTO.getId()).build();

        order1 = Order.builder().id(1).orderDate(currentDateTime).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        order2 = Order.builder().id(2).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        merchantOrder1 = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchant(merchant).build();

        orders.add(order1);
        orders.add(order2);

        orderDTO1 = new OrderDTO(order1);
        orderDTO2 = new OrderDTO(order2);
        orderDTOS.add(orderDTO1);
        orderDTOS.add(orderDTO2);

        shippingAddressDTO = AddressDTO.builder().id(1).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        orderCreateDTO = OrderCreateDTO.builder().orderDate(currentDateTime).customerId(userDTO.getUserId()).email(userDTO.getEmail()).userDTO(userDTO).build();

        order1Updated = Order.builder().id(1).orderDate(currentDateTime).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        orderUpdateDTO = new OrderUpdateDTO(order1.getId(), order1.getOrderDate(), order1.getStatus(), userDTO.getEmail(), userDTO);
        merchantOrderDTO1 = new MerchantOrderDTO(merchantOrder1.getId(), merchantOrder1.getOrderDate(), merchantOrder1.getStatus(), userDTO.getUserId(), userDTO.getEmail(), order1.getId(), merchant.getId());
        merchantOrderDTO1 = new MerchantOrderDTO(merchantOrder1, userDTO.getEmail());
        shippingOrder1 = new ShippingOrder(userDTO.getUserId(), order1.getId(), merchantOrder1.getId());
        shippingOrderDTO1 = new ShippingOrderDTO(shippingOrder1, userDTO.getEmail());

        isEvent = false;
    }

    @Test
    void test_CreateOrder() throws NotFoundException {
        // Define the behavior of the mock
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        // Call the service method that uses the Repository
        OrderDTO result = orderService.createOrder(orderCreateDTO, isEvent);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
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
        OrderDTO result = orderService.deleteOrder(userDTO.getUserId(), order1.getId(), isEvent);
        OrderDTO expected = orderDTO1;

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        MerchantOrderUpdateDTO merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(merchantOrder1, userDTO.getEmail());
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        ShippingOrderUpdateDTO shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(shippingOrder1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);

        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrderIsEvent() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        MerchantOrderUpdateDTO merchantOrderUpdateDTO = new MerchantOrderUpdateDTO(merchantOrder1, userDTO.getEmail());
        merchantOrderUpdateDTO.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        ShippingOrderUpdateDTO shippingOrderUpdateDTO = new ShippingOrderUpdateDTO(shippingOrder1);
        shippingOrderUpdateDTO.setShippingOrderStatus(ShippingOrderStatusEnum.CANCELLED);
        isEvent = true;

        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(merchantOrderService.fullCancelMerchantOrderByOrder(userDTO, order1Updated.getId(), isEvent)).thenReturn(merchantOrderUpdateDTO);
        doReturn(shippingOrderUpdateDTO).when(shippingOrderService).fullCancelShippingOrderByOrder(userDTO, order1Updated.getId(), isEvent);
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(merchantOrderService, atLeastOnce()).fullCancelMerchantOrderByOrder(userDTO, order1Updated.getId(), isEvent);
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByOrder(userDTO, order1Updated.getId(), isEvent);
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
            orderService.fullCancelOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderIDIsEvent() {
        // Define the behavior of the mock
        int id = 2;
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(id, orderUpdateDTO, isEvent);
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
            orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailCancelledOrderIsEvent() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
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
            orderService.fullCancelOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderFailDifferentOrderIDAndCancelledOrderIsEvent() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.CANCELLED);
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.fullCancelOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_FullCancelOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrderByOrderId(userDTO, orderUpdateDTO.getId(), isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelOrderByOrderIdIsEvent() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.CANCELLED);
        isEvent = true;
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.fullCancelOrderByOrderId(userDTO, orderUpdateDTO.getId(), isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderIsEvent() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        isEvent = true;
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
        verify(orderRepository, atLeastOnce()).save(order1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectOrderByOrderId() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        order1Updated.setStatus(OrderStatusEnum.REJECTED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.rejectOrderByOrderId(userDTO, orderUpdateDTO.getId());
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
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
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailDifferentOrderIDIsEvent() {
        // Define the behavior of the mock
        int id = 2;
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
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
            orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailRejectedOrderIsEvent() {
        // Define the behavior of the mock
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.PENDING);
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
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
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_RejectOrderFailDifferentOrderIDAndRejectedOrderIsEvent() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.REJECTED);
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
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
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);
        when(orderRepository.save(order1Updated)).thenReturn(order1Updated);

        // Call the service method that uses the Repository
        OrderUpdateDTO result = orderService.approveOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
        OrderUpdateDTO expected = new OrderUpdateDTO(order1Updated, userDTO.getEmail());

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
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
            orderService.approveOrder(id, orderUpdateDTO, isEvent);
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
            orderService.approveOrder(orderUpdateDTO.getId(), orderUpdateDTO, isEvent);
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
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ApproveOrderFailDifferentOrderIDAndApprovedOrderIsEvent() {
        // Define the behavior of the mock
        int id = 2;
        orderUpdateDTO.setOrderStatus(OrderStatusEnum.APPROVED);
        isEvent = true;

        // Call the service method that uses the Repository
        BadPayloadException exception = assertThrows(BadPayloadException.class, () -> {
            orderService.rejectOrder(id, orderUpdateDTO, isEvent);
        });

        // Perform assertions
        assertEquals(BADPAYLOADEXCEPTIONMESSAGE, exception.getMessage());
    }

    @Test
    void test_ShipOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.APPROVED);
        order1Updated.setStatus(OrderStatusEnum.APPROVED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.SHIPPED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.shipOrder(userDTO, orderUpdateDTO.getId()));

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
    }

    @Test
    void test_DeliverOrder() throws NotFoundException {
        // Define the behavior of the mock
        order1.setStatus(OrderStatusEnum.SHIPPED);
        order1Updated.setStatus(OrderStatusEnum.SHIPPED);
        merchantOrderDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.SHIPPED);
        shippingOrderDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.DELIVERED);
        when(orderRepository.findById(order1.getId()).filter(o -> o.getUserId() == userDTO.getUserId())).thenReturn(Optional.ofNullable(order1));
        when(merchantOrderService.getUserMerchantOrder(userDTO, order1.getId())).thenReturn(merchantOrderDTO1);
        when(shippingOrderService.getUserShippingOrder(userDTO, order1.getId())).thenReturn(shippingOrderDTO1);

        // Call the service method that uses the Repository
        assertDoesNotThrow(() -> orderService.deliverOrder(userDTO, orderUpdateDTO.getId()));

        // Perform assertions
        verify(orderRepository, atLeastOnce()).findById(order1.getId());
        verify(merchantOrderService, atLeastOnce()).getUserMerchantOrder(userDTO, order1.getId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(userDTO, order1.getId());
    }

}
