package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MerchantDTOTest {
    int id;
    String name;
    String email;
    int addressId;
    MerchantDTO merchantDTOExpected;
    UserDTO userDTO;
    AddressDTO addressDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchantnumber1@gmail.com";
        addressId = 1;
        userDTO = UserDTO.builder()
                .userId(2)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantDTOExpected = new MerchantDTO(id, name, email, addressId, userDTO, addressDTO);
        merchantDTOExpected.setUserDTO(userDTO);
    }

    @Test
    void test_createMerchantDTO() {
        MerchantDTO merchantDTO = new MerchantDTO(id, name, email, addressId, userDTO, addressDTO);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(addressId, merchantDTO.getAddressId());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTOBuilder() {
        MerchantDTO merchantDTO = MerchantDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .addressId(addressId)
                .userDTO(userDTO)
                .addressDTO(addressDTO)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(addressId, merchantDTO.getAddressId());
        assertEquals(merchantDTOExpected, merchantDTO);
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
        result.setAddressId(addressId);
        result.setUserDTO(userDTO);
        result.setAddressDTO(addressDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(addressId, result.getAddressId());
        assertEquals(merchantDTOExpected, result);
        assertEquals(merchantDTOExpected.hashCode(), result.hashCode());
        assertEquals(merchantDTOExpected.toString(), result.toString());
    }
}
