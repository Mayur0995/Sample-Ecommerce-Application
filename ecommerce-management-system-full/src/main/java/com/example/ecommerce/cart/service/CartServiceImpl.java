package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.dto.CartRequest;
import com.example.ecommerce.cart.dto.CartResponse;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public List<CartResponse> getCart() {
    User user = getCurrentUser();
    List<CartItem> items = cartRepository.findByUser(user);

    return items.stream()
        .map(i -> new CartResponse(
            i.getProduct().getId(),
            i.getProduct().getName(),
            i.getQuantity(),
            i.getProduct().getPrice(),
            i.getProduct().getPrice() * i.getQuantity()))
        .collect(Collectors.toList());
  }

  @Transactional
  public void addToCart(CartRequest request) {
    User user = getCurrentUser();
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + request.getProductId()));

    if (!product.isActive()) {
      throw new BadRequestException("Product is inactive and cannot be added to the cart");
    }

    if (request.getQuantity() <= 0) {
      throw new BadRequestException("Quantity must be greater than zero");
    }

    // Check existing cart item
    cartRepository.findByUserAndProductId(user, request.getProductId())
        .ifPresentOrElse(existing -> {
          int newQuantity = existing.getQuantity() + request.getQuantity();
          if (newQuantity > product.getStock()) {
            throw new BadRequestException("Only " + product.getStock() + " items available in stock");
          }
          existing.setQuantity(newQuantity);
        }, () -> {
          if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Only " + product.getStock() + " items available in stock");
          }
          cartRepository.save(CartItem.builder()
              .user(user)
              .product(product)
              .quantity(request.getQuantity())
              .build());
        });
  }


  @Transactional
  public void updateCart(CartRequest request) {
    User user = getCurrentUser();
    CartItem item = cartRepository.findByUserAndProductId(user, request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

    item.setQuantity(request.getQuantity());
    cartRepository.save(item);
  }

  @Transactional
  public void removeItem(Long productId) {
    User user = getCurrentUser();
    CartItem item = cartRepository.findByUserAndProductId(user, productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not in cart with id " + productId));
    cartRepository.delete(item);
  }

  @Transactional
  public void clearCart() {
    User user = getCurrentUser();
    cartRepository.deleteByUser(user);
  }
}
