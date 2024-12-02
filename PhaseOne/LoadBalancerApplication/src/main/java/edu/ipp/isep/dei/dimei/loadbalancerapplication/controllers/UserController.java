package edu.ipp.isep.dei.dimei.loadbalancerapplication.controllers;

import edu.ipp.isep.dei.dimei.loadbalancerapplication.common.HttpHeaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static edu.ipp.isep.dei.dimei.loadbalancerapplication.common.ControllersGlobalVariables.USERS_URL;


@RestController
@RequestMapping("/users")
public class UserController implements HttpHeaderBuilder {

    private final RestTemplate restTemplate;
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> getUserId(@RequestHeader("Authorization") String authorizationToken) {
        logger("The getUserId() started...", null);

        HttpHeaders headers = buildHttpHeaderWithMediaType(authorizationToken.replace("Bearer ", ""));
        logger("The headers:\n {}", headers);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        logger("The User getUserId request:\n {}", request);

        try {
            return restTemplate.exchange(USERS_URL + "/users", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error occurred: Status - {}, Body - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void logger(String message, Object object) {
        logger.info(message, object);
    }

}
