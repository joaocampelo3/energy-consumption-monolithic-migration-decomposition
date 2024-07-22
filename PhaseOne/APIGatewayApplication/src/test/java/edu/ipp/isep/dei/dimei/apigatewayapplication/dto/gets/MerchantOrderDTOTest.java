package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.gets;


import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.MerchantOrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MerchantOrderDTOTest {

    final MerchantOrderStatusEnum merchantOrderStatus = MerchantOrderStatusEnum.PENDING;
    int id;
    Instant merchantOrderDate;
    int customerId;
    String email;
    int orderId;
    int merchantId;
    MerchantOrderDTO merchantOrderDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        merchantOrderDate = Instant.now();
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

    @Test
    void test_statusMerchantOrderDTOPending() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isPending());
    }

    @Test
    void test_statusMerchantOrderDTOPendingFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.APPROVED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isPending());
    }

    @Test
    void test_statusMerchantOrderDTOApproved() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.APPROVED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.APPROVED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isApproved());
    }

    @Test
    void test_statusMerchantOrderDTOApprovedFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isApproved());
    }

    @Test
    void test_statusMerchantOrderDTOCancelled() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.CANCELLED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.CANCELLED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isCancelled());
    }

    @Test
    void test_statusMerchantOrderDTOCancelledFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isCancelled());
    }

    @Test
    void test_statusMerchantOrderDTORejected() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.REJECTED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.REJECTED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isRejected());
    }

    @Test
    void test_statusMerchantOrderDTORejectedFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isRejected());
    }

    @Test
    void test_statusMerchantOrderDTOShipped() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.SHIPPED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.SHIPPED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isShipped());
    }

    @Test
    void test_statusMerchantOrderDTOShippedFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isShipped());
    }

    @Test
    void test_statusMerchantOrderDTODelivered() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.DELIVERED)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.DELIVERED, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertTrue(merchantDTO.isDelivered());
    }

    @Test
    void test_statusMerchantOrderDTODeliveredFail() {
        MerchantOrderDTO merchantDTO = MerchantOrderDTO.builder()
                .id(id)
                .merchantOrderDate(merchantOrderDate)
                .merchantOrderStatus(MerchantOrderStatusEnum.PENDING)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .merchantId(merchantId)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(merchantOrderDate, merchantDTO.getMerchantOrderDate());
        assertEquals(MerchantOrderStatusEnum.PENDING, merchantDTO.getMerchantOrderStatus());
        assertEquals(customerId, merchantDTO.getCustomerId());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(orderId, merchantDTO.getOrderId());
        assertEquals(merchantId, merchantDTO.getMerchantId());
        assertFalse(merchantDTO.isDelivered());
    }

    @Test
    void test_SetsMerchantOrderDTO() {
        MerchantOrderDTO result = MerchantOrderDTO.builder().build();

        result.setId(id);
        result.setMerchantOrderDate(merchantOrderDate);
        result.setMerchantOrderStatus(merchantOrderStatus);
        result.setCustomerId(customerId);
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
        assertEquals(merchantOrderDTOExpected.hashCode(), result.hashCode());
        assertEquals(merchantOrderDTOExpected, result);
        assertEquals(merchantOrderDTOExpected.toString(), result.toString());
    }
}
