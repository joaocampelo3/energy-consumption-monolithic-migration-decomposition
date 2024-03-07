package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.ShippingOrderDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Shipping Order Controller")
@RequiredArgsConstructor
@RequestMapping("/shippingorders")
public class ShippingOrderController {

    private final ShippingOrderService shippingOrderService;

    @GetMapping("/all")
    @Operation(description = "Get all shipping orders", responses = {@ApiResponse(responseCode = "200", description = "Shipping Orders found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<?> getAllShippingOrders(/*@RequestHeader("Authorization") String authorizationToken*/) {
        return new ResponseEntity<>(this.shippingOrderService.getAllShippingOrders(), HttpStatus.OK);
    }

    @GetMapping
    @Operation(description = "Get shipping orders by user", responses = {@ApiResponse(responseCode = "200", description = "Shipping Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE/*,
                                            examples = {
                                                    @ExampleObject(
                                                            value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}"
                                                    )
                                            }*/)})})
    public ResponseEntity<List<ShippingOrderDTO>> getUserShippingOrders(@RequestHeader("Authorization") String authorizationToken) {
        return new ResponseEntity<>(this.shippingOrderService.getUserShippingOrders(authorizationToken), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        ShippingOrderDTO shippingOrderDTO;
        try {
            shippingOrderDTO = this.shippingOrderService.getUserShippingOrder(authorizationToken, id);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(shippingOrderDTO, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}/cancel")
    public ResponseEntity<?> fullCancelShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.fullCancelShippingOrder(authorizationToken, id, shippingOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/reject")
    public ResponseEntity<?> rejectShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.rejectShippingOrder(authorizationToken, id, shippingOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException | InvalidQuantityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/approve")
    public ResponseEntity<?> approveShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.approveShippingOrder(authorizationToken, id, shippingOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/ship")
    public ResponseEntity<?> shippedShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.shippedShippingOrder(authorizationToken, id, shippingOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}/delivered")
    public ResponseEntity<?> deliveredShippingOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id, @RequestBody ShippingOrderUpdateDTO shippingOrderUpdateDTO) {
        try {
            return new ResponseEntity<>(this.shippingOrderService.deliveredShippingOrder(authorizationToken, id, shippingOrderUpdateDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (WrongFlowException | BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
