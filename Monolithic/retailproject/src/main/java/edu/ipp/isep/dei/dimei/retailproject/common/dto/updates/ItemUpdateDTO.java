package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemUpdateDTO {
    private int id;
    private double price;
    private int quantityInStock;

    public ItemUpdateDTO(Item item) {
        this.id = item.getId();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
    }
}
