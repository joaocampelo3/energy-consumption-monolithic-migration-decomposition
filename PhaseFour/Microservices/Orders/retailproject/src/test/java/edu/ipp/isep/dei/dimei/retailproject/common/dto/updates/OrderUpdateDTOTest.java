package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderUpdateDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    String email;
    Order order;
    OrderUpdateDTO orderUpdateDTOExpected;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        email = "johndoe1234@gmail.com";
        double price1 = 12.0;
        double price2 = 5.0;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
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
                .amount(price1 + price2)
                .paymentDateTime(currentDate)
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.PENDING)
                .build();

        order = Order.builder()
                .id(id)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(orderItems)
                .payment(payment)
                .build();

        orderUpdateDTOExpected = new OrderUpdateDTO(id, orderDate, orderStatus, email, userDTO);
    }

    @Test
    void test_createOrderUpdateDTO() {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(id, orderDate, orderStatus, email, userDTO);

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());

        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTOByOrder() {
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(order, userDTO.getEmail());
        orderUpdateDTOExpected.setUserDTO(null);

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());
        assertEquals(orderUpdateDTOExpected, orderUpdateDTO);
        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTOBuilder() {
        OrderUpdateDTO orderUpdateDTO = OrderUpdateDTO.builder()
                .id(id)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .email(email)
                .userDTO(userDTO)
                .build();

        assertNotNull(orderUpdateDTO);
        assertEquals(id, orderUpdateDTO.getId());
        assertEquals(orderDate, orderUpdateDTO.getOrderDate());
        assertEquals(orderStatus, orderUpdateDTO.getOrderStatus());
        assertEquals(email, orderUpdateDTO.getEmail());
        assertEquals(userDTO, orderUpdateDTO.getUserDTO());
        assertEquals(orderUpdateDTOExpected, orderUpdateDTO);
        assertEquals(orderUpdateDTOExpected.hashCode(), orderUpdateDTO.hashCode());
    }

    @Test
    void test_createOrderUpdateDTONoArgsConstructor() {
        OrderUpdateDTO orderUpdateDTO = OrderUpdateDTO.builder().build();
        assertNotNull(orderUpdateDTO);
    }

    @Test
    void test_SetsOrderUpdateDTO() {
        OrderUpdateDTO expected = new OrderUpdateDTO(id, orderDate, orderStatus, email, userDTO);
        OrderUpdateDTO result = OrderUpdateDTO.builder().build();

        result.setId(id);
        result.setOrderDate(orderDate);
        result.setOrderStatus(orderStatus);
        result.setEmail(email);
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(orderStatus, result.getOrderStatus());
        assertEquals(email, result.getEmail());
        assertEquals(expected, result);
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected.toString(), result.toString());
    }
}

