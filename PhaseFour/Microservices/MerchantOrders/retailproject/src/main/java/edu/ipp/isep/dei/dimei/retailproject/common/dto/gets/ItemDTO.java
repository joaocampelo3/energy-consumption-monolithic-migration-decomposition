package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

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
}
