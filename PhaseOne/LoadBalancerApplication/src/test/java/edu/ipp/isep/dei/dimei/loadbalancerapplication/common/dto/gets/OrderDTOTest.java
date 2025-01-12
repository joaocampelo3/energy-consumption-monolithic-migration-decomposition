package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;


import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    int customerId;
    String email;
    List<ItemQuantityDTO> orderItemsDTO = new ArrayList<>();
    double totalPrice;
    PaymentDTO paymentDTO;
    OrderDTO orderDTOExpected;

    @BeforeEach
    void beforeEach() {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        customerId = 1;
        email = "johndoe1234@gmail.com";
        double price1 = 12.0;
        double price2 = 5.0;

        ItemQuantityDTO itemQuantityDTO1 = ItemQuantityDTO.builder()
                .id(1)
                .itemId(1)
                .itemName("Item 1")
                .itemSku("ABC-12345-S-BL")
                .itemDescription("Item 1 description")
                .qty(3)
                .price(36.0)
                .build();

        orderItemsDTO.add(itemQuantityDTO1);

        ItemQuantityDTO itemQuantityDTO2 = ItemQuantityDTO.builder()
                .id(2)
                .itemId(2)
                .itemName("Item 2")
                .itemSku("ABC-12345-XS-BL")
                .itemDescription("Item 2 description")
                .qty(5)
                .price(25.0)
                .build();

        orderItemsDTO.add(itemQuantityDTO2);

        totalPrice = 61;
        paymentDTO = PaymentDTO.builder()
                .id(1)
                .amount(price1 + price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        orderDTOExpected = new OrderDTO(id, orderDate, orderStatus, customerId, orderItemsDTO, totalPrice, paymentDTO);
    }

    @Test
    void test_createOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(id, orderDate, orderStatus, customerId, orderItemsDTO, totalPrice, paymentDTO);

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(orderItemsDTO, orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());

        assertTrue(orderDTO.getTotalPrice() > 0);
        assertEquals(orderDTO.getTotalPrice(), orderItemsDTO.stream().mapToDouble(ItemQuantityDTO::getPrice).sum());
        assertTrue(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum() > 0);
        assertEquals(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum(), orderItemsDTO.stream().mapToInt(ItemQuantityDTO::getQty).sum());
        assertEquals(orderDTOExpected.hashCode(), orderDTO.hashCode());
    }

    @Test
    void test_createOrderDTOBuilder() {
        OrderDTO orderDTO = OrderDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .customerId(customerId)
                .orderItems(orderItemsDTO)
                .totalPrice(totalPrice)
                .paymentDTO(paymentDTO)
                .build();

        assertNotNull(orderDTO);
        assertEquals(id, orderDTO.getId());
        assertEquals(orderDate, orderDTO.getOrderDate());
        assertEquals(orderStatus, orderDTO.getOrderStatus());
        assertEquals(customerId, orderDTO.getCustomerId());
        assertEquals(orderItemsDTO, orderDTO.getOrderItems());
        assertEquals(paymentDTO, orderDTO.getPaymentDTO());

        assertTrue(orderDTO.getTotalPrice() > 0);
        assertEquals(orderDTO.getTotalPrice(), orderItemsDTO.stream().mapToDouble(ItemQuantityDTO::getPrice).sum());
        assertTrue(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum() > 0);
        assertEquals(orderDTO.getOrderItems().stream().mapToInt(ItemQuantityDTO::getQty).sum(), orderItemsDTO.stream().mapToInt(ItemQuantityDTO::getQty).sum());
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
        result.setOrderItems(orderItemsDTO);
        result.setTotalPrice(totalPrice);
        result.setPaymentDTO(paymentDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(orderItemsDTO, result.getOrderItems());
        assertEquals(totalPrice, result.getTotalPrice());
        assertEquals(paymentDTO, result.getPaymentDTO());
        assertEquals(orderDTOExpected.hashCode(), result.hashCode());
        assertEquals(orderDTOExpected, result);
        assertEquals(orderDTOExpected.toString(), result.toString());
    }
}
