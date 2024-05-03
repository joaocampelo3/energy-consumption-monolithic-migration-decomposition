package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PaymentTest {
    int id;
    double amount;
    LocalDateTime paymentDateTime;
    PaymentMethodEnum paymentMethod = PaymentMethodEnum.CARD;
    PaymentStatusEnum status = PaymentStatusEnum.PENDING;
    Payment paymentExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        amount = 10.0;
        LocalDateTime currentDate = LocalDateTime.now();
        paymentDateTime = currentDate;
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
}
