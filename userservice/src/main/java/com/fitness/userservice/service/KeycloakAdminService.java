package com.fitness.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Blocking method to create user in Keycloak
     */
    public String createUserInKeycloak(String email, String password, String firstName, String lastName) {
        String adminToken = getAdminToken();

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", email);
        userPayload.put("email", email);
        userPayload.put("firstName", firstName);
        userPayload.put("lastName", lastName);
        userPayload.put("enabled", true);

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);

        userPayload.put("credentials", Collections.singletonList(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    keycloakServerUrl + "/admin/realms/" + realm + "/users",
                    request,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                String location = response.getHeaders().getFirst("Location");
                if (location != null) {
                    String keycloakId = location.substring(location.lastIndexOf("/") + 1);
                    log.info("Created Keycloak user: {} with ID {}", email, keycloakId);
                    return keycloakId;
                }
            }

            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            log.error("Error creating Keycloak user: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getStatusCode(), e);
        }
    }

    /**
     * Get admin token in blocking way
     */
    private String getAdminToken() {
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&username=" + adminUsername +
                "&password=" + adminPassword +
                "&grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            if (response == null || !response.containsKey("access_token")) {
                log.error("Failed to retrieve Keycloak admin token: {}", response);
                throw new RuntimeException("Failed to retrieve admin token from Keycloak");
            }

            return (String) response.get("access_token");

        } catch (HttpClientErrorException e) {
            log.error("Error fetching Keycloak admin token: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve admin token from Keycloak: " + e.getStatusCode(), e);
        }
    }
}

