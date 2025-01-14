package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
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
    AddressDTO addressDTO;
    UserDTO userDTO;
    MerchantDTO merchantDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        name = "Merchant 1";
        email = "merchantnumber1@gmail.com";
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        userDTO = new UserDTO(1, "admin@email.com", RoleEnum.ADMIN);
        merchantDTOExpected = new MerchantDTO(id, name, email, addressDTO.getId(), userDTO, addressDTO);
    }

    @Test
    void test_createMerchantDTO() {
        MerchantDTO merchantDTO = new MerchantDTO(id, name, email, addressDTO.getId(), userDTO, addressDTO);

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(addressDTO.getId(), merchantDTO.getAddressId());
        assertEquals(userDTO, merchantDTO.getUserDTO());
        assertEquals(addressDTO, merchantDTO.getAddressDTO());
        assertEquals(merchantDTOExpected.hashCode(), merchantDTO.hashCode());
    }

    @Test
    void test_createMerchantDTOBuilder() {
        MerchantDTO merchantDTO = MerchantDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .addressId(addressDTO.getId())
                .userDTO(userDTO)
                .addressDTO(addressDTO)
                .build();

        assertNotNull(merchantDTO);
        assertEquals(id, merchantDTO.getId());
        assertEquals(name, merchantDTO.getName());
        assertEquals(email, merchantDTO.getEmail());
        assertEquals(addressDTO.getId(), merchantDTO.getAddressId());
        assertEquals(userDTO, merchantDTO.getUserDTO());
        assertEquals(addressDTO, merchantDTO.getAddressDTO());
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
        result.setAddressId(addressDTO.getId());
        result.setUserDTO(userDTO);
        result.setAddressDTO(addressDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(addressDTO.getId(), result.getAddressId());
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(addressDTO, result.getAddressDTO());
        assertEquals(merchantDTOExpected.hashCode(), result.hashCode());
        assertEquals(merchantDTOExpected, result);
        assertEquals(merchantDTOExpected.toString(), result.toString());
    }
}
