package edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects;

import edu.ipp.isep.dei.dimei.retailproject.domain.interfaces.valueObjects.IValueObject;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderQuantity implements IValueObject {
    @Column(name = "quantity_ordered")
    private int quantity;

    public OrderQuantity(int quantity) throws Exception {

        if (quantity < 0 || quantity > 999999) {
            throw new Exception("The number of quantity inserted is not valid");
        }
        this.quantity = quantity;
    }

    protected OrderQuantity() {
    }
}
