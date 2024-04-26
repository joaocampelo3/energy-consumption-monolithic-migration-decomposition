package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AddressDTOTest {
    int id;
    String street;
    String zipCode;
    String city;
    String country;
    AddressDTO addressDTOExpected;
    Address addressExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        street = "5th Avenue";
        zipCode = "10128";
        city = "New York";
        country = "USA";
        addressDTOExpected = new AddressDTO(id, street, zipCode, city, country);
        addressExpected = Address.builder()
                .id(id)
                .street(street)
                .zipCode(zipCode)
                .city(city)
                .country(country)
                .build();
    }

    @Test
    void test_createAddressDTO() {
        AddressDTO addressDTO = new AddressDTO(id, street, zipCode, city, country);

        assertNotNull(addressDTO);
        assertEquals(id, addressDTO.getId());
        assertEquals(street, addressDTO.getStreet());
        assertEquals(zipCode, addressDTO.getZipCode());
        assertEquals(city, addressDTO.getCity());
        assertEquals(country, addressDTO.getCountry());
        assertEquals(addressDTOExpected.hashCode(), addressDTO.hashCode());
    }

    @Test
    void test_createAddressDTOBuilder() {
        AddressDTO addressDTO = AddressDTO.builder()
                .id(id)
                .street(street)
                .zipCode(zipCode)
                .city(city)
                .country(country)
                .build();

        assertNotNull(addressDTO);
        assertEquals(id, addressDTO.getId());
        assertEquals(street, addressDTO.getStreet());
        assertEquals(zipCode, addressDTO.getZipCode());
        assertEquals(city, addressDTO.getCity());
        assertEquals(country, addressDTO.getCountry());
        assertEquals(addressDTOExpected.hashCode(), addressDTO.hashCode());
    }

    @Test
    void test_createAddressDTONoArgsConstructor() {
        AddressDTO addressDTO = AddressDTO.builder().build();
        assertNotNull(addressDTO);
    }

    @Test
    void test_dtoToEntityAddressDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
                .id(id)
                .street(street)
                .zipCode(zipCode)
                .city(city)
                .country(country)
                .build();

        Address address = addressDTO.dtoToEntity();

        assertNotNull(address);
        assertEquals(id, address.getId());
        assertEquals(street, address.getStreet());
        assertEquals(zipCode, address.getZipCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());
        assertEquals(addressExpected.hashCode(), address.hashCode());
    }

}
