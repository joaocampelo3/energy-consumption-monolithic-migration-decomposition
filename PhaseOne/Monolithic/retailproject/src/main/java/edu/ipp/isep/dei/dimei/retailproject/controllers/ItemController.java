package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
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
    @Cacheable(key = "#userDTO")
    @Operation(description = "Get items by user", responses = {@ApiResponse(responseCode = "200", description = "Items found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> getUserItems(@RequestBody UserDTO userDTO) {
        try {
            return new ResponseEntity<>(itemService.getUserItems(userDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{itemId}")
    @Cacheable(key = "#itemId")
    public ResponseEntity<Object> getUserItemById(@RequestBody UserDTO userDTO, @PathVariable int itemId) {
        try {
            return new ResponseEntity<>(this.itemService.getUserItemDTO(userDTO, itemId), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true)
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
    public ResponseEntity<Object> createItem(@RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.createItem(itemDTO), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{itemId}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO"),
                    @CacheEvict(key = "#itemId")
            }
    )
    public ResponseEntity<Object> deleteItem(@RequestBody UserDTO userDTO, @PathVariable int itemId) {
        try {
            return new ResponseEntity<>(this.itemService.deleteItem(userDTO, itemId), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/{itemId}/addStock")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#itemId")
            }
    )
    public ResponseEntity<Object> addItemStock(@PathVariable int itemId, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.addItemStock(itemId, itemUpdateDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/removeStock")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#id")
            }
    )
    public ResponseEntity<Object> removeItemStock(@PathVariable int id, @RequestBody ItemUpdateDTO itemUpdateDTO) {
        try {
            return new ResponseEntity<>(this.itemService.removeItemStock(id, itemUpdateDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
