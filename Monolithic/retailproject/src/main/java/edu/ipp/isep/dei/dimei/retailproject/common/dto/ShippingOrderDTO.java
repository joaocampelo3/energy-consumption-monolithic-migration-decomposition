package edu.ipp.isep.dei.dimei.retailproject.common.dto;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingOrderDTO {
    private int id;
    private LocalDateTime shippingOrderDate;
    private ShippingOrderStatusEnum shippingOrderStatus;
    private AddressDTO addressDTO;
    private int orderId;
    private int merchantOrderId;
    private int customerId;
    private String email;
}
