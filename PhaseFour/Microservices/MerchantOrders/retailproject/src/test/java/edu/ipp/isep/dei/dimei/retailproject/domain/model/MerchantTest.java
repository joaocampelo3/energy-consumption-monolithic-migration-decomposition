package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MerchantTest {
    int id;
    String name;
    String email;
    AddressDTO addressDTO;
    Merchant merchantExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchant_email@gmail.com";

        addressDTO = AddressDTO.builder()
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
                .addressId(addressDTO.getId())
                .build();
    }

    @Test
    void test_createMerchant() {
        Merchant merchant = new Merchant(id, name, email, addressDTO.getId());

        assertNotNull(merchant);
        assertEquals(id, merchant.getId());
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressDTO.getId(), merchant.getAddressId());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }

    @Test
    void test_createMerchant2() {
        Merchant merchant = new Merchant(name, email, addressDTO.getId());

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressDTO.getId(), merchant.getAddressId());
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
                .addressId(addressDTO.getId())
                .build();

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressDTO.getId(), merchant.getAddressId());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }
}
