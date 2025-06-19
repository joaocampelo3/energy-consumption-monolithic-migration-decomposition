package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;


import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    int customerId;
    Order order;
    OrderDTO orderDTOExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        customerId = 1;
        int userId = 1;

        order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userId)
                .build();

        orderDTOExpected = new OrderDTO(id, orderDate, orderStatus, customerId, null, 0.0, null);
    }

    @Test
    void test_createOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(id, orderDate, orderStatus, customerId, null, 0.0, null);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTOByOrder() {
        OrderDTO orderDTO = new OrderDTO(order);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTOByOrder2() {
        OrderDTO orderDTO = new OrderDTO(order);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertNull(orderDTO.getOrderItems());
        assertNull(orderDTO.getPaymentDTO());
        assertEquals(0, orderDTO.getTotalPrice());
    }

    @Test
    void test_createOrderDTOBuilder() {
        OrderDTO orderDTO = OrderDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .customerId(customerId)
                .build();

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTONoArgsConstructor() {
        OrderDTO orderDTO = OrderDTO.builder().build();
        assertNotNull(orderDTO);
    }

    @Test
    void test_isPendingOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.PENDING);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isPending());
    }

    @Test
    void test_isPendingOrderDTOFail() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isPending());
    }

    @Test
    void test_isApprovedOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.APPROVED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isApproved());
    }

    @Test
    void test_isApprovedOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isApproved());
    }

    @Test
    void test_isRejectedOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.REJECTED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isRejected());
    }

    @Test
    void test_isRejectedOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isRejected());
    }

    @Test
    void test_isCancelledOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.CANCELLED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isCancelled());
    }

    @Test
    void test_isCancelledOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isCancelled());
    }

    @Test
    void test_isShippedOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.SHIPPED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isShipped());
    }

    @Test
    void test_isShippedOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isShipped());
    }

    @Test
    void test_isDeliveredOrderDTO() {
        orderDTOExpected.setOrderStatus(OrderStatusEnum.DELIVERED);
        assertNotNull(orderDTOExpected);
        assertTrue(orderDTOExpected.isDelivered());
    }

    @Test
    void test_isDeliveredOrderDTOFail() {
        assertNotNull(orderDTOExpected);
        assertFalse(orderDTOExpected.isDelivered());
    }

    @Test
    void test_SetsOrderDTO() {
        OrderDTO result = OrderDTO.builder().build();

        result.setId(id);
        result.setOrderDate(orderDate);
        result.setOrderStatus(orderStatus);
        result.setCustomerId(customerId);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(orderDTOExpected.hashCode(), result.hashCode());
        assertEquals(orderDTOExpected, result);
        assertEquals(orderDTOExpected.toString(), result.toString());
    }
}
