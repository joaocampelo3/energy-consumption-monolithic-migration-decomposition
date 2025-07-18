package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ShippingOrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.SHIPPING_ORDER_READ_URL;
import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.SHIPPING_ORDER_URL;

@RestController
@RequestMapping("/shippingorders")
public class ShippingOrderController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;

    @Autowired
    public ShippingOrderController(RestTemplate restTemplate, UserController userController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllShippingOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            try {
                return restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/all", HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserShippingOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(SHIPPING_ORDER_READ_URL, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{shippingOrderId}")
    public ResponseEntity<Object> getUserShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(SHIPPING_ORDER_READ_URL + "/" + shippingOrderId, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }

    }

    @PatchMapping(path = "/{shippingOrderId}/cancel")
    public ResponseEntity<Object> fullCancelShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/cancel", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/reject")
    public ResponseEntity<Object> rejectShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/reject", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/approve")
    public ResponseEntity<Object> approveShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/approve", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/ship")
    public ResponseEntity<Object> shippedShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/ship", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/delivered")
    public ResponseEntity<Object> deliveredShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/delivered", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        ResponseEntity<Object> objectResponseEntity = userController.getUserId(authorizationToken);

        if (objectResponseEntity.getStatusCode() == HttpStatus.OK && objectResponseEntity.getBody() instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(objectResponseEntity.getBody(), UserDTO.class);
        } else if (objectResponseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || objectResponseEntity.getStatusCode() == HttpStatus.FORBIDDEN || objectResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            return objectResponseEntity;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected response type");
    }
}
