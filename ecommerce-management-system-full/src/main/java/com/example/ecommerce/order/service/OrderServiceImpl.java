package com.example.ecommerce.order.service;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.repository.UserRepository;
import com.example.ecommerce.order.dto.OrderResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public OrderServiceImpl(OrderRepository orderRepository,
      CartRepository cartRepository,
      ProductRepository productRepository,
      UserRepository userRepository) {
    this.orderRepository = orderRepository;
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("User not found"));
  }

  @Transactional
  public OrderResponse placeOrder() {
    User user = getCurrentUser();
    List<CartItem> cartItems = cartRepository.findByUser(user);

    if (cartItems.isEmpty()) {
      throw new BadRequestException("Cart is empty. Cannot place order.");
    }

    // Create order
    Order order = new Order();
    order.setUser(user);
    order.setCreatedAt(LocalDateTime.now());

    // Calculate order items and total price
    List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
      Product product = cartItem.getProduct();

      if (product.getStock() < cartItem.getQuantity()) {
        throw new BadRequestException("Not enough stock for product: " + product.getName());
      }
      product.setStock(product.getStock() - cartItem.getQuantity());
      productRepository.save(product);

      return OrderItem.builder()
          .order(order)
          .product(product)
          .quantity(cartItem.getQuantity())
          .price(product.getPrice())
          .build();
    }).collect(Collectors.toList());

// Calculate totalAmount separately
    double totalAmount = orderItems.stream()
        .mapToDouble(item -> item.getQuantity() * item.getPrice())
        .sum();

    order.setItems(orderItems);
    order.setTotalAmount(totalAmount);


    orderRepository.save(order);

    // Clear cart
    cartRepository.deleteByUser(user);

    return new OrderResponse(
        order.getId(),
        totalAmount,
        order.getCreatedAt(),
        orderItems.stream()
            .map(item -> new OrderResponse.ItemDetail(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice()))
            .collect(Collectors.toList())
    );
  }

  public List<OrderResponse> getOrders() {
    User user = getCurrentUser();
    return orderRepository.findByUser(user).stream()
        .map(order -> new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getCreatedAt(),
            order.getItems().stream()
                .map(item -> new OrderResponse.ItemDetail(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getPrice()))
                .collect(Collectors.toList())
        ))
        .collect(Collectors.toList());
  }
}
