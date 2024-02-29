package edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueObjects.IValueObject;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockQuantity implements IValueObject {
    @Column(name = "item_stock_quantity")
    private int quantity;

    public StockQuantity(int quantity) throws Exception {

        if (quantity < 0 || quantity > 999999) {
            throw new Exception("The number of quantity inserted is not valid");
        }
        this.quantity = quantity;
    }
}
