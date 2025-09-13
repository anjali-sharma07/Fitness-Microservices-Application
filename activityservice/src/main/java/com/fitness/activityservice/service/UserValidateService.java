package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidateService {

    private final WebClient userServiceWebClient;

    public boolean validUser(String userId){
        try {
            return userServiceWebClient.get()
                    .uri("/api/user/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }
        catch(WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Invalid User Request: " + userId);
            else if(e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                throw new RuntimeException("Unauthorized access while fetching user " + userId);
            else if(e.getStatusCode() == HttpStatus.FORBIDDEN)
                throw new RuntimeException("Forbidden request for user " + userId);
            else if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RuntimeException("User Not Found: " + userId);
            else if(e.getStatusCode() == HttpStatus.CONFLICT)
                throw new RuntimeException("Conflict while fetching user " + userId);
            else if(e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS)
                throw new RuntimeException("Too many requests made to USER-SERVICE");

            // 5xx SERVER ERRORS
            else if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                throw new RuntimeException("USER-SERVICE encountered Internal Server Error");
            else if(e.getStatusCode() == HttpStatus.BAD_GATEWAY)
                throw new RuntimeException("Bad Gateway while contacting USER-SERVICE");
            else if(e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                throw new RuntimeException("USER-SERVICE is unavailable");
            else if(e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT)
                throw new RuntimeException("Gateway Timeout while calling USER-SERVICE");
            else
            throw new RuntimeException("Unexpected Error: " + e.getStatusCode() + " - " + e.getMessage());
        }

    }
}
