package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {
    private int id;
    private String street;
    private String zipCode;
    private String city;
    private String country;
}
