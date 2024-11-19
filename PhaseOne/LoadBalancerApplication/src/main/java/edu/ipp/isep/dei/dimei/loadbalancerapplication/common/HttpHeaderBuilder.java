package edu.ipp.isep.dei.dimei.loadbalancerapplication.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public interface HttpHeaderBuilder {

    default HttpHeaders buildHttpHeader(String authorizationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authorizationToken);

        return headers;
    }
}
