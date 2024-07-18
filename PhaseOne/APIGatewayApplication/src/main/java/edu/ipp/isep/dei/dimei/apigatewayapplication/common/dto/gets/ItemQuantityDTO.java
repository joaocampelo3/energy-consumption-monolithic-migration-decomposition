package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets;

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
}
