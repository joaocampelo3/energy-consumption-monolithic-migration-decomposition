package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.creates;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.ItemQuantityDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.PaymentDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
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
}
