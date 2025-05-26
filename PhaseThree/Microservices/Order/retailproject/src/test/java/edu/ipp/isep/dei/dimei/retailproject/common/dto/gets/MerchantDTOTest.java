package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MerchantDTOTest {
    int id;
    String name;
    String email;
    MerchantDTO merchantDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchantnumber1@gmail.com";
        merchantDTOExpected = new MerchantDTO(id, name, email);
    }

    @Test
    void test_createMerchantDTO() {
        MerchantDTO merchantDTO = new MerchantDTO(id, name, email);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTOBuilder() {
        MerchantDTO merchantDTO = MerchantDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTONoArgsConstructor() {
        MerchantDTO merchantDTO = MerchantDTO.builder().build();
        assertNotNull(merchantDTO);
    }

    @Test
    void test_SetsMerchantDTO() {
        MerchantDTO result = MerchantDTO.builder().build();

        result.setId(id);
        result.setName(name);
        result.setEmail(email);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(merchantDTOExpected.hashCode(), result.hashCode());
        assertEquals(merchantDTOExpected, result);
        assertEquals(merchantDTOExpected.toString(), result.toString());
    }
}
