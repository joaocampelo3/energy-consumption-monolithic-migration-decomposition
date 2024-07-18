package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.MerchantOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Merchant Order Controller")
@RequiredArgsConstructor
@RequestMapping("/merchantorders")
@CacheConfig(cacheNames = "merchantorders")
public class MerchantOrderController {

    private final MerchantOrderService merchantOrderService;

    @GetMapping("/all")
    @Cacheable
    @Operation(description = "Get all merchant orders", responses = {@ApiResponse(responseCode = "200", description = "Merchant Orders found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<List<MerchantOrderDTO>> getAllMerchantOrders() {
        return new ResponseEntity<>(this.merchantOrderService.getAllMerchantOrders(), HttpStatus.OK);
    }

    @GetMapping
    @Cacheable(key = "#authorizationToken")
    @Operation(description = "Get merchant orders by user", responses = {@ApiResponse(responseCode = "200", description = "Merchant Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> getUserMerchantOrders(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(this.merchantOrderService.getUserMerchantOrders(authorizationToken), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{id}")
    @Cacheable(key = "#id")
    public ResponseEntity<Object> getUserMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        MerchantOrderDTO merchantOrderDTO;
        try {
            merchantOrderDTO = this.merchantOrderService.getUserMerchantOrder(authorizationToken, id);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(merchantOrderDTO, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}/cancel")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    public ResponseEntity<Object> fullCancelMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.merchantOrderService.fullCancelMerchantOrder(authorizationToken, id, merchantOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/reject")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    public ResponseEntity<Object> rejectMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.merchantOrderService.rejectMerchantOrder(authorizationToken, id, merchantOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PatchMapping(path = "/{id}/approve")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    public ResponseEntity<Object> approveMerchantOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody MerchantOrderUpdateDTO merchantOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.merchantOrderService.approveMerchantOrder(authorizationToken, id, merchantOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
