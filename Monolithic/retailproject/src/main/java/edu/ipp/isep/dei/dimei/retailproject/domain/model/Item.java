package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "items",
        indexes = {
                @Index(columnList = "item_name"),
                @Index(columnList = "item_category")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 50)
    @Column(name = "item_name", nullable = false)
    private String name;
    @Size(max = 250)
    @Column(name = "item_description", nullable = false)
    private String description;
    @Column(name = "item_price", nullable = false)
    private double price;

    @Embedded
    private StockQuantity quantityInStock;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_category", referencedColumnName = "id")
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_merchant", referencedColumnName = "id")
    private Merchant merchant;

    public Item(String name, String description, double price, int quantityInStock, Category category, Merchant merchant) throws InvalidQuantityException {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = new StockQuantity(quantityInStock);
        this.category = category;
        this.merchant = merchant;
    }

}
