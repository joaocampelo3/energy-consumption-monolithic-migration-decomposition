package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    final OrderStatusEnum status = OrderStatusEnum.PENDING;
    int id;
    double price;
    Instant orderDate;
    UserDTO userDTO;
    List<ItemQuantity> itemQuantities = new ArrayList<>();
    Payment payment;
    Order orderExpected;
    Order orderExpected2;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        Instant currentDate = Instant.now();
        orderDate = currentDate;
        price = 12.0;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
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
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        orderExpected = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();

        orderExpected2 = Order.builder()
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();
    }

    @Test
    void test_createOrder() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId(), itemQuantities, payment);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_createOrder2() {
        Order order = new Order(orderDate, status, userDTO.getUserId(), itemQuantities, payment);

        assertNotNull(order);
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected2, order);
        assertEquals(orderExpected2.hashCode(), order.hashCode());
    }

    @Test
    void test_createOrderBuilder() {
        Order order = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_OrderSets() {
        Order order = Order.builder().build();

        order.setId(id);
        order.setOrderDate(orderDate);
        order.setStatus(status);
        order.setUserId(userDTO.getUserId());
        order.setItemQuantities(itemQuantities);
        order.setPayment(payment);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(itemQuantities, order.getItemQuantities());
        assertEquals(payment, order.getPayment());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_isPendingFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.APPROVED;
        Order order = new Order(id, orderDate, newStatus, userDTO.getUserId(), itemQuantities, payment);

        assertFalse(order.isPending());
    }

    @Test
    void test_isApprovedFail() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId(), itemQuantities, payment);

        assertFalse(order.isApproved());
    }

    @Test
    void test_isShippedFail() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId(), itemQuantities, payment);

        assertFalse(order.isShipped());
    }

    @Test
    void test_isPendingOrApprovedFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.REJECTED;
        Order order = new Order(id, orderDate, newStatus, userDTO.getUserId(), itemQuantities, payment);

        assertFalse(order.isPendingOrApproved());
    }
}
