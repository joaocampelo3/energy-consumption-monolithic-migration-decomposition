package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.MerchantOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class MerchantOrderDTO {
    private int id;
    private Instant merchantOrderDate;
    private MerchantOrderStatusEnum merchantOrderStatus;
    private int customerId;
    private String email;
    private int orderId;
    private int merchantId;

    public boolean isPending() {
        return merchantOrderStatus == MerchantOrderStatusEnum.PENDING;
    }

    public boolean isApproved() {
        return merchantOrderStatus == MerchantOrderStatusEnum.APPROVED;
    }

    public boolean isRejected() {
        return merchantOrderStatus == MerchantOrderStatusEnum.REJECTED;
    }

    public boolean isCancelled() {
        return merchantOrderStatus == MerchantOrderStatusEnum.CANCELLED;
    }

    public boolean isShipped() {
        return merchantOrderStatus == MerchantOrderStatusEnum.SHIPPED;
    }

    public boolean isDelivered() {
        return merchantOrderStatus == MerchantOrderStatusEnum.DELIVERED;
    }
}
