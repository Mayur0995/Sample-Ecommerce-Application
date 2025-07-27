package com.example.ecommerce.product.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Product createProduct(Product product) {
    return productRepository.save(product);
  }

  @Override
  public Product updateProduct(Long id, Product product) {
    Product existing = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    existing.setName(product.getName());
    existing.setDescription(product.getDescription());
    existing.setPrice(product.getPrice());
    existing.setStock(product.getStock());
    return productRepository.save(existing);
  }

  @Override
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new ResourceNotFoundException("Product not found with id " + id);
    }
    productRepository.deleteById(id);
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
  }

  public String activateProduct(@PathVariable Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    product.setActive(true);
    productRepository.save(product);
    return "Product activated successfully";
  }

}
