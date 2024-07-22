package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.ShippingOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class ShippingOrderUpdateDTO {
    private int id;
    private Instant shippingOrderDate;
    private ShippingOrderStatusEnum shippingOrderStatus;
    private int addressId;
    private int orderId;
    private int merchantOrderId;
    private int userId;
    private UserDTO userDTO;
}
