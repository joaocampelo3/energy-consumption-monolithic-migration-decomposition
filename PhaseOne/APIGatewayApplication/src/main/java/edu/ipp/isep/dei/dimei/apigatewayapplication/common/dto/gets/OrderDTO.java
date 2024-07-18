package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
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
