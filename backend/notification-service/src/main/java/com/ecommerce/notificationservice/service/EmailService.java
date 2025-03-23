package com.ecommerce.notificationservice.service;

public interface EmailService {
    
    /**
     * Send an email
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param content the email content (HTML)
     */
    void sendEmail(String to, String subject, String content);
}