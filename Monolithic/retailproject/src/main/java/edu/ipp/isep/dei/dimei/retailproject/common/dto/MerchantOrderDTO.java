package edu.ipp.isep.dei.dimei.retailproject.common.dto;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchantOrderDTO {
    private int id;
    private LocalDateTime merchantOrderDate;
    private MerchantOrderStatusEnum merchantOrderStatus;
    private int customerId;
    private String email;
    private int orderId;
}
