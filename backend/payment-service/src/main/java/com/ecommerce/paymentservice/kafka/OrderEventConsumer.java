package com.ecommerce.paymentservice.kafka;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = KafkaTopics.ORDERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderEventListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event: {}, status: {}", orderEvent.getOrderId(), orderEvent.getStatus());
        
        switch (orderEvent.getStatus()) {
            case CREATED:
                // When order is created, process payment
                handleOrderCreated(orderEvent);
                break;
            case CANCELLED:
                // When order is cancelled, could trigger refund if applicable
                handleOrderCancelled(orderEvent);
                break;
            default:
                log.info("No payment action needed for order status: {}", orderEvent.getStatus());
        }
    }
    
    private void handleOrderCreated(OrderEvent orderEvent) {
        log.info("Processing payment for new order: {}", orderEvent.getOrderId());
        
        // Create PaymentRequest from OrderEvent
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderEvent.getOrderId())
                .userId(orderEvent.getUserId())
                .amount(orderEvent.getTotalAmount())
                .paymentMethod(orderEvent.getPaymentMethod())
                .build();
        
        try {
            // Process payment
            paymentService.processPayment(paymentRequest);
            log.info("Payment processed successfully for order: {}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process payment for order: {}", orderEvent.getOrderId(), e);
            // In a real system, we might publish a payment failure event to trigger compensation
        }
    }
    
    private void handleOrderCancelled(OrderEvent orderEvent) {
        log.info("Checking for refund needs for cancelled order: {}", orderEvent.getOrderId());
        
        // In a real system, we would have a more sophisticated refund flow
        // For now, we'll just log that the order was cancelled
        
        // We could get the latest payment for the order and refund it if it's been processed
        try {
            // Get all payments for the order
            var payments = paymentService.getPaymentsByOrderId(orderEvent.getOrderId());
            
            // Find completed payments that could be refunded
            payments.stream()
                    .filter(payment -> "COMPLETED".equals(payment.getStatus().name()))
                    .findFirst()
                    .ifPresent(payment -> {
                        log.info("Initiating refund for payment: {} of cancelled order: {}", 
                                 payment.getId(), orderEvent.getOrderId());
                        try {
                            paymentService.refundPayment(payment.getId());
                            log.info("Refund processed for cancelled order: {}", orderEvent.getOrderId());
                        } catch (Exception e) {
                            log.error("Failed to process refund for cancelled order: {}", 
                                     orderEvent.getOrderId(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error checking refund needs for cancelled order: {}", 
                     orderEvent.getOrderId(), e);
        }
    }
}