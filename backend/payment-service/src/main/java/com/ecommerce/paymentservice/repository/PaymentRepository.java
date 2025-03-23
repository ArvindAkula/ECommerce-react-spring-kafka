package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    List<Payment> findByOrderId(String orderId);
    
    List<Payment> findByUserId(String userId);
    
    Optional<Payment> findByOrderIdAndStatus(String orderId, Payment.PaymentStatus status);
}