package com.fitness.gateway.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password must be required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    String password;

    String firstName;
    String lastName;
    String keycloakId;

}
