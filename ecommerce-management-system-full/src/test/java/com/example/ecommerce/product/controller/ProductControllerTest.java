package com.example.ecommerce.product.controller;

import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock
  private ProductService productService;

  @InjectMocks
  private ProductController productController;

  // ------------------ GET ALL PRODUCTS ------------------
  @Test
  void getAllProducts_ShouldReturnProductList() {
    List<Product> products = Arrays.asList(
        new Product(1L, "Product", "1", 100.0, 10, true),
        new Product(2L, "Product", "2", 50.0, 5, true)
    );

    when(productService.getAllProducts()).thenReturn(products);

    List<Product> result = productController.getAllProducts();

    assertEquals(2, result.size());
    assertEquals("Product", result.get(0).getName());
    verify(productService, times(1)).getAllProducts();
  }

  // ------------------ GET PRODUCT BY ID ------------------
  @Test
  void getProductById_ShouldReturnProduct() {
    Product product = new Product(1L, "Product", "1", 100.0, 10, true);

    when(productService.getProductById(1L)).thenReturn(product);

    Product result = productController.getProductById(1L);

    assertEquals("Product", result.getName());
    verify(productService, times(1)).getProductById(1L);
  }

  // ------------------ CREATE PRODUCT ------------------
  @Test
  void createProduct_ShouldReturnCreatedProduct() {
    Product product = new Product(1L, "New Product", "1", 120.0, 15, true);

    when(productService.createProduct(product)).thenReturn(product);

    ResponseEntity<Product> response = productController.createProduct(product);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("New Product", response.getBody().getName());
    verify(productService, times(1)).createProduct(product);
  }

  // ------------------ UPDATE PRODUCT ------------------
  @Test
  void updateProduct_ShouldReturnUpdatedProduct() {
    Product product = new Product(1L, "Product", "1", 150.0, 20, true);

    when(productService.updateProduct(1L, product)).thenReturn(product);

    ResponseEntity<Product> response = productController.updateProduct(1L, product);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Product", response.getBody().getName());
    verify(productService, times(1)).updateProduct(1L, product);
  }

  // ------------------ DELETE PRODUCT ------------------
  @Test
  void deleteProduct_ShouldReturnNoContent() {
    ResponseEntity<Void> response = productController.deleteProduct(1L);

    assertEquals(204, response.getStatusCodeValue());
    verify(productService, times(1)).deleteProduct(1L);
  }

  // ------------------ ACTIVATE PRODUCT ------------------
  @Test
  void activateProduct_ShouldReturnSuccessMessage() {
    when(productService.activateProduct(1L)).thenReturn("Product activated");

    ResponseEntity<String> response = productController.activateProduct(1L);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Product activated", response.getBody());
    verify(productService, times(1)).activateProduct(1L);
  }
}

