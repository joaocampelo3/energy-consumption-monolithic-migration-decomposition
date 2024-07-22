package edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemUpdateDTO {
    private int id;
    private String sku;
    private double price;
    private int quantityInStock;
    private UserDTO userDTO;
}
