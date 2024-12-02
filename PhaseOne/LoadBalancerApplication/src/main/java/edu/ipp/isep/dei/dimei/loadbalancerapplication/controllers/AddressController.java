package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;


@RestController
@RequestMapping("/addresses")
public class AddressController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;

    @Autowired
    public AddressController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<Object> createAddress(@RequestHeader("Authorization") String authorizationToken, @RequestBody AddressDTO addressDTO) {
        HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
        HttpEntity<AddressDTO> requestEntity = new HttpEntity<>(addressDTO, headers);

        return restTemplate.exchange(USERS_URL + "/addresses", HttpMethod.POST, requestEntity, Object.class);
    }

}