package com.example.ecommerce.product.controller;

import com.example.ecommerce.cart.controller.CartController;
import com.example.ecommerce.cart.dto.CartRequest;
import com.example.ecommerce.cart.dto.CartResponse;
import com.example.ecommerce.cart.service.CartService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

  @Mock
  private CartService cartService;

  @InjectMocks
  private CartController cartController;

  @Test
  void viewCart_ShouldReturnCartItems() {
    List<CartResponse> responses = Arrays.asList(
        new CartResponse(1L, "Product 1", 2, 100.0, 200.0),
        new CartResponse(2L, "Product 2", 1, 50.0, 50.0)
    );

    when(cartService.getCart()).thenReturn(responses);

    List<CartResponse> result = cartController.viewCart();

    assertEquals(2, result.size());
    assertEquals("Product 1", result.get(0).getProductName());
    verify(cartService, times(1)).getCart();
  }

  @Test
  void addToCart_ShouldReturnSuccessMessage() {
    CartRequest request = new CartRequest(1L, 2);

    ResponseEntity<String> response = cartController.addToCart(request);

    assertEquals("Item added to cart", response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    verify(cartService, times(1)).addToCart(request);
  }

  @Test
  void updateCart_ShouldReturnSuccessMessage() {
    CartRequest request = new CartRequest(1L, 5);

    ResponseEntity<String> response = cartController.updateCart(request);

    assertEquals("Cart updated", response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    verify(cartService, times(1)).updateCart(request);
  }

  @Test
  void removeItem_ShouldReturnSuccessMessage() {
    ResponseEntity<String> response = cartController.removeItem(1L);

    assertEquals("Item removed from cart", response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    verify(cartService, times(1)).removeItem(1L);
  }

  @Test
  void clearCart_ShouldReturnSuccessMessage() {
    ResponseEntity<String> response = cartController.clearCart();

    assertEquals("Cart cleared", response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    verify(cartService, times(1)).clearCart();
  }
}


