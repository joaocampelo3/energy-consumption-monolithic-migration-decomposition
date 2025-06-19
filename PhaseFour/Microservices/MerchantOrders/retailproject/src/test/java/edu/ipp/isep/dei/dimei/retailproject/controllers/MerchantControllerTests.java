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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MerchantControllerTests {
    final String exceptionMerchantNotFound = "Merchant not found.";
    final String exceptionMerchantBadRequest = "Wrong merchant payload.";
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
    boolean isEvent;

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
                .addressId(addressDTO.getId())
                .build();

        merchantDTO2 = MerchantDTO.builder()
                .id(2)
                .name("Merchant 2")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO.getId())
                .build();

        merchantDTOS.add(merchantDTO1);
        merchantDTOS.add(merchantDTO2);

        merchantUpdateDTO = MerchantDTO.builder()
                .id(2)
                .name("Merchant 2 Changes")
                .email("merchant_email@gmail.com")
                .addressId(addressDTOUpdated.getId())
                .build();
        isEvent = false;
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
    void test_GetMerchantByIdFail() throws NotFoundException {
        int id = 1;
        // Define the behavior of the mock
        when(merchantService.getMerchant(id)).thenThrow(new NotFoundException(exceptionMerchantNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantController.getMerchantById(id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantService, atLeastOnce()).getMerchant(id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateMerchant() {
        // Define the behavior of the mock
        when(merchantService.createMerchant(merchantDTO1, isEvent)).thenReturn(merchantDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantResponseEntity = merchantController.createMerchant(merchantDTO1);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantDTO1, HttpStatus.CREATED);

        // Perform assertions
        verify(merchantService, atMostOnce()).createMerchant(merchantDTO1, isEvent);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_UpdateMerchant() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.updateMerchant(id, merchantUpdateDTO, isEvent)).thenReturn(merchantUpdateDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantResponseEntity = merchantController.updateMerchant(id, merchantUpdateDTO);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantUpdateDTO, HttpStatus.ACCEPTED);

        // Perform assertions
        verify(merchantService, atMostOnce()).updateMerchant(id, merchantUpdateDTO, isEvent);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_UpdateMerchantFail1() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.updateMerchant(id, merchantUpdateDTO, isEvent)).thenThrow(new NotFoundException(exceptionMerchantNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantController.updateMerchant(id, merchantUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantService, atLeastOnce()).updateMerchant(id, merchantUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_UpdateMerchantFail2() throws BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.updateMerchant(id, merchantUpdateDTO, isEvent)).thenThrow(new BadPayloadException(exceptionMerchantBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantController.updateMerchant(id, merchantUpdateDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(merchantService, atLeastOnce()).updateMerchant(id, merchantUpdateDTO, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeleteMerchant() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.deleteMerchant(id, isEvent)).thenReturn(merchantDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> merchantResponseEntity = merchantController.deleteMerchant(id);
        ResponseEntity<MerchantDTO> merchantResponseEntityExpected = new ResponseEntity<>(merchantDTO1, HttpStatus.OK);

        // Perform assertions
        verify(merchantService, atMostOnce()).deleteMerchant(id, isEvent);
        assertNotNull(merchantResponseEntity);
        assertEquals(merchantResponseEntityExpected, merchantResponseEntity);
    }

    @Test
    void test_DeleteMerchantFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(merchantService.deleteMerchant(id, isEvent)).thenThrow(new NotFoundException(exceptionMerchantNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = merchantController.deleteMerchant(id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionMerchantNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(merchantService, atLeastOnce()).deleteMerchant(id, isEvent);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }
}
