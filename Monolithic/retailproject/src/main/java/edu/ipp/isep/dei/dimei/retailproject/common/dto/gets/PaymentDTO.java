package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class PaymentDTO {
    private int id;
    private double amount;
    private LocalDateTime paymentDateTime;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum status;

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.paymentDateTime = payment.getPaymentDateTime();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
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
