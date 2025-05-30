package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_quantities")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ItemQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Embedded
    @Column(nullable = false)
    private OrderQuantity quantityOrdered;

    private int itemId;

    private double price;

    public ItemQuantity(OrderQuantity quantityOrdered, int itemId, double price) {
        this.quantityOrdered = quantityOrdered;
        this.itemId = itemId;
        this.price = price;
    }

    public double getTotalPrice() {
        return this.price * quantityOrdered.getQuantity();
    }
}
