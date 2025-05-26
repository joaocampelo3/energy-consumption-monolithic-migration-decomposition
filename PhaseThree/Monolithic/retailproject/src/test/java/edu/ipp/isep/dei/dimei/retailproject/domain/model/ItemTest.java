package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ItemTest {
    int id;
    String name;
    String sku;
    String description;
    double price;
    StockQuantity quantityInStock;
    Merchant merchant;
    Item itemExpected;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        name = "Item 1";
        sku = "ABC-12345-S-BL";
        description = "Item 1 description";
        price = 12.0;
        quantityInStock = new StockQuantity(5);

        merchant = Merchant.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(1)
                .build();

        itemExpected = Item.builder()
                .id(id)
                .name(name)
                .sku(sku)
                .description(description)
                .price(price)
                .quantityInStock(quantityInStock)
                .merchant(merchant)
                .build();

    }

    @Test
    void test_createItem() throws InvalidQuantityException {
        Item item = new Item(name, sku, description, price, quantityInStock.getQuantity(), merchant);

        assertNotNull(item);
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(merchant, item.getMerchant());
        itemExpected.setId(0);
        assertEquals(itemExpected.getId(), item.getId());
        assertEquals(itemExpected, item);
        assertEquals(itemExpected.hashCode(), item.hashCode());
    }

    @Test
    void test_createItem2() {
        Item item = new Item(id, name, sku, description, price, quantityInStock, merchant);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(merchant, item.getMerchant());
        assertEquals(itemExpected, item);
        assertEquals(itemExpected.hashCode(), item.hashCode());
    }

    @Test
    void test_createItemBuilder() {
        Item item = Item.builder()
                .id(id)
                .name(name)
                .sku(sku)
                .description(description)
                .price(price)
                .quantityInStock(quantityInStock)
                .merchant(merchant)
                .build();

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(merchant, item.getMerchant());
        assertEquals(itemExpected.hashCode(), item.hashCode());
        assertEquals(itemExpected, item);
    }

    @Test
    void test_ItemSets() {
        Item item = Item.builder().build();

        item.setId(id);
        item.setName(name);
        item.setSku(sku);
        item.setDescription(description);
        item.setPrice(price);
        item.setQuantityInStock(quantityInStock);
        item.setMerchant(merchant);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(merchant, item.getMerchant());
        assertEquals(itemExpected.hashCode(), item.hashCode());
        assertEquals(itemExpected, item);
    }
}
