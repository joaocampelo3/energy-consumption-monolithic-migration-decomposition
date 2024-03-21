package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentDTO.dtoToEntity();

        if (!existsPaymentByStreetAndZipCodeAndCityAndCountry(payment)) {
            payment = this.paymentRepository.save(payment);
        }
        return payment;
    }

    private boolean existsPaymentByStreetAndZipCodeAndCityAndCountry(Payment payment) {
        return this.paymentRepository.existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(payment.getAmount(), payment.getPaymentDateTime(), payment.getPaymentMethod());
    }
}
