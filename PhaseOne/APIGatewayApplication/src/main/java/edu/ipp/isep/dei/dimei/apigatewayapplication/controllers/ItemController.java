package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.updates.ItemUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.ITEM_URL;

@RestController
@RequestMapping("/items")
public class ItemController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;

    @Autowired
    public ItemController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItems() {
        return restTemplate.getForObject(ITEM_URL + "/all", ResponseEntity.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("Authorization") String authorizationToken) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(ITEM_URL, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserItemById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<Integer> requestEntity = new HttpEntity<>(id, headers);

        return restTemplate.exchange(ITEM_URL + "/" + id, HttpMethod.GET, requestEntity, Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("Authorization") String authorizationToken, @RequestBody ItemDTO itemDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ItemDTO> requestEntity = new HttpEntity<>(itemDTO, headers);

        return restTemplate.postForObject(ITEM_URL, requestEntity, ResponseEntity.class);

    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(ITEM_URL + "/" + id, HttpMethod.DELETE, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/addStock")
    public ResponseEntity<Object> addItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ItemUpdateDTO> requestEntity = new HttpEntity<>(itemUpdateDTO, headers);

        return restTemplate.exchange(ITEM_URL + "/" + id + "/addStock", HttpMethod.PATCH, requestEntity, Object.class);
    }

    @PatchMapping(path = "/{id}/removeStock")
    public ResponseEntity<Object> removeItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        HttpEntity<ItemUpdateDTO> requestEntity = new HttpEntity<>(itemUpdateDTO, headers);

        return restTemplate.exchange(ITEM_URL + "/" + id + "/removeStock", HttpMethod.PATCH, requestEntity, Object.class);
    }

}