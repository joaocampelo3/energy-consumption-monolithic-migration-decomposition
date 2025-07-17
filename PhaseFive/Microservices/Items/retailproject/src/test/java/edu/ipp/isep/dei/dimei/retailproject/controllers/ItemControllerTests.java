package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
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
    final String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    final String exceptionItemNotFound = "Item not found.";
    final String exceptionBadRequest = "You can not create item for this merchant.";
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

    @BeforeEach
    void beforeEach() {

        categoryDTO = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        merchant = Merchant.builder()
                .id(0)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .build();

        merchantDTO = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
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
    void test_CreateItem() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(jwtTokenDummy, itemDTO1, false)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.createItem(jwtTokenDummy, itemDTO1);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = new ResponseEntity<>(itemDTO1, HttpStatus.CREATED);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(jwtTokenDummy, itemDTO1, false);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_CreateItemFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(jwtTokenDummy, itemDTO1, false)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.createItem(jwtTokenDummy, itemDTO1);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(jwtTokenDummy, itemDTO1, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateItemFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(jwtTokenDummy, itemDTO1, false)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.createItem(jwtTokenDummy, itemDTO1);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(jwtTokenDummy, itemDTO1, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeleteItem() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.deleteItem(jwtTokenDummy, id, false)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.deleteItem(jwtTokenDummy, id);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).deleteItem(jwtTokenDummy, id, false);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_DeleteItemFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.deleteItem(jwtTokenDummy, id, false)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.deleteItem(jwtTokenDummy, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).deleteItem(jwtTokenDummy, id, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_AddItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(jwtTokenDummy, id, itemDTO1Update, false)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.addItemStock(jwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(jwtTokenDummy, id, itemDTO1Update, false);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_AddItemStock1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(jwtTokenDummy, id, itemDTO1Update, false)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.addItemStock(jwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(jwtTokenDummy, id, itemDTO1Update, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_AddItemStock2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(jwtTokenDummy, id, itemDTO1Update, false)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.addItemStock(jwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(jwtTokenDummy, id, itemDTO1Update, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RemoveItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 2;
        when(itemService.removeItemStock(jwtTokenDummy, id, itemDTO2Update, false)).thenReturn(itemDTO2);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.removeItemStock(jwtTokenDummy, id, itemDTO2Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO2);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(jwtTokenDummy, id, itemDTO2Update, false);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_RemoveItemStockFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.removeItemStock(jwtTokenDummy, id, itemDTO1Update, false)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.removeItemStock(jwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(jwtTokenDummy, id, itemDTO1Update, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RemoveItemStockFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.removeItemStock(jwtTokenDummy, id, itemDTO1Update, false)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.removeItemStock(jwtTokenDummy, id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(jwtTokenDummy, id, itemDTO1Update, false);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

}
