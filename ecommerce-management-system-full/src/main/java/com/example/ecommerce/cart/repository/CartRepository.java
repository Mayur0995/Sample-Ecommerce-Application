package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(User user);
  Optional<CartItem> findByUserAndProductId(User user, Long productId);
  void deleteByUser(User user);
}
