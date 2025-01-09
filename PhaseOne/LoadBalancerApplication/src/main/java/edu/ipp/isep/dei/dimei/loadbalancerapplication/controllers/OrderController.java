package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.OrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.ORDER_URL;

@RestController
@RequestMapping("/orders")
public class OrderController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private final UserController userController;
    private final AddressController addressController;

    @Autowired
    public OrderController(RestTemplate restTemplate, UserController userController, AddressController addressController) {
        this.restTemplate = restTemplate;
        this.userController = userController;
        this.addressController = addressController;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(headers);
            try {
                return restTemplate.exchange(ORDER_URL + "/all", HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(ORDER_URL, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderCreateDTO orderDTO) {
        Object userBody = getUserDTO(authorizationToken);
        Object addressBody = createAddressDTO(authorizationToken, orderDTO.getAddress());

        if (userBody instanceof UserDTO userDTO && addressBody instanceof AddressDTO addressDTO) {
            orderDTO.setUserDTO(userDTO);
            orderDTO.setAddress(addressDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<OrderCreateDTO> request = new HttpEntity<>(orderDTO, headers);

            try {
                return restTemplate.exchange(ORDER_URL, HttpMethod.POST, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) userBody;
        }
    }


    @GetMapping(path = "/{orderId}")
    public ResponseEntity<Object> getUserOrderById(@RequestHeader("Authorization") String authorizationToken,
                                                   @PathVariable int orderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            try {
                return restTemplate.exchange(ORDER_URL + "/" + orderId, HttpMethod.GET, request, Object.class);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/user/{userId}/order/{orderId}")
    public ResponseEntity<Object> deleteOrder(@RequestHeader("Authorization") String authorizationToken,
                                              @PathVariable int userId, @PathVariable int orderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            String url = ORDER_URL + "/user/{userId}/order/{orderId}";
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDTO, headers);
            try {
                return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class, userId, orderId);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{orderId}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@RequestHeader("Authorization") String authorizationToken,
                                                      @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            String url = ORDER_URL + "/{orderId}/cancel";
            orderUpdateDTO.setUserDTO(userDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderId);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{orderId}/reject")
    public ResponseEntity<Object> rejectOrderById(@RequestHeader("Authorization") String authorizationToken,
                                                  @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            String url = ORDER_URL + "/{orderId}/reject";
            orderUpdateDTO.setUserDTO(userDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderId);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{orderId}/approve")
    public ResponseEntity<Object> approveOrderById(@RequestHeader("Authorization") String authorizationToken,
                                                   @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            String url = ORDER_URL + "/{orderId}/approve";
            orderUpdateDTO.setUserDTO(userDTO);
            HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            try {
                return restTemplate.exchange(url, HttpMethod.PATCH, request, Object.class, orderId);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
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
            return objectResponseEntity;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected response type");
    }

    private Object createAddressDTO(String authorizationToken, AddressDTO addressDTO) {
        ResponseEntity<Object> objectResponseEntity = addressController.createAddress(authorizationToken, addressDTO);

        if (objectResponseEntity.getStatusCode() == HttpStatus.OK && objectResponseEntity.getBody() instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(objectResponseEntity.getBody(), AddressDTO.class);
        }

        return objectResponseEntity;
    }
}
