package com.example.ecommerce.order.service;

import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUser(User user);
}
