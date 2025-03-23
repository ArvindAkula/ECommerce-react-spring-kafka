package com.ecommerce.paymentservice.dto;

import com.ecommerce.paymentservice.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private String id;
    
    private String orderId;
    
    private String userId;
    
    private BigDecimal amount;
    
    private Payment.PaymentStatus status;
    
    private String paymentMethod;
    
    private String transactionId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Masked card details for security
    private String maskedCardNumber;
}