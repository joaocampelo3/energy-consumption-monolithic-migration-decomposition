package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
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
class UserControllerTest {
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    UserDTO userDTO;
    HttpEntity<HttpHeaders> userRequestEntity;
    HttpHeaders headers;
    AuthenticationResponse authenticationResponse;
    ParameterizedTypeReference<Object> responseType;
    ResponseEntity<Object> mockResponseEntity;

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userDTO = new UserDTO(1, "merchant_email@gmail.com", RoleEnum.MERCHANT);

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        userRequestEntity = new HttpEntity<>(headers);

        authenticationResponse = new AuthenticationResponse(jwtTokenDummy);

        responseType = new ParameterizedTypeReference<>() {
        };
    }

    @Test
    void test_login() {
        mockResponseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);

        when(restTemplate.exchange(USERS_URL + "/users", HttpMethod.GET, userRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = userController.getUserId(jwtTokenDummy);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/users", HttpMethod.GET, userRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void test_loginFail() {
        mockResponseEntity = new ResponseEntity<>("User or Password not correct", HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(USERS_URL + "/users", HttpMethod.GET, userRequestEntity, responseType)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = userController.getUserId(jwtTokenDummy);

        verify(restTemplate, times(1)).exchange(USERS_URL + "/users", HttpMethod.GET, userRequestEntity, responseType);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User or Password not correct", response.getBody());
    }
}