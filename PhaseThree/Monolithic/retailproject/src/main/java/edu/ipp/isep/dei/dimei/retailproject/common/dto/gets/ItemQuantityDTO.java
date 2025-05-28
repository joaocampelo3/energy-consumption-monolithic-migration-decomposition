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
        this.itemId = itemQuantity.getItemId().getId();
        this.itemName = itemQuantity.getItemId().getName();
        this.itemSku = itemQuantity.getItemId().getSku();
        this.itemDescription = itemQuantity.getItemId().getDescription();
        this.qty = itemQuantity.getQuantityOrdered().getQuantity();
        this.price = itemQuantity.getItemId().getPrice() * itemQuantity.getQuantityOrdered().getQuantity();
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
        OrderQuantity quantity = new OrderQuantity(this.qty);
        return ItemQuantity.builder()
                .id(this.id)
                .quantityOrdered(quantity)
                .item(dtoToItem())
                .price(this.price)
                .build();
    }
}
