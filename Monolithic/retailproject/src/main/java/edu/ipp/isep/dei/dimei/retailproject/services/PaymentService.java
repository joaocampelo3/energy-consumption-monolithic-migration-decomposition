package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Payment not found.";

    public Payment createPayment(PaymentDTO paymentDTO) throws NotFoundException {
        Payment payment = paymentDTO.dtoToEntity();

        if (!existsPaymentByStreetAndZipCodeAndCityAndCountry(payment)) {
            this.paymentRepository.save(payment);
        }

        return getPayment(payment);
    }

    private boolean existsPaymentByStreetAndZipCodeAndCityAndCountry(Payment payment) {
        return this.paymentRepository.existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(payment.getAmount(), payment.getPaymentDateTime(), payment.getPaymentMethod());
    }

    public Payment getPayment(Payment payment) throws NotFoundException {
        return this.paymentRepository.findPaymentByAmountAndPaymentDateTimeAndPaymentMethod(payment.getAmount(), payment.getPaymentDateTime(), payment.getPaymentMethod())
                .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
    }
}
