package edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueobjects.IValueObject;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class OrderQuantity implements IValueObject {
    @Column(name = "quantity_ordered")
    private int quantity;

    public OrderQuantity(int quantity) throws InvalidQuantityException {
        if (quantity < 0 || quantity > Integer.MAX_VALUE) {
            throw new InvalidQuantityException("The number of quantity inserted is not valid");
        }
        this.quantity = quantity;
    }

    public OrderQuantity() throws InvalidQuantityException {
        if (this.quantity < 0 || this.quantity > Integer.MAX_VALUE) {
            throw new InvalidQuantityException("The number of quantity inserted is not valid");
        }
    }
}
