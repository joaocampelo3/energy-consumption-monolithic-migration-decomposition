package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.LoginDTO;
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

    @Test
    void test_SetsLoginDTO() {
        LoginDTO result = LoginDTO.builder().build();

        result.setEmail(email);
        result.setPassword(password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(password, result.getPassword());
        assertEquals(loginDTOExpected.hashCode(), result.hashCode());
        assertEquals(loginDTOExpected, result);
        assertEquals(loginDTOExpected.toString(), result.toString());
    }

    @Test
    void test_getterAndSetter() {
        LoginDTO loginDTO = new LoginDTO("a", "b");
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        assertEquals(email, loginDTO.getEmail());
        assertEquals(password, loginDTO.getPassword());
    }
}
