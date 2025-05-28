package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PaymentTest {
    final PaymentStatusEnum status = PaymentStatusEnum.PENDING;
    int id;
    double amount;
    Instant paymentDateTime;
    PaymentMethodEnum paymentMethod = PaymentMethodEnum.CARD;
    Payment paymentExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        amount = 10.0;
        paymentDateTime = Instant.now();
        paymentExpected = Payment.builder()
                .id(id)
                .amount(amount)
                .paymentDateTime(paymentDateTime)
                .paymentMethod(paymentMethod)
                .status(status)
                .build();
    }

    @Test
    void test_createPayment() {
        Payment payment = new Payment(id, amount, paymentDateTime, paymentMethod, status);

        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(paymentDateTime, payment.getPaymentDateTime());
        assertEquals(paymentMethod, payment.getPaymentMethod());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentExpected.hashCode(), payment.hashCode());
        assertEquals(paymentExpected, payment);
    }

    @Test
    void test_createPaymentBuilder() {
        Payment payment = Payment.builder()
                .id(id)
                .amount(amount)
                .paymentDateTime(paymentDateTime)
                .paymentMethod(paymentMethod)
                .status(status)
                .build();

        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(paymentDateTime, payment.getPaymentDateTime());
        assertEquals(paymentMethod, payment.getPaymentMethod());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentExpected.hashCode(), payment.hashCode());
        assertEquals(paymentExpected, payment);
    }

    @Test
    void test_PaymentSets() {
        Payment payment = Payment.builder().build();

        payment.setId(id);
        payment.setAmount(amount);
        payment.setPaymentDateTime(paymentDateTime);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(status);

        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(paymentDateTime, payment.getPaymentDateTime());
        assertEquals(paymentMethod, payment.getPaymentMethod());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentExpected.hashCode(), payment.hashCode());
        assertEquals(paymentExpected, payment);
    }
}
