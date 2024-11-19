package edu.ipp.isep.dei.dimei.loadbalancerapplication.dto.auth;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationResponseTest {
    static final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";

    @Test
    void test_createAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(JwtTokenDummy);

        assertNotNull(authenticationResponse);
        assertEquals(JwtTokenDummy, authenticationResponse.getToken());

    }

    @Test
    void test_noArgsConstructorAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        assertNotNull(authenticationResponse);
    }

    @Test
    void test_getterAndSetter() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(JwtTokenDummy);
        assertEquals(JwtTokenDummy, response.getToken());
    }

}
