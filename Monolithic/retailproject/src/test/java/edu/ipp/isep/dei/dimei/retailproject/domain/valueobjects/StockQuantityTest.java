package edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects;

import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StockQuantityTest {
    final int validQuantity = 1;
    final int invalidQuantity = -1;
    final String exceptionInvalidQuantity = "The number of quantity inserted is not valid";

    StockQuantity validStockQuantityExpected;
    int newQuantity;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        validStockQuantityExpected = new StockQuantity(validQuantity);
    }

    @Test
    void test_StockQuantity() throws InvalidQuantityException {
        StockQuantity result = new StockQuantity(validQuantity);

        assertNotNull(result);
        assertEquals(validQuantity, result.getQuantity());
        assertEquals(validStockQuantityExpected.getQuantity(), result.getQuantity());
    }

    @Test
    void test_StockQuantityFail() {
        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            new StockQuantity(invalidQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());
    }

    @Test
    void test_SetsStockQuantity() throws InvalidQuantityException {
        StockQuantity result = new StockQuantity(validQuantity + 1);

        result.setQuantity(validQuantity);

        assertNotNull(result);
        assertEquals(validQuantity, result.getQuantity());
        assertEquals(validStockQuantityExpected.getQuantity(), result.getQuantity());
    }

    @Test
    void test_increaseStockQuantity() throws InvalidQuantityException {
        StockQuantity result = new StockQuantity(validQuantity);
        newQuantity = 2;

        result.increaseStockQuantity(newQuantity);

        assertNotNull(result);
        assertEquals(newQuantity, result.getQuantity());
    }

    @Test
    void test_increaseStockQuantityFail1() throws InvalidQuantityException {
        StockQuantity stockQuantity = new StockQuantity(validQuantity);

        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            stockQuantity.increaseStockQuantity(invalidQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());
    }

    @Test
    void test_increaseStockQuantityFail2() throws InvalidQuantityException {
        newQuantity = 0;
        StockQuantity stockQuantity = new StockQuantity(validQuantity);

        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            stockQuantity.increaseStockQuantity(newQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());
    }

    @Test
    void test_decreaseStockQuantity() throws InvalidQuantityException {
        StockQuantity result = new StockQuantity(validQuantity);
        newQuantity = 0;

        result.decreaseStockQuantity(newQuantity);

        assertNotNull(result);
        assertEquals(newQuantity, result.getQuantity());
    }

    @Test
    void test_decreaseStockQuantityFail1() throws InvalidQuantityException {
        StockQuantity stockQuantity = new StockQuantity(validQuantity);

        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            stockQuantity.decreaseStockQuantity(invalidQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());
    }

    @Test
    void test_decreaseStockQuantityFail2() throws InvalidQuantityException {
        newQuantity = 3;
        StockQuantity stockQuantity = new StockQuantity(validQuantity);

        InvalidQuantityException result = assertThrows(InvalidQuantityException.class, () -> {
            stockQuantity.decreaseStockQuantity(newQuantity);
        });

        assertNotNull(result);
        assertEquals(exceptionInvalidQuantity, result.getMessage());
    }
}
