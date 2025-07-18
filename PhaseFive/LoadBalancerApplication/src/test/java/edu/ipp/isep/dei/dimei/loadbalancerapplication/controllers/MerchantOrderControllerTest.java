package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_ORDER_READ_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_ORDER_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantOrderControllerTest {
    @Mock
    RestTemplate restTemplate = new RestTemplate();
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    HttpHeaders headers;
    HttpEntity<UserDTO> requestEntity;
    @InjectMocks
    MerchantOrderController merchantOrderController;
    @Mock
    UserController userController;
    OrderDTO orderDTO;
    MerchantOrderDTO merchantOrderDTO;
    MerchantOrderUpdateDTO merchantOrderUpdateDTO;
    MerchantOrderUpdateDTO merchantOrderDTOExpected;
    List<MerchantOrderDTO> merchantOrderDTOS = new ArrayList<>();
    UserDTO userDTO;
    AddressDTO addressDTO;
    PaymentDTO paymentDTO;
    ItemQuantityDTO itemQuantityDTO1;
    ItemQuantityDTO itemQuantityDTO2;
    List<ItemQuantityDTO> itemQuantityListDTO = new ArrayList<>();
    MerchantDTO merchantDTO;
    LinkedHashMap<String, Object> mockUserControllerResponse;
    LinkedHashMap<String, Object> mockAddressControllerResponse;

    @BeforeEach
    void beforeEach() {
        double price1 = 12.0;
        double price2 = 5.0;

        userDTO = UserDTO.builder().userId(1).email("merchant_email@gmail.com").role(RoleEnum.MERCHANT).build();

        addressDTO = AddressDTO.builder().id(0).street("5th Avenue").zipCode("10128").city("New York").country("USA").build();

        merchantDTO = MerchantDTO.builder().id(0).name("Merchant 1").email("merchant_email@gmail.com").addressId(addressDTO.getId()).userDTO(userDTO).addressDTO(addressDTO).build();

        itemQuantityDTO1 = ItemQuantityDTO.builder().id(1).itemId(1).itemName("Item 1").itemSku("ABC-12345-S-BL").itemDescription("Item 1 Description").qty(2).price(price1).build();

        itemQuantityDTO2 = ItemQuantityDTO.builder().id(2).itemId(2).itemName("Item 2").itemSku("ABC-12345-M-BL").itemDescription("Item 2 Description").qty(1).price(price2).build();

        paymentDTO = PaymentDTO.builder().id(0).amount(itemQuantityDTO1.getPrice() * itemQuantityDTO1.getQty() + itemQuantityDTO2.getPrice() * itemQuantityDTO2.getQty()).paymentDateTime(Instant.now()).paymentMethod(PaymentMethodEnum.CARD).status(PaymentStatusEnum.ACCEPTED).build();

        itemQuantityListDTO.add(itemQuantityDTO1);
        itemQuantityListDTO.add(itemQuantityDTO2);

        orderDTO = new OrderDTO(0, Instant.now(), OrderStatusEnum.PENDING, userDTO.getUserId(), itemQuantityListDTO, price1 + price2, paymentDTO);

        merchantOrderDTO = new MerchantOrderDTO(0, Instant.now(), MerchantOrderStatusEnum.PENDING, userDTO.getUserId(), userDTO.getEmail(), orderDTO.getId(), merchantDTO.getId());

        merchantOrderDTOS.add(merchantOrderDTO);
        merchantOrderUpdateDTO = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO.getId()).merchantOrderDate(merchantOrderDTO.getMerchantOrderDate()).merchantOrderStatus(merchantOrderDTO.getMerchantOrderStatus()).email(userDTO.getEmail()).userDTO(userDTO).build();
        merchantOrderDTOExpected = MerchantOrderUpdateDTO.builder().id(merchantOrderDTO.getId()).merchantOrderDate(merchantOrderDTO.getMerchantOrderDate()).merchantOrderStatus(merchantOrderDTO.getMerchantOrderStatus()).email(userDTO.getEmail()).userDTO(userDTO).build();

        headers = new HttpHeaders();
        headers.set("Authorization", jwtTokenDummy);
        headers.setContentType(MediaType.APPLICATION_JSON);

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
    void test_GetAllMerchantOrders() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.getAllMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTOS, response.getBody());
    }

    @Test
    void test_GetAllMerchantOrdersFail1() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.getAllMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllMerchantOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.getAllMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserMerchantOrders() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTOS, response.getBody());
    }

    @Test
    void test_GetUserMerchantOrdersFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserMerchantOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserMerchantOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTO, HttpStatus.OK);
        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL + "/" + merchantOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL + "/" + merchantOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTO, response.getBody());
    }

    @Test
    void test_GetUserMerchantOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(MERCHANT_ORDER_READ_URL + "/" + merchantOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(MERCHANT_ORDER_READ_URL + "/" + merchantOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserMerchantOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.getUserMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_FullCancelMerchantOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.OK);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.fullCancelMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTOExpected, response.getBody());
    }

    @Test
    void test_FullCancelMerchantOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.fullCancelMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_FullCancelMerchantOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.fullCancelMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_RejectMerchantOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.OK);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.rejectMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTOExpected, response.getBody());
    }

    @Test
    void test_RejectMerchantOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.rejectMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_RejectMerchantOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.rejectMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_ApproveMerchantOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(merchantOrderDTOExpected, HttpStatus.OK);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = merchantOrderController.approveMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(merchantOrderDTOExpected, response.getBody());
    }

    @Test
    void test_ApproveMerchantOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        String url = MERCHANT_ORDER_URL + "/" + merchantOrderDTO.getId() + "/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = merchantOrderController.approveMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_ApproveMerchantOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = merchantOrderController.approveMerchantOrderById(jwtTokenDummy, merchantOrderDTO.getId(), merchantOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}
