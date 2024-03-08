package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueObjects.StockQuantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemDTO {
    private int id;
    private String itemName;
    private String itemDescription;
    private double price;
    private int quantityInStock;
    private CategoryDTO category;
    private MerchantDTO merchant;

    public ItemDTO(Item item) {
        this.id = item.getId();
        this.itemName = item.getName();
        this.itemDescription = item.getDescription();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
        this.category = new CategoryDTO(item.getCategory());
        this.merchant = new MerchantDTO(item.getMerchant());
    }

    public ItemDTO(Item item, CategoryDTO categoryDTO, MerchantDTO merchantOrderDTO) {
        this.id = item.getId();
        this.itemName = item.getName();
        this.itemDescription = item.getDescription();
        this.price = item.getPrice();
        this.quantityInStock = item.getQuantityInStock().getQuantity();
        this.category = categoryDTO;
        this.merchant = merchantOrderDTO;
    }

    public Item dtoToEntity() {
        return Item.builder()
                .id(this.id)
                .name(this.itemName)
                .description(this.itemDescription)
                .price(this.price)
                .quantityInStock(new StockQuantity())
                .category(this.category.dtoToEntity())
                .merchant(this.merchant.dtoToEntity())
                .build();
    }
}
