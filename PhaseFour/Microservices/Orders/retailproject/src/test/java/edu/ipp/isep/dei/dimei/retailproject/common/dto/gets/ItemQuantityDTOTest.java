package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.ItemQuantity;
import edu.ipp.isep.dei.dimei.retailproject.domain.valueobjects.OrderQuantity;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemQuantityDTOTest {
    int id;
    int itemId;
    String itemName;
    String itemSku;
    String itemDescription;
    int qty;
    double price;
    ItemQuantityDTO itemQuantityDTOExpected;
    ItemQuantity itemQuantityExpected;
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

        itemQuantityExpected = ItemQuantity.builder()
                .id(id)
                .item(item)
                .quantityOrdered(new OrderQuantity(qty))
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
    void test_dtoToEntityItemQuantityDTO() throws InvalidQuantityException {
        ItemQuantityDTO itemQuantityDTO = ItemQuantityDTO.builder()
                .id(id)
                .itemId(itemId)
                .itemName(itemName)
                .itemSku(itemSku)
                .itemDescription(itemDescription)
                .qty(qty)
                .price(price)
                .build();

        ItemQuantity itemQuantity = itemQuantityDTO.dtoToEntity();

        assertNotNull(itemQuantity);
        assertEquals(id, itemQuantity.getId());
        assertEquals(qty, itemQuantity.getQuantityOrdered().getQuantity());
        assertEquals(item, itemQuantity.getItem());

    }

    @Test
    void test_dtoToItemItemQuantityDTO() {
        ItemQuantityDTO itemQuantityDTO = ItemQuantityDTO.builder()
                .id(id)
                .itemId(itemId)
                .itemName(itemName)
                .itemSku(itemSku)
                .itemDescription(itemDescription)
                .qty(qty)
                .price(price)
                .build();

        Item item1 = itemQuantityDTO.dtoToItem();

        assertNotNull(item1);
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getSku(), item1.getSku());
        assertEquals(item.getDescription(), item1.getDescription());
        assertEquals(item.getPrice(), item1.getPrice());

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
