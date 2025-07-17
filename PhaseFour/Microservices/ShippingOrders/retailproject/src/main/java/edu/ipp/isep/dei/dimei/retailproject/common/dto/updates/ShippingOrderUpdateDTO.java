package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
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

    public ShippingOrderUpdateDTO(ShippingOrder shippingOrder) {
        this.id = shippingOrder.getId();
        this.shippingOrderDate = shippingOrder.getShippingOrderDate();
        this.shippingOrderStatus = shippingOrder.getStatus();
        this.addressId = shippingOrder.getShippingAddressId();
        this.orderId = shippingOrder.getOrderId();
        this.merchantOrderId = shippingOrder.getMerchantOrderId();
        this.userId = shippingOrder.getUserId();
    }
}
