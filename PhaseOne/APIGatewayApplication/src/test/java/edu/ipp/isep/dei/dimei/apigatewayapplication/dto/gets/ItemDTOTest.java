package edu.ipp.isep.dei.dimei.apigatewayapplication.dto.gets;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ItemDTOTest {
    int id;
    String itemName;
    String sku;
    String itemDescription;
    double price;
    int quantityInStock;
    CategoryDTO category;
    MerchantDTO merchant;
    ItemDTO itemDTOExpected;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {
        id = 1;
        itemName = "Item 1";
        sku = "ABC-12345-S-BL";
        itemDescription = "Item 1 description";
        price = 12.0;
        quantityInStock = 10;
        category = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 description")
                .build();
        merchant = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchantnumber1@gmail.com")
                .addressId(1)
                .build();
        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);
        itemDTOExpected = new ItemDTO(id, itemName, sku, itemDescription, price, quantityInStock, category, merchant, userDTO);
    }

    @Test
    void test_createItemDTO() {
        ItemDTO itemDTO = new ItemDTO(id, itemName, sku, itemDescription, price, quantityInStock, category, merchant, userDTO);

        assertNotNull(itemDTO);
        assertEquals(id, itemDTO.getId());
        assertEquals(itemName, itemDTO.getItemName());
        assertEquals(sku, itemDTO.getSku());
        assertEquals(itemDescription, itemDTO.getItemDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(quantityInStock, itemDTO.getQuantityInStock());
        assertEquals(category, itemDTO.getCategory());
        assertEquals(merchant, itemDTO.getMerchant());
        assertEquals(itemDTOExpected.hashCode(), itemDTO.hashCode());
        assertEquals(itemDTOExpected, itemDTO);
    }

    @Test
    void test_createItemDTO2() {
        ItemDTO itemDTO = new ItemDTO(id, itemName, sku, itemDescription, price, quantityInStock, category, merchant, userDTO);

        assertNotNull(itemDTO);
        assertEquals(category, itemDTO.getCategory());
        assertEquals(merchant, itemDTO.getMerchant());
        assertEquals(itemDTOExpected, itemDTO);
        assertEquals(itemDTOExpected.hashCode(), itemDTO.hashCode());
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
                .category(category)
                .merchant(merchant)
                .build();
        itemDTOExpected.setUserDTO(null);

        assertNotNull(itemDTO);
        assertEquals(id, itemDTO.getId());
        assertEquals(itemName, itemDTO.getItemName());
        assertEquals(sku, itemDTO.getSku());
        assertEquals(itemDescription, itemDTO.getItemDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(quantityInStock, itemDTO.getQuantityInStock());
        assertEquals(category, itemDTO.getCategory());
        assertEquals(merchant, itemDTO.getMerchant());
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
        itemDTOExpected.setUserDTO(null);

        result.setId(id);
        result.setItemName(itemName);
        result.setSku(sku);
        result.setItemDescription(itemDescription);
        result.setPrice(price);
        result.setQuantityInStock(quantityInStock);
        result.setCategory(category);
        result.setMerchant(merchant);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(itemName, result.getItemName());
        assertEquals(sku, result.getSku());
        assertEquals(itemDescription, result.getItemDescription());
        assertEquals(price, result.getPrice());
        assertEquals(quantityInStock, result.getQuantityInStock());
        assertEquals(category, result.getCategory());
        assertEquals(merchant, result.getMerchant());
        assertEquals(itemDTOExpected.hashCode(), result.hashCode());
        assertEquals(itemDTOExpected, result);
        assertEquals(itemDTOExpected.toString(), result.toString());
    }

}
