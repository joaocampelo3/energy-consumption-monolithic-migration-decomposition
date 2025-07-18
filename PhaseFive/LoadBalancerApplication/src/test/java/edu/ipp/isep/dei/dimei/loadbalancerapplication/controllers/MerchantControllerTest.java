package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_READ_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantControllerTest {
    @Mock
    RestTemplate restTemplate = new RestTemplate();
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    HttpHeaders headers;
    HttpEntity<UserDTO> requestUserEntity;
    HttpEntity<UserDTO> requestEntity;
    @InjectMocks
    MerchantController merchantController;
    @Mock
    UserController userController;
    @Mock
    AddressController addressController;
    MerchantDTO merchantDTO;
    List<MerchantDTO> merchantDTOS = new ArrayList<>();
    UserDTO userDTO;
    AddressDTO addressDTO;
    LinkedHashMap<String, Object> mockUserControllerResponse;
    LinkedHashMap<String, Object> mockAddressControllerResponse;

    @BeforeEach
    void beforeEach() {
        userDTO = UserDTO.builder().userId(1).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        addressDTO = AddressDTO.builder().id(0).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        merchantDTO = MerchantDTO.builder().id(0).name("Merchant 1").email("merchant_email@gmail.com").addressId(addressDTO.getId()).userDTO(userDTO).addressDTO(addressDTO).build();

        merchantDTOS.add(merchantDTO);

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        requestUserEntity = new HttpEntity<>(headers);

        mockUserControllerResponse = new LinkedHashMap<>();
        mockUserControllerResponse.put("userId", userDTO.getUserId());
        mockUserControllerResponse.put("email", userDTO.getEmail());
        mockUserControllerResponse.put("role", userDTO.getRole().name());

        mockAddressControllerResponse = new LinkedHashMap<>();
        mockAddressControllerResponse.put("id", addressDTO.getId());
        mockAddressControllerResponse.put("street", addressDTO.getStreet());
        mockAddressControllerResponse.put("zipCode", addressDTO.getZipCode());
        mockAddressControllerResponse.put("city", addressDTO.getCity());
        mockAddressControllerResponse.put("country", addressDTO.getCountry());
    }

    @Test
    void test_GetAllMerchants() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantDTOS, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantController.getAllMerchants(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantDTOS, response.getBody());
    }

    @Test
    void test_GetAllMerchantsFail1() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(MERCHANT_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantController.getAllMerchants(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllMerchantsFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantController.getAllMerchants(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateMerchant() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MerchantDTO> requestMerchantEntity = new HttpEntity<>(merchantDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantDTOS, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_URL, HttpMethod.POST, requestMerchantEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantController.createMerchant(jwtTokenDummy, merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(MERCHANT_URL, HttpMethod.POST, requestMerchantEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantDTOS, response.getBody());
    }

    @Test
    void test_CreateMerchantFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MerchantDTO> requestMerchantEntity = new HttpEntity<>(merchantDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        when(restTemplate.exchange(MERCHANT_URL, HttpMethod.POST, requestMerchantEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantController.createMerchant(jwtTokenDummy, merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(MERCHANT_URL, HttpMethod.POST, requestMerchantEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_CreateMerchantFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> response = merchantController.createMerchant(jwtTokenDummy, merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateMerchantFail3() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantController.createMerchant(jwtTokenDummy, merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetMerchantById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantDTO, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_READ_URL + "/" + merchantDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantController.getMerchantById(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_READ_URL + "/" + merchantDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantDTO, response.getBody());
    }

    @Test
    void test_GetMerchantByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(MERCHANT_READ_URL + "/" + merchantDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantController.getMerchantById(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_READ_URL + "/" + merchantDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetMerchantByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantController.getMerchantById(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_DeleteMerchant() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantDTO, HttpStatus.OK);
        String url = MERCHANT_URL + "/" + merchantDTO.getId();
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantController.deleteMerchant(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantDTO, response.getBody());
    }

    @Test
    void test_DeleteMerchantFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        String url = MERCHANT_URL + "/" + merchantDTO.getId();
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantController.deleteMerchant(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_DeleteMerchantFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantController.deleteMerchant(jwtTokenDummy, merchantDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_UpdateMerchant() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantDTO, HttpStatus.OK);

        HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO, headers);

        String url = MERCHANT_URL + "/" + merchantDTO.getId();
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantController.updateMerchant(jwtTokenDummy, merchantDTO.getId(), merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantDTO, response.getBody());
    }

    @Test
    void test_UpdateMerchantFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO, headers);

        String url = MERCHANT_URL + "/" + merchantDTO.getId();
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantController.updateMerchant(jwtTokenDummy, merchantDTO.getId(), merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_UpdateMerchantFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> response = merchantController.updateMerchant(jwtTokenDummy, merchantDTO.getId(), merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_UpdateMerchantFail3() {
        // Mocked response object
        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantController.updateMerchant(jwtTokenDummy, merchantDTO.getId(), merchantDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}
