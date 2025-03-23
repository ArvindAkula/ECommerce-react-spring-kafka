package com.ecommerce.inventoryservice.service.impl;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.model.OrderItem;
import com.ecommerce.inventoryservice.dto.ProductRequest;
import com.ecommerce.inventoryservice.dto.ProductResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.exception.InsufficientStockException;
import com.ecommerce.inventoryservice.exception.ProductNotFoundException;
import com.ecommerce.inventoryservice.model.Product;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import com.ecommerce.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product: {}", productRequest.getName());
        
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .category(productRequest.getCategory())
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(String productId) {
        log.info("Getting product with ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products");
        
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Getting products in category: {}", category);
        
        List<Product> products = productRepository.findByCategory(category);
        
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        
        List<Product> products = productRepository.searchByKeyword(keyword);
        
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getId());
        
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId) {
        log.info("Deleting product with ID: {}", productId);
        
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
        
        productRepository.deleteById(productId);
        log.info("Product deleted: {}", productId);
    }

    @Override
    @Transactional
    public ProductResponse increaseStock(StockUpdateRequest stockUpdateRequest) {
        log.info("Increasing stock for product ID: {} by {}", 
                stockUpdateRequest.getProductId(), stockUpdateRequest.getQuantity());
        
        Product product = productRepository.findById(stockUpdateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + stockUpdateRequest.getProductId()));
        
        product.increaseStock(stockUpdateRequest.getQuantity());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Stock increased for product: {}, new stock: {}", 
                updatedProduct.getId(), updatedProduct.getStockQuantity());
        
        // Publish inventory update event
        publishInventoryUpdate(updatedProduct.getId(), updatedProduct.getStockQuantity());
        
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse decreaseStock(StockUpdateRequest stockUpdateRequest) {
        log.info("Decreasing stock for product ID: {} by {}", 
                stockUpdateRequest.getProductId(), stockUpdateRequest.getQuantity());
        
        Product product = productRepository.findById(stockUpdateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + stockUpdateRequest.getProductId()));
        
        if (product.getStockQuantity() < stockUpdateRequest.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        
        product.reduceStock(stockUpdateRequest.getQuantity());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Stock decreased for product: {}, new stock: {}", 
                updatedProduct.getId(), updatedProduct.getStockQuantity());
        
        // Publish inventory update event
        publishInventoryUpdate(updatedProduct.getId(), updatedProduct.getStockQuantity());
        
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public boolean isInStock(String productId, int quantity) {
        log.info("Checking if product ID: {} is in stock with quantity: {}", productId, quantity);
        
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean processOrderStockChanges(String orderId, List<OrderItem> items) {
        log.info("Processing order stock changes for order: {}", orderId);
        
        Map<String, Integer> productQuantities = new HashMap<>();
        
        // Aggregate quantities by product ID
        for (OrderItem item : items) {
            productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        
        // Check if all products have sufficient stock
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            if (!isInStock(entry.getKey(), entry.getValue())) {
                log.warn("Insufficient stock for product ID: {} in order: {}", entry.getKey(), orderId);
                return false;
            }
        }
        
        // Update stock for all products
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            try {
                Product product = productRepository.findById(entry.getKey())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + entry.getKey()));
                
                product.reduceStock(entry.getValue());
                productRepository.save(product);
                
                // Publish inventory update
                publishInventoryUpdate(product.getId(), product.getStockQuantity());
                
            } catch (Exception e) {
                log.error("Failed to update stock for product ID: {} in order: {}", entry.getKey(), orderId, e);
                return false;
            }
        }
        
        log.info("Successfully processed stock changes for order: {}", orderId);
        return true;
    }
    
    private void publishInventoryUpdate(String productId, int newStockQuantity) {
        Map<String, Object> update = new HashMap<>();
        update.put("productId", productId);
        update.put("stockQuantity", newStockQuantity);
        update.put("timestamp", System.currentTimeMillis());
        
        kafkaTemplate.send(KafkaTopics.INVENTORY_UPDATES_TOPIC, productId, update);
        log.info("Published inventory update for product: {}, new stock: {}", productId, newStockQuantity);
    }
    
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .inStock(product.getStockQuantity() > 0)
                .build();
    }
}