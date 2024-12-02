package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.MERCHANT_URL;

@RestController
@RequestMapping("/merchants")
public class MerchantController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;
    private final AddressController addressController;

    @Autowired
    public MerchantController(RestTemplate restTemplate, UserController userController, AddressController addressController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
        this.addressController = addressController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllMerchants(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(MERCHANT_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping(path = "/{merchantId}")
    public ResponseEntity<Object> getMerchantById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(MERCHANT_URL + "/" + merchantId, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createMerchant(@RequestHeader("Authorization") String authorizationToken, @RequestBody MerchantDTO merchantDTO) {
        Object userBody = getUserDTO(authorizationToken);
        Object addressBody = createAddressDTO(authorizationToken, merchantDTO.getAddressDTO());

        if (userBody instanceof UserDTO userDTO && userDTO.equals(merchantDTO.getUserDTO()) && addressBody instanceof AddressDTO addressDTO) {
            merchantDTO.setAddressDTO(addressDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO, headers);
            return restTemplate.postForObject(MERCHANT_URL, request, ResponseEntity.class);
        } else {
            return (ResponseEntity<Object>) userBody;
        }
    }

    @PatchMapping(path = "/{merchantId}")
    public ResponseEntity<Object> updateMerchant(@RequestHeader("Authorization") String authorizationToken, @PathVariable int merchantId, @RequestBody MerchantDTO merchantDTO) {
        Object body = getUserDTO(authorizationToken);
        Object addressBody = createAddressDTO(authorizationToken, merchantDTO.getAddressDTO());

        if (body instanceof UserDTO userDTO && userDTO.equals(merchantDTO.getUserDTO()) && addressBody instanceof AddressDTO addressDTO) {
            merchantDTO.setAddressDTO(addressDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO, headers);
            return restTemplate.patchForObject(MERCHANT_URL + "/" + merchantId, request, ResponseEntity.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteMerchant(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(MERCHANT_URL + "/" + id, HttpMethod.DELETE, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    private Object getUserDTO(String authorizationToken) {
        return userController.getUserId(authorizationToken).getBody();
    }

    private Object createAddressDTO(String authorizationToken, AddressDTO addressDTO) {
        return addressController.createAddress(authorizationToken, addressDTO).getBody();
    }
}
