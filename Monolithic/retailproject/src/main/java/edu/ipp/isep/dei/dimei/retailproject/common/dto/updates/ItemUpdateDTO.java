package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemUpdateDTO {
    private int id;
    private String sku;
    private double price;
    private int quantityInStock;

    public ItemUpdateDTO(Item item) {
        this.id = item.getId();
        this.sku = item.getSku();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
    }
}
