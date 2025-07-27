package com.example.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private String description;
  private double price;
  private int stock;
  private boolean active;
}
