package com.example.ecommerce.user.service;

import com.example.ecommerce.security.JwtUtil;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.user.dto.AuthRequest;
import com.example.ecommerce.user.dto.AuthResponse;
import com.example.ecommerce.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public void register(AuthRequest request, boolean admin) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BadRequestException("Username already exists");
    }
    String role = admin ? "ROLE_ADMIN" : "ROLE_USER";
    User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(role)
        .build();
    userRepository.save(user);
  }

  public AuthResponse login(AuthRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Invalid username or password");
    }

    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
    return new AuthResponse(token);
  }
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

    userRepository.delete(user);
  }

}
