package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
        return new GsonBuilder().serializeNulls().create().fromJson(json, MerchantEvent.class);
    }

    public String toJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
