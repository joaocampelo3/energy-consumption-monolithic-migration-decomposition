package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserTest {
    int id;
    String firstname;
    String lastname;
    Account account;
    User userExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        firstname = "John";
        lastname = "Doe";
        account = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();

        userExpected = User.builder()
                .id(id)
                .firstname(firstname)
                .lastname(lastname)
                .account(account)
                .build();
    }

    @Test
    void test_createUser() {
        User user = new User(id, firstname, lastname, account);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(firstname, user.getFirstname());
        assertEquals(lastname, user.getLastname());
        assertEquals(account, user.getAccount());
        assertEquals(userExpected.hashCode(), user.hashCode());
        assertEquals(userExpected, user);
    }

    @Test
    void test_createUserBuilder() {
        User user = User.builder()
                .id(id)
                .firstname(firstname)
                .lastname(lastname)
                .account(account)
                .build();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(firstname, user.getFirstname());
        assertEquals(lastname, user.getLastname());
        assertEquals(account, user.getAccount());
        assertEquals(userExpected.hashCode(), user.hashCode());
        assertEquals(userExpected, user);
    }
}
