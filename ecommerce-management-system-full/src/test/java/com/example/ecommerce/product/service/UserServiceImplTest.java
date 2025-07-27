package com.example.ecommerce.product.service;

import com.example.ecommerce.security.JwtUtil;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.user.dto.AuthRequest;
import com.example.ecommerce.user.dto.AuthResponse;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private UserServiceImpl userService;

  private AuthRequest authRequest;
  private User user;

  @BeforeEach
  void setUp() {
    authRequest = new AuthRequest("testuser", "password");

    user = User.builder()
        .id(1L)
        .username("testuser")
        .password("encodedPassword")
        .role("ROLE_USER")
        .build();
  }

  // ================== register() ==================
  @Test
  void register_WhenUsernameExists_ShouldThrowException() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    assertThrows(BadRequestException.class, () -> userService.register(authRequest, false));
  }

  @Test
  void register_ShouldSaveUserWithEncodedPassword_AndAssignRole() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

    userService.register(authRequest, false);

    verify(userRepository).save(argThat(savedUser ->
        savedUser.getUsername().equals("testuser")
            && savedUser.getPassword().equals("encodedPassword")
            && savedUser.getRole().equals("ROLE_USER")
    ));
  }

  @Test
  void register_AdminFlag_ShouldAssignAdminRole() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

    userService.register(authRequest, true);

    verify(userRepository).save(argThat(savedUser ->
        savedUser.getRole().equals("ROLE_ADMIN")
    ));
  }

  // ================== login() ==================
  @Test
  void login_WhenUserNotFound_ShouldThrowUnauthorized() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    assertThrows(UnauthorizedException.class, () -> userService.login(authRequest));
  }

  @Test
  void login_WhenPasswordDoesNotMatch_ShouldThrowUnauthorized() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

    assertThrows(UnauthorizedException.class, () -> userService.login(authRequest));
  }

  @Test
  void login_WhenValid_ShouldReturnAuthResponseWithToken() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
    when(jwtUtil.generateToken("testuser", "ROLE_USER")).thenReturn("mockToken");

    AuthResponse response = userService.login(authRequest);

    assertNotNull(response);
    assertEquals("mockToken", response.getToken());
  }

  // ================== deleteUser() ==================
  @Test
  void deleteUser_WhenNotFound_ShouldThrowException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
  }

  @Test
  void deleteUser_WhenFound_ShouldDeleteUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    userService.deleteUser(1L);

    verify(userRepository, times(1)).delete(user);
  }
}