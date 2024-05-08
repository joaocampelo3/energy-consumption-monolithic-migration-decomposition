package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payments",
        indexes = {
                @Index(columnList = "payment_date"),
                @Index(columnList = "payment_method"),
                @Index(columnList = "payment_status")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"payment_amount", "payment_date", "payment_method"})
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "payment_amount", nullable = false)
    private double amount;
    @Column(name = "payment_date", nullable = false)
    private Instant paymentDateTime;
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;
    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

}
