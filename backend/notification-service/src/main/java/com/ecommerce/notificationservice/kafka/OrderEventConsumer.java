package com.ecommerce.notificationservice.kafka;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.common.model.OrderItem;
import com.ecommerce.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.ORDERS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderEventListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event: {}, status: {}", orderEvent.getOrderId(), orderEvent.getStatus());
        
        switch (orderEvent.getStatus()) {
            case CREATED:
                sendOrderCreatedNotification(orderEvent);
                break;
            case PAYMENT_COMPLETED:
                // This will likely be handled by the PaymentEventConsumer instead
                break;
            case SHIPPED:
                sendOrderShippedNotification(orderEvent);
                break;
            case DELIVERED:
                sendOrderDeliveredNotification(orderEvent);
                break;
            case CANCELLED:
                sendOrderCancelledNotification(orderEvent);
                break;
            default:
                log.info("No notification needed for order status: {}", orderEvent.getStatus());
        }
    }
    
    private void sendOrderCreatedNotification(OrderEvent orderEvent) {
        log.info("Sending order created notification for order: {}", orderEvent.getOrderId());
        
        // For demo purposes, we're using a dummy email
        String recipientEmail = "customer@example.com";
        
        // Create order details string
        String orderDetails = buildOrderDetailsHtml(orderEvent);
        
        notificationService.sendOrderConfirmation(
                orderEvent.getOrderId(),
                orderEvent.getUserId(),
                recipientEmail,
                orderDetails
        );
    }
    
    private void sendOrderShippedNotification(OrderEvent orderEvent) {
        log.info("Sending order shipped notification for order: {}", orderEvent.getOrderId());
        
        // For demo purposes, we're using a dummy email
        String recipientEmail = "customer@example.com";
        
        // Create a notification for order shipped
        notificationService.processEventNotification(
                "ORDER_SHIPPED",
                orderEvent.getOrderId(),
                orderEvent.getUserId(),
                recipientEmail,
                "Your order has been shipped and is on its way!"
        );
    }
    
    private void sendOrderDeliveredNotification(OrderEvent orderEvent) {
        log.info("Sending order delivered notification for order: {}", orderEvent.getOrderId());
        
        // For demo purposes, we're using a dummy email
        String recipientEmail = "customer@example.com";
        
        // Create a notification for order delivered
        notificationService.processEventNotification(
                "ORDER_DELIVERED",
                orderEvent.getOrderId(),
                orderEvent.getUserId(),
                recipientEmail,
                "Your order has been delivered. Thank you for shopping with us!"
        );
    }
    
    private void sendOrderCancelledNotification(OrderEvent orderEvent) {
        log.info("Sending order cancelled notification for order: {}", orderEvent.getOrderId());
        
        // For demo purposes, we're using a dummy email
        String recipientEmail = "customer@example.com";
        
        // Create a notification for order cancelled
        notificationService.processEventNotification(
                "ORDER_CANCELLED",
                orderEvent.getOrderId(),
                orderEvent.getUserId(),
                recipientEmail,
                "Your order has been cancelled."
        );
    }
    
    private String buildOrderDetailsHtml(OrderEvent orderEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Product</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>");
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderItem item : orderEvent.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
            
            sb.append("<tr>");
            sb.append("<td>").append(item.getName()).append("</td>");
            sb.append("<td>").append(item.getQuantity()).append("</td>");
            sb.append("<td>$").append(item.getPrice()).append("</td>");
            sb.append("<td>$").append(subtotal).append("</td>");
            sb.append("</tr>");
        }
        
        sb.append("<tr><td colspan='3' align='right'><strong>Total:</strong></td><td>$")
                .append(total).append("</td></tr>");
        sb.append("</table>");
        
        return sb.toString();
    }
}