package com.ecommerce.orderservice.config;

import com.ecommerce.common.event.OrderEvent.OrderStatus;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderLineItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                    .status(OrderStatus.DELIVERED) // Using the correct status from OrderEvent
                    .totalAmount(new BigDecimal("999.98"))
                    .paymentMethod("CREDIT_CARD")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .updatedAt(LocalDateTime.now().minusDays(4))
                    .items(new ArrayList<>()) // Initialize the list
                    .build();
                
                OrderLineItem item1 = OrderLineItem.builder()
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .name("Smartphone X")
                    .quantity(1)
                    .price(new BigDecimal("899.99"))
                    .order(completedOrder)
                    .build();
                
                OrderLineItem item2 = OrderLineItem.builder()
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .name("Bluetooth Speaker")
                    .quantity(1)
                    .price(new BigDecimal("99.99"))
                    .order(completedOrder)
                    .build();
                
                // Add items using addItem method to ensure bidirectional relationship
                completedOrder.addItem(item1);
                completedOrder.addItem(item2);

                // Create a pending order
                Order pendingOrder = Order.builder()
                    .id(UUID.randomUUID().toString())
                    .userId("user1")
                    .status(OrderStatus.PAYMENT_PENDING) // Using the correct status from OrderEvent
                    .totalAmount(new BigDecimal("1299.99"))
                    .paymentMethod("PAYPAL")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now().minusDays(1))
                    .items(new ArrayList<>()) // Initialize the list
                    .build();
                
                OrderLineItem item3 = OrderLineItem.builder()
                    .productId(UUID.randomUUID().toString()) // This would need to match a real product ID in a real app
                    .name("Laptop Pro")
                    .quantity(1)
                    .price(new BigDecimal("1299.99"))
                    .order(pendingOrder)
                    .build();
                
                // Add item using addItem method to ensure bidirectional relationship
                pendingOrder.addItem(item3);
                
                orderRepository.saveAll(List.of(completedOrder, pendingOrder));
                log.info("Added sample orders with IDs: {} and {}", 
                         completedOrder.getId(), pendingOrder.getId());
            } else {
                log.info("Orders already exist. Skipping initialization.");
            }
        };
    }
}