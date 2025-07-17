package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "shipping_orders",
        indexes = {
                @Index(columnList = "shipping_order_status"),
                @Index(columnList = "order_id"),
                @Index(columnList = "merchant_order_id"),
                @Index(columnList = "user_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ShippingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "shipping_order_date", nullable = false)
    private Instant shippingOrderDate;
    @Column(name = "shipping_order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShippingOrderStatusEnum status;
    @Column(name = "shipping_order_addressId", nullable = false)
    private int shippingAddressId;

    @Column(name = "order_id", nullable = false)
    private int orderId;

    @Column(name = "merchant_order_id", nullable = false)
    private int merchantOrderId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    public ShippingOrder(int userId, int orderId, Instant orderDate, int merchantOrderId, int addressId) {
        this.shippingOrderDate = orderDate;
        this.status = ShippingOrderStatusEnum.PENDING;
        this.shippingAddressId = addressId;
        this.orderId = orderId;
        this.merchantOrderId = merchantOrderId;
        this.userId = userId;
    }

    public boolean isPending() {
        return status == ShippingOrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return status == ShippingOrderStatusEnum.APPROVED;
    }

    public boolean isRejected() {
        return status == ShippingOrderStatusEnum.REJECTED;
    }

    public boolean isShipped() {
        return status == ShippingOrderStatusEnum.SHIPPED;
    }

    public boolean isPendingOrApproved() {
        return this.isPending() || this.isApproved();
    }
}
