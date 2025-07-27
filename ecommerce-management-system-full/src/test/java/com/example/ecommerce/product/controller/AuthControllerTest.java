package com.example.ecommerce.product.controller;

import com.example.ecommerce.user.controller.AuthController;
import com.example.ecommerce.user.dto.AuthRequest;
import com.example.ecommerce.user.dto.AuthResponse;
import com.example.ecommerce.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthController authController;

  @Test
  void register_ShouldReturnSuccessMessage() {
    AuthRequest request = new AuthRequest("testuser", "password");

    ResponseEntity<String> response = authController.register(request, false);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("User registered successfully", response.getBody());
    verify(userService, times(1)).register(request, false);
  }

  @Test
  void login_ShouldReturnAuthResponse() {
    AuthRequest request = new AuthRequest("testuser", "password");
    AuthResponse authResponse = new AuthResponse("mockToken");

    when(userService.login(request)).thenReturn(authResponse);

    ResponseEntity<AuthResponse> response = authController.login(request);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("mockToken", response.getBody().getToken());
    verify(userService, times(1)).login(request);
  }

  @Test
  void deleteUser_ShouldReturnSuccessMessage() {
    ResponseEntity<String> response = authController.deleteUser(1L);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("User deleted successfully", response.getBody());
    verify(userService, times(1)).deleteUser(1L);
  }
}

