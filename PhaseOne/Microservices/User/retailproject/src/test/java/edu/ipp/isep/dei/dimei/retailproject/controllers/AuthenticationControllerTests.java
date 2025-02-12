package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.AuthenticationService;
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
class AuthenticationControllerTests {

    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    AuthenticationController authenticationController;
    @Mock
    AuthenticationService authenticationService;
    RegisterDTO registerDTO;
    LoginDTO loginDTO;
    AuthenticationResponse authenticationResponse;

    @BeforeEach
    void beforeEach() {
        registerDTO = RegisterDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .build();

        loginDTO = LoginDTO.builder()
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .build();

        authenticationResponse = AuthenticationResponse.builder()
                .token(jwtTokenDummy)
                .build();
    }

    @Test
    void test_RegsiterUser() {
        // Define the behavior of the mock
        when(authenticationService.register(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the Repository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.register(registerDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).register(registerDTO);
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);
    }

    @Test
    void test_RegsiterAdminUser() {
        // Define the behavior of the mock
        when(authenticationService.registerAdmin(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the Repository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.registerAdmin(registerDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).registerAdmin(registerDTO);
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);
    }

    @Test
    void test_RegsiterMerchantUser() {
        // Define the behavior of the mock
        when(authenticationService.registerMerchant(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the Repository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.registerMerchant(registerDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).registerMerchant(registerDTO);
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);
    }

    @Test
    void test_LoginUser() throws NotFoundException {
        // Define the behavior of the mock
        when(authenticationService.login(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the Repository
        ResponseEntity<Object> authenticationResponseResponseEntity = authenticationController.login(loginDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).login((loginDTO));
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);
    }

    @Test
    void test_LoginUserFail() throws NotFoundException {
        // Define the behavior of the mock
        when(authenticationService.login(any())).thenThrow(new NotFoundException("User or Password not correct"));

        // Call the service method that uses the Repository
        ResponseEntity<Object> authenticationResponseResponseEntity = authenticationController.login(loginDTO);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).login((loginDTO));
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(HttpStatus.NOT_FOUND, authenticationResponseResponseEntity.getStatusCode());
        assertEquals("User or Password not correct", authenticationResponseResponseEntity.getBody());
    }

}
