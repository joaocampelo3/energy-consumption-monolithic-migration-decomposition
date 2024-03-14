package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.ShippingOrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ShippingOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class ShippingOrderDTO {
    private int id;
    private LocalDateTime shippingOrderDate;
    private ShippingOrderStatusEnum shippingOrderStatus;
    private AddressDTO addressDTO;
    private int orderId;
    private int merchantOrderId;
    private String email;

    public ShippingOrderDTO(ShippingOrder shippingOrder) {
        this.id = shippingOrder.getId();
        this.shippingOrderDate = shippingOrder.getShippingOrderDate();
        this.shippingOrderStatus = shippingOrder.getStatus();
        this.addressDTO = new AddressDTO(shippingOrder.getShippingAddress());
        this.orderId = shippingOrder.getOrder().getId();
        this.merchantOrderId = shippingOrder.getMerchantOrder().getId();
        this.email = shippingOrder.getUser().getAccount().getEmail();
    }
}
