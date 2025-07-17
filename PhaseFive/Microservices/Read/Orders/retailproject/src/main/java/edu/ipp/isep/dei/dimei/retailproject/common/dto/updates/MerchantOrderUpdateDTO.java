package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.MerchantOrderStatusEnum;
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
}
