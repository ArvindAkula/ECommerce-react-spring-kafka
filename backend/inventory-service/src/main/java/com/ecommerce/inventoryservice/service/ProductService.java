package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.ProductRequest;
import com.ecommerce.inventoryservice.dto.ProductResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;

import java.util.List;

public interface ProductService {
    
    /**
     * Create a new product
     * 
     * @param productRequest the product details
     * @return the created product
     */
    ProductResponse createProduct(ProductRequest productRequest);
    
    /**
     * Get a product by ID
     * 
     * @param productId the product ID
     * @return the product if found
     */
    ProductResponse getProductById(String productId);
    
    /**
     * Get all products
     * 
     * @return list of all products
     */
    List<ProductResponse> getAllProducts();
    
    /**
     * Get products by category
     * 
     * @param category the category
     * @return list of products in the category
     */
    List<ProductResponse> getProductsByCategory(String category);
    
    /**
     * Search products by keyword
     * 
     * @param keyword the search keyword
     * @return list of products matching the keyword
     */
    List<ProductResponse> searchProducts(String keyword);
    
    /**
     * Update a product
     * 
     * @param productId the product ID
     * @param productRequest the updated product details
     * @return the updated product
     */
    ProductResponse updateProduct(String productId, ProductRequest productRequest);
    
    /**
     * Delete a product
     * 
     * @param productId the product ID
     */
    void deleteProduct(String productId);
    
    /**
     * Increase product stock quantity
     * 
     * @param stockUpdateRequest the stock update details
     * @return the updated product
     */
    ProductResponse increaseStock(StockUpdateRequest stockUpdateRequest);
    
    /**
     * Decrease product stock quantity
     * 
     * @param stockUpdateRequest the stock update details
     * @return the updated product
     */
    ProductResponse decreaseStock(StockUpdateRequest stockUpdateRequest);
    
    /**
     * Check if a product is in stock
     * 
     * @param productId the product ID
     * @param quantity the quantity to check
     * @return true if the product is in stock with the required quantity
     */
    boolean isInStock(String productId, int quantity);
    
    /**
     * Process order stock changes (called by Kafka consumer when order events are received)
     * 
     * @param orderId the order ID
     * @param items the order items with product IDs and quantities
     * @return true if stock was successfully updated for all items
     */
    boolean processOrderStockChanges(String orderId, List<com.ecommerce.common.model.OrderItem> items);
}