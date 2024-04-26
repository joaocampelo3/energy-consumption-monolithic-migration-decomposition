package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AddressTest {
    int id;
    String street;
    String zipCode;
    String city;
    String country;
    Address addressExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        street = "5th Avenue";
        zipCode = "10128";
        city = "New York";
        country = "USA";
        addressExpected = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
    }

    @Test
    void test_createAccount() {
        Address address = new Address(street, zipCode, country, city);

        assertNotNull(address);
        assertEquals(street, address.getStreet());
        assertEquals(zipCode, address.getZipCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());
        addressExpected.setId(0);
        assertEquals(addressExpected.hashCode(), address.hashCode());
        assertEquals(addressExpected, address);
    }

    @Test
    void test_createAccountBuilder() {
        Address address = Address.builder()
                .id(id)
                .street(street)
                .zipCode(zipCode)
                .city(city)
                .country(country)
                .build();

        assertNotNull(address);
        assertEquals(id, address.getId());
        assertEquals(street, address.getStreet());
        assertEquals(zipCode, address.getZipCode());
        assertEquals(city, address.getCity());
        assertEquals(country, address.getCountry());
        assertEquals(addressExpected.hashCode(), address.hashCode());
        assertEquals(addressExpected, address);
    }
}
