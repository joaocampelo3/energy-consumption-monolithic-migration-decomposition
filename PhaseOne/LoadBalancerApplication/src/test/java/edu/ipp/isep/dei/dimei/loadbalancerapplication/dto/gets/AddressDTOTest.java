package edu.ipp.isep.dei.dimei.loadbalancerapplication.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AddressDTOTest {
    int id;
    String street;
    String zipCode;
    String city;
    String country;
    AddressDTO addressDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        street = "5th Avenue";
        zipCode = "10128";
        city = "New York";
        country = "USA";
        addressDTOExpected = new AddressDTO(id, street, zipCode, city, country);
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
    void test_SetsAddressDTO() {
        AddressDTO result = AddressDTO.builder().build();

        result.setId(id);
        result.setStreet(street);
        result.setZipCode(zipCode);
        result.setCity(city);
        result.setCountry(country);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(street, result.getStreet());
        assertEquals(zipCode, result.getZipCode());
        assertEquals(city, result.getCity());
        assertEquals(country, result.getCountry());
        assertEquals(addressDTOExpected.hashCode(), result.hashCode());
        assertEquals(addressDTOExpected, result);
        assertEquals(addressDTOExpected.toString(), result.toString());
    }

}
