package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.AuthRequest;
import com.example.ecommerce.user.dto.AuthResponse;


public interface UserService {
  void register(AuthRequest request, boolean admin);
  public AuthResponse login(AuthRequest request);
  public void deleteUser(Long id);
}
