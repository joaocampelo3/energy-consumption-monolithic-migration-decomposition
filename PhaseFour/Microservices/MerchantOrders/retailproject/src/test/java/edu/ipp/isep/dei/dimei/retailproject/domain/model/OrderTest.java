package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderTest {
    final OrderStatusEnum status = OrderStatusEnum.PENDING;
    int id;
    double price;
    Instant orderDate;
    UserDTO userDTO;
    Merchant merchant;
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

        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(1)
                .name("Order 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO.getId())
                .build();

        orderExpected = Order.builder()
                .id(id)
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .build();

        orderExpected2 = Order.builder()
                .orderDate(orderDate)
                .status(status)
                .userId(userDTO.getUserId())
                .build();
    }

    @Test
    void test_createOrder() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId());

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_createOrder2() {
        Order order = new Order(orderDate, status, userDTO.getUserId());

        assertNotNull(order);
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
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
                .build();

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());

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

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(userDTO.getUserId(), order.getUserId());
        assertEquals(orderExpected.hashCode(), order.hashCode());
        assertEquals(orderExpected, order);
    }

    @Test
    void test_isPendingFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.APPROVED;
        Order order = new Order(id, orderDate, newStatus, userDTO.getUserId());

        assertFalse(order.isPending());
    }

    @Test
    void test_isApprovedFail() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId());

        assertFalse(order.isApproved());
    }

    @Test
    void test_isShippedFail() {
        Order order = new Order(id, orderDate, status, userDTO.getUserId());

        assertFalse(order.isShipped());
    }

    @Test
    void test_isPendingOrApprovedFail() {
        OrderStatusEnum newStatus = OrderStatusEnum.REJECTED;
        Order order = new Order(id, orderDate, newStatus, userDTO.getUserId());

        assertFalse(order.isPendingOrApproved());
    }
}
