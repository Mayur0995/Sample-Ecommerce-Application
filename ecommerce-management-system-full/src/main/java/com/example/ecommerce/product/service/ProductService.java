package com.example.ecommerce.product.service;

import com.example.ecommerce.product.Product;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductService {
  Product createProduct(Product product);
  Product updateProduct(Long id, Product product);
  void deleteProduct(Long id);
  List<Product> getAllProducts();
  Product getProductById(Long id);
  public String activateProduct(@PathVariable Long id);
}
