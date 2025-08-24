package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemQuantityTest {
    int id;
    OrderQuantity quantityOrdered;
    Item item;
    ItemQuantity itemQuantityExpected;
    double price;
    int categoryId = 1;
    int merchantId = 1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        quantityOrdered = new OrderQuantity(5);
        price = 12.0;

        item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(price)
                .quantityInStock(new StockQuantity(10))
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        itemQuantityExpected = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .price(price)
                .build();
    }

    @Test
    void test_createItemQuantity() {
        ItemQuantity itemQuantity = new ItemQuantity(quantityOrdered, item, price);

        assertNotNull(itemQuantity);
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        assertEquals(price, itemQuantity.getPrice());
        itemQuantityExpected.setId(0);
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantity2() {
        ItemQuantity itemQuantity = new ItemQuantity(id, quantityOrdered, item, price);

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        assertEquals(price, itemQuantity.getPrice());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantityBuilder() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .price(price)
                .build();

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        assertEquals(price, itemQuantity.getPrice());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_GetTotalPriceItemQuantity() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .price(price)
                .build();

        assertTrue(itemQuantity.getTotalPrice() >= 0);
        assertEquals(item.getPrice() * quantityOrdered.getQuantity(), itemQuantity.getTotalPrice());
        assertEquals(Double.hashCode(item.getPrice() * quantityOrdered.getQuantity()), Double.hashCode(itemQuantity.getTotalPrice()));
    }

}
