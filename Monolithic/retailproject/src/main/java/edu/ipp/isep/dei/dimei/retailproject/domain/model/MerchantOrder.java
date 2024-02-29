package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_orders",
        indexes = {
                @Index(columnList = "merchent_order_status"),
                @Index(columnList = "customer_id"),
                @Index(columnList = "order_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MerchantOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "merchant_order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "merchent_order_status")
    @Enumerated(EnumType.STRING)
    private MerchantOrderStatusEnum status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true, referencedColumnName = "id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;
}
