package com.openclassrooms.front.client;

import com.openclassrooms.front.session.SessionAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class GatewayClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway.base-url}")
    private String baseUrl;


    private HttpHeaders authHeaders(SessionAuth auth) {
        if (auth == null || auth.getUsername() == null || auth.getPassword() == null) {
            return new HttpHeaders();
        }

        String token = auth.getUsername() + ":" + auth.getPassword();
        String base64 = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64);
        return headers;
    }

    public ResponseEntity<String> get(String path, SessionAuth auth) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(auth));
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> post(String path, Object body, SessionAuth auth) {
        HttpEntity<Object> entity = new HttpEntity<>(body, authHeaders(auth));
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> put(String path, Object body, SessionAuth auth) {
        HttpEntity<Object> entity = new HttpEntity<>(body, authHeaders(auth));
        return restTemplate.exchange(baseUrl + path, HttpMethod.PUT, entity, String.class);
    }

    public ResponseEntity<String> delete(String path, SessionAuth auth) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(auth));
        return restTemplate.exchange(baseUrl + path, HttpMethod.DELETE, entity, String.class);
    }

    public ResponseEntity<String> getNotesByPatientId(Long patientId, SessionAuth auth) {
        return get("/notes/patient/" + patientId, auth);
    }
}
