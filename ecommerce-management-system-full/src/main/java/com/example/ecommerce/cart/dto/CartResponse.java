package com.example.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
  private Long productId;
  private String productName;
  private int quantity;
  private double price;
  private double totalPrice;
}
