package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RegisterDTOTest {
    String firstname;
    String lastname;
    String email;
    String password;
    RegisterDTO registerDTOExpected;

    @BeforeEach
    void beforeEach() {
        firstname = "John";
        lastname = "Doe";
        email = "johndoe1234@gmail.com";
        password = "johndoe_password";
        registerDTOExpected = new RegisterDTO(firstname, lastname, email, password);
    }

    @Test
    void test_createRegisterDTO() {
        RegisterDTO registerDTO = new RegisterDTO(firstname, lastname, email, password);

        assertNotNull(registerDTO);
        assertEquals(firstname, registerDTO.getFirstname());
        assertEquals(lastname, registerDTO.getLastname());
        assertEquals(email, registerDTO.getEmail());
        assertEquals(password, registerDTO.getPassword());
        assertEquals(registerDTOExpected.hashCode(), registerDTO.hashCode());
    }

    @Test
    void test_createRegisterDTOBuilder() {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .build();

        assertNotNull(registerDTO);
        assertEquals(firstname, registerDTO.getFirstname());
        assertEquals(lastname, registerDTO.getLastname());
        assertEquals(email, registerDTO.getEmail());
        assertEquals(password, registerDTO.getPassword());
        assertEquals(registerDTOExpected.hashCode(), registerDTO.hashCode());
    }

    @Test
    void test_createRegisterDTONoArgsConstructor() {
        RegisterDTO registerDTO = RegisterDTO.builder().build();
        assertNotNull(registerDTO);
    }
}
