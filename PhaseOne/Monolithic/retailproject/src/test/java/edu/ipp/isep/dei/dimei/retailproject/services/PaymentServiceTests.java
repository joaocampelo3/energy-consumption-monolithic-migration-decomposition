package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {
    @InjectMocks
    PaymentService paymentService;
    @Mock
    PaymentRepository paymentRepository;
    PaymentDTO paymentDTO1;
    Payment payment1;

    @BeforeEach
    void beforeEach() {
        payment1 = Payment.builder()
                .id(1)
                .amount(10)
                .paymentDateTime(Instant.now())
                .paymentMethod(PaymentMethodEnum.CARD)
                .status(PaymentStatusEnum.ACCEPTED)
                .build();

        paymentDTO1 = new PaymentDTO(payment1);
    }

    @Test
    void test_CreatePayment() {
        // Define the behavior of the mock
        paymentDTO1.setId(0);
        Payment newPayment1 = paymentDTO1.dtoToEntity();
        when(paymentRepository.existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(paymentDTO1.getAmount(), paymentDTO1.getPaymentDateTime(), paymentDTO1.getPaymentMethod()))
                .thenReturn(false);
        when(paymentRepository.save(newPayment1)).thenReturn(payment1);

        // Call the service method that uses the Repository
        Payment result = paymentService.createPayment(paymentDTO1);
        Payment expected = payment1;

        // Perform assertions
        verify(paymentRepository, atLeastOnce()).existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(paymentDTO1.getAmount(), paymentDTO1.getPaymentDateTime(), paymentDTO1.getPaymentMethod());
        verify(paymentRepository, atLeastOnce()).save(newPayment1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateExistingPayment() {
        // Define the behavior of the mock
        when(paymentRepository.existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(paymentDTO1.getAmount(), paymentDTO1.getPaymentDateTime(), paymentDTO1.getPaymentMethod()))
                .thenReturn(true);

        // Call the service method that uses the Repository
        Payment result = paymentService.createPayment(paymentDTO1);
        Payment expected = payment1;

        // Perform assertions
        verify(paymentRepository, atLeastOnce()).existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(paymentDTO1.getAmount(), paymentDTO1.getPaymentDateTime(), paymentDTO1.getPaymentMethod());
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
