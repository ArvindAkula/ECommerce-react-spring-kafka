package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.model.Notification;

import java.util.List;

public interface NotificationService {
    
    /**
     * Send a notification
     * 
     * @param notification the notification to send
     * @return the sent notification
     */
    Notification sendNotification(Notification notification);
    
    /**
     * Create and send an order confirmation notification
     * 
     * @param orderId the order ID
     * @param userId the user ID
     * @param recipientEmail the recipient email
     * @param orderDetails the order details for email content
     * @return the sent notification
     */
    Notification sendOrderConfirmation(String orderId, String userId, String recipientEmail, String orderDetails);
    
    /**
     * Create and send a payment confirmation notification
     * 
     * @param orderId the order ID
     * @param userId the user ID
     * @param recipientEmail the recipient email
     * @param paymentDetails the payment details for email content
     * @return the sent notification
     */
    Notification sendPaymentConfirmation(String orderId, String userId, String recipientEmail, String paymentDetails);
    
    /**
     * Create and send a payment failure notification
     * 
     * @param orderId the order ID
     * @param userId the user ID
     * @param recipientEmail the recipient email
     * @param failureReason the reason for payment failure
     * @return the sent notification
     */
    Notification sendPaymentFailure(String orderId, String userId, String recipientEmail, String failureReason);
    
    /**
     * Get all notifications for a user
     * 
     * @param userId the user ID
     * @return list of notifications for the user
     */
    List<Notification> getNotificationsByUserId(String userId);
    
    /**
     * Get all notifications for an order
     * 
     * @param orderId the order ID
     * @return list of notifications for the order
     */
    List<Notification> getNotificationsByOrderId(String orderId);
    
    /**
     * Process notifications from events (order events, payment events, etc.)
     * 
     * @param eventType the type of event
     * @param orderId the order ID
     * @param userId the user ID
     * @param recipientEmail the recipient email
     * @param details the details for email content
     * @return the processed notification
     */
    Notification processEventNotification(String eventType, String orderId, String userId, String recipientEmail, String details);
}