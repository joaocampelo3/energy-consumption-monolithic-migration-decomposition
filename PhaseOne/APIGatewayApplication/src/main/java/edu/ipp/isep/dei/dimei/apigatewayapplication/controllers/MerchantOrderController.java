package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.MerchantOrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.MERCHANT_ORDER_URL;

@RestController
@RequestMapping("/merchantorders")
public class MerchantOrderController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;

    @Autowired
    public MerchantOrderController(RestTemplate restTemplate, UserController userController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllMerchantOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(MERCHANT_ORDER_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserMerchantOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(MERCHANT_ORDER_URL, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{merchantOrderId}")
    public ResponseEntity<Object> getUserMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/cancel")
    public ResponseEntity<Object> fullCancelMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

            return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/cancel", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/reject")
    public ResponseEntity<Object> rejectMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

            return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/reject", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/approve")
    public ResponseEntity<Object> approveMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);

            return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/approve", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        return userController.getUserId(authorizationToken).getBody();
    }
}
