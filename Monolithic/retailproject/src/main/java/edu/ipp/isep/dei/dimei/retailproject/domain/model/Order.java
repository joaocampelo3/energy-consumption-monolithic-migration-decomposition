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
                @Index(columnList = "customer_id")
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

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemQuantity> itemQuantities;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    public Order(Instant orderDate, OrderStatusEnum status, User user, List<ItemQuantity> itemQuantities, Payment payment) {
        this.orderDate = orderDate;
        this.status = status;
        this.user = user;
        this.itemQuantities = itemQuantities;
        this.payment = payment;
    }
}
