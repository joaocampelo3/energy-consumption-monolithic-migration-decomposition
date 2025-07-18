package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ShippingOrderUpdateDTO;
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

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.SHIPPING_ORDER_READ_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.SHIPPING_ORDER_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingOrderControllerTest {
    @Mock
    RestTemplate restTemplate = new RestTemplate();
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    HttpHeaders headers;
    HttpEntity<UserDTO> requestEntity;
    @InjectMocks
    ShippingOrderController shippingOrderController;
    @Mock
    UserController userController;
    OrderDTO orderDTO;
    MerchantOrderDTO merchantOrderDTO;
    ShippingOrderDTO shippingOrderDTO;
    ShippingOrderUpdateDTO shippingOrderUpdateDTO;
    ShippingOrderUpdateDTO shippingOrderDTOExpected;
    List<ShippingOrderDTO> shippingOrderDTOS = new ArrayList<>();
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
        shippingOrderDTO = new ShippingOrderDTO(0, Instant.now(), ShippingOrderStatusEnum.PENDING, addressDTO.getId(), orderDTO.getId(), merchantOrderDTO.getId(), userDTO.getEmail());

        shippingOrderDTOS.add(shippingOrderDTO);
        shippingOrderUpdateDTO = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO.getId())
                .shippingOrderDate(shippingOrderDTO.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO.getShippingOrderStatus())
                .addressId(addressDTO.getId())
                .orderId(orderDTO.getId())
                .merchantOrderId(merchantDTO.getId())
                .userId(userDTO.getUserId())
                .userDTO(userDTO).build();
        shippingOrderDTOExpected = ShippingOrderUpdateDTO.builder()
                .id(shippingOrderDTO.getId())
                .shippingOrderDate(shippingOrderDTO.getShippingOrderDate())
                .shippingOrderStatus(shippingOrderDTO.getShippingOrderStatus())
                .addressId(addressDTO.getId())
                .orderId(orderDTO.getId())
                .merchantOrderId(merchantDTO.getId())
                .userId(userDTO.getUserId())
                .userDTO(userDTO).build();
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
    void test_GetAllShippingOrders() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.getAllShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOS, response.getBody());
    }

    @Test
    void test_GetAllShippingOrdersFail1() {
        // Mocked response object
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.getAllShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllShippingOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.getAllShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserShippingOrders() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOS, response.getBody());
    }

    @Test
    void test_GetUserShippingOrdersFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserShippingOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTO, HttpStatus.OK);
        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/" + shippingOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL + "/" + shippingOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTO, response.getBody());
    }

    @Test
    void test_GetUserShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/" + shippingOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(SHIPPING_ORDER_READ_URL + "/" + shippingOrderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.getUserShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_FullCancelShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.OK);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.fullCancelShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOExpected, response.getBody());
    }

    @Test
    void test_FullCancelShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.fullCancelShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_FullCancelShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.fullCancelShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_RejectShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.OK);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.rejectShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOExpected, response.getBody());
    }

    @Test
    void test_RejectShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.rejectShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_RejectShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.rejectShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_ApproveShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.OK);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.approveShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOExpected, response.getBody());
    }

    @Test
    void test_ApproveShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.approveShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_ApproveShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.approveShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_ShippedShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.OK);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/ship";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.shippedShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOExpected, response.getBody());
    }

    @Test
    void test_ShippedShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/ship";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.shippedShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_ShippedShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.shippedShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_DeliveredShippingOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(shippingOrderDTOExpected, HttpStatus.OK);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/delivered";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = shippingOrderController.deliveredShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingOrderDTOExpected, response.getBody());
    }

    @Test
    void test_DeliveredShippingOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        String url = SHIPPING_ORDER_URL + "/" + shippingOrderDTO.getId() + "/delivered";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = shippingOrderController.deliveredShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_DeliveredShippingOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = shippingOrderController.deliveredShippingOrderById(jwtTokenDummy, shippingOrderDTO.getId(), shippingOrderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}
