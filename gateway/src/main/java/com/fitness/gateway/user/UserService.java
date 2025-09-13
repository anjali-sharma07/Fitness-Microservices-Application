package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validUser(String userId) {
        return userServiceWebClient.get()
                .uri("/api/user/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Invalid User Request: " + userId));
                    else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                        return Mono.error(new RuntimeException("Unauthorized access while fetching user " + userId));
                    else if (e.getStatusCode() == HttpStatus.FORBIDDEN)
                        return Mono.error(new RuntimeException("Forbidden request for user " + userId));
                    else if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                        return Mono.error(new RuntimeException("User Not Found: " + userId));
                    else if (e.getStatusCode() == HttpStatus.CONFLICT)
                        return Mono.error(new RuntimeException("Conflict while fetching user " + userId));
                    else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS)
                        return Mono.error(new RuntimeException("Too many requests made to USER-SERVICE"));

                        // 5xx SERVER ERRORS
                    else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                        return Mono.error(new RuntimeException("USER-SERVICE encountered Internal Server Error"));
                    else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY)
                        return Mono.error(new RuntimeException("Bad Gateway while contacting USER-SERVICE"));
                    else if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                        return Mono.error(new RuntimeException("USER-SERVICE is unavailable"));
                    else if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT)
                        return Mono.error(new RuntimeException("Gateway Timeout while calling USER-SERVICE"));
                    else
                        return Mono.error(new RuntimeException("Unexpected Error: " + e.getStatusCode() + " - " + e.getMessage()));

                });


    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        return userServiceWebClient.post()
                .uri("/api/user/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Invalid User Request: " + e.getMessage()));
                    else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                        return Mono.error(new RuntimeException("Unauthorized access while fetching user " + e.getMessage()));
                    else if (e.getStatusCode() == HttpStatus.FORBIDDEN)
                        return Mono.error(new RuntimeException("Forbidden request for user " + e.getMessage()));
                    else if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                        return Mono.error(new RuntimeException("User Not Found: " + e.getMessage()));
                    else if (e.getStatusCode() == HttpStatus.CONFLICT)
                        return Mono.error(new RuntimeException("Conflict while fetching user " + e.getMessage()));
                    else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS)
                        return Mono.error(new RuntimeException("Too many requests made to USER-SERVICE"));

                        // 5xx SERVER ERRORS
                    else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                        return Mono.error(new RuntimeException("USER-SERVICE encountered Internal Server Error"));
                    else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY)
                        return Mono.error(new RuntimeException("Bad Gateway while contacting USER-SERVICE"));
                    else if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                        return Mono.error(new RuntimeException("USER-SERVICE is unavailable"));
                    else if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT)
                        return Mono.error(new RuntimeException("Gateway Timeout while calling USER-SERVICE"));
                    else
                        return Mono.error(new RuntimeException("Unexpected Error: " + e.getStatusCode() + " - " + e.getMessage()));
                });
    }
}
