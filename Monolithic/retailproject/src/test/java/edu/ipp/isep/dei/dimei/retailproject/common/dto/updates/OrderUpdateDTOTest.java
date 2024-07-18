package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.*;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class OrderUpdateDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    String email;
    Order order;
    OrderUpdateDTO orderUpdateDTOExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        email = "johndoe1234@gmail.com";
        double price1 = 12.0;
        double price2 = 5.0;

        Account account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        List<ItemQuantity> orderItems = new ArrayList<>();

        Item item1 = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(price1)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Item 2")
                .sku("ABC-12345-XS-BL")
                .description("Item 2 description")
                .price(price2)
                .build();

        ItemQuantity itemQuantity1 = new ItemQuantity(1, new OrderQuantity(3), item1, price1);
        ItemQuantity itemQuantity2 = new ItemQuantity(2, new OrderQuantity(5), item2, price2);

        orderItems.add(itemQuantity1);
        orderItems.add(itemQuantity2);

        Payment payment = Payment.builder()
                .id(1)
                .amount(price1+price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        order = Order.builder()
                .id(id)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(orderItems)
                .payment(payment)
                .build();

        orderUpdateDTOExpected = new OrderUpdateDTO(id, orderDate, orderStatus, email);
    }

    @Test
    void test_createOrderUpdateDTO() {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(id, orderDate, orderStatus, email);

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());

        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTOByOrder() {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(order);

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());

        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTOBuilder() {
        OrderUpdateDTO orderUpdateDTO = OrderUpdateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .email(email)
                .build();

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());

        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTONoArgsConstructor() {
        OrderUpdateDTO orderUpdateDTO = OrderUpdateDTO.builder().build();
        assertNotNull(orderUpdateDTO);
    }

    @Test
    void test_SetsOrderUpdateDTO() {
        OrderUpdateDTO expected = new OrderUpdateDTO(id, orderDate, orderStatus, email);
        OrderUpdateDTO result = OrderUpdateDTO.builder().build();

        result.setId(id);
        result.setOrderDate(orderDate);
        result.setOrderStatus(orderStatus);
        result.setEmail(email);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(email, result.getEmail());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }
}

