package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ItemDTO {
    private int id;
    private String itemName;
    private String sku;
    private String itemDescription;
    private double price;
    private int quantityInStock;
    private CategoryDTO category;
    private MerchantDTO merchant;
    private UserDTO userDTO;

    public ItemDTO(Item item) {
        this.id = item.getId();
        this.itemName = item.getName();
        this.sku = item.getSku();
        this.itemDescription = item.getDescription();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
        this.merchant = new MerchantDTO(item.getMerchant());
    }

    public ItemDTO(Item item, CategoryDTO categoryDTO, MerchantDTO merchantOrderDTO) {
        this.id = item.getId();
        this.itemName = item.getName();
        this.sku = item.getSku();
        this.itemDescription = item.getDescription();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
        this.category = categoryDTO;
        this.merchant = merchantOrderDTO;
    }

    public Item dtoToEntity() throws InvalidQuantityException {
        return Item.builder()
                .id(this.id)
                .name(this.itemName)
                .sku(this.sku)
                .description(this.itemDescription)
                .price(this.price)
                .quantityInStock(new StockQuantity(this.quantityInStock))
                .merchant(this.merchant.dtoToEntity())
                .build();
    }
}
