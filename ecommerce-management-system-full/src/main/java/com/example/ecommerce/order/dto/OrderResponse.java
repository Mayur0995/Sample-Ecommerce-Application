package com.example.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
  private Long id;
  private double totalAmount;
  private LocalDateTime createdAt;
  private List<ItemDetail> items;

  @Data
  @AllArgsConstructor
  public static class ItemDetail {
    private String productName;
    private int quantity;
    private double price;
  }
}
