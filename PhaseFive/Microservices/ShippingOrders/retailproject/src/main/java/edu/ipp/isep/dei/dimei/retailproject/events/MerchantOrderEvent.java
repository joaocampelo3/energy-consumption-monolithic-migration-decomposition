package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class MerchantOrderEvent {
    private int id;
    private Instant orderDate;
    private MerchantOrderStatusEnum status;
    private int customerId;
    private int orderId;
    private int merchantId;
    private EventTypeEnum eventTypeEnum;

    public MerchantOrderEvent(MerchantOrderDTO merchantOrderDTO, EventTypeEnum eventTypeEnum) {
        this.id = merchantOrderDTO.getId();
        this.orderDate = merchantOrderDTO.getMerchantOrderDate();
        this.status = merchantOrderDTO.getMerchantOrderStatus();
        this.customerId = merchantOrderDTO.getCustomerId();
        this.orderId = merchantOrderDTO.getOrderId();
        this.merchantId = merchantOrderDTO.getMerchantId();
        this.eventTypeEnum = eventTypeEnum;
    }

    public MerchantOrderEvent(MerchantOrderUpdateDTO merchantOrderUpdateDTO, EventTypeEnum eventTypeEnum) {
        this.id = merchantOrderUpdateDTO.getId();
        this.orderDate = merchantOrderUpdateDTO.getMerchantOrderDate();
        this.status = merchantOrderUpdateDTO.getMerchantOrderStatus();
        if (merchantOrderUpdateDTO.getUserDTO() != null)
            this.customerId = merchantOrderUpdateDTO.getUserDTO().getUserId();
        this.orderId = merchantOrderUpdateDTO.getOrderId();
        this.merchantId = merchantOrderUpdateDTO.getMerchantId();
        this.eventTypeEnum = eventTypeEnum;
    }

    public static MerchantOrderEvent fromJson(String json) {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .serializeNulls()
                .create()
                .fromJson(json, MerchantOrderEvent.class);
    }

    public String toJson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .serializeNulls()
                .create()
                .toJson(this);
    }
}
