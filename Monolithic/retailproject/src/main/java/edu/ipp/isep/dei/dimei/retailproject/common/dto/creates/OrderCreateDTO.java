package edu.ipp.isep.dei.dimei.retailproject.common.dto.creates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public Order dtoToEntity(User user) {
        return Order.builder()
                .id(this.id)
                .orderDate(this.orderDate)
                .status(OrderStatusEnum.PENDING)
                .user(user)
                .itemQuantities(this.getOrderItems().stream()
                        .map(itemQuantityDTO ->  {
                            try {
                                return itemQuantityDTO.dtoToEntity();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList()))
                .payment(this.paymentDTO.dtoToEntity())
                .build();
    }
}
