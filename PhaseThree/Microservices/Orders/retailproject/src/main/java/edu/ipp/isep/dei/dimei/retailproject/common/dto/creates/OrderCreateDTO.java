package edu.ipp.isep.dei.dimei.retailproject.common.dto.creates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Order;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class OrderCreateDTO {
    private int id;
    private Instant orderDate;
    private OrderStatusEnum orderStatus;
    private int customerId;
    private String email;
    private List<ItemQuantityDTO> orderItems;
    private double totalPrice;
    private PaymentDTO payment;
    private int merchantId;
    private AddressDTO address;
    private UserDTO userDTO;

    public OrderCreateDTO(Order order, String email, AddressDTO address, UserDTO userDTO) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.customerId = order.getUserId();
        this.email = email;
        this.orderItems = order.getItemQuantities().stream().map(ItemQuantityDTO::new).toList();
        this.totalPrice = order.getItemQuantities().stream().mapToDouble(ItemQuantity::getTotalPrice).sum();
        this.payment = order.getPayment() == null ? null : new PaymentDTO(order.getPayment());
        this.address = address;
        this.userDTO = userDTO;
    }

    public Order dtoToEntity(Payment payment, List<ItemQuantity> itemQuantities) {
        return Order.builder()
                .id(this.id)
                .orderDate(this.orderDate)
                .status(OrderStatusEnum.PENDING)
                .userId(userDTO.getUserId())
                .itemQuantities(itemQuantities)
                .payment(payment)
                .build();
    }
}
