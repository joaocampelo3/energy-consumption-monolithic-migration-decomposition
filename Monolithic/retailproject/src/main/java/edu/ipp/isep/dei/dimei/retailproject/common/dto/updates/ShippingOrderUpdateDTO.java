package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class ShippingOrderUpdateDTO {
    private int id;
    private LocalDateTime shippingOrderDate;
    private ShippingOrderStatusEnum shippingOrderStatus;
    private AddressDTO addressDTO;
    private int orderId;
    private int merchantOrderId;
    private String email;

    public ShippingOrderUpdateDTO(ShippingOrder shippingOrder) {
        this.id = shippingOrder.getId();
        this.shippingOrderDate = shippingOrder.getShippingOrderDate();
        this.shippingOrderStatus = shippingOrder.getStatus();
        this.addressDTO = new AddressDTO(shippingOrder.getShippingAddress());
        this.orderId = shippingOrder.getOrder().getId();
        this.merchantOrderId = shippingOrder.getMerchantOrder().getId();
        this.email = shippingOrder.getUser().getAccount().getEmail();
    }
}
