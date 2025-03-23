package com.ecommerce.common.event;

import com.ecommerce.common.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    @NotBlank
    private String orderId;
    
    @NotBlank
    private String userId;
    
    @NotEmpty
    private List<OrderItem> items;
    
    @NotNull
    @Positive
    private BigDecimal totalAmount;
    
    @NotBlank
    private String paymentMethod;
    
    @NotNull
    private LocalDateTime timestamp;
    
    @NotNull
    private OrderStatus status;

    public enum OrderStatus {
        CREATED,
        PAYMENT_PENDING,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}