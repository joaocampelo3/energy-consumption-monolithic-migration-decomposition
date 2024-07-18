package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTests {
    @InjectMocks
    AddressService addressService;
    @Mock
    AddressRepository addressRepository;
    AddressDTO addressDTO1;
    Address address1;
    Account account;
    User user;

    @BeforeEach
    void beforeEach() {
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

        addressDTO1 = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        address1 = addressDTO1.dtoToEntity();
        address1.setUser(user);
    }

    @Test
    void test_CreateAddress() throws NotFoundException {
        // Define the behavior of the mock
        when(addressRepository.existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(addressDTO1.getStreet(), addressDTO1.getZipCode(), addressDTO1.getCity(), addressDTO1.getCountry(), user.getId()))
                .thenReturn(false);
        when(addressRepository.save(address1)).thenReturn(address1);

        // Call the service method that uses the Repository
        Address result = addressService.createAddress(addressDTO1, user);
        Address expected = address1;

        // Perform assertions
        verify(addressRepository, atLeastOnce()).existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(addressDTO1.getStreet(), addressDTO1.getZipCode(), addressDTO1.getCity(), addressDTO1.getCountry(), user.getId());
        verify(addressRepository, atLeastOnce()).save(address1);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_CreateExistingAddress() throws NotFoundException {
        // Define the behavior of the mock
        when(addressRepository.existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(addressDTO1.getStreet(), addressDTO1.getZipCode(), addressDTO1.getCity(), addressDTO1.getCountry(), user.getId()))
                .thenReturn(true);
        when(addressRepository.findAddressByStreetAndZipCodeAndCityAndCountry(address1.getStreet(), address1.getZipCode(), address1.getCity(), address1.getCountry()))
                .thenReturn(Optional.ofNullable(address1));

        // Call the service method that uses the Repository
        Address result = addressService.createAddress(addressDTO1, user);
        Address expected = address1;

        // Perform assertions
        verify(addressRepository, atLeastOnce()).existsAddressByStreetAndZipCodeAndCityAndCountryAndUserId(addressDTO1.getStreet(), addressDTO1.getZipCode(), addressDTO1.getCity(), addressDTO1.getCountry(), user.getId());
        verify(addressRepository, atLeastOnce()).findAddressByStreetAndZipCodeAndCityAndCountry(address1.getStreet(), address1.getZipCode(), address1.getCity(), address1.getCountry());
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
