package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDTO {
    private int id;
    private double amount;
    private Instant paymentDateTime;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum status;

    public PaymentDTO(double amount, Instant paymentDateTime, PaymentMethodEnum paymentMethod, PaymentStatusEnum status) {
        this.amount = amount;
        this.paymentDateTime = paymentDateTime;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }
}
