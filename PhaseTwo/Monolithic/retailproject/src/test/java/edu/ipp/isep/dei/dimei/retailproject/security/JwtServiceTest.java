package edu.ipp.isep.dei.dimei.retailproject.security;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    @InjectMocks
    JwtService jwtService;
    UserDTO userDTO;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token.expirationTimeSecs}")
    private long tokenExpirationTime;
    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsInN1YiI6ImFkbWluX2VtYWlsQGdtYWlsLmNvbSIsImlhdCI6MTc1NjAyMzMxNiwiZXhwIjo5OTk5OTk5OTk5fQ.32RDPrLKxzoLyDkYId3e5BRQdPI7c71hix1UYaIWt4Q";
    private Instant expirationDateExpected;

    @BeforeEach
    void beforeEach() {
        userDTO = new UserDTO(1, "admin_email@gmail.com", RoleEnum.ADMIN);

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "tokenExpirationTime", tokenExpirationTime);

        expirationDateExpected = LocalDateTime.of(2286, 11, 20, 17, 46, 39).toInstant(ZoneOffset.UTC);
    }

    @Test
    void test_extractUsername() {
        // Call the service method
        String username = jwtService.extractUsername(token);

        // Perform assertions
        assertEquals(userDTO.getEmail(), username);
    }

    @Test
    void test_isTokenValid() {
        // Call the service method
        boolean isValid = jwtService.isTokenValid(token, userDTO);

        // Perform assertions
        assertTrue(isValid);
    }

    @Test
    void test_extractClaims() {
        // Call the service method
        Date expirationDate = jwtService.extractClaims(token, Claims::getExpiration);
        // Perform assertions
        assertEquals(Date.from(expirationDateExpected), expirationDate);
    }

    @Test
    void test_extractRole() {
        // Call the service method
        String role = jwtService.extractRole(token);
        // Perform assertions
        assertEquals(userDTO.getRole().name(), role);
    }

    @Test
    void test_extractUserId() {
        // Call the service method
        int id = jwtService.extractUserId(token);
        // Perform assertions
        assertEquals(userDTO.getUserId(), id);
    }

}
