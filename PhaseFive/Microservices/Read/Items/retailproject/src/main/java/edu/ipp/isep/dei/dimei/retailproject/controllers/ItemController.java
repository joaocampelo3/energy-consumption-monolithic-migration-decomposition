package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Item Controller")
@RequiredArgsConstructor
@RequestMapping("/items")
@CacheConfig(cacheNames = "items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/all")
    @Cacheable
    @Operation(
            description = "Get all items service",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Items found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Ok\",\"Message\": [{\"id\": 1, \"itemName\": \"Item 1\", \"sku\": \"ABC-12345-S-BL\", \"itemDescription\": \"Item 1 description\", \"price\": 12.0, \"quantityInStock\": 8, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}, {\"id\": 2, \"itemName\": \"Item 2\", \"sku\": \"ABC-12345-XL-BL\", \"itemDescription\": \"Item 2 description\", \"price\": 20.0, \"quantityInStock\": 5, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}]}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> getAllItems() {
        return new ResponseEntity<>(itemService.getAllItems(), HttpStatus.OK);
    }

    @GetMapping
    @Cacheable(key = "#authorizationToken")
    @Operation(
            description = "Get items by user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User Items found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Ok\",\"Message\": [{\"id\": 1, \"itemName\": \"Item 1\", \"sku\": \"ABC-12345-S-BL\", \"itemDescription\": \"Item 1 description\", \"price\": 12.0, \"quantityInStock\": 8, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}, {\"id\": 2, \"itemName\": \"Item 2\", \"sku\": \"ABC-12345-XL-BL\", \"itemDescription\": \"Item 2 description\", \"price\": 20.0, \"quantityInStock\": 5, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}]}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 404,\"Status\": \"Not Found\",\"Message\": \"User not found.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> getUserItems(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(itemService.getUserItems(authorizationToken), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{id}")
    @Cacheable(key = "#id")
    @Operation(
            description = "Get user item by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Item found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 200,\"Status\": \"Ok\",\"Message\": {\"id\": 1, \"itemName\": \"Item 1\", \"sku\": \"ABC-12345-S-BL\", \"itemDescription\": \"Item 1 description\", \"price\": 12.0, \"quantityInStock\": 8, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Item not found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 404,\"Status\": \"Not Found\",\"Message\": \"Item not found.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 404,\"Status\": \"Not Found\",\"Message\": \"User not found.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> getUserItemById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.itemService.getUserItemDTO(authorizationToken, id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
