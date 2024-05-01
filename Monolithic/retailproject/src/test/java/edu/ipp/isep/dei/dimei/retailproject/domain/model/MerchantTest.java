package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MerchantTest {
    int id;
    String name;
    String email;
    Address address;
    Merchant merchantExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        name = "Merchant 1";
        email = "merchant_email@gmail.com";

        address = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantExpected = Merchant.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();
    }

    @Test
    void test_createMerchant() {
        Merchant merchant = new Merchant(id, name, email, address);

        assertNotNull(merchant);
        assertEquals(id, merchant.getId());
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(address, merchant.getAddress());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }

    @Test
    void test_createMerchant2() {
        Merchant merchant = new Merchant(name, email, address);

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(address, merchant.getAddress());
        merchantExpected.setId(0);
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }

    @Test
    void test_createMerchantBuilder() {
        Merchant merchant = Merchant.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(address, merchant.getAddress());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }
}
