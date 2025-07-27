package com.example.ecommerce.product.service;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.order.dto.OrderResponse;
import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.order.service.OrderRepository;
import com.example.ecommerce.order.service.OrderServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  private User user;
  private Product product;
  private CartItem cartItem;
  private Order order;

  @BeforeEach
  void setUp() {
    // Mock security context
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("testuser");

    SecurityContext context = mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    // Setup user
    user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    // Setup product
    product = new Product();
    product.setId(1L);
    product.setName("Test Product");
    product.setStock(10);
    product.setPrice(100.0);
    product.setActive(true);

    // Setup cart item
    cartItem = CartItem.builder()
        .user(user)
        .product(product)
        .quantity(2)
        .build();

    // Setup order
    order = new Order();
    order.setId(1L);
    order.setUser(user);
    order.setTotalAmount(200.0);
    order.setCreatedAt(LocalDateTime.now());
    order.setItems(Arrays.asList(
        OrderItem.builder()
            .product(product)
            .quantity(2)
            .price(100.0)
            .order(order)
            .build()
    ));
  }

  @Test
  void placeOrder_ShouldCreateOrderAndClearCart() {
    when(cartRepository.findByUser(user)).thenReturn(Arrays.asList(cartItem));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order saved = invocation.getArgument(0);
      saved.setId(1L);
      return saved;
    });

    OrderResponse response = orderService.placeOrder();

    assertNotNull(response);
    assertEquals(200.0, response.getTotalAmount());
    verify(cartRepository, times(1)).deleteByUser(user);
    verify(productRepository, times(1)).save(product);
  }

  @Test
  void placeOrder_WhenCartEmpty_ShouldThrowException() {
    when(cartRepository.findByUser(user)).thenReturn(Collections.emptyList());

    assertThrows(BadRequestException.class, () -> orderService.placeOrder());
  }

  @Test
  void placeOrder_WhenStockInsufficient_ShouldThrowException() {
    product.setStock(1); // less than cart quantity
    when(cartRepository.findByUser(user)).thenReturn(Arrays.asList(cartItem));

    assertThrows(BadRequestException.class, () -> orderService.placeOrder());
  }

  @Test
  void getOrders_ShouldReturnOrderResponses() {
    when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

    List<OrderResponse> responses = orderService.getOrders();

    assertEquals(1, responses.size());
    assertEquals(200.0, responses.get(0).getTotalAmount());
    assertEquals(1, responses.get(0).getItems().size());
    assertEquals("Test Product", responses.get(0).getItems().get(0).getProductName());
  }

  @Test
  void getOrders_WhenNoOrders_ShouldReturnEmptyList() {
    when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());

    List<OrderResponse> responses = orderService.getOrders();

    assertTrue(responses.isEmpty());
  }

  @Test
  void getCurrentUser_WhenUserNotFound_ShouldThrowUnauthorized() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    // Reset context to force method call
    SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
    assertThrows(UnauthorizedException.class, () -> {
      orderService.getOrders();
    });
  }
}

