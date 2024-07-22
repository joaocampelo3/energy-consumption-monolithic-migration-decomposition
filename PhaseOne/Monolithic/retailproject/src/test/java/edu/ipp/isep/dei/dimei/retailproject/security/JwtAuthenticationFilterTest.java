package edu.ipp.isep.dei.dimei.retailproject.security;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {
    final String TokenDummy = "AAA1bbb2CcC3";
    final String JwtTokenDummy = BEARER_PREFIX + TokenDummy;
    final String email = "johndoe1234@gmail.com";
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtService jwtService;
    private UserDTO userDTO;


    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(1, email, RoleEnum.USER);
    }

    @Test
    void test_doFilterInternal_ValidToken() throws ServletException, IOException {
        // Mock request header and path
        String json = "{\"userId\":1,\"email\":\"johndoe1234@gmail.com\",\"role\":\"USER\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(JwtTokenDummy);
        when(request.getServletPath()).thenReturn("/api/test");
        when(jwtService.extractUsername(TokenDummy)).thenReturn(email);
        when(request.getReader()).thenReturn(reader);
        when(jwtService.isTokenValid(TokenDummy, userDTO)).thenReturn(true);

        // Call the method under test
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(request, atLeastOnce()).getHeader("Authorization");
        verify(request, atLeastOnce()).getServletPath();
        verify(jwtService, atLeastOnce()).extractUsername(TokenDummy);
        verify(request, atLeastOnce()).getReader();
        verify(jwtService, atLeastOnce()).isTokenValid(TokenDummy, userDTO);
    }


    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String invalidToken = "invalid_jwt_token";

        // Mock request header and path
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(request.getServletPath()).thenReturn("/api/test");

        // Mock JWT service behavior
        when(jwtService.extractUsername(invalidToken)).thenReturn(null);

        // Call the method under test
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify interactions
        verify(jwtService, atLeastOnce()).extractUsername(invalidToken);
        verify(jwtService, never()).isTokenValid(any(), any());

        // Verify filter chain invocation
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_UnauthorizedPath() throws ServletException, IOException {
        // Mock request path
        when(request.getServletPath()).thenReturn("/swagger");

        // Call the method under test
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify interactions
        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());

        // Verify filter chain invocation
        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }
}
