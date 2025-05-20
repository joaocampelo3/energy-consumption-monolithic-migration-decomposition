package edu.ipp.isep.dei.dimei.retailproject.security;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    @InjectMocks
    JwtService jwtService;
    Account account;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token.expirationTimeSecs}")
    private long tokenExpirationTime;
    int userId;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "tokenExpirationTime", tokenExpirationTime);
    }


    @Test
    void test_generateToken() {
        // Call the service method
        String token = jwtService.generateToken(account, userId);

        // Perform assertions
        assertNotNull(token);
        assertEquals(account.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void test_extractUsername() {
        // Call the service method
        String token = jwtService.generateToken(account, userId);
        String username = jwtService.extractUsername(token);

        // Perform assertions
        assertEquals(account.getUsername(), username);
    }

    @Test
    void test_isTokenValid() {
        // Call the service method
        String token = jwtService.generateToken(account, userId);
        boolean isValid = jwtService.isTokenValid(token, account);

        // Perform assertions
        assertTrue(isValid);
    }

    @Test
    void test_extractRole() {
        // Call the service method
        String token = jwtService.generateToken(account, userId);
        String role = jwtService.extractRole(token);

        // Perform assertions
        assertEquals(RoleEnum.USER.name(), role);
    }

}
