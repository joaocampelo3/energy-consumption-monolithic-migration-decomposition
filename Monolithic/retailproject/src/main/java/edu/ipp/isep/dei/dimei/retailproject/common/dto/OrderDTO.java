package edu.ipp.isep.dei.dimei.retailproject.common.dto;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    private int id;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemDTO> orderItems;
    private double totalPrice;
}
