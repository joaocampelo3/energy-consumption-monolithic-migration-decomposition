package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.CATEGORY_URL;

@RestController
@RequestMapping("/categories")
public class CategoryController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;

    @Autowired
    public CategoryController(RestTemplate restTemplate, UserController userController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllCategories(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(CATEGORY_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCategoryById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestHeader("Authorization") String authorizationToken, @RequestBody CategoryDTO categoryDTO) {
        Object body = getUserDTO(authorizationToken);
        if (body instanceof UserDTO userDTO && userDTO.equals(categoryDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            return restTemplate.postForObject(CATEGORY_URL, request, ResponseEntity.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        Object body = getUserDTO(authorizationToken);
        if (body instanceof UserDTO userDTO && userDTO.equals(categoryDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            return restTemplate.patchForObject(CATEGORY_URL + "/" + id, request, ResponseEntity.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<Object> deleteCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);
        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.DELETE, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        return userController.getUserId(authorizationToken).getBody();
    }
}
