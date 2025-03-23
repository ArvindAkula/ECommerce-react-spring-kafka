package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.ProductRequest;
import com.ecommerce.inventoryservice.dto.ProductResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.exception.InsufficientStockException;
import com.ecommerce.inventoryservice.exception.ProductNotFoundException;
import com.ecommerce.inventoryservice.model.Product;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import com.ecommerce.inventoryservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, kafkaTemplate);
    }

    @Test
    void createProduct_ShouldSaveProduct() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .category("Electronics")
                .build();
        
        Product savedProduct = createProduct();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        
        // Act
        ProductResponse response = productService.createProduct(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(savedProduct.getId(), response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(request.getStockQuantity(), response.getStockQuantity());
        assertTrue(response.isInStock());
        
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        Product product = createProduct();
        product.setId(productId);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Act
        ProductResponse response = productService.getProductById(productId);
        
        // Assert
        assertNotNull(response);
        assertEquals(productId, response.getId());
        assertEquals(product.getName(), response.getName());
        
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        String productId = "nonexistent";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        verify(productRepository).findById(productId);
    }

    @Test
    void increaseStock_ShouldUpdateStock() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        StockUpdateRequest request = new StockUpdateRequest(productId, 5);
        
        Product product = createProduct();
        product.setId(productId);
        product.setStockQuantity(10);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // Act
        ProductResponse response = productService.increaseStock(request);
        
        // Assert
        assertEquals(15, response.getStockQuantity()); // 10 original + 5 increased
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    void decreaseStock_WhenSufficientStock_ShouldUpdateStock() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        StockUpdateRequest request = new StockUpdateRequest(productId, 5);
        
        Product product = createProduct();
        product.setId(productId);
        product.setStockQuantity(10);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // Act
        ProductResponse response = productService.decreaseStock(request);
        
        // Assert
        assertEquals(5, response.getStockQuantity()); // 10 original - 5 decreased
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    void decreaseStock_WhenInsufficientStock_ShouldThrowException() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        StockUpdateRequest request = new StockUpdateRequest(productId, 15);
        
        Product product = createProduct();
        product.setId(productId);
        product.setStockQuantity(10);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> productService.decreaseStock(request));
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void isInStock_WhenSufficientStock_ShouldReturnTrue() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        int quantity = 5;
        
        Product product = createProduct();
        product.setId(productId);
        product.setStockQuantity(10);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Act
        boolean result = productService.isInStock(productId, quantity);
        
        // Assert
        assertTrue(result);
        verify(productRepository).findById(productId);
    }
    
    @Test
    void isInStock_WhenInsufficientStock_ShouldReturnFalse() {
        // Arrange
        String productId = UUID.randomUUID().toString();
        int quantity = 15;
        
        Product product = createProduct();
        product.setId(productId);
        product.setStockQuantity(10);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Act
        boolean result = productService.isInStock(productId, quantity);
        
        // Assert
        assertFalse(result);
        verify(productRepository).findById(productId);
    }

    private Product createProduct() {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .category("Electronics")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}