package com.example.ecommerce.product.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductServiceImpl productService;

  @Test
  void createProduct_ShouldReturnSavedProduct() {
    Product product = new Product();
    product.setId(1L);
    when(productRepository.save(product)).thenReturn(product);

    Product result = productService.createProduct(product);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(productRepository, times(1)).save(product);
  }

  @Test
  void getProductById_WhenFound_ShouldReturnProduct() {
    Product product = new Product();
    product.setId(1L);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    Product result = productService.getProductById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(productRepository, times(1)).findById(1L);
  }

  @Test
  void getProductById_WhenNotFound_ShouldThrowException() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
  }

  @Test
  void getAllProducts_ShouldReturnProductList() {
    Product product1 = new Product();
    Product product2 = new Product();
    when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

    List<Product> result = productService.getAllProducts();

    assertEquals(2, result.size());
    verify(productRepository, times(1)).findAll();
  }

}

