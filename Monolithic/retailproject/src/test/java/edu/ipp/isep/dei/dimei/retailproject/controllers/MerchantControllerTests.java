package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantControllerTests {
    @InjectMocks
    MerchantController merchantController;
    @Mock
    MerchantService merchantService;
    MerchantDTO merchantDTO1;
    MerchantDTO merchantDTO2;
    List<MerchantDTO> merchantDTOS = new ArrayList<>();
    MerchantDTO merchantUpdateDTO;
    AddressDTO addressDTO;
    AddressDTO addressDTOUpdated;

    @BeforeEach
    void beforeEach() {
        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        addressDTOUpdated = AddressDTO.builder()
                .id(2)
                .street("Different Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantDTO1 = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("johndoe1234@gmail.com")
                .address(addressDTO)
                .build();

        merchantDTO2 = MerchantDTO.builder()
                .id(2)
                .name("Merchant 2")
                .email("merchant_email@gmail.com")
                .address(addressDTO)
                .build();

        merchantDTOS.add(merchantDTO1);
        merchantDTOS.add(merchantDTO2);

        merchantUpdateDTO = MerchantDTO.builder()
                .id(2)
                .name("Merchant 2 Changes")
                .email("merchant_email@gmail.com")
                .address(addressDTOUpdated)
                .build();
    }

    @Test
    void test_GetAllMerchants() {
        // Define the behavior of the mock
        when(merchantService.getAllMerchants()).thenReturn(merchantDTOS);

        // Call the service method that uses the Repository
        ResponseEntity<?> merchantResponseEntity = merchantController.getAllMerchants();
        ResponseEntity<List<MerchantDTO>> merchantResponseEntityExpected = ResponseEntity.ok(merchantDTOS);

        // Perform assertions
        verify(merchantService, atLeastOnce()).getAllMerchants();
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_GetMerchantById() throws NotFoundException {
        int id = 1;
        // Define the behavior of the mock
        when(merchantService.getMerchant(id)).thenReturn(merchantDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> merchantResponseEntity = merchantController.getMerchantById(id);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = ResponseEntity.ok(merchantDTO1);

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchant(id);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_CreateMerchant() throws NotFoundException {
        // Define the behavior of the mock
        when(merchantService.createMerchant(merchantDTO1)).thenReturn(merchantDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> merchantResponseEntity = merchantController.createMerchant(merchantDTO1);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantDTO1, HttpStatus.CREATED);

        // Perform assertions
        verify(merchantService, atMostOnce()).createMerchant(merchantDTO1);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_UpdateMerchant() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.updateMerchant(id, merchantUpdateDTO)).thenReturn(merchantUpdateDTO);

        // Call the service method that uses the Repository
        ResponseEntity<?> merchantResponseEntity = merchantController.updateMerchant(id, merchantUpdateDTO);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantUpdateDTO, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantService, atMostOnce()).updateMerchant(id, merchantUpdateDTO);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_DeleteMerchant() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.deleteMerchant(id)).thenReturn(merchantDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> merchantResponseEntity = merchantController.deleteMerchant(id);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantDTO1, HttpStatus.OK);

        // Perform assertions
        verify(merchantService, atMostOnce()).deleteMerchant(id);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

}
