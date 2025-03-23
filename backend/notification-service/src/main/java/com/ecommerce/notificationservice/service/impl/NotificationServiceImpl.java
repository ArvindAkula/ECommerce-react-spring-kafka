package com.ecommerce.notificationservice.service.impl;

import com.ecommerce.notificationservice.model.Notification;
import com.ecommerce.notificationservice.repository.NotificationRepository;
import com.ecommerce.notificationservice.service.EmailService;
import com.ecommerce.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Notification sendNotification(Notification notification) {
        log.info("Sending notification: {}", notification.getType());
        
        // Set generated ID if not provided
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID().toString());
        }
        
        // Set status to PENDING if not set
        if (notification.getStatus() == null) {
            notification.setStatus(Notification.NotificationStatus.PENDING);
        }
        
        // Save notification before sending
        Notification savedNotification = notificationRepository.save(notification);
        
        try {
            // Send the email
            emailService.sendEmail(
                    notification.getRecipientEmail(),
                    notification.getSubject(),
                    notification.getContent()
            );
            
            // Update notification status to SENT
            savedNotification.setStatus(Notification.NotificationStatus.SENT);
            savedNotification.setSentAt(LocalDateTime.now());
            
            log.info("Notification sent successfully: {}", savedNotification.getId());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", savedNotification.getId(), e);
            savedNotification.setStatus(Notification.NotificationStatus.FAILED);
        }
        
        // Save the updated notification
        return notificationRepository.save(savedNotification);
    }

    @Override
    public Notification sendOrderConfirmation(String orderId, String userId, String recipientEmail, String orderDetails) {
        log.info("Creating order confirmation notification for order: {}", orderId);
        
        String subject = "Order Confirmation - Your order has been placed";
        String content = buildOrderConfirmationEmail(orderId, orderDetails);
        
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .type(Notification.NotificationType.ORDER_CONFIRMATION)
                .status(Notification.NotificationStatus.PENDING)
                .orderId(orderId)
                .build();
        
        return sendNotification(notification);
    }

    @Override
    public Notification sendPaymentConfirmation(String orderId, String userId, String recipientEmail, String paymentDetails) {
        log.info("Creating payment confirmation notification for order: {}", orderId);
        
        String subject = "Payment Confirmation - Your payment has been processed";
        String content = buildPaymentConfirmationEmail(orderId, paymentDetails);
        
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .type(Notification.NotificationType.PAYMENT_CONFIRMATION)
                .status(Notification.NotificationStatus.PENDING)
                .orderId(orderId)
                .build();
        
        return sendNotification(notification);
    }

    @Override
    public Notification sendPaymentFailure(String orderId, String userId, String recipientEmail, String failureReason) {
        log.info("Creating payment failure notification for order: {}", orderId);
        
        String subject = "Payment Failed - Action Required";
        String content = buildPaymentFailureEmail(orderId, failureReason);
        
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .type(Notification.NotificationType.PAYMENT_FAILURE)
                .status(Notification.NotificationStatus.PENDING)
                .orderId(orderId)
                .build();
        
        return sendNotification(notification);
    }

    @Override
    public List<Notification> getNotificationsByUserId(String userId) {
        log.info("Getting notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getNotificationsByOrderId(String orderId) {
        log.info("Getting notifications for order: {}", orderId);
        return notificationRepository.findByOrderId(orderId);
    }

    @Override
    public Notification processEventNotification(String eventType, String orderId, String userId, String recipientEmail, String details) {
        log.info("Processing event notification type: {} for order: {}", eventType, orderId);
        
        switch (eventType) {
            case "ORDER_CREATED":
                return sendOrderConfirmation(orderId, userId, recipientEmail, details);
            case "PAYMENT_COMPLETED":
                return sendPaymentConfirmation(orderId, userId, recipientEmail, details);
            case "PAYMENT_FAILED":
                return sendPaymentFailure(orderId, userId, recipientEmail, details);
            default:
                log.warn("Unknown event type: {}", eventType);
                return null;
        }
    }
    
    // Helper methods to build email content
    private String buildOrderConfirmationEmail(String orderId, String orderDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>Order Confirmation</h1>");
        sb.append("<p>Thank you for your order!</p>");
        sb.append("<p>Order ID: ").append(orderId).append("</p>");
        sb.append("<h2>Order Details:</h2>");
        sb.append("<div>").append(orderDetails).append("</div>");
        sb.append("<p>If you have any questions, please contact our customer support.</p>");
        sb.append("<p>Best regards,<br>The ReactShop Team</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
    
    private String buildPaymentConfirmationEmail(String orderId, String paymentDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>Payment Confirmation</h1>");
        sb.append("<p>Your payment for order ").append(orderId).append(" has been successfully processed.</p>");
        sb.append("<h2>Payment Details:</h2>");
        sb.append("<div>").append(paymentDetails).append("</div>");
        sb.append("<p>Thank you for your purchase!</p>");
        sb.append("<p>Best regards,<br>The ReactShop Team</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
    
    private String buildPaymentFailureEmail(String orderId, String failureReason) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>Payment Failed</h1>");
        sb.append("<p>We were unable to process your payment for order ").append(orderId).append(".</p>");
        sb.append("<h2>Reason:</h2>");
        sb.append("<p>").append(failureReason).append("</p>");
        sb.append("<p>Please review your payment information and try again.</p>");
        sb.append("<p>If you continue to experience issues, please contact our customer support.</p>");
        sb.append("<p>Best regards,<br>The ReactShop Team</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}