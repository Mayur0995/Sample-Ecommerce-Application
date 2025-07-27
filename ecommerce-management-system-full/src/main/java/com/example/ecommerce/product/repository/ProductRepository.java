package com.example.ecommerce.product.repository;

import com.example.ecommerce.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByActiveTrue();
}
