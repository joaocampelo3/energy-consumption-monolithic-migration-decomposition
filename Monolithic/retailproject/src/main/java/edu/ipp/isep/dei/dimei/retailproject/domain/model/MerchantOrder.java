package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "merchant_orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id"})
        },
        indexes = {
                @Index(columnList = "merchant_order_status"),
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

    @Column(name = "merchant_order_status")
    @Enumerated(EnumType.STRING)
    private MerchantOrderStatusEnum status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    public MerchantOrder(User user, Order order, Merchant merchant) {
        this.orderDate = order.getOrderDate();
        this.status = MerchantOrderStatusEnum.PENDING;
        this.user = user;
        this.order = order;
        this.merchant = merchant;
    }
}
