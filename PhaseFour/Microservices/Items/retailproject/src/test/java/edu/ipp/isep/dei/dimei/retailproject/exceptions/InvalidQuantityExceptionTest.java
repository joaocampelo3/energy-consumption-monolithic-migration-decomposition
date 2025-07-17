package edu.ipp.isep.dei.dimei.retailproject.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class InvalidQuantityExceptionTest {
    String message;

    @BeforeEach
    void beforeEach() {
        message = "The number of quantity inserted is not valid";
    }

    @Test
    void test_constructorInvalidQuantityException() {
        InvalidQuantityException result = new InvalidQuantityException(message);

        assertNotNull(result);
        assertEquals(result.getMessage(), message);
    }
}
