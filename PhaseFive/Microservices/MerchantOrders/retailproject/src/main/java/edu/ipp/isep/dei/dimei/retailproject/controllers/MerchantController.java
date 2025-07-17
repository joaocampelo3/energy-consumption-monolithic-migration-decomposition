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
@Tag(name = "Merchant Controller")
@RequiredArgsConstructor
@RequestMapping("/merchants")
@CacheConfig(cacheNames = "merchants")
public class MerchantController {

    private final MerchantService merchantService;
    private final boolean isEvent = false;

    @PostMapping
    @Caching(
            evict = {@CacheEvict(allEntries = true)}
    )
    @Operation(
            description = "Create a merchant",
            responses = {
                    @ApiResponse
            }
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Merchant was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Object> createMerchant(@RequestBody MerchantDTO merchantDTO) {
        return new ResponseEntity<>(this.merchantService.createMerchant(merchantDTO, isEvent), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{merchantId}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#merchantId")
            }
    )
    public ResponseEntity<Object> updateMerchant(@PathVariable int merchantId, @RequestBody MerchantDTO merchantDTO) {
        try {
            return new ResponseEntity<>(this.merchantService.updateMerchant(merchantId, merchantDTO, isEvent), HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadPayloadException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{merchantId}")
    @Caching(
            evict = {@CacheEvict(allEntries = true),
                    @CacheEvict(key = "#merchantId")
            }
    )
    public ResponseEntity<Object> deleteMerchant(@PathVariable int merchantId) {
        try {
            return new ResponseEntity<>(this.merchantService.deleteMerchant(merchantId, isEvent), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
