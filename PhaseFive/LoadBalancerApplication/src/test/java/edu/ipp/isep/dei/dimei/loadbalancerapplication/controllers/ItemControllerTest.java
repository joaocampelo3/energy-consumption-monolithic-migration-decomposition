package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ITEM_READ_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ITEM_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    RestTemplate restTemplate = new RestTemplate();
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    HttpHeaders headers;
    HttpEntity<UserDTO> requestUserEntity;
    HttpEntity<UserDTO> requestEntity;
    @InjectMocks
    ItemController itemController;
    @Mock
    UserController userController;
    List<ItemDTO> itemDTOS = new ArrayList<>();
    UserDTO userDTO;
    AddressDTO addressDTO;
    CategoryDTO categoryDTO;
    ItemDTO itemDTO;
    MerchantDTO merchantDTO;
    ItemUpdateDTO itemUpdateDTO;
    LinkedHashMap<String, Object> mockUserControllerResponse;
    LinkedHashMap<String, Object> mockAddressControllerResponse;

    @BeforeEach
    void beforeEach() {
        double price1 = 12.0;

        userDTO = UserDTO.builder().userId(1).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        addressDTO = AddressDTO.builder().id(0).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        merchantDTO = MerchantDTO.builder().id(0).name("Merchant 1").email("merchant_email@gmail.com").addressId(addressDTO.getId()).userDTO(userDTO).addressDTO(addressDTO).build();

        categoryDTO = new CategoryDTO(1, "Category 1", "Category 1 desc", userDTO);

        itemDTO = new ItemDTO(1, "Item 1", "ABC-12345-S-BL", "Item 1 Description", price1, 2, categoryDTO, merchantDTO, userDTO);

        itemDTOS.add(itemDTO);

        itemUpdateDTO = new ItemUpdateDTO(itemDTO.getId(), itemDTO.getSku(), itemDTO.getPrice(), itemDTO.getQuantityInStock(), itemDTO.getUserDTO());

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

        requestUserEntity = new HttpEntity<>(headers);

        mockUserControllerResponse = new LinkedHashMap<>();
        mockUserControllerResponse.put("userId", userDTO.getUserId());
        mockUserControllerResponse.put("email", userDTO.getEmail());
        mockUserControllerResponse.put("role", userDTO.getRole().name());

        mockAddressControllerResponse = new LinkedHashMap<>();
        mockAddressControllerResponse.put("id", addressDTO.getId());
        mockAddressControllerResponse.put("street", addressDTO.getStreet());
        mockAddressControllerResponse.put("zipCode", addressDTO.getZipCode());
        mockAddressControllerResponse.put("city", addressDTO.getCity());
        mockAddressControllerResponse.put("country", addressDTO.getCountry());
    }

    @Test
    void test_GetAllItems() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTOS, HttpStatus.OK);
        when(restTemplate.exchange(ITEM_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.getAllItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTOS, response.getBody());
    }

    @Test
    void test_GetAllItemsFail1() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ITEM_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.getAllItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllItemsFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.getAllItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserItems() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTOS, HttpStatus.OK);
        when(restTemplate.exchange(ITEM_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.getUserItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTOS, response.getBody());
    }

    @Test
    void test_GetUserItemsFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ITEM_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.getUserItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserItemsFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.getUserItems(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateItem() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemDTO> requestItemEntity = new HttpEntity<>(itemDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTO, HttpStatus.OK);
        when(restTemplate.exchange(ITEM_URL, HttpMethod.POST, requestItemEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.createItem(jwtTokenDummy, itemDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_URL, HttpMethod.POST, requestItemEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
    }

    @Test
    void test_CreateItemFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemDTO> requestItemEntity = new HttpEntity<>(itemDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ITEM_URL, HttpMethod.POST, requestItemEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.createItem(jwtTokenDummy, itemDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_URL, HttpMethod.POST, requestItemEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_CreateItemFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.createItem(jwtTokenDummy, itemDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserItemById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTO, HttpStatus.OK);
        when(restTemplate.exchange(ITEM_READ_URL + "/{itemId}", HttpMethod.GET, requestEntity, Object.class, itemDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.getUserItemById(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL + "/{itemId}", HttpMethod.GET, requestEntity, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
    }

    @Test
    void test_GetUserItemByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        when(restTemplate.exchange(ITEM_READ_URL + "/{itemId}", HttpMethod.GET, requestEntity, Object.class, itemDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.getUserItemById(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_READ_URL + "/{itemId}", HttpMethod.GET, requestEntity, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserItemByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.getUserItemById(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_DeleteItem() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTO, HttpStatus.OK);
        when(restTemplate.exchange(ITEM_URL + "/" + itemDTO.getId(), HttpMethod.DELETE, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.deleteItem(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_URL + "/" + itemDTO.getId(), HttpMethod.DELETE, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemDTO, response.getBody());
    }

    @Test
    void test_DeleteOrderFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ITEM_URL + "/" + itemDTO.getId(), HttpMethod.DELETE, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.deleteItem(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ITEM_URL + "/" + itemDTO.getId(), HttpMethod.DELETE, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_DeleteOrderFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.deleteItem(jwtTokenDummy, itemDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_AddItemStock() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTO, HttpStatus.OK);

        itemUpdateDTO.setQuantityInStock(itemDTO.getQuantityInStock() + 1);
        HttpEntity<ItemUpdateDTO> request = new HttpEntity<>(itemUpdateDTO, headers);

        String url = ITEM_URL + "/{id}/addStock";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.addItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        itemDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() + 1);
        assertEquals(itemDTO, response.getBody());
    }

    @Test
    void test_AddItemStockFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        itemUpdateDTO.setQuantityInStock(itemDTO.getQuantityInStock() + 1);
        HttpEntity<ItemUpdateDTO> request = new HttpEntity<>(itemUpdateDTO, headers);

        String url = ITEM_URL + "/{id}/addStock";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.addItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_AddItemStockFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.addItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_RemoveItemStock() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(itemDTO, HttpStatus.OK);

        itemUpdateDTO.setQuantityInStock(itemDTO.getQuantityInStock() - 1);
        HttpEntity<ItemUpdateDTO> request = new HttpEntity<>(itemUpdateDTO, headers);

        String url = ITEM_URL + "/{id}/removeStock";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = itemController.removeItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        itemDTO.setQuantityInStock(itemUpdateDTO.getQuantityInStock() - 1);
        assertEquals(itemDTO, response.getBody());
    }

    @Test
    void test_RemoveItemStockFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        itemUpdateDTO.setQuantityInStock(itemDTO.getQuantityInStock() - 1);
        HttpEntity<ItemUpdateDTO> request = new HttpEntity<>(itemUpdateDTO, headers);

        String url = ITEM_URL + "/{id}/removeStock";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = itemController.removeItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, itemDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_RemoveItemStockFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = itemController.removeItemStock(jwtTokenDummy, itemDTO.getId(), itemUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}
