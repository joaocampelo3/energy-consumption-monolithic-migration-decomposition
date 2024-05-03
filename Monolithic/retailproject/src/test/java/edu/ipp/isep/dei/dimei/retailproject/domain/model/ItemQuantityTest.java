package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemQuantityTest {
    int id;
    OrderQuantity quantityOrdered;
    Item item;
    ItemQuantity itemQuantityExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        quantityOrdered = new OrderQuantity(5);
        Category category = Category.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        Address address = Address.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        Merchant merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(address)
                .build();

        item = Item.builder()
                .id(1)
                .name("Item 1")
                .sku("ABC-12345-S-BL")
                .description("Item 1 description")
                .price(12.0)
                .quantityInStock(new StockQuantity(10))
                .category(category)
                .merchant(merchant)
                .build();
        itemQuantityExpected = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .build();
    }

    @Test
    void test_createItemQuantity() {
        ItemQuantity itemQuantity = new ItemQuantity(quantityOrdered, item);

        assertNotNull(itemQuantity);
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        itemQuantityExpected.setId(0);
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantity2() {
        ItemQuantity itemQuantity = new ItemQuantity(id, quantityOrdered, item);

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_createItemQuantityBuilder() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .build();

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(quantityOrdered.getQuantity(), itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(quantityOrdered, itemQuantity.getQuantityOrdered());
        assertEquals(item, itemQuantity.getItem());
        assertEquals(itemQuantityExpected.hashCode(), itemQuantity.hashCode());
        assertEquals(itemQuantityExpected, itemQuantity);
    }

    @Test
    void test_GetTotalPriceItemQuantity() {
        ItemQuantity itemQuantity = ItemQuantity.builder()
                .id(id)
                .quantityOrdered(quantityOrdered)
                .item(item)
                .build();

        assertTrue(itemQuantity.getTotalPrice() >= 0);
        assertEquals(item.getPrice() * quantityOrdered.getQuantity(), itemQuantity.getTotalPrice());
        assertEquals(Double.hashCode(item.getPrice() * quantityOrdered.getQuantity()), Double.hashCode(itemQuantity.getTotalPrice()));
    }

}
