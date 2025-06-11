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
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO paymentDTO;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.customerId = order.getUserId();

        if (order.getItemQuantities() == null) {
            this.orderItems = new ArrayList<>();
            this.totalPrice = 0;
        } else {
            this.orderItems = order.getItemQuantities().stream().map(ItemQuantityDTO::new).toList();
            this.totalPrice = order.getItemQuantities().stream().mapToDouble(ItemQuantity::getPrice).sum();
        }
        this.paymentDTO = new PaymentDTO(order.getPayment());
    }

    public boolean isPending() {
        return orderStatus == OrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return orderStatus == OrderStatusEnum.APPROVED;
    }

    public boolean isRejected() {
        return orderStatus == OrderStatusEnum.REJECTED;
    }

    public boolean isCancelled() {
        return orderStatus == OrderStatusEnum.CANCELLED;
    }

    public boolean isShipped() {
        return orderStatus == OrderStatusEnum.SHIPPED;
    }

    public boolean isDelivered() {
        return orderStatus == OrderStatusEnum.DELIVERED;
    }
}
