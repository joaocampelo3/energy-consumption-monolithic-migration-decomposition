package edu.ipp.isep.dei.dimei.retailproject.common.dto.creates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class OrderCreateDTO {
    private int id;
    private LocalDateTime orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO paymentDTO;
    private int merchantId;
    private AddressDTO addressDTO;

    public Order dtoToEntity(User user, Payment payment, List<ItemQuantity> itemQuantities) {
        return Order.builder()
                .id(this.id)
                .orderDate(this.orderDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();
    }
}
