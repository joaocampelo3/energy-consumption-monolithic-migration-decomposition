package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.updates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.MerchantOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MerchantOrderUpdateDTOTest {
    final MerchantOrderStatusEnum merchantOrderStatus = MerchantOrderStatusEnum.PENDING;
    int id;
    Instant merchantOrderDate;
    String email;
    String userEmail;
    int orderId;
    int merchantId;
    MerchantOrderUpdateDTO merchantOrderUpdateDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        merchantOrderDate = Instant.now();
        email = "merchantnumber1@gmail.com";
        userEmail = "johndoe1234@gmail.com";
        orderId = 1;
        merchantId = 1;
        merchantOrderUpdateDTOExpected = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId);
    }

    @Test
    void test_createMerchantOrderUpdateDTO() {
        MerchantOrderUpdateDTO merchantDTO = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId);

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
    void test_createMerchantOrderUpdateDTONoArgsConstructor() {
        MerchantOrderUpdateDTO merchantDTO = MerchantOrderUpdateDTO.builder().build();
        assertNotNull(merchantDTO);
    }

    @Test
    void test_SetsMerchantOrderUpdateDTO() {
        MerchantOrderUpdateDTO expected = new MerchantOrderUpdateDTO(id, merchantOrderDate, merchantOrderStatus, email, orderId, merchantId);
        MerchantOrderUpdateDTO result = MerchantOrderUpdateDTO.builder().build();

        result.setId(id);
        result.setMerchantOrderDate(merchantOrderDate);
        result.setMerchantOrderStatus(merchantOrderStatus);
        result.setEmail(email);
        result.setOrderId(orderId);
        result.setMerchantId(merchantId);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(merchantOrderDate, result.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, result.getMerchantOrderStatus());
        assertEquals(email, result.getEmail());
        assertEquals(orderId, result.getOrderId());
        assertEquals(merchantId, result.getMerchantId());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }
}