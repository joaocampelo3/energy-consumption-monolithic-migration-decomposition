package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTests {
    @InjectMocks
    MerchantService merchantService;
    @Mock
    MerchantRepository merchantRepository;
    MerchantDTO merchantDTO1;
    Merchant newMerchant1;
    Merchant merchant1;
    Merchant merchant1Updated;
    UserDTO userDTO1;
    AddressDTO addressDTO1;
    MerchantDTO merchantDTO2;
    Merchant merchant2;
    AddressDTO addressDTO2;
    List<Merchant> merchantList = new ArrayList<>();
    List<MerchantDTO> merchantDTOList = new ArrayList<>();
    MerchantDTO merchantDTO1Updated;
    AddressDTO addressDTOUpdated;
    boolean isEvent;

    @BeforeEach
    void beforeEach() {
        userDTO1 = UserDTO.builder()
                .userId(1)
                .email("merchant_email@gmail.com")
                .role(RoleEnum.MERCHANT)
                .build();

        addressDTO1 = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        addressDTO2 = AddressDTO.builder()
                .id(2)
                .street("Different Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        newMerchant1 = Merchant.builder()
                .id(0)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO1.getId())
                .build();

        merchant1 = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO1.getId())
                .build();

        merchant1Updated = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO1.getId())
                .build();

        merchant2 = Merchant.builder()
                .id(2)
                .name("Merchant 2")
                .email("merchant_email2@gmail.com")
                .addressId(addressDTO2.getId())
                .build();

        merchantList.add(merchant1);
        merchantList.add(merchant2);

        merchantDTO1 = new MerchantDTO(merchant1);
        merchantDTO1.setAddressDTO(addressDTO1);
        merchantDTO2 = new MerchantDTO(merchant2);
        merchantDTO2.setAddressDTO(addressDTO2);

        merchantDTOList.add(merchantDTO1);
        merchantDTOList.add(merchantDTO2);

        merchantDTO1Updated = merchantDTO1;
        addressDTOUpdated = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();
        isEvent = false;
    }

    @Test
    void test_GetMerchantByUser() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantRepository.findByEmail(userDTO1.getEmail())).thenReturn(Optional.ofNullable(merchant1));

        // Call the service method that uses the Repository
        Merchant result = merchantService.getMerchantByUser(userDTO1);
        Merchant expected = merchant1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findByEmail(userDTO1.getEmail());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetAllMerchants() {
        // Define the behavior of the mock
        when(merchantRepository.findAll()).thenReturn(merchantList);
        merchantDTO1.setAddressDTO(null);
        merchantDTO2.setAddressDTO(null);

        // Call the service method that uses the Repository
        List<MerchantDTO> result = merchantService.getAllMerchants();
        List<MerchantDTO> expected = merchantDTOList;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findAll();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetMerchant() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantRepository.findById(merchant1.getId())).thenReturn(Optional.ofNullable(merchant1));
        merchantDTO1.setAddressDTO(null);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.getMerchant(merchant1.getId());
        MerchantDTO expected = merchantDTO1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateMerchant() {
        // Define the behavior of the mock
        when(merchantRepository.save(newMerchant1))
                .thenReturn(merchant1);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.createMerchant(merchantDTO1, isEvent);
        MerchantDTO expected = new MerchantDTO(merchant1);

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).save(newMerchant1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_UpdateMerchant() throws NotFoundException, BadPayloadException {
        // Define the behavior of the mock
        merchantDTO1Updated.setName(merchantDTO1Updated.getName() + " Changed");
        addressDTOUpdated.setStreet(addressDTOUpdated.getStreet() + " Changed");
        merchantDTO1Updated.setAddressId(addressDTOUpdated.getId());
        merchant1Updated.setName(merchant1Updated.getName() + " Changed");
        merchant1Updated.setAddressId(addressDTOUpdated.getId());

        when(merchantRepository.findById(merchant1.getId())).thenReturn(Optional.ofNullable(merchant1));
        when(merchantRepository.save(merchant1Updated))
                .thenReturn(merchant1Updated);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.updateMerchant(merchant1.getId(), merchantDTO1Updated, isEvent);
        merchantDTO1Updated.setAddressDTO(null);
        MerchantDTO expected = merchantDTO1Updated;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        verify(merchantRepository, atLeastOnce()).save(merchant1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteMerchant() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantRepository.findById(merchant1.getId())).thenReturn(Optional.ofNullable(merchant1));
        merchantDTO1.setAddressDTO(null);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.deleteMerchant(merchant1.getId(), isEvent);
        MerchantDTO expected = merchantDTO1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
