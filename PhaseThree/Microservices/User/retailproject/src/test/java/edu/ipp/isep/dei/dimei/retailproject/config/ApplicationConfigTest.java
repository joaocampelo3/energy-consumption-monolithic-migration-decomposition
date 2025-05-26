package edu.ipp.isep.dei.dimei.retailproject.config;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationConfigTest {
    @InjectMocks
    ApplicationConfig applicationConfig;
    @Mock
    UserRepository userRepository;
    String email;
    Account account;
    Account accountExpected;
    User user;
    User userExpected;

    @BeforeEach
    void beforeEach() {
        email = "johndoe1234@gmail.com";

        account = Account.builder()
                .id(2)
                .email(email)
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        accountExpected = Account.builder()
                .id(1)
                .email(email)
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .id(2)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        userExpected = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .account(accountExpected)
                .build();
    }

    @Test
    void test_userDetailsService() {
        // Define the behavior of the mock
        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.ofNullable(userExpected));

        // Call the service method that uses the Repository
        UserDetails result = applicationConfig.userDetailsService().loadUserByUsername(userExpected.getAccount().getUsername());
        UserDetails expected = userExpected.getAccount();

        // Perform assertions
        verify(userRepository, atLeastOnce()).findByAccountEmail(email);
        assertNotNull(result);
        assertEquals(result, expected);
    }

    @Test
    void test_userDetailsServiceFail() {
        // Define the behavior of the mock
        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.ofNullable(user));

        // Call the service method that uses the Repository
        UserDetails result = applicationConfig.userDetailsService().loadUserByUsername(user.getAccount().getUsername());
        UserDetails expected = userExpected.getAccount();

        // Perform assertions
        verify(userRepository, atLeastOnce()).findByAccountEmail(email);
        assertNotNull(result);
        assertNotEquals(result, expected);
    }
}
