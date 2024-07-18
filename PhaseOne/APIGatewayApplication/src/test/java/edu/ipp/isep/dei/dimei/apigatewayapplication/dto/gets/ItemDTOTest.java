package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.gets;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.MerchantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ItemDTOTest {
    int id;
    String itemName;
    String sku;
    String itemDescription;
    double price;
    int quantityInStock;
    CategoryDTO categoryDTO;
    MerchantDTO merchantDTO;
    ItemDTO itemDTOExpected;

    @BeforeEach
    void beforeEach() {
        id = 1;
        itemName = "Item 1";
        sku = "ABC-12345-S-BL";
        itemDescription = "Item 1 description";
        price = 12.0;
        quantityInStock = 10;
        categoryDTO = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 description")
                .build();
        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantDTO = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchantnumber1@gmail.com")
                .address(addressDTO)
                .build();
        itemDTOExpected = new ItemDTO(id, itemName, sku, itemDescription, price, quantityInStock, categoryDTO, merchantDTO);
    }

    @Test
    void test_createItemDTO() {
        ItemDTO itemDTO = new ItemDTO(id, itemName, sku, itemDescription, price, quantityInStock, categoryDTO, merchantDTO);

        assertNotNull(itemDTO);
        assertEquals(id, itemDTO.getId());
        assertEquals(itemName, itemDTO.getItemName());
        assertEquals(sku, itemDTO.getSku());
        assertEquals(itemDescription, itemDTO.getItemDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(quantityInStock, itemDTO.getQuantityInStock());
        assertEquals(categoryDTO, itemDTO.getCategory());
        assertEquals(merchantDTO, itemDTO.getMerchant());
        assertEquals(itemDTOExpected.hashCode(), itemDTO.hashCode());
        assertEquals(itemDTOExpected, itemDTO);
    }

    @Test
    void test_createItemDTOBuilder() {
        ItemDTO itemDTO = ItemDTO.builder()
                .id(id)
                .itemName(itemName)
                .sku(sku)
                .itemDescription(itemDescription)
                .price(price)
                .quantityInStock(quantityInStock)
                .category(categoryDTO)
                .merchant(merchantDTO)
                .build();

        assertNotNull(itemDTO);
        assertEquals(id, itemDTO.getId());
        assertEquals(itemName, itemDTO.getItemName());
        assertEquals(sku, itemDTO.getSku());
        assertEquals(itemDescription, itemDTO.getItemDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(quantityInStock, itemDTO.getQuantityInStock());
        assertEquals(categoryDTO, itemDTO.getCategory());
        assertEquals(merchantDTO, itemDTO.getMerchant());
        assertEquals(itemDTOExpected.hashCode(), itemDTO.hashCode());
    }

    @Test
    void test_createItemDTONoArgsConstructor() {
        ItemDTO itemDTO = ItemDTO.builder().build();
        assertNotNull(itemDTO);
    }

    @Test
    void test_SetsItemDTO() {
        ItemDTO result = ItemDTO.builder().build();

        result.setId(id);
        result.setItemName(itemName);
        result.setSku(sku);
        result.setItemDescription(itemDescription);
        result.setPrice(price);
        result.setQuantityInStock(quantityInStock);
        result.setCategory(categoryDTO);
        result.setMerchant(merchantDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(itemName, result.getItemName());
        assertEquals(sku, result.getSku());
        assertEquals(itemDescription, result.getItemDescription());
        assertEquals(price, result.getPrice());
        assertEquals(quantityInStock, result.getQuantityInStock());
        assertEquals(categoryDTO, result.getCategory());
        assertEquals(merchantDTO, result.getMerchant());
        assertEquals(itemDTOExpected.hashCode(), result.hashCode());
        assertEquals(itemDTOExpected, result);
        assertEquals(itemDTOExpected.toString(), result.toString());
    }

}
