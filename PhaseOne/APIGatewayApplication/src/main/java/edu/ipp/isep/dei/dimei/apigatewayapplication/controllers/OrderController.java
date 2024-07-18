package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.OrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.ORDER_URL;

@RestController
@RequestMapping("/orders")
public class OrderController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;

    @Autowired
    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return restTemplate.getForObject(ORDER_URL + "/all", ResponseEntity.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserOrders(@RequestHeader("Authorization") String authorizationToken) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(ORDER_URL, HttpMethod.GET, requestEntity, Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderCreateDTO orderDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<OrderCreateDTO> requestEntity = new HttpEntity<>(orderDTO, headers);

        return restTemplate.exchange(ORDER_URL, HttpMethod.POST, requestEntity, Object.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<OrderCreateDTO> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(ORDER_URL + "/" + id, HttpMethod.GET, requestEntity, Object.class);
    }

    @DeleteMapping(path = "/user/{userId}/order/{orderId}")
    public ResponseEntity<Object> deleteOrder(@PathVariable int userId, @PathVariable int orderId) {
        return restTemplate.exchange(ORDER_URL + "/user/" + userId + "/order/" + orderId, HttpMethod.DELETE, null, Object.class);
    }

    @PatchMapping(path = "/{id}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<OrderUpdateDTO> requestEntity = new HttpEntity<>(orderUpdateDTO, headers);

        return restTemplate.exchange(ORDER_URL + "/" + id + "/cancel", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/reject")
    public ResponseEntity<Object> rejectOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<OrderUpdateDTO> requestEntity = new HttpEntity<>(orderUpdateDTO, headers);

        return restTemplate.exchange(ORDER_URL + "/" + id + "/reject", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/approve")
    public ResponseEntity<Object> approveOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<OrderUpdateDTO> requestEntity = new HttpEntity<>(orderUpdateDTO, headers);

        return restTemplate.exchange(ORDER_URL + "/" + id + "/approve", HttpMethod.PATCH, requestEntity, Object.class);
    }
}
