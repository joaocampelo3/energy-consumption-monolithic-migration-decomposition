package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_orders",
        indexes = {
                @Index(columnList = "shipping_order_status"),
                @Index(columnList = "order_id"),
                @Index(columnList = "merchant_order_id"),
                @Index(columnList = "customer_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ShippingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shipping_order_date", nullable = false)
    private LocalDateTime shippingOrderDate;
    @Column(name = "shipping_order_status")
    @Enumerated(EnumType.STRING)
    private ShippingOrderStatusEnum status;

    @OneToOne(optional = false)
    private Address shippingAddress;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true, referencedColumnName = "id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_order_id", unique = true, referencedColumnName = "id")
    private MerchantOrder merchantOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;
}
