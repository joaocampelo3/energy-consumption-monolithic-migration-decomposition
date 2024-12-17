package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

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

        if (userBody instanceof UserDTO userDTO && addressBody instanceof AddressDTO addressDTO) {
            merchantDTO.setAddressDTO(addressDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<MerchantDTO> request = new HttpEntity<>(merchantDTO, headers);
            return restTemplate.exchange(MERCHANT_URL, HttpMethod.POST, request, Object.class);
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
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            return restTemplate.exchange(MERCHANT_URL + "/" + merchantId, HttpMethod.PATCH, request, Object.class);
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
        ResponseEntity<Object> objectResponseEntity = userController.getUserId(authorizationToken);

        if (objectResponseEntity.getStatusCode() == HttpStatus.OK && objectResponseEntity.getBody() instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(objectResponseEntity.getBody(), UserDTO.class);
        } else if (objectResponseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || objectResponseEntity.getStatusCode() == HttpStatus.FORBIDDEN || objectResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            return objectResponseEntity.getBody();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected response type");
    }

    private Object createAddressDTO(String authorizationToken, AddressDTO addressDTO) {
        ResponseEntity<Object> objectResponseEntity =  addressController.createAddress(authorizationToken, addressDTO);

        if (objectResponseEntity.getStatusCode() == HttpStatus.OK && objectResponseEntity.getBody() instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(objectResponseEntity.getBody(), AddressDTO.class);
        }

        return objectResponseEntity.getBody();
    }
}
