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
    private OrderQuantity quantityOrdered;

    @ManyToOne(optional = false)
    private Item item;

    public ItemQuantity(OrderQuantity quantityOrdered, Item item) {
        this.quantityOrdered = quantityOrdered;
        this.item = item;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantityOrdered.getQuantity();
    }
}
