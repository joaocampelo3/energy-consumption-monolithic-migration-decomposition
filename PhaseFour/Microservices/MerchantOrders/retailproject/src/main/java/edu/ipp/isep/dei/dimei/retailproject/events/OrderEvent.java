package edu.ipp.isep.dei.dimei.retailproject.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.events.enums.EventTypeEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class OrderEvent {
    private int id;
    private Instant orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO paymentDTO;
    private int merchantId;
    private AddressDTO addressDTO;
    private UserDTO userDTO;
    private EventTypeEnum eventTypeEnum;

    public OrderEvent(OrderCreateDTO orderCreateDTO, EventTypeEnum eventTypeEnum) {
        this.id = orderCreateDTO.getId();
        this.orderDate = orderCreateDTO.getOrderDate();
        this.orderStatus = orderCreateDTO.getOrderStatus();
        this.customerId = orderCreateDTO.getCustomerId();
        this.email = orderCreateDTO.getEmail();
        this.orderItems = orderCreateDTO.getOrderItems();
        this.totalPrice = orderCreateDTO.getTotalPrice();
        this.paymentDTO = orderCreateDTO.getPayment();
        this.merchantId = orderCreateDTO.getMerchantId();
        this.addressDTO = orderCreateDTO.getAddress();
        this.userDTO = orderCreateDTO.getUserDTO();
        this.eventTypeEnum = eventTypeEnum;
    }

    public OrderEvent(OrderDTO orderDTO, EventTypeEnum eventTypeEnum) {
        this.id = orderDTO.getId();
        this.orderDate = orderDTO.getOrderDate();
        this.orderStatus = orderDTO.getOrderStatus();
        this.customerId = orderDTO.getCustomerId();
        this.orderItems = orderDTO.getOrderItems();
        this.totalPrice = orderDTO.getTotalPrice();
        this.paymentDTO = orderDTO.getPaymentDTO();
        this.eventTypeEnum = eventTypeEnum;
    }

    public OrderEvent(OrderUpdateDTO orderUpdateDTO, EventTypeEnum eventTypeEnum) {
        this.id = orderUpdateDTO.getId();
        this.orderDate = orderUpdateDTO.getOrderDate();
        this.orderStatus = orderUpdateDTO.getOrderStatus();
        this.email = orderUpdateDTO.getEmail();
        this.userDTO = orderUpdateDTO.getUserDTO();
        this.eventTypeEnum = eventTypeEnum;
    }

    public static OrderEvent fromJson(String json) {
        Gson gson = new Gson();
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .serializeNulls()
                .create()
                .fromJson(json, OrderEvent.class);
    }

    public String toJson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .serializeNulls()
                .create()
                .toJson(this);
    }

    public Order toOrder() throws InvalidQuantityException {
        return new Order(this.id, this.orderDate, this.orderStatus, this.customerId);
    }
}
