package edu.ipp.isep.dei.dimei.retailproject.common.dto.auth;

import org.junit.jupiter.api.Test;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthenticationResponseTest {
    static String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";

    @Test
    void test_createAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwtTokenDummy);

        assertNotNull(authenticationResponse);
        assertEquals(jwtTokenDummy, authenticationResponse.getToken());

    }

    @Test
    void test_noArgsConstructorAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        assertNotNull(authenticationResponse);
    }

    @Test
    void test_buildAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwtTokenDummy)
                .build();

        assertNotNull(authenticationResponse);
        assertEquals(jwtTokenDummy, authenticationResponse.getToken());
    }

    @Test
    void test_getterAndSetter() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtTokenDummy);
        assertEquals(jwtTokenDummy, response.getToken());
    }

}
