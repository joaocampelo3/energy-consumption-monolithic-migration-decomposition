package edu.ipp.isep.dei.dimei.retailproject.controllers;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.services.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Address Controller")
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Object> createAddress(@RequestHeader("Authorization") String authorizationToken, @RequestBody AddressDTO addressDTO) {
        try {
            return ResponseEntity.ok(addressService.createAddress(authorizationToken, addressDTO));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
