package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets.RegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;


@RestController
@RequestMapping("/auth")
public class AuthenticationController implements HttpHeaderBuilder {

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
        return restTemplate.exchange(USERS_URL + "/auth/register", HttpMethod.POST, request, new ParameterizedTypeReference<>() {});
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestHeader("Authorization") String authorizationToken, @RequestBody RegisterDTO registerDTO) {
        logger("The registerAdmin() started...", null);
        HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken.replace("Bearer ", ""));
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO, headers);
        logger("The Authentication register admin request:\n {}", request);
        return restTemplate.exchange(USERS_URL + "/auth/register/admin", HttpMethod.POST, request, new ParameterizedTypeReference<>() {});
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<AuthenticationResponse> registerMerchant(@RequestHeader("Authorization") String authorizationToken, @RequestBody RegisterDTO registerDTO) {
        logger("The registerMerchant() started...", null);
        HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken.replace("Bearer ", ""));
        HttpEntity<RegisterDTO> request = new HttpEntity<>(registerDTO, headers);
        logger("The Authentication register merchant request:\n {}", request);
        return restTemplate.exchange(USERS_URL + "/auth/register/merchant", HttpMethod.POST, request, new ParameterizedTypeReference<>() {});
    }

    private void logger(String message, Object object) {
        logger.info(message, object);
    }
}
