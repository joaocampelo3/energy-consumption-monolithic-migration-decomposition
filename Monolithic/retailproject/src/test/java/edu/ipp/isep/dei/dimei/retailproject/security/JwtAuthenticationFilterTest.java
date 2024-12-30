package edu.ipp.isep.dei.dimei.retailproject.security;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {
    final String tokenDummy = "AAA1bbb2CcC3";
    final String jwtTokenDummy = BEARER_PREFIX + tokenDummy;
    final String email = "johndoe1234@gmail.com";
    final String password = "johndoe_password";
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    Account userDetails;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetails = new Account(email, password, RoleEnum.USER);
    }

    @Test
    void test_doFilterInternal_ValidToken() throws ServletException, IOException {
        // Mock request header and path
        when(request.getHeader("Authorization")).thenReturn(jwtTokenDummy);
        when(request.getServletPath()).thenReturn("/api/test");
        when(jwtService.extractUsername(tokenDummy)).thenReturn(email);
        when(jwtService.isTokenValid(tokenDummy, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Call the method under test
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(jwtService, atLeastOnce()).extractUsername(tokenDummy);
        verify(jwtService, atLeastOnce()).isTokenValid(tokenDummy, userDetails);
        verify(userDetailsService, atLeastOnce()).loadUserByUsername(email);
        verify(filterChain, atLeastOnce()).doFilter(request, response);
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
        verify(userDetailsService, never()).loadUserByUsername(any());

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
        verify(userDetailsService, never()).loadUserByUsername(any());

        // Verify filter chain invocation
        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }
}
