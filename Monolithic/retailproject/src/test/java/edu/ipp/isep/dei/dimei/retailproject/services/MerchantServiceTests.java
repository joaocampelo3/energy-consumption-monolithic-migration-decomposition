package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
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
    @Mock
    UserService userService;
    @Mock
    AddressService addressService;
    MerchantDTO merchantDTO1;
    Merchant newMerchant1;
    Merchant merchant1;
    Merchant merchant1Updated;
    Account account1;
    User user1;
    Address address1;
    MerchantDTO merchantDTO2;
    Merchant merchant2;
    Account account2;
    User user2;
    Address address2;
    List<Merchant> merchantList = new ArrayList<>();
    List<MerchantDTO> merchantDTOList = new ArrayList<>();
    MerchantDTO merchantDTO1Updated;
    AddressDTO addressDTOUpdated;

    @BeforeEach
    void beforeEach() {
        account1 = Account.builder()
                .id(1)
                .email("merchant_email@gmail.com")
                .password("merchant_password")
                .role(RoleEnum.MERCHANT)
                .build();

        account2 = Account.builder()
                .id(2)
                .email("merchant_email2@gmail.com")
                .password("merchant_password2")
                .role(RoleEnum.MERCHANT)
                .build();

        user1 = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(account1)
                .build();

        user2 = User.builder()
                .id(2)
                .firstname("Another")
                .lastname("Doe")
                .account(account2)
                .build();

        address1 = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        address2 = Address.builder()
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
                .address(address1)
                .build();

        merchant1 = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(address1)
                .build();

        merchant1Updated = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(address1)
                .build();

        merchant2 = Merchant.builder()
                .id(2)
                .name("Merchant 2")
                .email("merchant_email2@gmail.com")
                .address(address2)
                .build();

        merchantList.add(merchant1);
        merchantList.add(merchant2);

        merchantDTO1 = new MerchantDTO(merchant1);
        merchantDTO2 = new MerchantDTO(merchant2);

        merchantDTOList.add(merchantDTO1);
        merchantDTOList.add(merchantDTO2);

        merchantDTO1Updated = merchantDTO1;
        addressDTOUpdated = merchantDTO1.getAddress();

    }

    @Test
    void test_GetMerchantByUser() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantRepository.findByEmail(user1.getAccount().getEmail())).thenReturn(Optional.ofNullable(merchant1));

        // Call the service method that uses the Repository
        Merchant result = merchantService.getMerchantByUser(user1);
        Merchant expected = merchant1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findByEmail(user1.getAccount().getEmail());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetAllMerchants() {
        // Define the behavior of the mock
        when(merchantRepository.findAll()).thenReturn(merchantList);

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

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.getMerchant(merchant1.getId());
        MerchantDTO expected = merchantDTO1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateMerchant() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.findByEmail(merchantDTO1.getEmail()))
                .thenReturn(user1);
        when(addressService.createAddress(merchantDTO1.getAddress(), user1))
                .thenReturn(address1);
        when(merchantRepository.save(newMerchant1))
                .thenReturn(merchant1);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.createMerchant(merchantDTO1);
        MerchantDTO expected = new MerchantDTO(merchant1);

        // Perform assertions
        verify(userService, atLeastOnce()).findByEmail(merchantDTO1.getEmail());
        verify(addressService, atLeastOnce()).createAddress(merchantDTO1.getAddress(), user1);
        verify(merchantRepository, atLeastOnce()).save(newMerchant1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_UpdateMerchant() throws NotFoundException, BadPayloadException {
        // Define the behavior of the mock
        merchantDTO1Updated.setName(merchantDTO1.getName() + " Changed");
        addressDTOUpdated.setStreet(merchantDTO1.getAddress().getStreet() + " Changed");
        merchantDTO1Updated.setAddress(addressDTOUpdated);
        merchant1Updated.setName(merchant1Updated.getName() + " Changed");
        merchant1Updated.getAddress().setStreet(merchant1Updated.getAddress().getStreet() + " Changed");

        when(merchantRepository.findById(merchant1.getId())).thenReturn(Optional.ofNullable(merchant1));
        when(userService.findByEmail(merchantDTO1.getEmail()))
                .thenReturn(user1);
        when(addressService.createAddress(merchantDTO1Updated.getAddress(), user1))
                .thenReturn(addressDTOUpdated.dtoToEntity());
        when(merchantRepository.save(merchant1Updated))
                .thenReturn(merchant1Updated);

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.updateMerchant(merchant1.getId(), merchantDTO1Updated);
        MerchantDTO expected = merchantDTO1Updated;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        verify(userService, atLeastOnce()).findByEmail(merchantDTO1.getEmail());
        verify(addressService, atLeastOnce()).createAddress(merchantDTO1.getAddress(), user1);
        verify(merchantRepository, atLeastOnce()).save(merchant1Updated);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_DeleteMerchant() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantRepository.findById(merchant1.getId())).thenReturn(Optional.ofNullable(merchant1));

        // Call the service method that uses the Repository
        MerchantDTO result = merchantService.deleteMerchant(merchant1.getId());
        MerchantDTO expected = merchantDTO1;

        // Perform assertions
        verify(merchantRepository, atLeastOnce()).findById(merchant1.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
