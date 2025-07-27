package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.service.OrderService;
import com.example.ecommerce.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderResponse> placeOrder() {
    return ResponseEntity.ok(orderService.placeOrder());
  }

  @GetMapping
  public List<OrderResponse> getOrders() {
    return orderService.getOrders();
  }
}
