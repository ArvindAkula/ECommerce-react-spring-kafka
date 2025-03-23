package com.ecommerce.inventoryservice.kafka;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(
            topics = KafkaTopics.ORDERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderEventListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event: {}, status: {}", orderEvent.getOrderId(), orderEvent.getStatus());
        
        switch (orderEvent.getStatus()) {
            case CREATED:
                // When order is created, update inventory
                handleOrderCreated(orderEvent);
                break;
            case CANCELLED:
                // When order is cancelled, restore inventory
                handleOrderCancelled(orderEvent);
                break;
            default:
                log.info("No inventory action needed for order status: {}", orderEvent.getStatus());
        }
    }
    
    private void handleOrderCreated(OrderEvent orderEvent) {
        log.info("Processing inventory update for new order: {}", orderEvent.getOrderId());
        
        boolean stockUpdated = productService.processOrderStockChanges(orderEvent.getOrderId(), orderEvent.getItems());
        
        if (stockUpdated) {
            log.info("Successfully updated inventory for order: {}", orderEvent.getOrderId());
        } else {
            log.error("Failed to update inventory for order: {}", orderEvent.getOrderId());
            // In a real-world scenario, you might want to publish a failure event back to Kafka
            // or implement a compensating transaction
        }
    }
    
    private void handleOrderCancelled(OrderEvent orderEvent) {
        log.info("Restoring inventory for cancelled order: {}", orderEvent.getOrderId());
        
        // For each item in the cancelled order, restore the stock
        orderEvent.getItems().forEach(item -> {
            try {
                productService.increaseStock(
                        new com.ecommerce.inventoryservice.dto.StockUpdateRequest(
                                item.getProductId(), 
                                item.getQuantity()
                        )
                );
                log.info("Restored stock for product: {} in cancelled order: {}", 
                        item.getProductId(), orderEvent.getOrderId());
            } catch (Exception e) {
                log.error("Failed to restore stock for product: {} in cancelled order: {}", 
                        item.getProductId(), orderEvent.getOrderId(), e);
            }
        });
    }
}