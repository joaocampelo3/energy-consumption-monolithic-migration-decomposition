package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.MerchantOrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_ORDER_URL;

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
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL + "/all", HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserMerchantOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{merchantOrderId}")
    public ResponseEntity<Object> getUserMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/cancel")
    public ResponseEntity<Object> fullCancelMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/cancel", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/reject")
    public ResponseEntity<Object> rejectMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/reject", HttpMethod.PATCH, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{merchantOrderId}/approve")
    public ResponseEntity<Object> approveMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantOrderId, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantOrderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantOrderUpdateDTO> request = new HttpEntity<>(merchantOrderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + merchantOrderId + "/approve", HttpMethod.PATCH, request, Object.class);
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
