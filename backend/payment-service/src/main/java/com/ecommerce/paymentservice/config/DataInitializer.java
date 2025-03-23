package com.ecommerce.paymentservice.config;

import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PaymentRepository paymentRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting payment database initialization...");
            
            if (paymentRepository.count() == 0) {
                log.info("No payments found. Adding sample payments.");
                
                // Create a successful payment
                Payment successfulPayment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(UUID.randomUUID().toString()) // This would need to match a real order ID in a real app
                    .amount(new BigDecimal("999.98"))
                    .status(PaymentStatus.SUCCESSFUL)
                    .paymentMethod("CREDIT_CARD")
                    .transactionId(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now().minusDays(4))
                    .updatedAt(LocalDateTime.now().minusDays(4))
                    .build();
                
                // Create a pending payment
                Payment pendingPayment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(UUID.randomUUID().toString()) // This would need to match a real order ID in a real app
                    .amount(new BigDecimal("1299.99"))
                    .status(PaymentStatus.PENDING)
                    .paymentMethod("PAYPAL")
                    .transactionId(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now().minusDays(1))
                    .build();
                
                // Create a failed payment
                Payment failedPayment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(UUID.randomUUID().toString()) // This would need to match a real order ID in a real app
                    .amount(new BigDecimal("2499.99"))
                    .status(PaymentStatus.FAILED)
                    .paymentMethod("CREDIT_CARD")
                    .transactionId(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .updatedAt(LocalDateTime.now().minusDays(3))
                    .failureReason("Insufficient funds")
                    .build();
                
                paymentRepository.saveAll(List.of(successfulPayment, pendingPayment, failedPayment));
                log.info("Added sample payments");
            } else {
                log.info("Payments already exist. Skipping initialization.");
            }
        };
    }
}
