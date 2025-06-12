package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class ShippingOrderEvent {
    private int id;
    private Instant shippingOrderDate;
    private ShippingOrderStatusEnum status;
    private int shippingAddressId;
    private int orderId;
    private int merchantOrderId;
    private String email;
    private int userId;
    private EventTypeEnum eventTypeEnum;

    public ShippingOrderEvent(ShippingOrderDTO shippingOrderDTO, EventTypeEnum eventTypeEnum) {
        this.id = shippingOrderDTO.getId();
        this.shippingOrderDate = shippingOrderDTO.getShippingOrderDate();
        this.status = shippingOrderDTO.getShippingOrderStatus();
        this.shippingAddressId = shippingOrderDTO.getAddressId();
        this.orderId = shippingOrderDTO.getOrderId();
        this.merchantOrderId = shippingOrderDTO.getMerchantOrderId();
        this.email = shippingOrderDTO.getEmail();
        this.eventTypeEnum = eventTypeEnum;
    }

    public ShippingOrderEvent(ShippingOrderUpdateDTO shippingOrderUpdateDTO, EventTypeEnum eventTypeEnum) {
        this.id = shippingOrderUpdateDTO.getId();
        this.shippingOrderDate = shippingOrderUpdateDTO.getShippingOrderDate();
        this.status = shippingOrderUpdateDTO.getShippingOrderStatus();
        this.shippingAddressId = shippingOrderUpdateDTO.getAddressId();
        this.orderId = shippingOrderUpdateDTO.getOrderId();
        this.merchantOrderId = shippingOrderUpdateDTO.getMerchantOrderId();
        this.userId = shippingOrderUpdateDTO.getUserId();
        this.eventTypeEnum = eventTypeEnum;
    }

    public static ShippingOrderEvent fromJson(String json) {
        Gson gson = new Gson();
        return new GsonBuilder().serializeNulls().create().fromJson(json, ShippingOrderEvent.class);
    }

    public String toJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public ShippingOrder toShippingOrder() throws InvalidQuantityException {
        return new ShippingOrder(this.id, this.shippingOrderDate, this.status, this.shippingAddressId, this.orderId, this.merchantOrderId, this.userId);
    }
}
