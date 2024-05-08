package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Order Controller")
@RequiredArgsConstructor
@RequestMapping("/orders")
@CacheConfig(cacheNames = "orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    @Cacheable
    @Operation(description = "Get all orders service", responses = {@ApiResponse(responseCode = "200", description = "Orders found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping
    @Cacheable(key = "#authorizationToken")
    @Operation(description = "Get orders by user", responses = {@ApiResponse(responseCode = "200", description = "Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> getUserOrders(@RequestHeader("Authorization") String authorizationToken) {
        try {
            return new ResponseEntity<>(orderService.getUserOrders(authorizationToken), HttpStatus.OK);
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
            description = "Create an order",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Order was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderCreateDTO orderDTO) {
        try {
            return new ResponseEntity<>(orderService.createOrder(authorizationToken, orderDTO), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{id}")
    @Cacheable(key = "#id")
    public ResponseEntity<Object> getUserOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        OrderDTO orderDTO;
        try {
            orderDTO = orderService.getUserOrder(authorizationToken, id);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @DeleteMapping(path = "/user/{userId}/order/{orderId}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    public ResponseEntity<Object> deleteOrder(@PathVariable int userId, @PathVariable int orderId) {
        try {
            return new ResponseEntity<>(this.orderService.deleteOrder(userId, orderId), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#authorizationToken"),
                    @CacheEvict(key = "#id")
            }
    )
    @PatchMapping(path = "/{id}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.fullCancelOrder(authorizationToken, id, orderUpdateDTO), HttpStatus.ACCEPTED);
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
    public ResponseEntity<Object> rejectOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.rejectOrder(authorizationToken, id, orderUpdateDTO), HttpStatus.ACCEPTED);
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
    public ResponseEntity<Object> approveOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.approveOrder(authorizationToken, id, orderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
