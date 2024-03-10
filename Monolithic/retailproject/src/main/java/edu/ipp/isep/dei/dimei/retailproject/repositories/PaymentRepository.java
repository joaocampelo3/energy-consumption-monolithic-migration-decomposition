package edu.ipp.isep.dei.dimei.retailproject.repositories;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Integer> {
    Optional<Payment> findById(int id);

    Optional<Payment> findPaymentByAmountAndPaymentDateTimeAndPaymentMethod(double payment_amount, LocalDateTime payment_date, PaymentMethodEnum payment_method);

    boolean existsPaymentByAmountAndPaymentDateTimeAndPaymentMethod(double payment_amount, LocalDateTime payment_date, PaymentMethodEnum payment_method);
}
