package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "merchant_orders",
        indexes = {
                @Index(columnList = "merchant_order_status"),
                @Index(columnList = "order_id"),
                @Index(columnList = "merchant_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MerchantOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "merchant_order_date", nullable = false)
    private Instant orderDate;

    @Column(name = "merchant_order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantOrderStatusEnum status;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "order_id", nullable = false)
    private int orderId;

    @Column(name = "merchant_id", nullable = false)
    private int merchantId;

    public MerchantOrder(int userId, int orderId, Instant orderDate, int merchantId) {
        this.orderDate = orderDate;
        this.status = MerchantOrderStatusEnum.PENDING;
        this.userId = userId;
        this.orderId = orderId;
        this.merchantId = merchantId;
    }

    public boolean isPending() {
        return status == MerchantOrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return status == MerchantOrderStatusEnum.APPROVED;
    }

    public boolean isRejected() {
        return status == MerchantOrderStatusEnum.REJECTED;
    }

    public boolean isCancelled() {
        return status == MerchantOrderStatusEnum.CANCELLED;
    }

    public boolean isShipped() {
        return status == MerchantOrderStatusEnum.SHIPPED;
    }

    public boolean isDelivered() {
        return status == MerchantOrderStatusEnum.DELIVERED;
    }
}
