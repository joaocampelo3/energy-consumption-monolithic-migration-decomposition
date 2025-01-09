package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
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
    UserDTO userDTO;
    ItemUpdateDTO itemUpdateDTOExpected;

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
                .addressDTO(addressDTO)
                .build();

        userDTO = new UserDTO(1, "merchantnumber1@gmail.com", RoleEnum.MERCHANT);

        itemUpdateDTOExpected = new ItemUpdateDTO(id, sku, price, quantityInStock, userDTO);
    }

    @Test
    void test_createItemUpdateDTO() {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(id, sku, price, quantityInStock, userDTO);

        assertNotNull(itemUpdateDTO);
        assertEquals(id, itemUpdateDTO.getId());
        assertEquals(sku, itemUpdateDTO.getSku());
        assertEquals(price, itemUpdateDTO.getPrice());
        assertEquals(quantityInStock, itemUpdateDTO.getQuantityInStock());
        assertEquals(userDTO, itemUpdateDTO.getUserDTO());
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
        assertEquals(userDTO, itemUpdateDTO.getUserDTO());
        assertEquals(itemUpdateDTOExpected.hashCode(), itemUpdateDTO.hashCode());
    }

    @Test
    void test_createItemUpdateDTONoArgsConstructor() {
        ItemUpdateDTO itemUpdateDTO = ItemUpdateDTO.builder().build();
        assertNotNull(itemUpdateDTO);
    }

    @Test
    void test_SetsItemUpdateDTO() {
        ItemUpdateDTO expected = new ItemUpdateDTO(id, sku, price, quantityInStock, userDTO);
        ItemUpdateDTO result = ItemUpdateDTO.builder().build();

        result.setId(id);
        result.setSku(sku);
        result.setPrice(price);
        result.setQuantityInStock(quantityInStock);
        result.setUserDTO(userDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(sku, result.getSku());
        assertEquals(price, result.getPrice());
        assertEquals(quantityInStock, result.getQuantityInStock());
        assertEquals(userDTO, result.getUserDTO());
        assertEquals(expected.hashCode(), result.hashCode());
        assertEquals(expected, result);
        assertEquals(expected.toString(), result.toString());
    }

}
