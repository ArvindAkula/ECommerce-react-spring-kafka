package com.ecommerce.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @NotBlank
    private String productId;
    
    @NotBlank
    private String name;
    
    @NotNull
    @Positive
    private BigDecimal price;
    
    @NotNull
    @Positive
    private Integer quantity;
}