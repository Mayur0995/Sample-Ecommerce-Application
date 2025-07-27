package com.example.ecommerce.cart.controller;

import com.example.ecommerce.cart.dto.CartRequest;
import com.example.ecommerce.cart.dto.CartResponse;
import com.example.ecommerce.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  public List<CartResponse> viewCart() {
    return cartService.getCart();
  }

  @PostMapping
  public ResponseEntity<String> addToCart(@RequestBody CartRequest request) {
    cartService.addToCart(request);
    return ResponseEntity.ok("Item added to cart");
  }

  @PutMapping
  public ResponseEntity<String> updateCart(@RequestBody CartRequest request) {
    cartService.updateCart(request);
    return ResponseEntity.ok("Cart updated");
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<String> removeItem(@PathVariable Long productId) {
    cartService.removeItem(productId);
    return ResponseEntity.ok("Item removed from cart");
  }

  @DeleteMapping("/clear")
  public ResponseEntity<String> clearCart() {
    cartService.clearCart();
    return ResponseEntity.ok("Cart cleared");
  }
}
