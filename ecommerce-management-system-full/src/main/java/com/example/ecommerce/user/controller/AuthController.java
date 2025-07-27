package com.example.ecommerce.user.controller;

import com.example.ecommerce.user.service.UserService;
import com.example.ecommerce.user.dto.AuthRequest;
import com.example.ecommerce.user.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody AuthRequest request, boolean admin) {
    userService.register(request, admin);
    return ResponseEntity.ok("User registered successfully");
  }


  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok("User deleted successfully");
  }
}

