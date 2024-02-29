package edu.ipp.isep.dei.dimei.retailproject.common.dto;

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
    private String country;
    private String city;
    private int customerId;
    private String email;
}
