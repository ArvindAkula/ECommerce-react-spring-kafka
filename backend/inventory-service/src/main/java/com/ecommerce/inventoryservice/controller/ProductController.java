package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.ProductRequest;
import com.ecommerce.inventoryservice.dto.ProductResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("REST request to create a product: {}", productRequest.getName());
        ProductResponse response = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        log.info("REST request to get product by ID: {}", productId);
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("REST request to get all products");
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.info("REST request to get products by category: {}", category);
        List<ProductResponse> responses = productService.getProductsByCategory(category);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.info("REST request to search products with keyword: {}", keyword);
        List<ProductResponse> responses = productService.searchProducts(keyword);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest productRequest) {
        log.info("REST request to update product with ID: {}", productId);
        ProductResponse response = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        log.info("REST request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/increase-stock")
    public ResponseEntity<ProductResponse> increaseStock(
            @PathVariable String productId,
            @Valid @RequestBody StockUpdateRequest stockUpdateRequest) {
        log.info("REST request to increase stock for product ID: {}", productId);
        
        // Ensure productId in path and request body match
        if (!productId.equals(stockUpdateRequest.getProductId())) {
            stockUpdateRequest = new StockUpdateRequest(productId, stockUpdateRequest.getQuantity());
        }
        
        ProductResponse response = productService.increaseStock(stockUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/decrease-stock")
    public ResponseEntity<ProductResponse> decreaseStock(
            @PathVariable String productId,
            @Valid @RequestBody StockUpdateRequest stockUpdateRequest) {
        log.info("REST request to decrease stock for product ID: {}", productId);
        
        // Ensure productId in path and request body match
        if (!productId.equals(stockUpdateRequest.getProductId())) {
            stockUpdateRequest = new StockUpdateRequest(productId, stockUpdateRequest.getQuantity());
        }
        
        ProductResponse response = productService.decreaseStock(stockUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/in-stock")
    public ResponseEntity<Boolean> isInStock(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") int quantity) {
        log.info("REST request to check if product ID: {} is in stock with quantity: {}", productId, quantity);
        boolean inStock = productService.isInStock(productId, quantity);
        return ResponseEntity.ok(inStock);
    }
}