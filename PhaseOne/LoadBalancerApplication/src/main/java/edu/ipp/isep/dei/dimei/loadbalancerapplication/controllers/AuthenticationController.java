package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.RegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public AuthenticationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginDTO loginDTO) {
        logger("The login() started...", null);
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO);
        logger("The Authentication login request:\n {}", request);

        return restTemplate.exchange(
                USERS_URL + "/auth/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDTO registerDTO) {
        logger("The register() started...", null);
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        logger("The Authentication register request:\n {}", request);
        return restTemplate.postForObject(USERS_URL + "/auth/register", request, ResponseEntity.class);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterDTO registerDTO) {
        logger("The registerAdmin() started...", null);
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        logger("The Authentication register admin request:\n {}", request);
        return restTemplate.postForObject(USERS_URL + "/auth/register/admin", request, ResponseEntity.class);
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<AuthenticationResponse> registerMerchant(@RequestBody RegisterDTO registerDTO) {
        logger("The registerMerchant() started...", null);
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO);
        logger("The Authentication register merchant request:\n {}", request);
        return restTemplate.postForObject(USERS_URL + "/auth/register/merchant", request, ResponseEntity.class);
    }

    private void logger(String message, Object object) {
        logger.info(message, object);
    }
}
