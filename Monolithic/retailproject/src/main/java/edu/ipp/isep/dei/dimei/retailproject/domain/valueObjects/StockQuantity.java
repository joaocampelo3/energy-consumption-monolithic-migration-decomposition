package edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueObjects.IValueObject;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Getter
@Setter
public class StockQuantity implements IValueObject {
    @Column(name = "item_stock_quantity")
    private int quantity;

    public StockQuantity(int quantity) throws InvalidQuantityException {

        isStockQuantityValid(quantity);

        this.quantity = quantity;
    }

    private void isStockQuantityValid(int quantity) throws InvalidQuantityException {
        if (quantity < 0 || quantity > 999999) {
            throw new InvalidQuantityException("The number of quantity inserted is not valid");
        }
    }

    public void increaseStockQuantity(int quantity) throws InvalidQuantityException {
        if (this.quantity < quantity) {
            isStockQuantityValid(quantity);
            this.quantity = quantity;
        }
    }

    public void decreaseStockQuantity(int quantity) throws InvalidQuantityException {
        if (this.quantity > quantity) {
            isStockQuantityValid(quantity);
            this.quantity = quantity;
        }
    }

}
