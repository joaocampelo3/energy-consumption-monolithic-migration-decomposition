package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class OrderDTO {
    private int id;
    private Instant orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO paymentDTO;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.customerId = order.getUser().getId();
        this.email = order.getUser().getAccount().getEmail();

        if (order.getItemQuantities() == null) {
            this.orderItems = new ArrayList<>();
            this.totalPrice = 0;
        } else {
            this.orderItems = order.getItemQuantities().stream().map(itemQuantity -> new ItemQuantityDTO(itemQuantity)).toList();
            this.totalPrice = order.getItemQuantities().stream().mapToDouble(ItemQuantity::getTotalPrice).sum();
        }
        this.paymentDTO = new PaymentDTO(order.getPayment());


    }
}
