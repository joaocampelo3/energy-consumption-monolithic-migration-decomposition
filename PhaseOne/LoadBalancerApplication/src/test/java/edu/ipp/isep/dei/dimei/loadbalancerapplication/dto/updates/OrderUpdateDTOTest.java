package edu.ipp.isep.dei.dimei.loadbalancerapplication.dto.updates;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class OrderUpdateDTOTest {

    final OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    int id;
    Instant orderDate;
    String email;
    UserDTO userDTO;
    OrderUpdateDTO orderUpdateDTOExpected;

    @BeforeEach
    void beforeEach() {
        Instant currentDate = Instant.now();
        id = 1;
        orderDate = currentDate;
        email = "johndoe1234@gmail.com";
        userDTO = new UserDTO(1, email, RoleEnum.USER);

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
        assertEquals(userDTO, orderUpdateDTO.getUserDTO());

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
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }
}

