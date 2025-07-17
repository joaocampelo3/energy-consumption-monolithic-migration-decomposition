package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MerchantOrderHelperServiceTests {
    static final String exceptionBadPayload = "Wrong merchant order payload.";
    static final String exceptionNotFound = "Merchant Order not found.";
    final Instant currentDateTime = Instant.now();
    @InjectMocks
    MerchantOrderHelperService merchantOrderHelperService;
    @Mock
    MerchantOrderRepository merchantOrderRepository;

    MerchantOrderDTO merchantOrderDTO1;
    MerchantOrderUpdateDTO merchantOrderUpdateDTO1;
    AddressDTO addressDTO;
    Order order1;
    OrderUpdateDTO orderUpdateDTO1;
    MerchantOrder newMerchantOrder1;
    MerchantOrder merchantOrder1;
    MerchantOrder merchantOrder1Updated;
    UserDTO userDTO;
    UserDTO merchantUserDTO;
    ShippingOrder shippingOrder1;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO1;
    int merchantId = 1;
    boolean isEvent;

    @BeforeEach
    void beforeEach() {
        userDTO = UserDTO.builder().userId(1).email("johndoe1234@gmail.com").role(RoleEnum.USER).build();

        merchantUserDTO = UserDTO.builder().userId(2).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        addressDTO = AddressDTO.builder().id(1).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        order1 = Order.builder().id(1).orderDate(currentDateTime).status(OrderStatusEnum.PENDING).userId(userDTO.getUserId()).build();

        orderUpdateDTO1 = new OrderUpdateDTO(order1, userDTO.getEmail());

        newMerchantOrder1 = MerchantOrder.builder().id(0).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrder1 = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrder1Updated = MerchantOrder.builder().id(1).orderDate(currentDateTime).status(MerchantOrderStatusEnum.PENDING).userId(merchantUserDTO.getUserId()).orderId(order1.getId()).merchantId(merchantId).build();

        merchantOrderDTO1 = MerchantOrderDTO.builder().id(1).merchantOrderDate(currentDateTime).merchantOrderStatus(MerchantOrderStatusEnum.PENDING).customerId(merchantOrder1.getUserId()).email(merchantUserDTO.getEmail()).orderId(order1.getId()).merchantId(merchantOrder1.getMerchantId()).build();

        merchantOrderUpdateDTO1 = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO1.getId()).merchantOrderDate(merchantOrderDTO1.getMerchantOrderDate()).merchantOrderStatus(merchantOrderDTO1.getMerchantOrderStatus()).email(merchantOrderDTO1.getEmail()).orderId(merchantOrderDTO1.getOrderId()).merchantId(merchantOrderDTO1.getMerchantId()).build();

        shippingOrder1 = ShippingOrder.builder().id(1).shippingOrderDate(currentDateTime).status(ShippingOrderStatusEnum.PENDING).shippingAddressId(addressDTO.getId()).orderId(order1.getId()).merchantOrderId(merchantOrder1.getOrderId()).userId(userDTO.getUserId()).build();

        shippingOrderUpdateDTO1 = new ShippingOrderUpdateDTO(shippingOrder1);

        isEvent = false;
    }

    @Test
    void test_GetUserMerchantOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        when(merchantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(merchantOrder1));

        // Call the service method that uses the Repository
        MerchantOrderDTO result = merchantOrderHelperService.getUserMerchantOrder(merchantUserDTO, id);
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
        MerchantOrder result = merchantOrderHelperService.createMerchantOrder(merchantUserDTO, order1, id);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).save(newMerchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderByOrder() throws NotFoundException {
        // Define the behavior of the mock
        int id = merchantOrder1.getId();
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);
        merchantOrder1Updated.setStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.findById(id).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderHelperService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_FullCancelMerchantOrderByOrder2() throws NotFoundException {
        // Define the behavior of the mock
        merchantOrder1.setStatus(MerchantOrderStatusEnum.CANCELLED);
        order1.setStatus(OrderStatusEnum.CANCELLED);
        merchantOrderUpdateDTO1.setMerchantOrderStatus(MerchantOrderStatusEnum.CANCELLED);

        when(merchantOrderRepository.findByOrderId(order1.getId()).filter(o -> o.getMerchantId() == merchantUserDTO.getUserId())).thenReturn(Optional.ofNullable(merchantOrder1));


        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderHelperService.fullCancelMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
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
        MerchantOrder result = merchantOrderHelperService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
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
            merchantOrderHelperService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
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
        MerchantOrder result = merchantOrderHelperService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
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
            merchantOrderHelperService.getUserMerchantOrderByOrder(merchantUserDTO, order1.getId());
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
        MerchantOrder result = merchantOrderHelperService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
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
            merchantOrderHelperService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
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
        MerchantOrder result = merchantOrderHelperService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
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
            merchantOrderHelperService.getUserMerchantOrderById(merchantUserDTO, order1.getId(), isEvent);
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
        when(merchantOrderRepository.save(merchantOrder1Updated)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrderUpdateDTO result = merchantOrderHelperService.fullCancelMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1, isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
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
        MerchantOrderUpdateDTO result = merchantOrderHelperService.rejectMerchantOrderByOrder(merchantUserDTO, order1.getId(), isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
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
        MerchantOrderUpdateDTO result = merchantOrderHelperService.rejectMerchantOrderByShippingOrder(merchantUserDTO, shippingOrder1, isEvent);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findByOrderId(order1.getId());
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
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
        MerchantOrderUpdateDTO result = merchantOrderHelperService.approveMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
        merchantOrderUpdateDTO1.setUserDTO(null);
        MerchantOrderUpdateDTO expected = merchantOrderUpdateDTO1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
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
            merchantOrderHelperService.approveMerchantOrder(id, merchantOrderUpdateDTO1, isEvent);
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
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderHelperService.shipMerchantOrder(merchantUserDTO, id, isEvent);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
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
        when(merchantOrderRepository.save(merchantOrder1)).thenReturn(merchantOrder1Updated);

        // Call the service method that uses the Repository
        MerchantOrder result = merchantOrderHelperService.deliverMerchantOrder(merchantUserDTO, id, isEvent);
        MerchantOrder expected = merchantOrder1;

        // Perform assertions
        verify(merchantOrderRepository, atLeastOnce()).findById(id);
        verify(merchantOrderRepository, atLeastOnce()).save(merchantOrder1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteMerchantOrderByOrderId() {
        // Call the service method that uses the Repository
        merchantOrderHelperService.deleteMerchantOrderByOrderId(order1.getId(), isEvent);

        verify(merchantOrderRepository, times(1)).deleteByOrderId(order1.getId());
    }

}
