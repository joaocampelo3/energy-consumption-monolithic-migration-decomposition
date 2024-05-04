package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class LoginDTOTest {
    String email;
    String password;
    LoginDTO loginDTOExpected;

    @BeforeEach
    void beforeEach() {
        email = "johndoe1234@gmail.com";
        password = "johndoe_password";
        loginDTOExpected = new LoginDTO(email, password);
    }

    @Test
    void test_createLoginDTO() {
        LoginDTO loginDTO = new LoginDTO(email, password);

        assertNotNull(loginDTO);
        assertEquals(email, loginDTO.getEmail());
        assertEquals(password, loginDTO.getPassword());
        assertEquals(loginDTOExpected, loginDTO);
        assertEquals(loginDTOExpected.hashCode(), loginDTO.hashCode());
    }

    @Test
    void test_createLoginDTOBuilder() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email(email)
                .password(password)
                .build();

        assertNotNull(loginDTO);
        assertEquals(email, loginDTO.getEmail());
        assertEquals(password, loginDTO.getPassword());
        assertEquals(loginDTOExpected.hashCode(), loginDTO.hashCode());
    }

    @Test
    void test_createLoginDTONoArgsConstructor() {
        LoginDTO loginDTO = LoginDTO.builder().build();
        assertNotNull(loginDTO);
    }
}
