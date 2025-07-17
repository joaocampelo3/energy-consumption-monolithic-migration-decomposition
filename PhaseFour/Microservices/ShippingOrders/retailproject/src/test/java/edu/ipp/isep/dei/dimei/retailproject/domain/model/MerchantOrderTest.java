package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MerchantOrderTest {
    final MerchantOrderStatusEnum status = MerchantOrderStatusEnum.PENDING;
    int id;
    double price;
    Instant orderDate;
    UserDTO userDTO;
    Order order;
    MerchantOrder merchantOrderExpected;
    int merchantId = 1;
    int orderId = 1;
    Instant currentDate;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        currentDate = Instant.now();
        orderDate = currentDate;
        price = 12.0;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        order = Order.builder()
                .id(orderId)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .build();

        merchantOrderExpected = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();
    }

    @Test
    void test_createMerchantOrder() {
        MerchantOrder merchantOrder = new MerchantOrder(id, orderDate, status, userDTO.getUserId(), orderId, merchantId);

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_createMerchantOrder2() {
        MerchantOrder merchantOrder = new MerchantOrder(userDTO.getUserId(), orderId, order.getOrderDate(), merchantId);

        assertNotNull(merchantOrder);
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(currentDate, merchantOrder.getOrderDate());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        merchantOrderExpected.setId(0);
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_createMerchantOrderBuilder() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_MerchantOrderSets() {
        MerchantOrder merchantOrder = MerchantOrder.builder().build();

        merchantOrder.setId(id);
        merchantOrder.setOrderDate(orderDate);
        merchantOrder.setStatus(status);
        merchantOrder.setUserId(userDTO.getUserId());
        merchantOrder.setOrderId(orderId);
        merchantOrder.setMerchantId(merchantId);

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(status, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
    }

    @Test
    void test_statusMerchantOrderPending() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertEquals(merchantOrderExpected.hashCode(), merchantOrder.hashCode());
        assertEquals(merchantOrderExpected, merchantOrder);
        assertTrue(merchantOrder.isPending());
    }

    @Test
    void test_statusMerchantOrderPendingFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.APPROVED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isPending());
    }

    @Test
    void test_statusMerchantOrderApproved() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.APPROVED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertTrue(merchantOrder.isApproved());
    }

    @Test
    void test_statusMerchantOrderApprovedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isApproved());
    }

    @Test
    void test_statusMerchantOrderRejected() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.REJECTED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.REJECTED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertTrue(merchantOrder.isRejected());
    }

    @Test
    void test_statusMerchantOrderRejectedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isRejected());
    }

    @Test
    void test_statusMerchantOrderCancelled() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.CANCELLED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.CANCELLED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertTrue(merchantOrder.isCancelled());
    }

    @Test
    void test_statusMerchantOrderCancelledFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isCancelled());
    }

    @Test
    void test_statusMerchantOrderShipped() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.SHIPPED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.SHIPPED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertTrue(merchantOrder.isShipped());
    }

    @Test
    void test_statusMerchantOrderShippedFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isShipped());
    }

    @Test
    void test_statusMerchantOrderDelivered() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.DELIVERED)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.DELIVERED, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertTrue(merchantOrder.isDelivered());
    }

    @Test
    void test_statusMerchantOrderDeliveredFail() {
        MerchantOrder merchantOrder = MerchantOrder.builder()
                .id(id)
                .orderDate(orderDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantOrder);
        assertEquals(id, merchantOrder.getId());
        assertEquals(orderDate, merchantOrder.getOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantOrder.getStatus());
        assertEquals(userDTO.getUserId(), merchantOrder.getUserId());
        assertEquals(orderId, merchantOrder.getOrderId());
        assertEquals(merchantId, merchantOrder.getMerchantId());
        assertFalse(merchantOrder.isDelivered());
    }
}
