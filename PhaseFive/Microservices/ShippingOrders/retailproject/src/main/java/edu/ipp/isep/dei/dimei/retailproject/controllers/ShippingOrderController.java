package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.updates.ShippingOrderUpdateDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.InvalidQuantityException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.WrongFlowException;
import edu.ipp.isep.dei.dimei.retailproject.services.ShippingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Shipping Order Controller")
@RequiredArgsConstructor
@RequestMapping("/shippingorders")
@CacheConfig(cacheNames = "shippingorders")
public class ShippingOrderController {

    private final @Lazy ShippingOrderService shippingOrderService;
    private static final boolean isEvent = false;

    @PatchMapping(path = "/{shippingOrderId}/cancel")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#shippingOrderId")
            }
    )
    public ResponseEntity<Object> fullCancelShippingOrderById(@PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.fullCancelShippingOrder(shippingOrderId, shippingOrderUpdateDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/reject")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#shippingOrderId")
            }
    )
    public ResponseEntity<Object> rejectShippingOrderById(@PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.rejectShippingOrder(shippingOrderId, shippingOrderUpdateDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/approve")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#shippingOrderId")
            }
    )
    public ResponseEntity<Object> approveShippingOrderById(@PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.approveShippingOrder(shippingOrderId, shippingOrderUpdateDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/ship")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#shippingOrderId")
            }
    )
    public ResponseEntity<Object> shippedShippingOrderById(@PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.shippedShippingOrder(shippingOrderId, shippingOrderUpdateDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{shippingOrderId}/delivered")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#shippingOrderId")
            }
    )
    public ResponseEntity<Object> deliveredShippingOrderById(@PathVariable int shippingOrderId, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.deliveredShippingOrder(shippingOrderId, shippingOrderUpdateDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
