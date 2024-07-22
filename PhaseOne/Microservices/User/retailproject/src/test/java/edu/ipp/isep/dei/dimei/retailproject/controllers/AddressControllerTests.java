package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTests {

    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    AddressController addressController;
    @Mock
    AddressService addressService;
    AddressDTO addressDTO;

    @BeforeEach
    void beforeEach() {
        int id = 1;
        String street = "5th Avenue";
        String zipCode = "10128";
        String city = "New York";
        String country = "USA";

        addressDTO = new AddressDTO(id, street, zipCode, city, country);
    }

    @Test
    void test_GetUserId() throws NotFoundException {
        // Define the behavior of the mock
        when(addressService.createAddress(JwtTokenDummy, addressDTO)).thenReturn(addressDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = addressController.createAddress(JwtTokenDummy, addressDTO);
        ResponseEntity<Object> expected = ResponseEntity.ok(addressDTO);

        // Perform assertions
        verify(addressService, atLeastOnce()).createAddress(JwtTokenDummy, addressDTO);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserIdFail() throws NotFoundException {
        // Define the behavior of the mock
        when(addressService.createAddress(JwtTokenDummy, addressDTO)).thenThrow(new NotFoundException("User not found."));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = addressController.createAddress(JwtTokenDummy, addressDTO);

        // Perform assertions
        verify(addressService, atLeastOnce()).createAddress(JwtTokenDummy, addressDTO);
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("User not found.", result.getBody());
    }

}
