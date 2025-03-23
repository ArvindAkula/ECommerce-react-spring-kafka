package com.ecommerce.paymentservice.service.impl;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.exception.PaymentProcessingException;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public List<PaymentResponse> getAllPayments() {
        log.info("Getting all payments");
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Processing payment for order: {}", paymentRequest.getOrderId());
        
        // In a real implementation, this would connect to a payment gateway
        // Here we'll simulate payment processing
        try {
            // Validate payment method and information
            validatePaymentInformation(paymentRequest);
            
            // Create payment entity
            String paymentId = UUID.randomUUID().toString();
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .orderId(paymentRequest.getOrderId())
                    .userId(paymentRequest.getUserId())
                    .amount(paymentRequest.getAmount())
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .status(Payment.PaymentStatus.PROCESSING)
                    .build();
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Simulate payment processing (in a real system this would call a payment gateway)
            String transactionId = simulatePaymentGateway(paymentRequest);
            
            // Update payment with transaction ID and status
            savedPayment.setTransactionId(transactionId);
            savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
            savedPayment = paymentRepository.save(savedPayment);
            
            // Publish payment event to Kafka
            publishPaymentEvent(savedPayment);
            
            log.info("Payment processed successfully for order: {}, payment ID: {}", 
                    paymentRequest.getOrderId(), paymentId);
            
            return mapToPaymentResponse(savedPayment);
            
        } catch (Exception e) {
            log.error("Payment processing failed for order: {}", paymentRequest.getOrderId(), e);
            
            // Create a failed payment record if there was an error
            Payment failedPayment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(paymentRequest.getOrderId())
                    .userId(paymentRequest.getUserId())
                    .amount(paymentRequest.getAmount())
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .status(Payment.PaymentStatus.FAILED)
                    .build();
            
            Payment savedFailedPayment = paymentRepository.save(failedPayment);
            
            // Publish failed payment event
            publishPaymentEvent(savedFailedPayment);
            
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResponse getPaymentById(String paymentId) {
        log.info("Getting payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        
        return mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(String orderId) {
        log.info("Getting payments for order: {}", orderId);
        
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByUserId(String userId) {
        log.info("Getting payments for user: {}", userId);
        
        List<Payment> payments = paymentRepository.findByUserId(userId);
        
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(String paymentId) {
        log.info("Refunding payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        
        // Validate that payment can be refunded
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new PaymentProcessingException("Cannot refund payment with status: " + payment.getStatus());
        }
        
        // Simulate refund processing
        boolean refundSuccessful = simulateRefundProcessing(payment);
        
        if (refundSuccessful) {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            Payment refundedPayment = paymentRepository.save(payment);
            
            // Publish refund event
            publishPaymentEvent(refundedPayment);
            
            log.info("Payment refunded successfully: {}", paymentId);
            
            return mapToPaymentResponse(refundedPayment);
        } else {
            throw new PaymentProcessingException("Failed to process refund for payment: " + paymentId);
        }
    }
    
    private void validatePaymentInformation(PaymentRequest paymentRequest) {
        // In a real implementation, this would validate card details, etc.
        if ("Credit Card".equalsIgnoreCase(paymentRequest.getPaymentMethod())) {
            if (paymentRequest.getCardNumber() == null || 
                paymentRequest.getCardExpiryMonth() == null || 
                paymentRequest.getCardExpiryYear() == null || 
                paymentRequest.getCardCvv() == null) {
                throw new PaymentProcessingException("Missing required card details");
            }
            
            // Additional validation logic (e.g., Luhn algorithm for card number)
        } else if ("PayPal".equalsIgnoreCase(paymentRequest.getPaymentMethod())) {
            // PayPal specific validation
        }
    }
    
    private String simulatePaymentGateway(PaymentRequest paymentRequest) {
        // In a real implementation, this would call an actual payment gateway API
        
        // Simulate processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // For demo purposes, fail some payments randomly
        if (Math.random() < 0.05) { // 5% chance of failure
            throw new PaymentProcessingException("Payment gateway error: Transaction declined");
        }
        
        // Generate a mock transaction ID
        return "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
    
    private boolean simulateRefundProcessing(Payment payment) {
        // In a real implementation, this would call a payment gateway API to process the refund
        
        // Simulate processing time
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // For demo purposes, fail some refunds randomly
        return Math.random() >= 0.05; // 95% success rate
    }
    
    private void publishPaymentEvent(Payment payment) {
        Map<String, Object> event = new HashMap<>();
        event.put("paymentId", payment.getId());
        event.put("orderId", payment.getOrderId());
        event.put("status", payment.getStatus());
        event.put("amount", payment.getAmount());
        event.put("timestamp", System.currentTimeMillis());
        
        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS_TOPIC, payment.getOrderId(), event);
        log.info("Published payment event for order: {}, status: {}", payment.getOrderId(), payment.getStatus());
    }
    
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        // Mask card number for security if present
        String maskedCardNumber = null;
        if (payment.getPaymentMethod().equals("Credit Card")) {
            maskedCardNumber = "XXXX-XXXX-XXXX-1234"; // In real app, this would use the actual last 4 digits
        }
        
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .maskedCardNumber(maskedCardNumber)
                .build();
    }
}