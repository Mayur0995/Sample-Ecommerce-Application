package com.example.ecommerce.cart.model;

import com.example.ecommerce.product.Product;
import com.example.ecommerce.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private int quantity;
}
