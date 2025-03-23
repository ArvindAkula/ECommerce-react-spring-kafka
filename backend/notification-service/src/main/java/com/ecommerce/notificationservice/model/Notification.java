package com.ecommerce.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    
    private String userId;
    
    private String recipientEmail;
    
    private String subject;
    
    @Column(length = 4000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    
    private String orderId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        ORDER_CONFIRMATION,
        ORDER_SHIPPED,
        ORDER_DELIVERED,
        PAYMENT_CONFIRMATION,
        PAYMENT_FAILURE,
        REFUND_ISSUED
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }
}