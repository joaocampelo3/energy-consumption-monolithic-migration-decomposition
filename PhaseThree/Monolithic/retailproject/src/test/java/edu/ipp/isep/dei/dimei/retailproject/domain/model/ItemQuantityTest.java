package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ItemQuantityTest {
    int id;
    OrderQuantity quantityOrdered;
    int itemId;
    ItemQuantity itemQuantityExpected;
    double price;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        quantityOrdered = new OrderQuantity(5);
        price = 12.0;

        itemId = 1;
        itemQuantityExpected = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .itemId(itemId)
                .price(price)
                .build();
    }

    @Test
    void test_createItemQuantity() {
        ItemQuantity itemQuantity = new ItemQuantity(quantityOrdered, itemId, price);

        assertNotNull(itemQuantity);
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(itemId, itemQuantity.getItemId());
        assertEquals(price, itemQuantity.getPrice());
        itemQuantityExpected.setId(0);
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantity2() {
        ItemQuantity itemQuantity = new ItemQuantity(id, quantityOrdered, itemId, price);

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(itemId, itemQuantity.getItemId());
        assertEquals(price, itemQuantity.getPrice());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantityBuilder() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .itemId(itemId)
                .price(price)
                .build();

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(itemId, itemQuantity.getItemId());
        assertEquals(price, itemQuantity.getPrice());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_GetTotalPriceItemQuantity() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .itemId(itemId)
                .price(price)
                .build();

        assertTrue(itemQuantity.getTotalPrice() >= 0);
        assertEquals(price * quantityOrdered.getQuantity(), itemQuantity.getTotalPrice());
        assertEquals(Double.hashCode(price * quantityOrdered.getQuantity()), Double.hashCode(itemQuantity.getTotalPrice()));
    }

}
