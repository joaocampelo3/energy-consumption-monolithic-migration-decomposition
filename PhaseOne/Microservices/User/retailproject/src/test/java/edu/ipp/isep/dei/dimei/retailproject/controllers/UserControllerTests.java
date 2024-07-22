package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;
    User user;
    Account account;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        account = Account.builder()
                .id(0)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        user = User.builder()
                .id(0)
                .firstname("John")
                .lastname("Doe")
                .account(account)
                .build();

        userDTO = new UserDTO(user);
    }

    @Test
    void test_GetUserId() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserId(JwtTokenDummy)).thenReturn(userDTO);

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = userController.getUserId(JwtTokenDummy);
        ResponseEntity<Object> expected = ResponseEntity.ok(userDTO);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserId(JwtTokenDummy);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void test_GetUserIdFail() throws NotFoundException {
        // Define the behavior of the mock
        when(userService.getUserId(JwtTokenDummy)).thenThrow(new NotFoundException("User not found."));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = userController.getUserId(JwtTokenDummy);

        // Perform assertions
        verify(userService, atLeastOnce()).getUserId(JwtTokenDummy);
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("User not found.", result.getBody());
    }

}
