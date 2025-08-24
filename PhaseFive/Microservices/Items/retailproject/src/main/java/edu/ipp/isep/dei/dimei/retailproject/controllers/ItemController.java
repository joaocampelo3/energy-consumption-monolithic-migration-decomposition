package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken")
            }
    )
    @Operation(
            description = "Create an item",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Item was created.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 201,\"Status\": \"Created\",\"Message\": {\"id\": 1, \"itemName\": \"Item 1\", \"sku\": \"ABC-12345-S-BL\", \"itemDescription\": \"Item 1 description\", \"price\": 12.0, \"quantityInStock\": 8, \"category\": {\"id\": 1, \"name\": \"Category 1\", \"description\": \"Category 1 description\"}, \"merchant\": {\"id\": 1, \"name\": \"Merchant 1\", \"email\": \"merchant_1@gmail.com\"}, \"address\": {\"id\": 1, \"street\": \"5th Avenue\", \"zipCode\": \"10128\", \"city\": \"New York\", \"country\": \"USA\"}}}"
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
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Create item for a different merchant",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 400,\"Status\": \"Bad Request\",\"Message\": \"You can not create item for this merchant.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("Authorization") String authorizationToken, @RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.createItem(authorizationToken, itemDTO, false), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{id}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    @Operation(
            description = "Delete an item",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Item was deleted.",
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
                    )
            }
    )
    public ResponseEntity<Object> deleteItem(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.itemService.deleteItem(authorizationToken, id, false), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/{id}/addStock")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    @Operation(
            description = "Add stock to an item",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock added to the item.",
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
                            description = "User not found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 404,\"Status\": \"Not Found\",\"Message\": \"User not found.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Add item stock for a different item or add stock lower than zero",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 400,\"Status\": \"Bad Request\",\"Message\": \"Wrong item payload.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Trying to decrease the item stock",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 400,\"Status\": \"Bad Request\",\"Message\": \"Wrong item payload.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> addItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.addItemStock(authorizationToken, id, itemUpdateDTO, false), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/removeStock")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    @Operation(
            description = "Remove stock to an item",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock removed to the item.",
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
                            description = "User not found.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 404,\"Status\": \"Not Found\",\"Message\": \"User not found.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Remove item stock for a different item or add stock lower than zero",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 400,\"Status\": \"Bad Request\",\"Message\": \"Wrong item payload.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Trying to increase the item stock",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"code\": 400,\"Status\": \"Bad Request\",\"Message\": \"Wrong item payload.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> removeItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.removeItemStock(authorizationToken, id, itemUpdateDTO, false), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
