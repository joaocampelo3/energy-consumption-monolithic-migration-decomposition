package edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueobjects.IValueObject;
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
    private static final String INVALIDQUANTITYEXCEPTION = "The number of quantity inserted is not valid";

    @Column(name = "item_stock_quantity")
    private int quantity;

    public StockQuantity(int quantity) throws InvalidQuantityException {

        isStockQuantityValid(quantity);

        this.quantity = quantity;
    }

    private void isStockQuantityValid(int quantity) throws InvalidQuantityException {
        if (quantity < 0 || quantity > Integer.MAX_VALUE) {
            throw new InvalidQuantityException(INVALIDQUANTITYEXCEPTION);
        }
    }

    public void increaseStockQuantity(int quantity) throws InvalidQuantityException {
        if (this.quantity < quantity) {
            isStockQuantityValid(quantity);
            this.quantity = quantity;
        } else {
            throw new InvalidQuantityException(INVALIDQUANTITYEXCEPTION);
        }
    }

    public void decreaseStockQuantity(int quantity) throws InvalidQuantityException {
        if (this.quantity > quantity) {
            isStockQuantityValid(quantity);
            this.quantity = quantity;
        } else {
            throw new InvalidQuantityException(INVALIDQUANTITYEXCEPTION);
        }
    }

}
