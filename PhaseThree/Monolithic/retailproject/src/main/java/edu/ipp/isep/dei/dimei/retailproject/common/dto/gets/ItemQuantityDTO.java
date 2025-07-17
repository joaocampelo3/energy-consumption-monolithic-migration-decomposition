package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class ItemQuantityDTO {
    private int id;
    private int itemId;
    private String itemName;
    private String itemSku;
    private String itemDescription;
    private int qty;
    private double price;

    public ItemQuantityDTO(ItemQuantity itemQuantity) {
        this.id = itemQuantity.getId();
        this.itemId = itemQuantity.getItemId();
        this.qty = itemQuantity.getQuantityOrdered().getQuantity();
        this.price = itemQuantity.getPrice();
    }

    public ItemQuantity dtoToEntity() throws InvalidQuantityException {
        OrderQuantity quantity = new OrderQuantity(this.qty);
        return ItemQuantity.builder()
                .id(this.id)
                .quantityOrdered(quantity)
                .itemId(this.itemId)
                .price(this.price)
                .build();
    }
}
