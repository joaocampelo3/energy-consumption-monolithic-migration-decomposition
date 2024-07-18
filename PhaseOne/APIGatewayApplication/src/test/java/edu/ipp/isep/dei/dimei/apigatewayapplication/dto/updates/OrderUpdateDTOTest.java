package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.updates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OrderUpdateDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    String email;
    OrderUpdateDTO orderUpdateDTOExpected;

    @BeforeEach
    void beforeEach() {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        email = "johndoe1234@gmail.com";

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

