package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
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

import java.util.Optional;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    static final String EXCEPTION_NOT_FOUND = "User not found.";
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    JwtService jwtService;
    int accountId = 1;
    String email;
    String password;
    RoleEnum role = RoleEnum.USER;
    Account account;
    int userId = 1;
    String firstname;
    String lastname;
    User user;
    User userExpected;
    UserDTO userDTOExpected;

    @BeforeEach
    void beforeEach() {
        accountId = 1;
        email = "johndoe1234@gmail.com";
        password = "johndoe_password";
        account = Account.builder()
                .id(accountId)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(role)
                .build();

        userId = 1;
        firstname = "John";
        lastname = "Doe";

        user = User.builder()
                .id(userId)
                .firstname(firstname)
                .lastname(lastname)
                .account(account)
                .build();

        userExpected = User.builder()
                .id(userId)
                .firstname(firstname)
                .lastname(lastname)
                .account(account)
                .build();

        userDTOExpected = new UserDTO(userExpected);
    }

    @Test
    void test_GetUserByToken() throws NotFoundException {
        // Define the behavior of the mock
        when(jwtService.extractClaims(anyString(), any())).thenReturn(email);
        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.ofNullable(user));

        // Call the service method that uses the Repository
        User result = userService.getUserByToken(jwtTokenDummy);
        User expected = userExpected;

        // Perform assertions
        verify(jwtService, atLeastOnce()).extractClaims(anyString(), any());
        verify(userRepository, atLeastOnce()).findByAccountEmail(email);
        assertNotNull(result);
        assertEquals(email, result.getAccount().getEmail());
        assertEquals(expected.getAccount().getEmail(), result.getAccount().getEmail());
        assertEquals(expected, result);
    }

    @Test
    void test_GetEmailFromAuthorizationString() {
        // Define the behavior of the mock
        when(jwtService.extractClaims(anyString(), any())).thenReturn(email);

        // Call the service method that uses the Repository
        String result = userService.getEmailFromAuthorizationString(jwtTokenDummy);
        String expected = email;

        // Perform assertions
        verify(jwtService, atLeastOnce()).extractClaims(anyString(), any());
        assertNotNull(result);
        assertEquals(email, result);
        assertEquals(expected, result);
    }

    @Test
    void test_FindByEmail() throws NotFoundException {
        // Define the behavior of the mock
        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.ofNullable(user));

        // Call the service method that uses the Repository
        User result = userService.findByEmail(email);
        User expected = userExpected;

        // Perform assertions
        verify(userRepository, atLeastOnce()).findByAccountEmail(email);
        assertNotNull(result);
        assertEquals(email, result.getAccount().getEmail());
        assertEquals(expected.getAccount().getEmail(), result.getAccount().getEmail());
        assertEquals(expected, result);
    }

    @Test
    void test_FindByEmailFail() {
        // Call the service method that uses the Repository
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            userService.findByEmail(email);
        });

        assertNotNull(result);
        assertEquals(EXCEPTION_NOT_FOUND, result.getMessage());
    }

    @Test
    void test_GetUserId() throws NotFoundException {
        // Define the behavior of the mock
        when(jwtService.extractClaims(anyString(), any())).thenReturn(email);
        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.ofNullable(user));

        // Call the service method that uses the Repository
        UserDTO result = userService.getUserId(jwtTokenDummy);
        UserDTO expected = userDTOExpected;

        // Perform assertions
        verify(jwtService, atLeastOnce()).extractClaims(anyString(), any());
        verify(userRepository, atLeastOnce()).findByAccountEmail(email);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        assertEquals(expected, result);
    }
}
