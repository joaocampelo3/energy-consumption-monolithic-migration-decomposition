package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.MerchantOrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.MERCHANT_ORDER_URL;

@RestController
@RequestMapping("/merchantorders")
public class MerchantOrderController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;

    @Autowired
    public MerchantOrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<List<MerchantOrderDTO>> getAllMerchantOrders() {
        return restTemplate.getForObject(MERCHANT_ORDER_URL + "/all", ResponseEntity.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserMerchantOrders(@RequestHeader("Authorization") String authorizationToken) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(MERCHANT_ORDER_URL, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + id, HttpMethod.GET, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/cancel")
    public ResponseEntity<Object> fullCancelMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<MerchantOrderUpdateDTO> requestEntity = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + id + "/cancel", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/reject")
    public ResponseEntity<Object> rejectMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<MerchantOrderUpdateDTO> requestEntity = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + id + "/reject", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/approve")
    public ResponseEntity<Object> approveMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<MerchantOrderUpdateDTO> requestEntity = new HttpEntity<>(merchantOrderUpdateDTO, headers);

        return restTemplate.exchange(MERCHANT_ORDER_URL + "/" + id + "/approve", HttpMethod.PATCH, requestEntity, Object.class);
    }
}
