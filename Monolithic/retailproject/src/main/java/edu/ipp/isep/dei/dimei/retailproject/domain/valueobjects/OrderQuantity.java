package edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueobjects.IValueObject;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderQuantity implements IValueObject {
    @Column(name = "quantity_ordered")
    private int quantity;

    public OrderQuantity(int quantity) throws InvalidQuantityException {

        if (quantity < 0 || quantity > 999999) {
            throw new InvalidQuantityException("The number of quantity inserted is not valid");
        }
        this.quantity = quantity;
    }
}
