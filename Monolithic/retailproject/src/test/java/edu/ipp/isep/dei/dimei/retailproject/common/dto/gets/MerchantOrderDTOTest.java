package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;


import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MerchantOrderDTOTest {

    int id;
    LocalDateTime merchantOrderDate;
    MerchantOrderStatusEnum merchantOrderStatus = MerchantOrderStatusEnum.PENDING;
    int customerId;
    String email;
    int orderId;
    int merchantId;
    MerchantOrderDTO merchantOrderDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        merchantOrderDate = LocalDateTime.now();
        customerId = 1;
        email = "merchantnumber1@gmail.com";
        orderId = 1;
        merchantId = 1;
        merchantOrderDTOExpected = new MerchantOrderDTO(id, merchantOrderDate, merchantOrderStatus, customerId, email, orderId, merchantId);
    }

    @Test
    void test_createMerchantOrderDTO() {
        MerchantOrderDTO merchantDTO = new MerchantOrderDTO(id, merchantOrderDate, merchantOrderStatus, customerId, email, orderId, merchantId);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderDTOExpected.hashCode(), merchantOrderDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderDTOBuilder() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(merchantOrderStatus)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(merchantOrderStatus, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertEquals(merchantOrderDTOExpected.hashCode(), merchantOrderDTOExpected.hashCode());
    }

    @Test
    void test_createMerchantOrderDTONoArgsConstructor() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder().build();
        assertNotNull(merchantDTO);
    }
}
