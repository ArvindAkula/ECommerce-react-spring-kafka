package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    
    /**
     * Process a payment for an order
     * 
     * @param paymentRequest the payment details
     * @return the processed payment information
     */
    PaymentResponse processPayment(PaymentRequest paymentRequest);
    
    /**
     * Get payment by ID
     * 
     * @param paymentId the payment ID
     * @return the payment information if found
     */
    PaymentResponse getPaymentById(String paymentId);
    
    /**
     * Get payments for an order
     * 
     * @param orderId the order ID
     * @return list of payments for the order
     */
    List<PaymentResponse> getPaymentsByOrderId(String orderId);
    
    /**
     * Get payments for a user
     * 
     * @param userId the user ID
     * @return list of payments made by the user
     */
    List<PaymentResponse> getPaymentsByUserId(String userId);
    
    /**
     * Refund a payment
     * 
     * @param paymentId the payment ID to refund
     * @return the updated payment information
     */
    PaymentResponse refundPayment(String paymentId);
}