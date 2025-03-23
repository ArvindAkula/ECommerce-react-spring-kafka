package com.ecommerce.orderservice.model;

import com.ecommerce.common.event.OrderEvent.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    
    private String userId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> items = new ArrayList<>();
    
    private BigDecimal totalAmount;
    
    private String paymentMethod;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    // Helper method to add an item to the order
    public void addItem(OrderLineItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    // Helper method to remove an item from the order
    public void removeItem(OrderLineItem item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}