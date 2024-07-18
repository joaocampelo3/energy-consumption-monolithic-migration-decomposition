package edu.ipp.isep.dei.dimei.apigatewayapplication.controllers;

import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.apigatewayapplication.common.dto.gets.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.apigatewayapplication.common.ControllersGlobalVariables.USERS_URL;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final RestTemplate restTemplate;

    @Autowired
    public AuthenticationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginDTO loginDTO) {
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO);
        return restTemplate.postForObject(USERS_URL + "/login", request, ResponseEntity.class);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDTO registerDTO) {
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        return restTemplate.postForObject(USERS_URL + "/register", request, ResponseEntity.class);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterDTO registerDTO) {
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        return restTemplate.postForObject(USERS_URL + "/register/admin", request, ResponseEntity.class);
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<AuthenticationResponse> registerMerchant(@RequestBody RegisterDTO registerDTO) {
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        return restTemplate.postForObject(USERS_URL + "/register/merchant", request, ResponseEntity.class);
    }

}
