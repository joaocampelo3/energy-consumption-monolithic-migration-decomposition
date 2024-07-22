package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.ShippingOrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.SHIPPING_ORDER_URL;

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
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(SHIPPING_ORDER_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserShippingOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{shippingOrderId}")
    public ResponseEntity<Object> getUserShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }

    }

    @PatchMapping(path = "/{shippingOrderId}/cancel")
    public ResponseEntity<Object> fullCancelShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/cancel", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/reject")
    public ResponseEntity<Object> rejectShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/reject", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/approve")
    public ResponseEntity<Object> approveShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/approve", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/ship")
    public ResponseEntity<Object> shippedShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/ship", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/delivered")
    public ResponseEntity<Object> deliveredShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(shippingOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ShippingOrderUpdateDTO> request = new HttpEntity<>(shippingOrderUpdateDTO, headers);

            return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + shippingOrderId + "/delivered", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        return userController.getUserId(authorizationToken).getBody();
    }
}
