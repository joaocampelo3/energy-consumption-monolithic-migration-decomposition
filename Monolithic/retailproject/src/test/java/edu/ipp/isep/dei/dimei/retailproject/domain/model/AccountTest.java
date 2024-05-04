package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AccountTest {
    int id;
    String email;
    String password;
    RoleEnum role = RoleEnum.USER;
    Account accountExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        email = "johndoe1234@gmail.com";
        password = "johndoe_password";
        accountExpected = Account.builder()
                .id(1)
                .email("johndoe1234@gmail.com")
                .password("johndoe_password")
                .role(RoleEnum.USER)
                .build();
    }

    @Test
    void test_createAccount() {
        Account account = new Account(id, email, password, role);

        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(role, account.getRole());
        assertEquals(accountExpected.hashCode(), account.hashCode());
        assertEquals(accountExpected, account);
    }

    @Test
    void test_createAccount2() {
        Account account = new Account(email, password, role);

        assertNotNull(account);
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(role, account.getRole());
        accountExpected.setId(0);
        assertEquals(accountExpected.hashCode(), account.hashCode());
        assertEquals(accountExpected, account);
    }

    @Test
    void test_createAccountBuilder() {
        Account account = Account.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .build();

        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(role, account.getRole());
        assertEquals(accountExpected.hashCode(), account.hashCode());
        assertEquals(accountExpected, account);
        assertEquals(accountExpected, account);
    }
}
