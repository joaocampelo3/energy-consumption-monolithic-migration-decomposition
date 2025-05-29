package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AddressDTO {
    private int id;
    private String street;
    private String zipCode;
    private String city;
    private String country;
}
