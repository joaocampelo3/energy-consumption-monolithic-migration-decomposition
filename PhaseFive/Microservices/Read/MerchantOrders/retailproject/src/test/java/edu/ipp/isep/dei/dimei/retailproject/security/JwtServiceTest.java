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
    private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbl9lbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE3MzY2MTA2NzEsImV4cCI6NTcxMTgxNzU5OX0.kaxgtSl8izrNwi9kxN4qtQLNANbOrHVNDjEjk-uSdfM";
    private Instant expirationDateExpected;

    @BeforeEach
    void beforeEach() {
        userDTO = new UserDTO(1, "admin_email@gmail.com", RoleEnum.ADMIN);

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "tokenExpirationTime", tokenExpirationTime);

        expirationDateExpected = LocalDateTime.of(2150, 12, 31, 23, 59, 59).toInstant(ZoneOffset.UTC);
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

}
