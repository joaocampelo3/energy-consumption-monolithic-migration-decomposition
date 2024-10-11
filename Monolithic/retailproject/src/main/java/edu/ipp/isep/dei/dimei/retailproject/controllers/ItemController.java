package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ItemUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@RestController
@Tag(name = "Item Controller")
@RequiredArgsConstructor
@RequestMapping("/items")
@CacheConfig(cacheNames = "items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/all")
    @Cacheable
    @Operation(description = "Get all items service", responses = {@ApiResponse(responseCode = "200", description = "Items found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<Object> getAllItems() {
        return new ResponseEntity<>(itemService.getAllItems(), HttpStatus.OK);
    }

    @GetMapping
    @Cacheable(key = "#authorizationToken")
    @Operation(description = "Get items by user", responses = {@ApiResponse(responseCode = "200", description = "Items found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> getUserItems(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(itemService.getUserItems(authorizationToken), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{id}")
    @Cacheable(key = "#id")
    public ResponseEntity<Object> getUserItemById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.itemService.getUserItemDTO(authorizationToken, id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken")
            }
    )
    @Operation(
            description = "Create an item",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Item was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("Authorization") String authorizationToken, @RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.createItem(authorizationToken, itemDTO), HttpStatus.CREATED);
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
    public ResponseEntity<Object> deleteItem(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.itemService.deleteItem(authorizationToken, id), HttpStatus.OK);
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
    public ResponseEntity<Object> addItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.addItemStock(authorizationToken, id, itemUpdateDTO), HttpStatus.OK);
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
    public ResponseEntity<Object> removeItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.removeItemStock(authorizationToken, id, itemUpdateDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
