package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegisterRequest registerRequest = getUserDetails(token);
        if (registerRequest == null || registerRequest.getKeycloakId() == null) {
            log.error("Cannot sync user: Keycloak ID not found in token!");
            return chain.filter(exchange);
        }

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        if (userId == null) {
            userId = registerRequest.getKeycloakId();
        }

        String finalUserId = userId;

        return userService.validUser(userId)
                .flatMap(exist -> {
                    if (!exist) {
                        log.info("Syncing new user with Keycloak ID: {}", finalUserId);
                        return userService.registerUser(registerRequest).then(Mono.empty());
                    } else {
                        log.info("User already exists, skipping sync: {}", finalUserId);
                        return Mono.empty();
                    }
                })
                .then(Mono.defer(() -> {
                    ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                            .header("X-User-ID", finalUserId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutateRequest).build());
                }));
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String email = claims.getStringClaim("email");
            String sub = claims.getStringClaim("sub"); // Keycloak user ID
            String firstName = claims.getStringClaim("given_name");
            String lastName = claims.getStringClaim("family_name");

            if (sub == null || email == null || firstName == null) {
                log.error("Required claims are missing in token: {}", claims.getClaims());
                return null;
            }

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(email);
            registerRequest.setKeycloakId(sub);
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);

            // Generate a safe random password (max 20 chars)
            registerRequest.setPassword(generateRandomPassword(20));

            return registerRequest;

        } catch (Exception e) {
            log.error("Error parsing Keycloak token", e);
            return null;
        }
    }

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return encoded.length() > length ? encoded.substring(0, length) : encoded;
    }
}
