package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
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
        this.itemId = itemQuantity.getItem().getId();
        this.itemName = itemQuantity.getItem().getName();
        this.itemSku = itemQuantity.getItem().getSku();
        this.itemDescription = itemQuantity.getItem().getDescription();
        this.qty = itemQuantity.getQuantityOrdered().getQuantity();
        this.price = itemQuantity.getItem().getPrice() * itemQuantity.getQuantityOrdered().getQuantity();
    }

    public Item dtoToItem() {
        return Item.builder()
                .id(this.itemId)
                .name(this.itemName)
                .sku(this.itemSku)
                .description(this.itemDescription)
                .price(this.price)
                .build();
    }

    public ItemQuantity dtoToEntity() throws InvalidQuantityException {
        return ItemQuantity.builder()
                .id(this.id)
                .quantityOrdered(new OrderQuantity(this.qty))
                .item(dtoToItem())
                .build();
    }
}
