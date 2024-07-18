package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.CATEGORY_URL;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final RestTemplate restTemplate;

    @Autowired
    public CategoryController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return restTemplate.getForObject(CATEGORY_URL + "/all", ResponseEntity.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable int id) {
        return restTemplate.getForObject(CATEGORY_URL + "/" + id, ResponseEntity.class);
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody CategoryDTO categoryDTO) {
        HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO);
        return restTemplate.postForObject(CATEGORY_URL, request, ResponseEntity.class);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        HttpEntity<CategoryDTO> request = new HttpEntity<>(categoryDTO);
        return restTemplate.patchForObject(CATEGORY_URL + "/" + id, request, ResponseEntity.class);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<Object> deleteCategory(@PathVariable int id) {
        return restTemplate.exchange(CATEGORY_URL + "/" + id, HttpMethod.DELETE, null, Object.class);
    }
}
