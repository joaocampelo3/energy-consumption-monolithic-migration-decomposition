package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ItemQuantityDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Item Controller")
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/all")
    @Operation(description = "Get all items service", responses = {@ApiResponse(responseCode = "200", description = "Items found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<?> getAllItems(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(itemService.getAllItems(), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(description = "Get items by user", responses = {@ApiResponse(responseCode = "200", description = "Items found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE/*,
                                            examples = {
                                                    @ExampleObject(
                                                            value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}"
                                                    )
                                            }*/)})})
    public ResponseEntity<?> getUserItems(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(itemService.getUserItems(authorizationToken), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserItemById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        ItemQuantityDTO itemQuantityDTO;
        try {
            itemQuantityDTO = this.itemService.getUserItem(authorizationToken, id);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(itemQuantityDTO, HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            description = "Create an item",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Item was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE/*,
                                            examples = {
                                                    @ExampleObject(
                                                            value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}"
                                                    )
                                            }*/)})/*,
            @ApiResponse(responseCode = "409", description = "There is no stock available for one or more of the products selected", content = @Content),
            @ApiResponse(responseCode = "404", description = "Some of the selected products do not exist", content = @Content)*/})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createItem(@RequestHeader("Authorization") String authorizationToken, @RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.createItem(authorizationToken, itemDTO), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteItem(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.itemService.deleteItem(authorizationToken, id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{id}/addStock")
    public ResponseEntity<?> addItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.addItemStock(authorizationToken, id, itemDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/{id}/removeStock")
    public ResponseEntity<?> removeItemStock(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ItemDTO itemDTO) {
        try {
            return new ResponseEntity<>(this.itemService.removeItemStock(authorizationToken, id, itemDTO), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
