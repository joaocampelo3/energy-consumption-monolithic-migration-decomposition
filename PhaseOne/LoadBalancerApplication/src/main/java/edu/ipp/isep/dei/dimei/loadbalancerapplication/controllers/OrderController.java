package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.creates.OrderCreateDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.AddressDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.updates.OrderUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
            return restTemplate.exchange(ORDER_URL + "/all", HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getUserOrders(@RequestHeader("Authorization") String authorizationToken) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(ORDER_URL, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderCreateDTO orderDTO) {
        Object userBody = getUserDTO(authorizationToken);
        Object addressBody = createAddressDTO(authorizationToken, orderDTO.getAddress());

        if (userBody instanceof UserDTO userDTO && userDTO.equals(orderDTO.getUserDTO()) && addressBody instanceof AddressDTO addressDTO) {
            orderDTO.setAddress(addressDTO);
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<OrderCreateDTO> request = new HttpEntity<>(orderDTO, headers);

            return restTemplate.exchange(ORDER_URL, HttpMethod.POST, request, Object.class);
        } else {
            return (ResponseEntity<Object>) userBody;
        }
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<Object> getUserOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int orderId) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

            return restTemplate.exchange(ORDER_URL + "/" + orderId, HttpMethod.GET, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @DeleteMapping(path = "/user/{userId}/order/{orderId}")
    public ResponseEntity<Object> deleteOrder(@RequestHeader("Authorization") String authorizationToken, @PathVariable int userId, @PathVariable int orderId) {
        return restTemplate.exchange(ORDER_URL + "/user/" + userId + "/order/" + orderId, HttpMethod.DELETE, null, Object.class);
    }

    @PatchMapping(path = "/{orderId}/cancel")
    public ResponseEntity<Object> fullCancelOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(orderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

            return restTemplate.exchange(ORDER_URL + "/" + orderId + "/cancel", HttpMethod.PATCH, request, Object.class);

        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{orderId}/reject")
    public ResponseEntity<Object> rejectOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(orderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);
            return restTemplate.exchange(ORDER_URL + "/" + orderId + "/reject", HttpMethod.PATCH, request, Object.class);
        } else {
            return (ResponseEntity<Object>) body;
        }
    }

    @PatchMapping(path = "/{orderId}/approve")
    public ResponseEntity<Object> approveOrderById(@RequestHeader("Authorization") String authorizationToken, @PathVariable int orderId, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Object body = getUserDTO(authorizationToken);

        if (body instanceof UserDTO userDTO && userDTO.equals(orderUpdateDTO.getUserDTO())) {
            HttpHeaders headers = buildHttpHeader(authorizationToken);
            HttpEntity<OrderUpdateDTO> request = new HttpEntity<>(orderUpdateDTO, headers);

            return restTemplate.exchange(ORDER_URL + "/" + orderId + "/approve", HttpMethod.PATCH, request, Object.class);
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
