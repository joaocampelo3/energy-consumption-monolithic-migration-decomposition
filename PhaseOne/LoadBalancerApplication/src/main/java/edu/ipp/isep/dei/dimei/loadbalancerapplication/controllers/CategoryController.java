package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.CATEGORY_URL;

@RestController
@RequestMapping("/categories")
public class CategoryController implements HttpHeaderBuilder {

    private static Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final RestTemplate restTemplate;
    private final UserController userController;

    @Autowired
    public CategoryController(RestTemplate restTemplate, UserController userController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllCategories(@RequestHeader("Authorization") String authorizationToken) {
        logger("The getAllCategories() started...", null);
        Object object = getUserDTO(authorizationToken);

        if (object instanceof UserDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(headers);
            logger("The Categories getall request:\n {}", request);
            return restTemplate.exchange(CATEGORY_URL + "/all", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        } else {
            return (ResponseEntity<Object>) object;
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCategoryById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        logger("The getCategoryById() started...", null);
        Object body = getUserDTO(authorizationToken);

        logger("The authorization result:\n {}", body);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            logger("The Categories getbyid request:\n {}", request);
            return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestHeader("Authorization") String authorizationToken, @RequestBody CategoryDTO categoryDTO) {
        logger("The createCategory() started...", null);
        Object body = getUserDTO(authorizationToken);

        logger("The authorization result:\n {}", body);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            logger("The Categories creation request:\n {}", request);
            return restTemplate.exchange(CATEGORY_URL, HttpMethod.POST, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        logger("The updateCategory() started...", null);
        Object body = getUserDTO(authorizationToken);

        logger("The authorization result:\n {}", body);


        if (body instanceof UserDTO userDTO) {
            String url = CATEGORY_URL + "/{id}";
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO, headers);
            logger("The Categories update request:\n {}", request);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            return restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, id);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<Object> deleteCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        logger("The deleteCategory() started...", null);
        Object body = getUserDTO(authorizationToken);

        logger("The authorization result:\n {}", body);


        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            logger("The Categories delete request:\n {}", request);
            return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.DELETE, request, Object.class);
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
            logger("String response body identified: {}", objectResponseEntity.getBody());

            return objectResponseEntity.getBody();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected response type");
    }

    private void logger(String message, Object object) {
        logger.info(message, object);
    }
}
