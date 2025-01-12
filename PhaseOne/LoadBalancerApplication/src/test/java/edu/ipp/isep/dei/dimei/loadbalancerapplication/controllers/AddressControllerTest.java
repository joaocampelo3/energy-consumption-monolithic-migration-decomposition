package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.POST;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class AddressControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AddressController addressController;

    public AddressControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAddress_success() {
        // Arrange
        String authorizationToken = "Bearer testToken";
        AddressDTO addressDTO = new AddressDTO(1, "Street 1", "1234", "City 1", "Country1"); // Populate with necessary fields if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Mocked response object
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("id", 1);
        mockResponse.put("street", "Street 1");
        mockResponse.put("zipCode", "1234");
        mockResponse.put("city", "City 1");
        mockResponse.put("country", "Country1");

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(USERS_URL + "/addresses"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class))).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = addressController.createAddress(authorizationToken, addressDTO);

        verify(restTemplate, times(1)).exchange( eq(USERS_URL + "/addresses"), eq(HttpMethod.POST), any(HttpEntity.class),  eq(Object.class));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void createAddress_clientError() {
        // Arrange
        String authorizationToken = "Bearer testToken";
        AddressDTO addressDTO = new AddressDTO(1, "Street 1", "1234", "City 1", "Country1"); // Populate with necessary fields if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddressDTO> requestEntity = new HttpEntity<>(addressDTO, headers);

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        when(restTemplate.exchange(eq(USERS_URL + "/addresses"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class))).thenThrow(exception);

        // Act
        ResponseEntity<Object> response = addressController.createAddress(authorizationToken, addressDTO);

        // Assert
        verify(restTemplate, times(1)).exchange(USERS_URL + "/addresses", POST, requestEntity, Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400 Bad Request", response.getBody());
    }
}