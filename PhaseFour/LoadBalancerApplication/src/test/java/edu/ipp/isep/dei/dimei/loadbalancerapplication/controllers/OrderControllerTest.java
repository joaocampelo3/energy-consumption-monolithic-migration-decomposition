package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.*;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.OrderStatusEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentMethodEnum;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.PaymentStatusEnum;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ORDER_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.security.common.SecurityGlobalVariables.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock
    RestTemplate restTemplate = new RestTemplate();
    String jwtTokenDummy = BEARER_PREFIX + "AAA1bbb2CcC3";
    HttpHeaders headers;
    HttpEntity<UserDTO> requestUserEntity;
    HttpEntity<UserDTO> requestEntity;
    @InjectMocks
    OrderController orderController;
    @Mock
    UserController userController;
    @Mock
    AddressController addressController;
    OrderDTO orderDTO;
    OrderCreateDTO orderCreateDTO;
    OrderUpdateDTO orderUpdateDTO;
    OrderUpdateDTO orderDTOExpected;
    List<OrderDTO> orderDTOS = new ArrayList<>();
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

        orderCreateDTO = OrderCreateDTO.builder().id(orderDTO.getId()).orderDate(orderDTO.getOrderDate()).orderStatus(orderDTO.getOrderStatus()).customerId(orderDTO.getCustomerId()).email(userDTO.getEmail()).orderItems(orderDTO.getOrderItems()).totalPrice(orderDTO.getTotalPrice()).payment(paymentDTO).merchantId(merchantDTO.getId()).address(addressDTO).build();

        orderDTOS.add(orderDTO);
        orderUpdateDTO = OrderUpdateDTO.builder().id(orderDTO.getId()).orderDate(orderDTO.getOrderDate()).orderStatus(orderDTO.getOrderStatus()).email(userDTO.getEmail()).userDTO(userDTO).build();
        orderDTOExpected = OrderUpdateDTO.builder().id(orderDTO.getId()).orderDate(orderDTO.getOrderDate()).orderStatus(orderDTO.getOrderStatus()).email(userDTO.getEmail()).userDTO(userDTO).build();

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
    void test_GetAllOrders() {
        // Mocked response object
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(ORDER_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.getAllOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOS, response.getBody());
    }

    @Test
    void test_GetAllOrdersFail1() {
        // Mocked response object
        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ORDER_URL + "/all", HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.getAllOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL + "/all", HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetAllOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.getAllOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserOrders() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(ORDER_URL, HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.getUserOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOS, response.getBody());
    }

    @Test
    void test_GetUserOrdersFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ORDER_URL, HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.getUserOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL, HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserOrdersFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.getUserOrders(jwtTokenDummy);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateOrder() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderCreateDTO> requestOrderEntity = new HttpEntity<>(orderCreateDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOS, HttpStatus.OK);
        when(restTemplate.exchange(ORDER_URL, HttpMethod.POST, requestOrderEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.createOrder(jwtTokenDummy, orderCreateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(ORDER_URL, HttpMethod.POST, requestOrderEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOS, response.getBody());
    }

    @Test
    void test_CreateOrderFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderCreateDTO> requestOrderEntity = new HttpEntity<>(orderCreateDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        when(restTemplate.exchange(ORDER_URL, HttpMethod.POST, requestOrderEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.createOrder(jwtTokenDummy, orderCreateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        verify(restTemplate, times(1)).exchange(ORDER_URL, HttpMethod.POST, requestOrderEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_CreateOrderFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> mockAddressResponseEntity = new ResponseEntity<>(mockAddressControllerResponse, HttpStatus.OK);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(mockAddressResponseEntity);

        ResponseEntity<Object> response = orderController.createOrder(jwtTokenDummy, orderCreateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_CreateOrderFail3() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);
        when(addressController.createAddress(jwtTokenDummy, addressDTO)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.createOrder(jwtTokenDummy, orderCreateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(addressController, times(1)).createAddress(jwtTokenDummy, addressDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_GetUserOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTO, HttpStatus.OK);
        when(restTemplate.exchange(ORDER_URL + "/" + orderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.getUserOrderById(jwtTokenDummy, orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL + "/" + orderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void test_GetUserOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        when(restTemplate.exchange(ORDER_URL + "/" + orderDTO.getId(), HttpMethod.GET, requestEntity, Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.getUserOrderById(jwtTokenDummy, orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(ORDER_URL + "/" + orderDTO.getId(), HttpMethod.GET, requestEntity, Object.class);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_GetUserOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.getUserOrderById(jwtTokenDummy, orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_DeleteOrder() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);
        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTO, HttpStatus.OK);
        String url = ORDER_URL + "/user/{userId}/order/{orderId}";
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class, userDTO.getUserId(), orderDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.deleteOrder(jwtTokenDummy, userDTO.getUserId(), orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestEntity, Object.class, userDTO.getUserId(), orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void test_DeleteOrderFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>(userDTO, headers);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        String url = ORDER_URL + "/user/{userId}/order/{orderId}";
        when(restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class, userDTO.getUserId(), orderDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.deleteOrder(jwtTokenDummy, userDTO.getUserId(), orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.DELETE, requestEntity, Object.class, userDTO.getUserId(), orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_DeleteOrderFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.deleteOrder(jwtTokenDummy, userDTO.getUserId(), orderDTO.getId());

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_FullCancelOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOExpected, HttpStatus.OK);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.fullCancelOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOExpected, response.getBody());
    }

    @Test
    void test_FullCancelOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/cancel";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.fullCancelOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_FullCancelOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.fullCancelOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_RejectOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOExpected, HttpStatus.OK);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.rejectOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOExpected, response.getBody());
    }

    @Test
    void test_RejectOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/reject";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.rejectOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_RejectOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.rejectOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }

    @Test
    void test_ApproveOrderById() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(orderDTOExpected, HttpStatus.OK);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenReturn(mockResponseEntity);

        ResponseEntity<Object> response = orderController.approveOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTOExpected, response.getBody());
    }

    @Test
    void test_ApproveOrderByIdFail1() {
        // Mocked response object
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> mockUserResponseEntity = new ResponseEntity<>(mockUserControllerResponse, HttpStatus.OK);
        when(userController.getUserId(jwtTokenDummy)).thenReturn(mockUserResponseEntity);

        HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

        String url = ORDER_URL + "/{orderId}/approve";
        when(restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception message"));

        ResponseEntity<Object> response = orderController.approveOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        verify(restTemplate, times(1)).exchange(url, HttpMethod.PATCH, request, Object.class, orderDTO.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Exception message", response.getBody());
    }

    @Test
    void test_ApproveOrderByIdFail2() {
        // Mocked response object
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("Exception message", HttpStatus.NOT_FOUND);

        when(userController.getUserId(jwtTokenDummy)).thenReturn(responseEntity);

        ResponseEntity<Object> response = orderController.approveOrderById(jwtTokenDummy, orderDTO.getId(), orderUpdateDTO);

        verify(userController, times(1)).getUserId(jwtTokenDummy);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Exception message", response.getBody());
    }
}
