package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class OrderUpdateDTO {
    private int id;
    private UUID uuid;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private String email;

    public OrderUpdateDTO(Order order) {
        this.id = order.getId();
        this.uuid = order.getUuid();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.email = order.getUser().getAccount().getEmail();
    }
}
