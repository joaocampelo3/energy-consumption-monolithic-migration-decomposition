package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountTest {
    final RoleEnum role = RoleEnum.USER;
    int id;
    String email;
    String password;
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
        assertEquals(accountExpected.getUsername(), account.getUsername());
        assertEquals(accountExpected.isEnabled(), account.isEnabled());
        assertEquals(accountExpected.isAccountNonExpired(), account.isAccountNonExpired());
        assertEquals(accountExpected.isAccountNonLocked(), account.isAccountNonLocked());
        assertEquals(accountExpected.isCredentialsNonExpired(), account.isCredentialsNonExpired());
        assertEquals(accountExpected.getAuthorities(), account.getAuthorities());
        assertEquals(accountExpected.hashCode(), account.hashCode());
        assertEquals(accountExpected, account);
        assertEquals(accountExpected.toString(), account.toString());
    }

    @Test
    void test_AccountSets() {
        Account account = Account.builder().build();

        account.setId(id);
        account.setEmail(email);
        account.setPassword(password);
        account.setRole(role);

        assertEquals(id, account.getId());
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(role, account.getRole());
    }

    @Test
    void test_AccountSetsFalse() {
        Account account = Account.builder().build();

        account.setId(2);
        account.setEmail("aaaaaa@aaaa.com");
        account.setPassword("12345");
        account.setRole(RoleEnum.ADMIN);

        assertNotEquals(account.getId(), id);
        assertNotEquals(account.getEmail(), email);
        assertNotEquals(account.getPassword(), password);
        assertNotEquals(account.getRole(), role);
    }
}
