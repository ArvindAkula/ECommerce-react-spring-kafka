package com.ecommerce.orderservice.dto;

import com.ecommerce.common.event.OrderEvent.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private String userId;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;
    private String estimatedDelivery;
}