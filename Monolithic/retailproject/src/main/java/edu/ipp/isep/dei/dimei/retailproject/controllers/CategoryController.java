package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Category Controller")
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Get all categories", responses = {@ApiResponse(responseCode = "200", description = "Categories found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@RequestHeader("Authorization") String authorizationToken) {
        return new ResponseEntity<>(this.categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCategoryById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.categoryService.getCategory(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(
            description = "Create a category",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Category was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE/*,
                                            examples = {
                                                    @ExampleObject(
                                                            value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}"
                                                    )
                                            }*/)})/*,
            @ApiResponse(responseCode = "409", description = "There is no stock available for one or more of the products selected", content = @Content),
            @ApiResponse(responseCode = "404", description = "Some of the selected products do not exist", content = @Content)*/})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(/*@RequestHeader("Authorization") String authorizationToken,*/ @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(this.categoryService.createCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        try {
            return new ResponseEntity<>(this.categoryService.updateCategory(id, categoryDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.categoryService.deleteCategory(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
