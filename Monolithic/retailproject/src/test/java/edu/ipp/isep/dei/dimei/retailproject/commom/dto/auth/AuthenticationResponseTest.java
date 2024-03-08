package edu.ipp.isep.dei.dimei.retailproject.commom.dto.auth;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AuthenticationResponseTest {

    @Test
    public void test_createAuthenticationResponse() {
        String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(JwtTokenDummy);

        assertNotNull(authenticationResponse);
        assertEquals(JwtTokenDummy, authenticationResponse.getToken());

    }

}
