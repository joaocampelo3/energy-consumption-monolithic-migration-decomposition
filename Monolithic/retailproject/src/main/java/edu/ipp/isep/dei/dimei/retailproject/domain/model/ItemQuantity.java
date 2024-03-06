package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.OrderQuantity;
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
@ToString
public class ItemQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Embedded
    private OrderQuantity quantityOrdered;

    @OneToOne(optional = false)
    private Item item;

    public double getTotalPrice() {
        return item.getPrice() * quantityOrdered.getQuantity();
    }
}
