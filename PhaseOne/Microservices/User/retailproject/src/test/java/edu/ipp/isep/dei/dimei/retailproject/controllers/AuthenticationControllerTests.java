package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.AuthenticationService;
import edu.ipp.isep.dei.dimei.retailproject.services.UserService;
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

    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    AuthenticationController authenticationController;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    UserService userService;
    RegisterDTO registerDTO;
    LoginDTO loginDTO;
    User user;
    Account account;
    AuthenticationResponse authenticationResponse;
    UserDTO userDTO;

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

        account = Account.builder()
                .id(0)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .id(0)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        authenticationResponse = AuthenticationResponse.builder()
                .token(JwtTokenDummy)
                .build();

        userDTO = new UserDTO(user);
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
    void test_LoginUser() {
        // Define the behavior of the mock
        when(authenticationService.login(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the Repository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.login(loginDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        verify(authenticationService, atLeastOnce()).login((loginDTO));
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);
    }

    @Test
    void test_GetUserId() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserId(JwtTokenDummy)).thenReturn(userDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = authenticationController.getUserId(JwtTokenDummy);
        ResponseEntity<Object> expected = ResponseEntity.ok(userDTO);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserId(JwtTokenDummy);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserIdFail() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserId(JwtTokenDummy)).thenThrow(new NotFoundException("User not found."));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = authenticationController.getUserId(JwtTokenDummy);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserId(JwtTokenDummy);
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("User not found.", result.getBody());
    }

}
