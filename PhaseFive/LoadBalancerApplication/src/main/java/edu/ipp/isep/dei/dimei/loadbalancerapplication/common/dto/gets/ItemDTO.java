package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
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
}
