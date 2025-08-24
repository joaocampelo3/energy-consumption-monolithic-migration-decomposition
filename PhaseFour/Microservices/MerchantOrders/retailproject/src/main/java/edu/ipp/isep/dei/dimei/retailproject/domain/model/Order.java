package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "orders",
        indexes = {
                @Index(columnList = "order_status"),
                @Index(columnList = "user_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "order_date", nullable = false)
    private Instant orderDate;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    @Column(name = "user_id", nullable = false)
    private int userId;

    public Order(Instant orderDate, OrderStatusEnum status, int userId) {
        this.orderDate = orderDate;
        this.status = status;
        this.userId = userId;
    }

    public boolean isPending() {
        return status == OrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return status == OrderStatusEnum.APPROVED;
    }

    public boolean isShipped() {
        return status == OrderStatusEnum.SHIPPED;
    }

    public boolean isPendingOrApproved() {
        return this.isPending() || this.isApproved();
    }
}
