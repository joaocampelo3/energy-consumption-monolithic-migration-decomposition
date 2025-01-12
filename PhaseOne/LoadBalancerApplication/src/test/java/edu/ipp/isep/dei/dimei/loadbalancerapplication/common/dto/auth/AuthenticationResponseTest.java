package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationResponseTest {
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    AuthenticationResponse expected;

    @BeforeEach
    void beforeEach() {
        expected = new AuthenticationResponse(jwtTokenDummy);
    }

    @Test
    void test_createAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwtTokenDummy);

        assertNotNull(authenticationResponse);
        assertEquals(expected.getToken(), authenticationResponse.getToken());
        assertEquals(expected, authenticationResponse);
        assertEquals(expected.toString(), authenticationResponse.toString());
        assertTrue(expected.equals(authenticationResponse));
        assertEquals(expected.hashCode(), authenticationResponse.hashCode());

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
        assertEquals(expected.getToken(), authenticationResponse.getToken());
        assertEquals(expected, authenticationResponse);
        assertEquals(expected.hashCode(), authenticationResponse.hashCode());
    }

    @Test
    void test_getterAndSetter() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtTokenDummy);
        assertEquals(expected.getToken(), response.getToken());
        assertEquals(expected, response);
        assertEquals(expected.hashCode(), response.hashCode());
    }

}
