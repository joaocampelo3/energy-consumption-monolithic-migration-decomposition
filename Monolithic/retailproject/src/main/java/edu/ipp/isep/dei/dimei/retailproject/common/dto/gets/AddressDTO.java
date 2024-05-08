package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
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

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.street = address.getStreet();
        this.zipCode = address.getZipCode();
        this.city = address.getCity();
        this.country = address.getCountry();
    }

    public Address dtoToEntity() {
        return Address.builder()
                .id(this.id)
                .street(this.street)
                .zipCode(this.zipCode)
                .city(this.city)
                .country(this.country)
                .build();
    }
}
