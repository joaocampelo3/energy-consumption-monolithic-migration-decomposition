package edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects;

import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderQuantityTest {
    final int validQuantity = 1;
    final int invalidQuantity = -1;
    final String exceptionInvalidQuantity = "The number of quantity inserted is not valid";

    OrderQuantity validOrderQuantityExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        validOrderQuantityExpected = new OrderQuantity(validQuantity);
    }

    @Test
    void test_OrderQuantity() throws InvalidQuantityException {
        OrderQuantity result = new OrderQuantity(validQuantity);

        assertNotNull(result);
        assertEquals(validQuantity, result.getQuantity());
        assertEquals(validOrderQuantityExpected.getQuantity(), result.getQuantity());
    }

    @Test
    void test_OrderQuantityFail() {
        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            new OrderQuantity(invalidQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());


    }

    @Test
    void test_SetsOrderQuantity() throws InvalidQuantityException {
        OrderQuantity result = new OrderQuantity(validQuantity+1);

        result.setQuantity(validQuantity);

        assertNotNull(result);
        assertEquals(validQuantity, result.getQuantity());
        assertEquals(validOrderQuantityExpected.getQuantity(), result.getQuantity());
    }
}
