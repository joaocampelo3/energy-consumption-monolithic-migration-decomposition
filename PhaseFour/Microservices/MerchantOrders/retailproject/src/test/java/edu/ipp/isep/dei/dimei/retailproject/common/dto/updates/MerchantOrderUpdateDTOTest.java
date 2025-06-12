package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MerchantOrderUpdateDTOTest {
    final MerchantOrderStatusEnum merchantOrderStatus = MerchantOrderStatusEnum.PENDING;
    int id;
    Instant merchantOrderDate;
    String email;
    String userEmail;
    int orderId;
    int merchantId;
    MerchantOrderUpdateDTO merchantOrderUpdateDTOExpected;
    MerchantOrder merchantOrder;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        Instant currentDate = Instant.now();
        merchantOrderDate = currentDate;
        email = "merchantnumber1@gmail.com";
        userEmail = "johndoe1234@gmail.com";
        orderId = 1;
        merchantId = 1;
        merchantOrderUpdateDTOExpected = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId, userDTO);

        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("Other Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        Order order = Order.builder()
                .id(1)
                .orderDate(currentDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .build();

        Merchant merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email(email)
                .addressId(addressDTO.getId())
                .build();

        merchantOrder = MerchantOrder.builder()
                .id(1)
                .orderDate(currentDate)
                .status(MerchantOrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .orderId(order.getId())
                .merchant(merchant)
                .build();
    }

    @Test
    void test_createMerchantOrderUpdateDTO() {
        MerchantOrderUpdateDTO merchantDTO = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId, userDTO);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderUpdateDTOBuilder() {
        MerchantOrderUpdateDTO merchantDTO = MerchantOrderUpdateDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(merchantOrderStatus)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }


    @Test
    void test_createMerchantOrderUpdateDTOByMerchantOrder() {
        MerchantOrderUpdateDTO merchantDTO = new MerchantOrderUpdateDTO(merchantOrder, userDTO.getEmail());

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(userEmail, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderUpdateDTOExpected.hashCode(), merchantOrderUpdateDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderUpdateDTONoArgsConstructor() {
        MerchantOrderUpdateDTO merchantDTO = MerchantOrderUpdateDTO.builder().build();
        assertNotNull(merchantDTO);
    }

    @Test
    void test_SetsMerchantOrderUpdateDTO() {
        MerchantOrderUpdateDTO expected = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId, userDTO);
        MerchantOrderUpdateDTO result = MerchantOrderUpdateDTO.builder().build();

        result.setId(id);
        result.setMerchantOrderDate(merchantOrderDate);
        result.setMerchantOrderStatus(merchantOrderStatus);
        result.setEmail(email);
        result.setOrderId(orderId);
        result.setMerchantId(merchantId);
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(merchantOrderDate, result.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, result.getMerchantOrderStatus());
        assertEquals(email, result.getEmail());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantId, result.getMerchantId());
        assertEquals(expected, result);
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected.toString(), result.toString());
    }
}
