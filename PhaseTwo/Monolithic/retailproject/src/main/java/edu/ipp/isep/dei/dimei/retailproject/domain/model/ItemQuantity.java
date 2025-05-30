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

    @ManyToOne(optional = false)
    private Item item;

    private double price;

    public ItemQuantity(OrderQuantity quantityOrdered, Item item, double price) {
        this.quantityOrdered = quantityOrdered;
        this.item = item;
        this.price = price;
    }

    public double getTotalPrice() {
        return this.price * quantityOrdered.getQuantity();
    }
}
