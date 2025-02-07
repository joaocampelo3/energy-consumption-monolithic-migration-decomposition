package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"payment_id"})
        },
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemQuantity> itemQuantities;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    public Order(Instant orderDate, OrderStatusEnum status, int userId, List<ItemQuantity> itemQuantities, Payment payment) {
        this.orderDate = orderDate;
        this.status = status;
        this.userId = userId;
        this.itemQuantities = itemQuantities;
        this.payment = payment;
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
