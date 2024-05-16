package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class PaymentDTO {
    private int id;
    private double amount;
    private Instant paymentDateTime;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum status;

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.paymentDateTime = payment.getPaymentDateTime();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
    }

    public PaymentDTO(double amount, Instant paymentDateTime, PaymentMethodEnum paymentMethod, PaymentStatusEnum status) {
        this.amount = amount;
        this.paymentDateTime = paymentDateTime;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Payment dtoToEntity() {
        return Payment.builder()
                .id(this.id)
                .amount(this.amount)
                .paymentDateTime(this.paymentDateTime)
                .paymentMethod(this.paymentMethod)
                .status(this.status)
                .build();
    }
}
