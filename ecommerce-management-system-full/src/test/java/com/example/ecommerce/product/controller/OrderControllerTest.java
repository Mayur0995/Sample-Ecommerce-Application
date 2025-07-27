package com.example.ecommerce.product.controller;

import com.example.ecommerce.order.controller.OrderController;
import com.example.ecommerce.order.dto.OrderResponse;
import com.example.ecommerce.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock
  private OrderService orderService;

  @InjectMocks
  private OrderController orderController;

  @Test
  void placeOrder_ShouldReturnOrderResponse() {
    // Arrange
    OrderResponse.ItemDetail itemDetail = new OrderResponse.ItemDetail("Product 1", 2, 100.0);
    OrderResponse response = new OrderResponse(1L, 200.0, LocalDateTime.now(), Arrays.asList(itemDetail));
    when(orderService.placeOrder()).thenReturn(response);

    // Act
    ResponseEntity<OrderResponse> result = orderController.placeOrder();

    // Assert
    assertEquals(200, result.getStatusCodeValue());
    assertEquals(200.0, result.getBody().getTotalAmount());
    assertEquals("Product 1", result.getBody().getItems().get(0).getProductName());

    verify(orderService, times(1)).placeOrder();
  }

  @Test
  void getOrders_ShouldReturnListOfOrders() {
    // Arrange
    OrderResponse.ItemDetail item1 = new OrderResponse.ItemDetail("Product 1", 2, 100.0);
    OrderResponse order1 = new OrderResponse(1L, 200.0, LocalDateTime.now(), Arrays.asList(item1));

    OrderResponse.ItemDetail item2 = new OrderResponse.ItemDetail("Product 2", 1, 50.0);
    OrderResponse order2 = new OrderResponse(2L, 50.0, LocalDateTime.now(), Arrays.asList(item2));

    when(orderService.getOrders()).thenReturn(Arrays.asList(order1, order2));

    // Act
    List<OrderResponse> result = orderController.getOrders();

    // Assert
    assertEquals(2, result.size());
    assertEquals(200.0, result.get(0).getTotalAmount());
    assertEquals("Product 2", result.get(1).getItems().get(0).getProductName());

    verify(orderService, times(1)).getOrders();
  }
}

