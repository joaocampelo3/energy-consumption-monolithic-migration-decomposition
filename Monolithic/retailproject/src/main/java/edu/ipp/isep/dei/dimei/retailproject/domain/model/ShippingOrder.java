package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id"}),
                @UniqueConstraint(columnNames = {"merchant_order_id"})
        },
        indexes = {
                @Index(columnList = "shipping_order_status"),
                @Index(columnList = "customer_id")
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
    private LocalDateTime shippingOrderDate;
    @Column(name = "shipping_order_status")
    @Enumerated(EnumType.STRING)
    private ShippingOrderStatusEnum status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
    private Address shippingAddress;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_order_id", referencedColumnName = "id")
    private MerchantOrder merchantOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    public ShippingOrder(User user, Order order, MerchantOrder merchantOrder, Address address) {
        this.shippingOrderDate = order.getOrderDate();
        this.status = ShippingOrderStatusEnum.PENDING;
        this.shippingAddress = address;
        this.order = order;
        this.merchantOrder = merchantOrder;
        this.user = user;
    }
}
