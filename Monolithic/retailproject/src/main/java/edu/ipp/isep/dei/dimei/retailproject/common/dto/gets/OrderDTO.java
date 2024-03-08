package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Data
public class OrderDTO {
    private int id;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO paymentDTO;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.customerId = order.getUser().getId();
        this.email = order.getUser().getAccount().getEmail();

        if (order.getItemQuantities() == null) {
            this.orderItems = new ArrayList<>();
        } else {
            this.orderItems = order.getItemQuantities().stream().map(ItemQuantityDTO::new).collect(Collectors.toList());
        }

        this.paymentDTO = new PaymentDTO(order.getPayment());

        this.totalPrice = order.getItemQuantities().stream().mapToDouble(ItemQuantity::getTotalPrice).sum();
    }
}
