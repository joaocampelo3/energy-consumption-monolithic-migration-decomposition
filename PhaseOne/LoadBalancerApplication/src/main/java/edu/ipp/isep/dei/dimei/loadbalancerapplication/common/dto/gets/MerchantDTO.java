package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MerchantDTO {
    private int id;
    private String name;
    private String email;
    private int addressId;
    private UserDTO userDTO;
    @JsonProperty("address")
    private AddressDTO addressDTO;
}
