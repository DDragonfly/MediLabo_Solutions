package com.openclassrooms.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class GatewayClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private HttpHeaders authHeaders() {
        String token = USERNAME + ":" + PASSWORD;
        String base64 = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64);
        return headers;
    }

    public ResponseEntity<String> get(String path) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        return restTemplate.exchange(gatewayBaseUrl + path, HttpMethod.GET, entity, String.class);
    }
}
