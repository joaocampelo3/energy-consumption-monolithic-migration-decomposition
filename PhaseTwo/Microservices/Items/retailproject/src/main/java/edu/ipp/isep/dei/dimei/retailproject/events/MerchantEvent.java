package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class MerchantEvent {
    private int id;
    private String name;
    private String email;
    private EventTypeEnum eventTypeEnum;

    public MerchantEvent(MerchantDTO merchantDTO, EventTypeEnum eventTypeEnum) {
        this.id = merchantDTO.getId();
        this.name = merchantDTO.getName();
        this.email = merchantDTO.getEmail();
        this.eventTypeEnum = eventTypeEnum;
    }

    public static MerchantEvent fromJson(String json) {
        Gson gson = new Gson();
        return new GsonBuilder().serializeNulls().create().fromJson(json, MerchantEvent.class);
    }

    public String toJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public Merchant toMerchant() {
        return new Merchant(this.id, this.name, this.email);
    }
}
