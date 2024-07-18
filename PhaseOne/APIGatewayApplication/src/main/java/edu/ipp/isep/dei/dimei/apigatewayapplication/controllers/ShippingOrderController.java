package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
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

    @Autowired
    public ShippingOrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllShippingOrders() {
        return restTemplate.getForObject(SHIPPING_ORDER_URL + "/all", ResponseEntity.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserShippingOrders(@RequestHeader("Authorization") String authorizationToken) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id, HttpMethod.GET, requestEntity, Object.class);

    }

    @PatchMapping(path = "/{id}/cancel")
    public ResponseEntity<Object> fullCancelShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ShippingOrderUpdateDTO> requestEntity = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id + "/cancel", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/reject")
    public ResponseEntity<Object> rejectShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ShippingOrderUpdateDTO> requestEntity = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id + "/reject", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/approve")
    public ResponseEntity<Object> approveShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ShippingOrderUpdateDTO> requestEntity = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id + "/approve", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/ship")
    public ResponseEntity<Object> shippedShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ShippingOrderUpdateDTO> requestEntity = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id + "/ship", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/delivered")
    public ResponseEntity<Object> deliveredShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ShippingOrderUpdateDTO> requestEntity = new HttpEntity<>(shippingOrderUpdateDTO, headers);

        return restTemplate.exchange(SHIPPING_ORDER_URL + "/" + id + "/delivered", HttpMethod.PATCH, requestEntity, Object.class);
    }
}
