package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemEvent {
    private int id;
    private String name;
    private String sku;
    private String description;
    private double price;
    private int quantity;
    private CategoryDTO categoryDTO;
    private MerchantDTO merchantDTO;
    private EventTypeEnum eventTypeEnum;

    public ItemEvent(ItemDTO itemDTO, EventTypeEnum eventTypeEnum) {
        this.id = itemDTO.getId();
        this.name = itemDTO.getItemName();
        this.sku = itemDTO.getSku();
        this.description = itemDTO.getItemDescription();
        this.price = itemDTO.getPrice();
        this.quantity = itemDTO.getQuantityInStock();
        this.categoryDTO = itemDTO.getCategory();
        this.merchantDTO = itemDTO.getMerchant();
        this.eventTypeEnum = eventTypeEnum;
    }

    public static ItemEvent fromJson(String json) {
        Gson gson = new Gson();
        return new GsonBuilder().serializeNulls().create().fromJson(json, ItemEvent.class);
    }

    public String toJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public Item toItem() throws InvalidQuantityException {
        return new Item(this.id, this.name, this.sku, this.description, this.price, new StockQuantity(this.quantity), this.merchantDTO.dtoToEntity());
    }
}
