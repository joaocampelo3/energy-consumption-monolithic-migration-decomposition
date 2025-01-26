package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    AddressDTO addressDTO;
    HttpHeaders headers;
    HttpEntity<AddressDTO> requestEntity;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AddressController addressController;

    @BeforeEach
    void beforeEach() {
        addressDTO = new AddressDTO(1, "Street 1", "1234", "City 1", "Country1");

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        requestEntity = new HttpEntity<>(addressDTO, headers);
    }

    @Test
    void createAddress_success() {
        // Mocked response object
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("id", 1);
        mockResponse.put("street", "Street 1");
        mockResponse.put("zipCode", "1234");
        mockResponse.put("city", "City 1");
        mockResponse.put("country", "Country1");

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(USERS_URL + "/addresses", HttpMethod.POST, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = addressController.createAddress(jwtTokenDummy, addressDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/addresses", HttpMethod.POST, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void createAddress_clientError() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        when(restTemplate.exchange(USERS_URL + "/addresses", HttpMethod.POST, requestEntity, Object.class)).thenThrow(exception);

        // Act
        ResponseEntity<Object> response = addressController.createAddress(jwtTokenDummy, addressDTO);

        // Assert
        verify(restTemplate, times(1)).exchange(USERS_URL + "/addresses", POST, requestEntity, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400 Bad Request", response.getBody());
    }
}