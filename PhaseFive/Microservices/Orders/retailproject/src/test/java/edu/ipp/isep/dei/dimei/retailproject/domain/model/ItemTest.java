package edu.ipp.isep.dei.dimei.retailproject.domain.model;

import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.StockQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemTest {
    int id;
    String name;
    String sku;
    String description;
    double price;
    StockQuantity quantityInStock;
    Item itemExpected;
    int categoryId = 1;
    int merchantId = 1;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        name = "Item 1";
        sku = "ABC-12345-S-BL";
        description = "Item 1 description";
        price = 12.0;
        quantityInStock = new StockQuantity(5);

        itemExpected = Item.builder()
                .id(id)
                .name(name)
                .sku(sku)
                .description(description)
                .price(price)
                .quantityInStock(quantityInStock)
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

    }

    @Test
    void test_createItem() throws InvalidQuantityException {
        Item item = new Item(name, sku, description, price, quantityInStock.getQuantity(), categoryId, merchantId);

        assertNotNull(item);
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        itemExpected.setId(0);
        assertEquals(itemExpected.getId(), item.getId());
        assertEquals(itemExpected, item);
        assertEquals(itemExpected.hashCode(), item.hashCode());
    }

    @Test
    void test_createItem2() {
        Item item = new Item(id, name, sku, description, price, quantityInStock, categoryId, merchantId);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(categoryId, item.getCategoryId());
        assertEquals(merchantId, item.getMerchantId());
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
                .categoryId(categoryId)
                .merchantId(merchantId)
                .build();

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(categoryId, item.getCategoryId());
        assertEquals(merchantId, item.getMerchantId());
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
        item.setCategoryId(categoryId);
        item.setMerchantId(merchantId);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(sku, item.getSku());
        assertEquals(description, item.getDescription());
        assertEquals(price, item.getPrice());
        assertEquals(quantityInStock, item.getQuantityInStock());
        assertEquals(categoryId, item.getCategoryId());
        assertEquals(merchantId, item.getMerchantId());
        assertEquals(itemExpected.hashCode(), item.hashCode());
        assertEquals(itemExpected, item);
    }
}
