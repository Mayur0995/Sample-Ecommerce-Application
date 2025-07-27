package com.example.ecommerce.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password; // Stored as BCrypt hash

  @Column(nullable = false)
  private String role; // "ROLE_USER" or "ROLE_ADMIN"
}
