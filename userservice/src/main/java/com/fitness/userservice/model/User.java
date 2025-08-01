package com.fitness.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private String id;

   @Column(unique = true, nullable = false)
   private String email;

   @Column(unique = false, nullable = false)
   private String password;

   @Column(nullable = false)
   private String firstName;
   private String lastName;

   @Enumerated(EnumType.STRING)
   private UserRole role = UserRole.USER;

   @CreationTimestamp
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;
}
