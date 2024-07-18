package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.MerchantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.MERCHANT_URL;

@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final RestTemplate restTemplate;

    @Autowired
    public MerchantController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<List<MerchantDTO>> getAllMerchants() {
        return restTemplate.getForObject(MERCHANT_URL + "/all", ResponseEntity.class);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getMerchantById(@PathVariable int id) {
        return restTemplate.getForObject(MERCHANT_URL + "/" + id, ResponseEntity.class);
    }

    @PostMapping
    public ResponseEntity<Object> createMerchant(@RequestBody MerchantDTO merchantDTO) {
        HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO);
        return restTemplate.postForObject(MERCHANT_URL, request, ResponseEntity.class);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateMerchant(@PathVariable int id, @RequestBody MerchantDTO merchantDTO) {
        HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO);
        return restTemplate.patchForObject(MERCHANT_URL + "/" + id, request, ResponseEntity.class);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteMerchant(@PathVariable int id) {
        return restTemplate.exchange(MERCHANT_URL + "/" + id, HttpMethod.DELETE, null, Object.class);
    }
}
