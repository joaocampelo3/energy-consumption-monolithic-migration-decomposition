package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    static final String EXCEPTION_NOT_FOUND = "User not found.";
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    UserService userService;
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
    void test_GetRoleFromAuthorizationString() {
        // Define the behavior of the mock
        when(jwtService.extractRole(jwtTokenDummy.substring(7))).thenReturn(role.toString());

        // Call the service method that uses the Repository
        String result = userService.getRoleFromAuthorizationString(jwtTokenDummy);
        String expected = role.name();

        // Perform assertions
        verify(jwtService, atLeastOnce()).extractRole(jwtTokenDummy.substring(7));
        assertNotNull(result);
        assertEquals(role.name(), result);
        assertEquals(expected, result);
    }
}
