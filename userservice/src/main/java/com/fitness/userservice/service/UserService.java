package com.fitness.userservice.service;

import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository; // extends JpaRepository
    private final KeycloakAdminService keycloakAdminService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Check if user already exists in DB
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return mapToResponse(existingUser.get());
        }

        // 2. Create user in Keycloak
        String keycloakId;
        try {
            keycloakId = keycloakAdminService.createUserInKeycloak(
                    request.getEmail(),
                    request.getPassword(), // Keycloak stores password
                    request.getFirstName(),
                    request.getLastName()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }

        // 3. Save user in DB with hashed password
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hashed locally
        user.setKeycloakId(keycloakId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);

        // 4. Return response
        return mapToResponse(savedUser);
    }

    public UserResponse getByID(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponse(user);
    }

    public boolean existById(String id) {
        return userRepository.findById(id)
                .map(u -> u.getKeycloakId() != null)
                .orElse(false);
    }

    // helper mapper
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setKeycloakId(user.getKeycloakId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
