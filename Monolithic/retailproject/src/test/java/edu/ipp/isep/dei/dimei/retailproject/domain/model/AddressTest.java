package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AddressTest {
    int id;
    String street;
    String zipCode;
    String city;
    String country;
    Address addressExpected;
    Account account;
    User user;

    @BeforeEach
    void beforeEach() {
        id = 1;
        street = "5th Avenue";
        zipCode = "10128";
        city = "New York";
        country = "USA";
        account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();
        user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();
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

    @Test
    void test_AccountSets() {
        addressExpected.setUser(user);
        Address address = Address.builder().build();

        address.setId(id);
        address.setStreet(street);
        address.setZipCode(zipCode);
        address.setCity(city);
        address.setCountry(country);
        address.setUser(user);

        assertEquals(addressExpected.getUser(), address.getUser());
        assertTrue(addressExpected.equals(address) && address.equals(addressExpected));
        assertEquals(addressExpected.hashCode(), address.hashCode());
        assertEquals(addressExpected.toString(), address.toString());
    }
}
