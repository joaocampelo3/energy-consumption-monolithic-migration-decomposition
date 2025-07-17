package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
        assertEquals(registerDTOExpected, registerDTO);
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
        assertEquals(registerDTOExpected, registerDTO);
    }

    @Test
    void test_createRegisterDTONoArgsConstructor() {
        RegisterDTO registerDTO = RegisterDTO.builder().build();
        assertNotNull(registerDTO);
    }

    @Test
    void test_SetsRegisterDTO() {
        RegisterDTO result = RegisterDTO.builder().build();

        result.setFirstname(firstname);
        result.setLastname(lastname);
        result.setEmail(email);
        result.setPassword(password);

        assertNotNull(result);
        assertEquals(firstname, result.getFirstname());
        assertEquals(lastname, result.getLastname());
        assertEquals(email, result.getEmail());
        assertEquals(password, result.getPassword());
        assertEquals(registerDTOExpected.hashCode(), result.hashCode());
        assertEquals(registerDTOExpected, result);
        assertEquals(registerDTOExpected.toString(), result.toString());
    }

    @Test
    void test_getterAndSetter() {
        RegisterDTO registerDTO = new RegisterDTO("a", "b", "c", "d");
        registerDTO.setFirstname(firstname);
        registerDTO.setLastname(lastname);
        registerDTO.setEmail(email);
        registerDTO.setPassword(password);
        assertEquals(firstname, registerDTO.getFirstname());
        assertEquals(lastname, registerDTO.getLastname());
        assertEquals(email, registerDTO.getEmail());
        assertEquals(password, registerDTO.getPassword());
    }

    @Test
    void test_toString() {
        // Create a RegisterDTO object
        RegisterDTO result = RegisterDTO.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .build();

        // Test generated toString() method
        assertEquals(registerDTOExpected.toString(), result.toString());
    }


    @Test
    void test_equalsAndHashCode() {
        RegisterDTO registerDTO1 = new RegisterDTO("Jane", "Doe", "janedoe@gmail.com", "password123");
        RegisterDTO registerDTO2 = new RegisterDTO("Jane", "Doe", "janedoe@gmail.com", "password123");
        RegisterDTO registerDTO3 = new RegisterDTO("John", "Doe", "johndoe@gmail.com", "password123");

        // Test equality with itself
        assertEquals(registerDTO1, registerDTO1);

        // Test equality with the same data
        assertEquals(registerDTO1, registerDTO2);

        // Test inequality with different data
        assertNotEquals(registerDTO1, registerDTO3);

        // Test equality with null
        assertNotNull(registerDTO1);

        // Test equality with a different class type
        assertNotEquals(registerDTO1, new Object());

        // Test hashCode consistency
        assertEquals(registerDTO1.hashCode(), registerDTO2.hashCode());
        assertNotEquals(registerDTO1.hashCode(), registerDTO3.hashCode());
    }

    @Test
    void test_nullFields() {
        RegisterDTO registerDTO = new RegisterDTO(null, null, null, null);

        assertNull(registerDTO.getFirstname());
        assertNull(registerDTO.getLastname());
        assertNull(registerDTO.getEmail());
        assertNull(registerDTO.getPassword());
    }
}
