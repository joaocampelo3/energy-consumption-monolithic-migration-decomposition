package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.OrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Order Controller")
@RequiredArgsConstructor
@RequestMapping("/orders")
@CacheConfig(cacheNames = "orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true)}
    )
    @Operation(
            description = "Create an order",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Order was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        try {
            return new ResponseEntity<>(orderService.createOrder(orderDTO, false), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/user/{userId}/order/{orderId}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> deleteOrder(@PathVariable int userId, @PathVariable int orderId) {
        try {
            return new ResponseEntity<>(this.orderService.deleteOrder(userId, orderId, false), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#orderId")
            }
    )
    @PatchMapping(path = "/{orderId}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.fullCancelOrder(orderId, orderUpdateDTO, false), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{orderId}/reject")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> rejectOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.rejectOrder(orderId, orderUpdateDTO, false), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{orderId}/approve")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> approveOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.approveOrder(orderId, orderUpdateDTO, false), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
