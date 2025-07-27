package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.dto.CartResponse;
import com.example.ecommerce.cart.dto.CartRequest;

import java.util.List;

public interface CartService {
  List<CartResponse> getCart();
  void addToCart(CartRequest request);
  void updateCart(CartRequest request);
  void removeItem(Long productId);
  void clearCart();
}
