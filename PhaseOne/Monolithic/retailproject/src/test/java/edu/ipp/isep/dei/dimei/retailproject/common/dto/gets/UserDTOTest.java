package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class UserDTOTest {
    int userId;
    String email;
    UserDTO userDTOExpected;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        email = "johndoe1234@gmail.com";
        userDTOExpected = new UserDTO(userId, email, RoleEnum.USER);
    }

    @Test
    void test_createUserDTO() {
        UserDTO userDTO = new UserDTO(userId, email, RoleEnum.USER);

        assertNotNull(userDTO);
        assertEquals(userId, userDTO.getUserId());
        assertEquals(email, userDTO.getEmail());
        assertEquals(userDTOExpected, userDTO);
        assertEquals(userDTOExpected.hashCode(), userDTO.hashCode());
    }

    @Test
    void test_createUserDTOBuilder() {
        UserDTO userDTO = UserDTO.builder()
                .userId(userId)
                .email(email)
                .role(RoleEnum.USER)
                .build();

        assertNotNull(userDTO);
        assertEquals(userId, userDTO.getUserId());
        assertEquals(email, userDTO.getEmail());
        assertEquals(RoleEnum.USER, userDTO.getRole());
        assertEquals(userDTOExpected.hashCode(), userDTO.hashCode());
    }

    @Test
    void test_createUserDTONoArgsConstructor() {
        UserDTO userDTO = UserDTO.builder().build();
        assertNotNull(userDTO);
    }

    @Test
    void test_SetsUserDTO() {
        UserDTO result = UserDTO.builder().build();

        result.setUserId(userId);
        result.setEmail(email);
        result.setRole(RoleEnum.USER);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        assertEquals(RoleEnum.USER, result.getRole());
        assertEquals(userDTOExpected.hashCode(), result.hashCode());
        assertEquals(userDTOExpected, result);
        assertEquals(userDTOExpected.toString(), result.toString());
    }

    @Test
    void test_getterAndSetter() {
        UserDTO userDTO = new UserDTO(0, "b", RoleEnum.USER);
        userDTO.setUserId(userId);
        userDTO.setEmail(email);
        userDTO.setRole(RoleEnum.MERCHANT);

        assertEquals(userId, userDTO.getUserId());
        assertEquals(email, userDTO.getEmail());
        assertEquals(RoleEnum.MERCHANT, userDTO.getRole());
    }
}
