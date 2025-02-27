package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Category Controller")
@RequiredArgsConstructor
@RequestMapping("/categories")
@CacheConfig(cacheNames = "categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    @Cacheable
    @Operation(
            description = "Get all categories",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categories found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Ok\",\"Message\": \"[{\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 desc\"},{\"id\": 2, \"name\": \"Category 2\", \"description\": \"Category 2 desc\"}]}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return new ResponseEntity<>(this.categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Cacheable(key = "#id")
    @Operation(
            description = "Get category by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Ok\",\"Message\": \"{\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 desc\"}}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> getCategoryById(@PathVariable int id) {
        try {
            return new ResponseEntity<>(this.categoryService.getCategory(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true)}
    )
    @Operation(
            description = "Create a category",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Category created.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 201,\"Status\": \"Created\",\"Message\": \"{\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 desc\"}}"
                                            )
                                    }
                            )
                    )
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Category was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(this.categoryService.createCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{id}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#id")
            }
    )
    @Operation(
            description = "Update a category",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Category updated.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 202,\"Status\": \"Accepted\",\"Message\": \"{\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 desc\"}}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> updateCategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        try {
            return new ResponseEntity<>(this.categoryService.updateCategory(id, categoryDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{id}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#id")
            }
    )
    @Operation(
            description = "Delete a category",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category deleted.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Deleted\",\"Message\": \"{\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 desc\"}}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> deleteCategory(@PathVariable int id) {
        try {
            return new ResponseEntity<>(this.categoryService.deleteCategory(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
