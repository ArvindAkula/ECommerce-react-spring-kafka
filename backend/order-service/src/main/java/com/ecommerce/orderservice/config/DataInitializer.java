package com.ecommerce.orderservice.config;

import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final OrderRepository orderRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting order database initialization...");
            
            if (orderRepository.count() == 0) {
                log.info("No orders found. Adding sample orders.");
                
                // Create a completed order
                Order completedOrder = Order.builder()
                    .id(UUID.randomUUID().toString())
                    .userId("user1")
                    .status(OrderStatus.COMPLETED)
                    .totalAmount(new BigDecimal("999.98"))
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .updatedAt(LocalDateTime.now().minusDays(4))
                    .build();
                
                OrderItem item1 = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .productName("Smartphone X")
                    .quantity(1)
                    .price(new BigDecimal("899.99"))
                    .subtotal(new BigDecimal("899.99"))
                    .order(completedOrder)
                    .build();
                
                OrderItem item2 = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .productName("Bluetooth Speaker")
                    .quantity(1)
                    .price(new BigDecimal("99.99"))
                    .subtotal(new BigDecimal("99.99"))
                    .order(completedOrder)
                    .build();
                
                completedOrder.setOrderItems(List.of(item1, item2));

                // Create a pending order
                Order pendingOrder = Order.builder()
                    .id(UUID.randomUUID().toString())
                    .userId("user1")
                    .status(OrderStatus.PENDING)
                    .totalAmount(new BigDecimal("1299.99"))
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now().minusDays(1))
                    .build();
                
                OrderItem item3 = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .productName("Laptop Pro")
                    .quantity(1)
                    .price(new BigDecimal("1299.99"))
                    .subtotal(new BigDecimal("1299.99"))
                    .order(pendingOrder)
                    .build();
                
                pendingOrder.setOrderItems(List.of(item3));
                
                orderRepository.saveAll(List.of(completedOrder, pendingOrder));
                log.info("Added sample orders");
            } else {
                log.info("Orders already exist. Skipping initialization.");
            }
        };
    }
}
