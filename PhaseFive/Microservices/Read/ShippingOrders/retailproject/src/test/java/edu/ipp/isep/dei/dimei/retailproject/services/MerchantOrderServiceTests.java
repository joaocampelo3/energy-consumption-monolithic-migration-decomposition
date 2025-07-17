package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MerchantOrderServiceTests {
    static final String exceptionBadPayload = "Wrong merchant order payload.";
    static final String exceptionNotFound = "Merchant Order not found.";
    final Instant currentDateTime = Instant.now();
    @InjectMocks
    MerchantOrderService merchantOrderService;
    @Mock
    MerchantOrderRepository merchantOrderRepository;
    @Mock
    OrderService orderService;
    @Mock
    ShippingOrderService shippingOrderService;

    MerchantOrderDTO merchantOrderDTO1;
    MerchantOrderDTO merchantOrderDTO2;
    List<MerchantOrderDTO> merchantOrderDTOS = new ArrayList<>();
    MerchantOrderUpdateDTO merchantOrderUpdateDTO1;
    AddressDTO addressDTO;
    AddressDTO merchantAddressDTO;
    Order order1;
    Order order2;
    OrderUpdateDTO orderUpdateDTO1;
    MerchantOrder newMerchantOrder1;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder2;
    MerchantOrder merchantOrder1Updated;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
    List<MerchantOrder> merchantOrders = new ArrayList<>();
    ShippingOrder shippingOrder1;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO1;
    int merchantId = 1;
    boolean isEvent;

    @BeforeEach
    void beforeEach() {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();

        merchantUserDTO = UserDTO.builder().userId(2).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        addressDTO = AddressDTO.builder().id(1).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        merchantAddressDTO = AddressDTO.builder().id(2).street("Different Avenue").zipCode("10128").city("New York").country("USA").build();

        order1 = Order.builder().id(1).orderDate(currentDateTime).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        order2 = Order.builder().id(2).orderDate(currentDateTime).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        orderUpdateDTO1 = new OrderUpdateDTO(order1, userDTO.getEmail());

        newMerchantOrder1 = MerchantOrder.builder().id(0).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrder1 = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrder2 = MerchantOrder.builder().id(2).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order2.getId()).merchantId(merchantId).build();

        merchantOrder1Updated = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrders.add(merchantOrder1);
        merchantOrders.add(merchantOrder2);

        merchantOrderDTO1 = MerchantOrderDTO.builder().id(1).merchantOrderDate(currentDateTime).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).customerId(merchantOrder1.getUserId()).email(merchantUserDTO.getEmail()).orderId(order1.getId()).merchantId(merchantOrder1.getMerchantId()).build();

        merchantOrderDTO2 = MerchantOrderDTO.builder().id(2).merchantOrderDate(currentDateTime).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).customerId(merchantOrder2.getUserId()).email(merchantUserDTO.getEmail()).orderId(order2.getId()).merchantId(merchantOrder2.getMerchantId()).build();

        merchantOrderDTOS.add(merchantOrderDTO1);
        merchantOrderDTOS.add(merchantOrderDTO2);

        merchantOrderUpdateDTO1 = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO1.getId()).merchantOrderDate(merchantOrderDTO1.getMerchantOrderDate()).merchantOrderStatus(merchantOrderDTO1.getMerchantOrderStatus()).email(merchantOrderDTO1.getEmail()).orderId(merchantOrderDTO1.getOrderId()).merchantId(merchantOrderDTO1.getMerchantId()).build();

        shippingOrder1 = ShippingOrder.builder().id(1).shippingOrderDate(currentDateTime).status(ShippingOrderStatusEnum.PENDING).shippingAddressId(addressDTO.getId()).orderId(order1.getId()).merchantOrderId(merchantOrder1.getOrderId()).userId(userDTO.getUserId()).build();

        shippingOrderUpdateDTO1 = new ShippingOrderUpdateDTO(shippingOrder1);

        isEvent = false;
    }

    @Test
    void test_GetAllMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderRepository.findAll()).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getAllMerchantOrders(merchantUserDTO);
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrders() {
        // Define the behavior of the mock
        when(merchantOrderRepository.findByMerchantId(merchantUserDTO.getUserId())).thenReturn(merchantOrders);

        // Call the service method that uses the Repository
        List<MerchantOrderDTO> result = merchantOrderService.getUserMerchantOrders(merchantUserDTO);
        List<MerchantOrderDTO> expected = merchantOrderDTOS;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByMerchantId(merchantUserDTO.getUserId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserMerchantOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrderDTO result = merchantOrderService.getUserMerchantOrder(merchantUserDTO, id);
        MerchantOrderDTO expected = merchantOrderDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateMerchantOrder() {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(merchantOrderRepository.save(newMerchantOrder1)).thenReturn(merchantOrder1);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.createMerchantOrder(merchantUserDTO, order1, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).save(newMerchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrder() throws InvalidQuantityException, NotFoundException, WrongFlowException, BadPayloadException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantUserDTO.getEmail()));
        when(orderService.fullCancelOrderByOrderId(merchantUserDTO, merchantOrder1.getOrderId(), isEvent)).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.fullCancelShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated, isEvent)).thenReturn(shippingOrderUpdateDTO1);
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(orderService, atLeastOnce()).fullCancelOrderByOrderId(merchantUserDTO, merchantOrder1.getOrderId(), isEvent);
        verify(shippingOrderService, atLeastOnce()).fullCancelShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated, isEvent);
        verify(merchantOrderRepository, atLeastOnce()).findById(merchantOrder1.getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.fullCancelMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantUserDTO.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderByOrder2() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByAdminFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId())).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByOrderByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByOrderByUserFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }


    @Test
    void test_getUserMerchantOrderByIdByAdmin() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByAdminFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.ADMIN);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId())).thenReturn(Optional.empty());


        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_getUserMerchantOrderByIdByUser() throws NotFoundException {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_getUserMerchantOrderByIdByUserFail() {
        // Define the behavior of the mock
        merchantUserDTO.setRole(RoleEnum.USER);
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findById(order1.getId()).filter(o -> o.getUserId() == merchantUserDTO.getUserId())).thenReturn(Optional.empty());

        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            merchantOrderService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
        });

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(order1.getId());
        assertNotNull(result);
        assertEquals(exceptionNotFound, result.getMessage());
    }

    @Test
    void test_FullCancelMerchantOrderByByShippingOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = shippingOrder1.getMerchantOrderId();
        shippingOrder1.setStatus(ShippingOrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(shippingOrder1.getOrderId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.fullCancelMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1, isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrder() throws NotFoundException, WrongFlowException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, userDTO.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(orderService.rejectOrderByOrderId(merchantUserDTO, merchantOrder1Updated.getOrderId(), isEvent)).thenReturn(orderUpdateDTO1);
        when(shippingOrderService.rejectShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated)).thenReturn(shippingOrderUpdateDTO1);
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        verify(orderService, atLeastOnce()).rejectOrderByOrderId(merchantUserDTO, merchantOrder1.getOrderId(), isEvent);
        verify(shippingOrderService, atLeastOnce()).rejectShippingOrderByMerchantOrder(merchantUserDTO, merchantOrder1Updated);
        verify(merchantOrderRepository, atLeastOnce()).findById(merchantOrder1.getId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.rejectMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
    }

    @Test
    void test_RejectMerchantOrderByOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        order1.setStatus(OrderStatusEnum.REJECTED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderRepository.findByOrderId(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_RejectMerchantOrderByShippingOrder() throws WrongFlowException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        shippingOrder1.setStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.REJECTED);
        orderUpdateDTO1.setOrderStatus(OrderStatusEnum.REJECTED);
        shippingOrderUpdateDTO1.setShippingOrderStatus(ShippingOrderStatusEnum.REJECTED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.REJECTED);

        when(merchantOrderRepository.findByOrderId(order1.getId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.rejectMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1, isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveMerchantOrder() throws WrongFlowException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrderUpdateDTO1.setUserDTO(merchantUserDTO);

        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);
        when(merchantOrderRepository.findById(merchantOrder1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenAnswer(new Answer<Optional<MerchantOrder>>() {
            private int count = 0;

            @Override
            public Optional<MerchantOrder> answer(InvocationOnMock invocationOnMock) {
                if (count++ < 3) {
                    return Optional.ofNullable(merchantOrder1);
                }
                return Optional.ofNullable(merchantOrder1Updated);
            }
        });

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_ApproveMerchantOrderFail() {
        // Define the behavior of the mock
        int id = 2;

        // Call the service method that uses the Repository
        BadPayloadException result = assertThrows(BadPayloadException.class, () -> {
            merchantOrderService.approveMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        });

        // Perform assertions
        assertNotNull(result);
        assertEquals(exceptionBadPayload, result.getMessage());
    }

    @Test
    void test_ShippedMerchantOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrder1.setStatus(MerchantOrderStatusEnum.APPROVED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.SHIPPED);
        order1.setStatus(OrderStatusEnum.APPROVED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.SHIPPED);

        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.shipMerchantOrder(merchantUserDTO, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeliveredMerchantOrder() throws NotFoundException, WrongFlowException {
        // Define the behavior of the mock
        int id = 1;
        merchantOrder1.setStatus(MerchantOrderStatusEnum.SHIPPED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.DELIVERED);
        order1.setStatus(OrderStatusEnum.SHIPPED);
        shippingOrder1.setStatus(ShippingOrderStatusEnum.DELIVERED);

        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));
        when(orderService.getUserOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new OrderDTO(order1));
        when(shippingOrderService.getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId())).thenReturn(new ShippingOrderDTO(shippingOrder1, merchantOrderDTO1.getEmail()));
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderService.deliverMerchantOrder(merchantUserDTO, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(orderService, atLeastOnce()).getUserOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(shippingOrderService, atLeastOnce()).getUserShippingOrder(merchantUserDTO, merchantOrder1.getOrderId());
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteMerchantOrderByOrderId() {
        // Call the service method that uses the Repository
        merchantOrderService.deleteMerchantOrderByOrderId(order1.getId(), isEvent);

        verify(merchantOrderRepository, times(1)).deleteByOrderId(order1.getId());
    }

}
