package edu.ipp.isep.dei.dimei.loadbalancerapplication.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public interface HttpHeaderBuilder {

    default HttpHeaders buildHttpHeader(String authorizationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizationToken.replaceAll("Bearer ", ""));

        return headers;
    }

    default HttpHeaders buildHttpHeaderWithMediaType(String authorizationToken) {
        HttpHeaders headers = buildHttpHeader(authorizationToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
