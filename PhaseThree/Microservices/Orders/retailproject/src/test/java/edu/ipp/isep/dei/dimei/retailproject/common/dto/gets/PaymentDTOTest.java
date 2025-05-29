package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PaymentDTOTest {
    int id;
    double amount;
    Instant paymentDateTime;
    PaymentMethodEnum paymentMethod;
    PaymentStatusEnum status;
    PaymentDTO paymentDTOExpected;
    Payment paymentExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        amount = 0;
        paymentDateTime = Instant.now();
        paymentMethod = PaymentMethodEnum.CARD;
        status = PaymentStatusEnum.PENDING;
        paymentDTOExpected = new PaymentDTO(id, amount, paymentDateTime, paymentMethod, status);
        paymentExpected = Payment.builder()
                .id(id)
                .amount(amount)
                .paymentDateTime(paymentDateTime)
                .paymentMethod(paymentMethod)
                .status(status)
                .build();
    }

    @Test
    void test_createPaymentDTO() {
        PaymentDTO paymentDTO = new PaymentDTO(id, amount, paymentDateTime, paymentMethod, status);

        assertNotNull(paymentDTO);
        assertEquals(id, paymentDTO.getId());
        assertEquals(amount, paymentDTO.getAmount());
        assertEquals(paymentDateTime, paymentDTO.getPaymentDateTime());
        assertEquals(paymentMethod, paymentDTO.getPaymentMethod());
        assertEquals(status, paymentDTO.getStatus());
        assertEquals(paymentDTOExpected.hashCode(), paymentDTO.hashCode());
    }

    @Test
    void test_createPaymentDTO2() {
        PaymentDTO paymentDTO = new PaymentDTO(amount, paymentDateTime, paymentMethod, status);

        assertNotNull(paymentDTO);
        assertEquals(amount, paymentDTO.getAmount());
        assertEquals(paymentDateTime, paymentDTO.getPaymentDateTime());
        assertEquals(paymentMethod, paymentDTO.getPaymentMethod());
        assertEquals(status, paymentDTO.getStatus());
        paymentDTOExpected.setId(0);
        assertEquals(paymentDTOExpected.hashCode(), paymentDTO.hashCode());
    }

    @Test
    void test_createPaymentDTOBuilder() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .id(id)
                .amount(amount)
                .paymentDateTime(paymentDateTime)
                .paymentMethod(paymentMethod)
                .status(status)
                .build();

        assertNotNull(paymentDTO);
        assertEquals(id, paymentDTO.getId());
        assertEquals(amount, paymentDTO.getAmount());
        assertEquals(paymentDateTime, paymentDTO.getPaymentDateTime());
        assertEquals(paymentMethod, paymentDTO.getPaymentMethod());
        assertEquals(status, paymentDTO.getStatus());
        assertEquals(paymentDTOExpected.hashCode(), paymentDTO.hashCode());
    }

    @Test
    void test_createPaymentDTONoArgsConstructor() {
        PaymentDTO paymentDTO = new PaymentDTO();
        assertNotNull(paymentDTO);
    }

    @Test
    void test_dtoToEntityPaymentDTO() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .id(id)
                .amount(amount)
                .paymentDateTime(paymentDateTime)
                .paymentMethod(paymentMethod)
                .status(status)
                .build();

        Payment payment = paymentDTO.dtoToEntity();

        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(paymentDateTime, payment.getPaymentDateTime());
        assertEquals(paymentMethod, payment.getPaymentMethod());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentExpected.hashCode(), payment.hashCode());
    }

    @Test
    void test_SetsPaymentDTO() {
        PaymentDTO result = PaymentDTO.builder().build();

        result.setId(id);
        result.setAmount(amount);
        result.setPaymentDateTime(paymentDateTime);
        result.setPaymentMethod(paymentMethod);
        result.setStatus(status);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(amount, result.getAmount());
        assertEquals(paymentDateTime, result.getPaymentDateTime());
        assertEquals(paymentMethod, result.getPaymentMethod());
        assertEquals(status, result.getStatus());
        assertEquals(paymentDTOExpected.hashCode(), result.hashCode());
        assertEquals(paymentDTOExpected, result);
        assertEquals(paymentDTOExpected.toString(), result.toString());
    }
}
