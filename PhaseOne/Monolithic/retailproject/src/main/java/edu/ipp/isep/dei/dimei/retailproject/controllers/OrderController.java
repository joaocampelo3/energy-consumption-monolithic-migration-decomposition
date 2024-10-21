package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.OrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
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
    @Cacheable(key = "#userDTO")
    @Operation(description = "Get orders by user", responses = {@ApiResponse(responseCode = "200", description = "Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> getUserOrders(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(orderService.getUserOrders(userDTO), HttpStatus.OK);
    }

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO")
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
    public ResponseEntity<Object> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        try {
            return new ResponseEntity<>(orderService.createOrder(orderDTO), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{orderId}")
    @Cacheable(key = "#orderId")
    public ResponseEntity<Object> getUserOrderById(@RequestBody UserDTO userDTO, @PathVariable int orderId) {
        OrderDTO orderDTO;
        try {
            orderDTO = orderService.getUserOrder(userDTO, orderId);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @DeleteMapping(path = "/user/{userId}/order/{id}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO"),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> deleteOrder(@PathVariable int userId, @RequestBody UserDTO userDTO, @PathVariable int id) {
        try {
            return new ResponseEntity<>(this.orderService.deleteOrder(userId, userDTO, id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO"),
                    @CacheEvict(key = "#orderId")
            }
    )
    @PatchMapping(path = "/{orderId}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.fullCancelOrder(orderId, orderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{orderId}/reject")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO"),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> rejectOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.rejectOrder(orderId, orderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{orderId}/approve")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#userDTO"),
                    @CacheEvict(key = "#orderId")
            }
    )
    public ResponseEntity<Object> approveOrderById(@PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.orderService.approveOrder(orderId, orderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
