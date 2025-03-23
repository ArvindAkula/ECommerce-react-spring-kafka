package com.ecommerce.notificationservice.kafka;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "paymentEventListenerContainerFactory"
    )
    public void consumePaymentEvent(Map<String, Object> paymentEvent) {
        log.info("Received payment event for order: {}, status: {}", 
                paymentEvent.get("orderId"), paymentEvent.get("status"));
        
        String orderId = (String) paymentEvent.get("orderId");
        String status = paymentEvent.get("status").toString();
        
        switch (status) {
            case "COMPLETED":
                sendPaymentCompletedNotification(paymentEvent);
                break;
            case "FAILED":
                sendPaymentFailedNotification(paymentEvent);
                break;
            case "REFUNDED":
                sendRefundIssuedNotification(paymentEvent);
                break;
            default:
                log.info("No notification needed for payment status: {}", status);
        }
    }
    
    private void sendPaymentCompletedNotification(Map<String, Object> paymentEvent) {
        log.info("Sending payment completed notification for order: {}", paymentEvent.get("orderId"));
        
        String orderId = (String) paymentEvent.get("orderId");
        String userId = "user123"; // This would typically come from the event or be looked up
        String recipientEmail = "customer@example.com"; // This would typically come from user info
        
        // Format payment details
        BigDecimal amount = new BigDecimal(paymentEvent.get("amount").toString());
        String paymentDetails = buildPaymentDetailsHtml(paymentEvent, amount);
        
        notificationService.sendPaymentConfirmation(
                orderId,
                userId,
                recipientEmail,
                paymentDetails
        );
    }
    
    private void sendPaymentFailedNotification(Map<String, Object> paymentEvent) {
        log.info("Sending payment failed notification for order: {}", paymentEvent.get("orderId"));
        
        String orderId = (String) paymentEvent.get("orderId");
        String userId = "user123"; // This would typically come from the event or be looked up
        String recipientEmail = "customer@example.com"; // This would typically come from user info
        
        // Provide failure reason
        String failureReason = "There was an issue processing your payment. Please update your payment information and try again.";
        
        notificationService.sendPaymentFailure(
                orderId,
                userId,
                recipientEmail,
                failureReason
        );
    }
    
    private void sendRefundIssuedNotification(Map<String, Object> paymentEvent) {
        log.info("Sending refund notification for order: {}", paymentEvent.get("orderId"));
        
        String orderId = (String) paymentEvent.get("orderId");
        String userId = "user123"; // This would typically come from the event or be looked up
        String recipientEmail = "customer@example.com"; // This would typically come from user info
        
        BigDecimal amount = new BigDecimal(paymentEvent.get("amount").toString());
        String refundDetails = "A refund of $" + amount + " has been issued to your original payment method.";
        
        notificationService.processEventNotification(
                "REFUND_ISSUED",
                orderId,
                userId,
                recipientEmail,
                refundDetails
        );
    }
    
    private String buildPaymentDetailsHtml(Map<String, Object> paymentEvent, BigDecimal amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Payment Information</th><th>Details</th></tr>");
        
        sb.append("<tr><td>Order ID</td><td>").append(paymentEvent.get("orderId")).append("</td></tr>");
        sb.append("<tr><td>Transaction ID</td><td>").append(paymentEvent.get("paymentId")).append("</td></tr>");
        sb.append("<tr><td>Amount</td><td>$").append(amount).append("</td></tr>");
        sb.append("<tr><td>Payment Method</td><td>Credit Card (ending in XXXX)</td></tr>");
        sb.append("<tr><td>Status</td><td>Completed</td></tr>");
        sb.append("<tr><td>Date</td><td>").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</td></tr>");
        
        sb.append("</table>");
        return sb.toString();
    }
}