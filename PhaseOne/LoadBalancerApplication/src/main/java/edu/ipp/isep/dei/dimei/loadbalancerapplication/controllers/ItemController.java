package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.ItemUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ITEM_URL;

@RestController
@RequestMapping("/items")
public class ItemController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;

    @Autowired
    public ItemController(RestTemplate restTemplate, UserController userController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItems(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(ITEM_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(ITEM_URL, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserItemById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(ITEM_URL + "/" + id, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("Authorization") String authorizationToken, @RequestBody ItemDTO itemDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(itemDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ItemDTO> requestEntity = new HttpEntity<>(itemDTO, headers);

            return restTemplate.postForObject(ITEM_URL, requestEntity, ResponseEntity.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(ITEM_URL + "/" + id, HttpMethod.DELETE, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{id}/addStock")
    public ResponseEntity<Object> addItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(itemUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ItemUpdateDTO> requestEntity = new HttpEntity<>(itemUpdateDTO, headers);

            return restTemplate.exchange(ITEM_URL + "/" + id + "/addStock", HttpMethod.PATCH, requestEntity, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{id}/removeStock")
    public ResponseEntity<Object> removeItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(itemUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<ItemUpdateDTO> requestEntity = new HttpEntity<>(itemUpdateDTO, headers);
            return restTemplate.exchange(ITEM_URL + "/" + id + "/removeStock", HttpMethod.PATCH, requestEntity, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        return userController.getUserId(authorizationToken).getBody();
    }

}
