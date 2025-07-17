package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
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
    private int addressId;
    private int orderId;
    private int merchantOrderId;
    private String email;

    public ShippingOrderDTO(ShippingOrder shippingOrder, String email) {
        this.id = shippingOrder.getId();
        this.shippingOrderStatus = shippingOrder.getStatus();
        this.orderId = shippingOrder.getOrderId();
        this.merchantOrderId = shippingOrder.getMerchantOrderId();
        this.email = email;
    }

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
