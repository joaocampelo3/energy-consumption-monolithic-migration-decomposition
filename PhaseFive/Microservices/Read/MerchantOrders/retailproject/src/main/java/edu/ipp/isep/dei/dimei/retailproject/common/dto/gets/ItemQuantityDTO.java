package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

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
}
