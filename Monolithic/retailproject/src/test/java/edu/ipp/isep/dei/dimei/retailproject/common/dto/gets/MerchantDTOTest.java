package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MerchantDTOTest {
    int id;
    String name;
    String email;
    AddressDTO address;
    MerchantDTO merchantDTOExpected;
    Merchant merchantExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchantnumber1@gmail.com";
        address = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        merchantDTOExpected = new MerchantDTO(id, name, email, address);
        merchantExpected = Merchant.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address.dtoToEntity())
                .build();
    }

    @Test
    void test_createMerchantDTO() {
        MerchantDTO merchantDTO = new MerchantDTO(id, name, email, address);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(address, merchantDTO.getAddress());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTOBuilder() {
        MerchantDTO merchantDTO = MerchantDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(address, merchantDTO.getAddress());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTONoArgsConstructor() {
        MerchantDTO merchantDTO = MerchantDTO.builder().build();
        assertNotNull(merchantDTO);
    }

    @Test
    void test_dtoToEntityMerchantDTO() {
        MerchantDTO merchantDTO = MerchantDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();

        Merchant merchant = merchantDTO.dtoToEntity();

        assertNotNull(merchant);
        assertEquals(id, merchant.getId());
        assertEquals(name, merchant.getName());
        assertEquals(email, merchant.getEmail());
        assertEquals(address.dtoToEntity(), merchant.getAddress());
        assertEquals(merchantExpected.hashCode(), merchant.hashCode());
    }
}
