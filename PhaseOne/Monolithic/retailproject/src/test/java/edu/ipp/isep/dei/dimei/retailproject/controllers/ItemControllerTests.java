package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTests {
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

    MerchantDTO merchantDTO;

    AddressDTO addressDTO;

    UserDTO userDTO;

    @BeforeEach
    void beforeEach() {

        categoryDTO = CategoryDTO.builder()
                .id(1)
                .name("Category 1")
                .description("Category 1 Description")
                .build();

        addressDTO = AddressDTO.builder()
                .id(1)
                .street("5th Avenue")
                .zipCode("10128")
                .city("New York")
                .country("USA")
                .build();

        merchantDTO = MerchantDTO.builder()
                .id(1)
                .name("Merchant 1")
                .email("merchant_email@gmail.com")
                .addressId(addressDTO.getId())
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
                .userDTO(userDTO)
                .build();

        itemDTO1Update = ItemUpdateDTO.builder()
                .id(1)
                .price(10.0)
                .quantityInStock(14)
                .userDTO(userDTO)
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
                .userDTO(userDTO)
                .build();

        itemDTO2Update = ItemUpdateDTO.builder()
                .id(2)
                .price(20.0)
                .quantityInStock(14)
                .userDTO(userDTO)
                .build();

        items.add(itemDTO1);
        items.add(itemDTO2);

        userDTO = new UserDTO(1, "johndoe1234@gmail.com", RoleEnum.USER);
    }

    @Test
    void test_GetAllItems() throws NotFoundException {
        // Define the behavior of the mock
        when(itemService.getAllItems()).thenReturn(items);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.getAllItems();
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
        ResponseEntity<Object> result = itemController.getAllItems();
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

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
        when(itemService.getUserItems(userDTO)).thenReturn(items);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.getUserItems(userDTO);
        ResponseEntity<List<ItemDTO>> itemResponseEntityExpected = ResponseEntity.ok(items);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItems(userDTO);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_GetUserItemsFail() throws NotFoundException {
        // Define the behavior of the mock
        when(itemService.getUserItems(userDTO)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.getUserItems(userDTO);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItems(userDTO);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_GetItemById() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.getUserItemDTO(userDTO, id)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.getUserItemById(userDTO, id);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItemDTO(userDTO, id);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_GetItemByIdFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.getUserItemDTO(userDTO, id)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.getUserItemById(userDTO, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atLeastOnce()).getUserItemDTO(userDTO, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateItem() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(itemDTO1)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.createItem(itemDTO1);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = new ResponseEntity<>(itemDTO1, HttpStatus.CREATED);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(itemDTO1);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_CreateItemFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(itemDTO1)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.createItem(itemDTO1);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(itemDTO1);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_CreateItemFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        when(itemService.createItem(itemDTO1)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.createItem(itemDTO1);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).createItem(itemDTO1);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_DeleteItem() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.deleteItem(userDTO, id)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.deleteItem(userDTO, id);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).deleteItem(userDTO, id);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_DeleteItemFail() throws NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.deleteItem(userDTO, id)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.deleteItem(userDTO, id);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).deleteItem(userDTO, id);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_AddItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(id, itemDTO1Update)).thenReturn(itemDTO1);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.addItemStock(id, itemDTO1Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO1);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(id, itemDTO1Update);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_AddItemStock1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(id, itemDTO1Update)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.addItemStock(id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(id, itemDTO1Update);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_AddItemStock2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.addItemStock(id, itemDTO1Update)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.addItemStock(id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).addItemStock(id, itemDTO1Update);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RemoveItemStock() throws NotFoundException, InvalidQuantityException, BadPayloadException {
        // Define the behavior of the mock
        int id = 2;
        when(itemService.removeItemStock(id, itemDTO2Update)).thenReturn(itemDTO2);

        // Call the service method that uses the Repository
        ResponseEntity<Object> itemResponseEntity = itemController.removeItemStock(id, itemDTO2Update);
        ResponseEntity<ItemDTO> itemResponseEntityExpected = ResponseEntity.ok(itemDTO2);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(id, itemDTO2Update);
        assertNotNull(itemResponseEntity);
        assertEquals(itemResponseEntityExpected, itemResponseEntity);
    }

    @Test
    void test_RemoveItemStockFail1() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.removeItemStock(id, itemDTO1Update)).thenThrow(new NotFoundException(exceptionItemNotFound));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.removeItemStock(id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionItemNotFound, HttpStatus.NOT_FOUND);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(id, itemDTO1Update);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void test_RemoveItemStockFail2() throws InvalidQuantityException, BadPayloadException, NotFoundException {
        // Define the behavior of the mock
        int id = 1;
        when(itemService.removeItemStock(id, itemDTO1Update)).thenThrow(new BadPayloadException(exceptionBadRequest));

        // Call the service method that uses the Repository
        ResponseEntity<Object> result = itemController.removeItemStock(id, itemDTO1Update);
        ResponseEntity<Object> expected = new ResponseEntity<>(exceptionBadRequest, HttpStatus.BAD_REQUEST);

        // Perform assertions
        verify(itemService, atMostOnce()).removeItemStock(id, itemDTO1Update);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

}
