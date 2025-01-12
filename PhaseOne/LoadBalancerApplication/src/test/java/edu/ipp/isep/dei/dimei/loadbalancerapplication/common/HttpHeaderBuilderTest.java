package edu.ipp.isep.dei.dimei.loadbalancerapplication.common;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class HttpHeaderBuilderTest implements HttpHeaderBuilder {
    final String jwtTokenDummy = "AAA1bbb2CcC3";
    HttpHeaders headers;

    @BeforeEach
    void beforeEach() {
        headers = new HttpHeaders();
        headers.setBearerAuth(jwtTokenDummy);
    }

    @Test
    void test_buildHttpHeader() {
        HttpHeaders result = buildHttpHeader(jwtTokenDummy);

        assertNotNull(result);
        assertEquals(headers.get("Authorization"), result.get("Authorization"));
        assertEquals(headers, result);
        assertEquals(headers.hashCode(), result.hashCode());
    }

    @Test
    void test_buildHttpHeaderWithMediaType() {
        HttpHeaders result = buildHttpHeaderWithMediaType(jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        assertNotNull(result);
        assertEquals(headers.get("Authorization"), result.get("Authorization"));
        assertEquals(headers.get("Content-Type"), result.get("Content-Type"));
        assertEquals(headers, result);
        assertEquals(headers.hashCode(), result.hashCode());
    }

}
