package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.MerchantOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class MerchantOrderUpdateDTO {
    private int id;
    private Instant merchantOrderDate;
    private MerchantOrderStatusEnum merchantOrderStatus;
    private String email;
    private int orderId;
    private int merchantId;

    public MerchantOrderUpdateDTO(MerchantOrder merchantOrder) {
        this.id = merchantOrder.getId();
        this.merchantOrderDate = merchantOrder.getOrderDate();
        this.merchantOrderStatus = merchantOrder.getStatus();
        this.email = merchantOrder.getUser().getAccount().getEmail();
        this.orderId = merchantOrder.getOrder().getId();
        this.merchantId = merchantOrder.getMerchant().getId();
    }
}
