package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.ShippingOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class ShippingOrderDTO {
    private int id;
    private Instant shippingOrderDate;
    private ShippingOrderStatusEnum shippingOrderStatus;
    private AddressDTO addressDTO;
    private int orderId;
    private int merchantOrderId;
    private String email;

    public boolean isPending() {
        return shippingOrderStatus == ShippingOrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return shippingOrderStatus == ShippingOrderStatusEnum.APPROVED;
    }

    public boolean isRejected() {
        return shippingOrderStatus == ShippingOrderStatusEnum.REJECTED;
    }

    public boolean isCancelled() {
        return shippingOrderStatus == ShippingOrderStatusEnum.CANCELLED;
    }

    public boolean isShipped() {
        return shippingOrderStatus == ShippingOrderStatusEnum.SHIPPED;
    }

    public boolean isDelivered() {
        return shippingOrderStatus == ShippingOrderStatusEnum.DELIVERED;
    }

    public boolean isPendingOrApproved() {
        return this.isPending() || this.isApproved();
    }

    public boolean isPendingOrApprovedOrRejected() {
        return this.isPendingOrApproved() || this.isRejected();
    }

    public boolean isPendingOrApprovedOrCancelled() {
        return this.isPendingOrApproved() || this.isCancelled();
    }
}
