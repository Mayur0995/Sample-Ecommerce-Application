package com.example.ecommerce.product.service;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.dto.CartRequest;
import com.example.ecommerce.cart.dto.CartResponse;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.cart.service.CartServiceImpl;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CartServiceImpl cartService;

  private User user;
  private Product product;

  @BeforeEach
  void setUp() {
    // Mock Security Context
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("testuser");

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Common user & product setup
    user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    product = new Product();
    product.setId(1L);
    product.setName("Test Product");
    product.setPrice(100.0);
    product.setActive(true);
    product.setStock(10);

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
  }

  @Test
  void getCart_ShouldReturnCartResponses() {
    CartItem item = CartItem.builder()
        .user(user)
        .product(product)
        .quantity(2)
        .build();

    when(cartRepository.findByUser(user)).thenReturn(Arrays.asList(item));

    List<CartResponse> responses = cartService.getCart();

    assertEquals(1, responses.size());
    assertEquals(200.0, responses.get(0).getTotalPrice());
  }

  @Test
  void addToCart_WhenProductInactive_ShouldThrowException() {
    product.setActive(false);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    CartRequest request = new CartRequest(1L, 1);

    assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
  }

  @Test
  void addToCart_WhenQuantityZero_ShouldThrowException() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    CartRequest request = new CartRequest(1L, 0);

    assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
  }

  @Test
  void addToCart_WhenQuantityExceedsStock_ShouldThrowException() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    CartRequest request = new CartRequest(1L, 20);

    assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
  }

  @Test
  void addToCart_WhenNewItem_ShouldSaveCartItem() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());

    CartRequest request = new CartRequest(1L, 2);

    cartService.addToCart(request);

    verify(cartRepository, times(1)).save(any(CartItem.class));
  }

  @Test
  void addToCart_WhenExistingItem_ShouldUpdateQuantity() {
    CartItem existing = CartItem.builder()
        .user(user)
        .product(product)
        .quantity(3)
        .build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(existing));

    CartRequest request = new CartRequest(1L, 2);

    cartService.addToCart(request);

    assertEquals(5, existing.getQuantity());
  }

  @Test
  void updateCart_WhenItemNotInCart_ShouldThrowException() {
    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());

    CartRequest request = new CartRequest(1L, 2);

    assertThrows(ResourceNotFoundException.class, () -> cartService.updateCart(request));
  }

  @Test
  void updateCart_WhenItemExists_ShouldUpdateQuantity() {
    CartItem item = CartItem.builder()
        .user(user)
        .product(product)
        .quantity(2)
        .build();

    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(item));

    CartRequest request = new CartRequest(1L, 5);

    cartService.updateCart(request);

    verify(cartRepository, times(1)).save(item);
    assertEquals(5, item.getQuantity());
  }

  @Test
  void removeItem_WhenNotInCart_ShouldThrowException() {
    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> cartService.removeItem(1L));
  }

  @Test
  void removeItem_WhenExists_ShouldDeleteItem() {
    CartItem item = CartItem.builder()
        .user(user)
        .product(product)
        .quantity(1)
        .build();

    when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(item));

    cartService.removeItem(1L);

    verify(cartRepository, times(1)).delete(item);
  }

  @Test
  void clearCart_ShouldDeleteAllItemsForUser() {
    doNothing().when(cartRepository).deleteByUser(user);

    cartService.clearCart();

    verify(cartRepository, times(1)).deleteByUser(user);
  }
}
