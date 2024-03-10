package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.MerchantService;
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

import java.util.List;

@RestController
@Tag(name = "Merchant Controller")
@RequiredArgsConstructor
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/all")
    @Operation(description = "Get all merchants", responses = {@ApiResponse(responseCode = "200", description = "Merchants found."/*, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {@ExampleObject(value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}")})*/)})
    public ResponseEntity<List<MerchantDTO>> getAllMerchants() {
        return new ResponseEntity<>(this.merchantService.getAllMerchants(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getMerchantById(@PathVariable int id) {
        try {
            return new ResponseEntity<>(this.merchantService.getMerchant(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(
            description = "Create a merchant",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Merchant was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE/*,
                                            examples = {
                                                    @ExampleObject(
                                                            value = "{\"code\": 200,\"Status\": Ok,\"Message\": \"Login successfully.\"}"
                                                    )
                                            }*/)})/*,
            @ApiResponse(responseCode = "409", description = "There is no stock available for one or more of the products selected", content = @Content),
            @ApiResponse(responseCode = "404", description = "Some of the selected products do not exist", content = @Content)*/})
    public ResponseEntity<?> createMerchant(/*@RequestHeader("Authorization") String authorizationToken,*/ @RequestBody MerchantDTO merchantDTO) {
        try {
            return new ResponseEntity<>(this.merchantService.createMerchant(merchantDTO), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable int id, @RequestBody MerchantDTO merchantDTO) {
        try {
            return new ResponseEntity<>(this.merchantService.updateMerchant(id, merchantDTO), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable int id) {
        try {
            return new ResponseEntity<>(this.merchantService.deleteMerchant(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
