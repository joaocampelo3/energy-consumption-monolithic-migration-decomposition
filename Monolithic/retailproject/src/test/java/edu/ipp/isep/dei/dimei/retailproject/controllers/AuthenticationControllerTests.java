package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import edu.ipp.isep.dei.dimei.retailproject.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTests {

    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    AuthenticationController authenticationController;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    UserRepository userRepository;
    @Mock
    JwtService jwtService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder passwordEncoder;
    RegisterDTO registerDTO;
    LoginDTO loginDTO;
    User user;
    Account account;
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
    }

    @Test
    void test_RegsiterUser() {
        // Define the behavior of the mock
        when(authenticationService.register(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the UserRepository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.register(registerDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);

        verify(authenticationService, atLeastOnce()).register(registerDTO);
    }

    @Test
    void test_LoginUser() {
        // Define the behavior of the mock
        when(authenticationService.login(any())).thenReturn(authenticationResponse);

        // Call the service method that uses the UserRepository
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntity = authenticationController.login(loginDTO);
        ResponseEntity<AuthenticationResponse> authenticationResponseResponseEntityExpected = ResponseEntity.ok(authenticationResponse);

        // Perform assertions
        assertNotNull(authenticationResponseResponseEntity);
        assertEquals(authenticationResponseResponseEntityExpected, authenticationResponseResponseEntity);

        verify(authenticationService, atLeastOnce()).login((loginDTO));
    }

}
