package edu.ipp.isep.dei.dimei.retailproject.common.dto.updates;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Item;
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
class ItemUpdateDTOTest {
    int id;
    String itemName;
    String sku;
    String itemDescription;
    double price;
    int quantityInStock;
    CategoryDTO category;
    MerchantDTO merchant;
    ItemUpdateDTO itemUpdateDTOExpected;
    Item item;
    UserDTO userDTO;

    @BeforeEach
    void beforeEach() throws InvalidQuantityException {
        id = 1;
        itemName = "Item 1";
        sku = "ABC-12345-S-BL";
        itemDescription = "Item 1 description";
        price = 12.0;
        quantityInStock = 10;

        userDTO = UserDTO.builder()
                .userId(1)
                .email("johndoe1234@gmail.com")
                .role(RoleEnum.USER)
                .build();
        category = CategoryDTO.builder()
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

        merchant = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchantnumber1@gmail.com")
                .addressId(addressDTO.getId())
                .build();
        itemUpdateDTOExpected = new ItemUpdateDTO(id, sku, price, quantityInStock, userDTO);
        item = Item.builder()
                .id(id)
                .name(itemName)
                .sku(sku)
                .description(itemDescription)
                .price(price)
                .quantityInStock(new StockQuantity(quantityInStock))
                .category(category.dtoToEntity())
                .merchant(merchant.dtoToEntity())
                .build();
    }

    @Test
    void test_createItemUpdateDTO() {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(id, sku, price, quantityInStock, userDTO);

        assertNotNull(itemUpdateDTO);
        assertEquals(id, itemUpdateDTO.getId());
        assertEquals(sku, itemUpdateDTO.getSku());
        assertEquals(price, itemUpdateDTO.getPrice());
        assertEquals(quantityInStock, itemUpdateDTO.getQuantityInStock());
        assertEquals(itemUpdateDTOExpected, itemUpdateDTO);
        assertEquals(itemUpdateDTOExpected.hashCode(), itemUpdateDTO.hashCode());
    }

    @Test
    void test_createItemUpdateDTOBuilder() {
        ItemUpdateDTO itemUpdateDTO = ItemUpdateDTO.builder()
                .id(id)
                .sku(sku)
                .price(price)
                .quantityInStock(quantityInStock)
                .userDTO(userDTO)
                .build();

        assertNotNull(itemUpdateDTO);
        assertEquals(id, itemUpdateDTO.getId());
        assertEquals(sku, itemUpdateDTO.getSku());
        assertEquals(price, itemUpdateDTO.getPrice());
        assertEquals(quantityInStock, itemUpdateDTO.getQuantityInStock());
        assertEquals(itemUpdateDTOExpected, itemUpdateDTO);
        assertEquals(itemUpdateDTOExpected.hashCode(), itemUpdateDTO.hashCode());
    }

    @Test
    void test_createItemUpdateDTOByItem() {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(item);
        itemUpdateDTOExpected.setUserDTO(null);

        assertNotNull(itemUpdateDTO);
        assertEquals(id, itemUpdateDTO.getId());
        assertEquals(sku, itemUpdateDTO.getSku());
        assertEquals(price, itemUpdateDTO.getPrice());
        assertEquals(quantityInStock, itemUpdateDTO.getQuantityInStock());
        assertEquals(itemUpdateDTOExpected, itemUpdateDTO);
        assertEquals(itemUpdateDTOExpected.hashCode(), itemUpdateDTO.hashCode());
    }

    @Test
    void test_createItemUpdateDTONoArgsConstructor() {
        ItemUpdateDTO itemUpdateDTO = ItemUpdateDTO.builder().build();
        assertNotNull(itemUpdateDTO);
    }

    @Test
    void test_SetsItemUpdateDTO() {
        ItemUpdateDTO expected = new ItemUpdateDTO(id, sku, price, quantityInStock, null);
        ItemUpdateDTO result = ItemUpdateDTO.builder().build();

        result.setId(id);
        result.setSku(sku);
        result.setPrice(price);
        result.setQuantityInStock(quantityInStock);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(sku, result.getSku());
        assertEquals(price, result.getPrice());
        assertEquals(quantityInStock, result.getQuantityInStock());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }

}
