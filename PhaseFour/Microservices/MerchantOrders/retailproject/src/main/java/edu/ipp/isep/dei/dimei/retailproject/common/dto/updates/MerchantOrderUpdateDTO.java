package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
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
    private UserDTO userDTO;

    public MerchantOrderUpdateDTO(MerchantOrder merchantOrder, String email) {
        this.id = merchantOrder.getId();
        this.merchantOrderDate = merchantOrder.getOrderDate();
        this.merchantOrderStatus = merchantOrder.getStatus();
        this.email = email;
        this.orderId = merchantOrder.getOrderId();
        this.merchantId = merchantOrder.getMerchant().getId();
    }
}
