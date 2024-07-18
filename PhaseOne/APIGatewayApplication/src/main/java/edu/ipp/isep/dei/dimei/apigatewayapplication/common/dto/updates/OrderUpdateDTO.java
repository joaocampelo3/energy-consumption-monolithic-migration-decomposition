package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class OrderUpdateDTO {
    private int id;
    private Instant orderDate;
    private OrderStatusEnum orderStatus;
    private String email;
}
