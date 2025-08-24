package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ItemQuantityDTOTest {
    int id;
    int itemId;
    String itemName;
    String itemSku;
    String itemDescription;
    int qty;
    double price;
    ItemQuantityDTO itemQuantityDTOExpected;
    Item item;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        itemId = 1;
        itemName = "Item 1";
        itemSku = "ABC-12345-S-BL";
        itemDescription = "Item 1 description";
        qty = 3;
        price = 12.0;
        itemQuantityDTOExpected = new ItemQuantityDTO(id, itemId, itemName, itemSku, itemDescription, qty, price);
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .sku(itemSku)
                .description(itemDescription)
                .price(price)
                .build();
    }

    @Test
    void test_createItemQuantityDTO() {
        ItemQuantityDTO itemQuantityDTO = new ItemQuantityDTO(id, itemId, itemName, itemSku, itemDescription, qty, price);

        assertNotNull(itemQuantityDTO);
        assertEquals(id, itemQuantityDTO.getId());
        assertEquals(itemId, itemQuantityDTO.getItemId());
        assertEquals(itemName, itemQuantityDTO.getItemName());
        assertEquals(itemSku, itemQuantityDTO.getItemSku());
        assertEquals(itemDescription, itemQuantityDTO.getItemDescription());
        assertEquals(qty, itemQuantityDTO.getQty());
        assertEquals(price, itemQuantityDTO.getPrice());
    }

    @Test
    void test_createItemQuantityDTOBuilder() {
        ItemQuantityDTO itemQuantityDTO = ItemQuantityDTO.builder()
                .id(id)
                .itemId(itemId)
                .itemName(itemName)
                .itemSku(itemSku)
                .itemDescription(itemDescription)
                .qty(qty)
                .price(price)
                .build();

        assertNotNull(itemQuantityDTO);
        assertEquals(id, itemQuantityDTO.getId());
        assertEquals(itemId, itemQuantityDTO.getItemId());
        assertEquals(itemName, itemQuantityDTO.getItemName());
        assertEquals(itemSku, itemQuantityDTO.getItemSku());
        assertEquals(itemDescription, itemQuantityDTO.getItemDescription());
        assertEquals(qty, itemQuantityDTO.getQty());
        assertEquals(price, itemQuantityDTO.getPrice());
    }

    @Test
    void test_createItemQuantityDTONoArgsConstructor() {
        ItemQuantityDTO itemQuantityDTO = ItemQuantityDTO.builder().build();
        assertNotNull(itemQuantityDTO);
    }

    @Test
    void test_SetsItemQuantityDTO() {
        ItemQuantityDTO result = ItemQuantityDTO.builder().build();

        result.setId(id);
        result.setItemId(itemId);
        result.setItemName(itemName);
        result.setItemSku(itemSku);
        result.setItemDescription(itemDescription);
        result.setQty(qty);
        result.setPrice(price);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(itemId, result.getItemId());
        assertEquals(itemName, result.getItemName());
        assertEquals(itemSku, result.getItemSku());
        assertEquals(itemDescription, result.getItemDescription());
        assertEquals(qty, result.getQty());
        assertEquals(price, result.getPrice());
        assertEquals(itemQuantityDTOExpected.hashCode(), result.hashCode());
        assertEquals(itemQuantityDTOExpected, result);
        assertEquals(itemQuantityDTOExpected.toString(), result.toString());
    }

}
