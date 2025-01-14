package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    LoginDTO loginDTO;
    HttpEntity<LoginDTO> loginRequestEntity;
    RegisterDTO registerDTO;
    HttpEntity<RegisterDTO> registerRequestEntity;
    HttpHeaders headers;
    AuthenticationResponse authenticationResponse;
    ParameterizedTypeReference<Object> responseType;
    ResponseEntity<Object> mockResponseEntity;

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void beforeEach() {
        loginDTO = new LoginDTO("merchant_email@gmail.com", "merchant_password");
        registerDTO = new RegisterDTO("Merchant", "First", "merchant_email@gmail.com", "merchant_password");

        loginRequestEntity = new HttpEntity<>(loginDTO);
        registerRequestEntity = new HttpEntity<>(registerDTO);

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        authenticationResponse = new AuthenticationResponse(jwtTokenDummy);

        responseType = new ParameterizedTypeReference<>() {
        };
    }

    @Test
    void test_login() {
        mockResponseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/login", HttpMethod.POST, loginRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.login(loginDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/login", HttpMethod.POST, loginRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void test_loginFail() {
        mockResponseEntity = new ResponseEntity<>("User or Password not correct", HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/login", HttpMethod.POST, loginRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.login(loginDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/login", HttpMethod.POST, loginRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User or Password not correct", response.getBody());
    }

    @Test
    void test_register() {
        mockResponseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.register(registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void test_registerFail() {
        mockResponseEntity = new ResponseEntity<>("User or Password not correct", HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.register(registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User or Password not correct", response.getBody());
    }

    @Test
    void test_registerAdmin() {
        mockResponseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        registerRequestEntity = new HttpEntity<>(registerDTO, headers);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register/admin", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.registerAdmin(jwtTokenDummy, registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register/admin", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void test_registerAdminFail() {
        mockResponseEntity = new ResponseEntity<>("User or Password not correct", HttpStatus.NOT_FOUND);
        registerRequestEntity = new HttpEntity<>(registerDTO, headers);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register/admin", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.registerAdmin(jwtTokenDummy, registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register/admin", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User or Password not correct", response.getBody());
    }

    @Test
    void test_registerMerchant() {
        mockResponseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        registerRequestEntity = new HttpEntity<>(registerDTO, headers);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register/merchant", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.registerMerchant(jwtTokenDummy, registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register/merchant", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void test_registerMerchantFail() {
        mockResponseEntity = new ResponseEntity<>("User or Password not correct", HttpStatus.NOT_FOUND);
        registerRequestEntity = new HttpEntity<>(registerDTO, headers);

        when(restTemplate.exchange(USERS_URL + "/auth" + "/register/merchant", HttpMethod.POST, registerRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = authenticationController.registerMerchant(jwtTokenDummy, registerDTO);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/auth" + "/register/merchant", HttpMethod.POST, registerRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User or Password not correct", response.getBody());
    }
}