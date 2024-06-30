package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Address;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static edu.ipp.isep.dei.dimei.retailproject.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTests {
    final String JwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    final String exceptionItemNotFound = "Item not found.";
    @InjectMocks
    ItemController itemController;
    @Mock
    ItemService itemService;

    ItemDTO itemDTO1;
    ItemUpdateDTO itemDTO1Update;
    ItemDTO itemDTO2;
    ItemUpdateDTO itemDTO2Update;
    List<ItemDTO> items = new ArrayList<>();

    CategoryDTO categoryDTO;

    Merchant merchant;

    MerchantDTO merchantDTO;

    Address address;

    AddressDTO addressDTO;

    @BeforeEach
    void beforeEach() {

        categoryDTO = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        address = Address.builder()
                .id(0)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchant = Merchant.builder()
                .id(0)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(address)
                .build();

        merchantDTO = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .address(addressDTO)
                .build();

        itemDTO1 = ItemDTO.builder()
                .id(1)
                .itemName("Item 1")
                .sku("ABC-12345-S-BL")
                .itemDescription("Item 1 Description")
                .price(15.0)
                .quantityInStock(10)
                .category(categoryDTO)
                .merchant(merchantDTO)
                .build();

        itemDTO1Update = ItemUpdateDTO.builder()
                .id(1)
                .price(10.0)
                .quantityInStock(14)
                .build();

        itemDTO2 = ItemDTO.builder()
                .id(2)
                .itemName("Item 2")
                .sku("ABC-12345-M-BL")
                .itemDescription("Item 2 Description")
                .price(20.0)
                .quantityInStock(5)
                .category(categoryDTO)
                .merchant(merchantDTO)
                .build();

        itemDTO2Update = ItemUpdateDTO.builder()
                .id(2)
                .price(20.0)
                .quantityInStock(14)
                .build();

        items.add(itemDTO1);
        items.add(itemDTO2);
    }

    @Test
    void test_GetAllItems() throws NotFoundException {
        // Define the behavior of the mock
        when(itemService.getAllItems()).thenReturn(items);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.getAllItems();
        ResponseEntity<List<ItemDTO>> itemResponseEntityExpected = ResponseEntity.ok(items);

        // Perform assertions
        verify(itemService, atLeastOnce()).getAllItems();
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_GetAllItemsFail() throws NotFoundException {
        // Define the behavior of the mock
        when(itemService.getAllItems()).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<?> result = itemController.getAllItems();
        ResponseEntity<?> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atLeastOnce()).getAllItems();
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_GetUserItems() throws NotFoundException {
        // Define the behavior of the mock
        when(itemService.getUserItems(JwtTokenDummy)).thenReturn(items);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.getUserItems(JwtTokenDummy);
        ResponseEntity<List<ItemDTO>> itemResponseEntityExpected = ResponseEntity.ok(items);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItems(JwtTokenDummy);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_GetItemById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.getUserItemDTO(JwtTokenDummy, id)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.getUserItemById(JwtTokenDummy, id);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItemDTO(JwtTokenDummy, id);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_CreateItem() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(JwtTokenDummy, itemDTO1)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.createItem(JwtTokenDummy, itemDTO1);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = new ResponseEntity<>(itemDTO1, HttpStatus.CREATED);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(JwtTokenDummy, itemDTO1);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_DeleteItem() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.deleteItem(JwtTokenDummy, id)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.deleteItem(JwtTokenDummy, id);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).deleteItem(JwtTokenDummy, id);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_AddItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(JwtTokenDummy, id, itemDTO1Update)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.addItemStock(JwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(JwtTokenDummy, id, itemDTO1Update);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_RemoveItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 2;
        when(itemService.removeItemStock(JwtTokenDummy, id, itemDTO2Update)).thenReturn(itemDTO2);

        // Call the service method that uses the Repository
        ResponseEntity<?> itemResponseEntity = itemController.removeItemStock(JwtTokenDummy, id, itemDTO2Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO2);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(JwtTokenDummy, id, itemDTO2Update);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

}
