package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

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
        Object object = getUserDTO(authorizationToken);

        if (object instanceof UserDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(headers);
            try {
                return restTemplate.exchange(CATEGORY_URL + "/all", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
                });
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) object;
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCategoryById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            try {
                return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestHeader("Authorization") String authorizationToken, @RequestBody CategoryDTO categoryDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            try {
                return restTemplate.exchange(CATEGORY_URL, HttpMethod.POST, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO) {
            String url = CATEGORY_URL + "/{id}";
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, id);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<Object> deleteCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            try {
                return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.DELETE, request, Object.class);
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
