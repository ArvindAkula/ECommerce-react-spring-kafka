package com.ecommerce.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private BigDecimal price;
    
    private Integer stockQuantity;
    
    private String category;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Reduce product stock by the specified quantity
     * 
     * @param quantity the quantity to reduce
     * @return true if stock was reduced successfully, false if not enough stock
     */
    public boolean reduceStock(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
            return true;
        }
        return false;
    }
    
    /**
     * Increase product stock by the specified quantity
     * 
     * @param quantity the quantity to add
     */
    public void increaseStock(int quantity) {
        stockQuantity += quantity;
    }
}