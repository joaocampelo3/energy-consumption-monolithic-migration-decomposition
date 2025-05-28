package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MerchantTest {
    int id;
    String name;
    String email;
    int addressId;
    Merchant merchantExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchant_email@gmail.com";
        addressId = 1;

        merchantExpected = Merchant.builder()
                .id(id)
                .name(name)
                .email(email)
                .addressId(addressId)
                .build();
    }

    @Test
    void test_createMerchant() {
        Merchant merchant = new Merchant(id, name, email, addressId);

        assertNotNull(merchant);
        assertEquals(id, merchant.getId());
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressId, merchant.getAddressId());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }

    @Test
    void test_createMerchant2() {
        Merchant merchant = new Merchant(name, email, addressId);

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressId, merchant.getAddressId());
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
                .addressId(addressId)
                .build();

        assertNotNull(merchant);
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(addressId, merchant.getAddressId());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
        assertEquals(merchantExpected, merchant);
    }
}
