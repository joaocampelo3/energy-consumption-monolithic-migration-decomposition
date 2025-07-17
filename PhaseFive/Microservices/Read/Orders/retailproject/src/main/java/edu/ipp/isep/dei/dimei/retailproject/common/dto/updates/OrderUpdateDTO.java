package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class OrderUpdateDTO {
    private int id;
    private Instant orderDate;
    private OrderStatusEnum orderStatus;
    private String email;
    private UserDTO userDTO;

    public OrderUpdateDTO(Order order, String email) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.email = email;
    }
}
