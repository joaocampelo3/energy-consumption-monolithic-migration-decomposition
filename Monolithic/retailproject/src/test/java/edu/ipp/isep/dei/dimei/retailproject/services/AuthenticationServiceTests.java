package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
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
    String firstname;
    String lastname;
    String email;
    String password;

    Account account;
    User user;
    User userExpected;
    LoginDTO loginDTO;

    @BeforeEach
    void beforeEach() {
        firstname = "John";
        lastname = "Doe";
        email = "johndoe1234@gmail.com";
        password = "johndoe_password";

        registerDTO = RegisterDTO.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .build();

        account = Account.builder()
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .account(account)
                .build();

        userExpected = User.builder()
                .id(1)
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .account(account)
                .build();
        loginDTO = LoginDTO.builder()
                .email(user.getAccount().getEmail())
                .password(user.getAccount().getPassword())
                .build();
    }

    @Test
    void test_registerAdmin() {
        // Define the behavior of the mock
        account.setRole(RoleEnum.ADMIN);
        when(userRepository.save(user)).thenReturn(userExpected);
        when(jwtService.generateToken(userExpected.getAccount(), userExpected.getId())).thenReturn(jwtTokenDummy);

        // Call the service method that uses the Repository
        AuthenticationResponse result = authenticationService.registerAdmin(registerDTO);
        AuthenticationResponse expected = new AuthenticationResponse(jwtTokenDummy);

        // Perform assertions
        verify(userRepository, atLeastOnce()).save(user);
        verify(jwtService, atLeastOnce()).generateToken(userExpected.getAccount(), userExpected.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_registerMerchant() {
        // Define the behavior of the mock
        account.setRole(RoleEnum.MERCHANT);
        when(userRepository.save(user)).thenReturn(userExpected);
        when(jwtService.generateToken(userExpected.getAccount(), userExpected.getId())).thenReturn(jwtTokenDummy);

        // Call the service method that uses the Repository
        AuthenticationResponse result = authenticationService.registerMerchant(registerDTO);
        AuthenticationResponse expected = new AuthenticationResponse(jwtTokenDummy);

        // Perform assertions
        verify(userRepository, atLeastOnce()).save(user);
        verify(jwtService, atLeastOnce()).generateToken(userExpected.getAccount(), userExpected.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_register() {
        // Define the behavior of the mock
        when(userRepository.save(user)).thenReturn(userExpected);
        when(jwtService.generateToken(userExpected.getAccount(), userExpected.getId())).thenReturn(jwtTokenDummy);

        // Call the service method that uses the Repository
        AuthenticationResponse result = authenticationService.register(registerDTO);
        AuthenticationResponse expected = new AuthenticationResponse(jwtTokenDummy);

        // Perform assertions
        verify(userRepository, atLeastOnce()).save(user);
        verify(jwtService, atLeastOnce()).generateToken(userExpected.getAccount(), userExpected.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_login() throws NotFoundException {
        // Define the behavior of the mock
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        )).thenReturn(any());
        when(userRepository.findByAccountEmail(loginDTO.getEmail())).thenReturn(Optional.ofNullable(userExpected));
        when(jwtService.generateToken(userExpected.getAccount(), userExpected.getId())).thenReturn(jwtTokenDummy);

        // Call the service method that uses the Repository
        AuthenticationResponse result = authenticationService.login(loginDTO);
        AuthenticationResponse expected = new AuthenticationResponse(jwtTokenDummy);

        // Perform assertions
        verify(authenticationManager, atLeastOnce()).authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        verify(jwtService, atLeastOnce()).generateToken(userExpected.getAccount(), userExpected.getId());
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_loginFail() {
        // Define the behavior of the mock
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        )).thenReturn(any());

        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            authenticationService.login(loginDTO);
        });

        // Perform assertions
        verify(authenticationManager, atLeastOnce()).authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        assertNotNull(result);
        assertEquals("User or Password not correct", result.getMessage());
    }

}
