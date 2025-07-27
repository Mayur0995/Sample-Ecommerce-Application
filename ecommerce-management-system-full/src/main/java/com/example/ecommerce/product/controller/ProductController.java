package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  // Public: View all products
  @GetMapping
  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  // Public: View product by ID
  @GetMapping("/{id}")
  public Product getProductById(@PathVariable Long id) {
    return productService.getProductById(id);
  }

  // Admin: Create product
  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    return ResponseEntity.ok(productService.createProduct(product));
  }

  // Admin: Update product
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
    return ResponseEntity.ok(productService.updateProduct(id, product));
  }

  // Admin: Delete product
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
  @PutMapping("/{id}/activate")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<String> activateProduct(@PathVariable Long id) {
     return ResponseEntity.ok(productService.activateProduct(id));
  }

}
