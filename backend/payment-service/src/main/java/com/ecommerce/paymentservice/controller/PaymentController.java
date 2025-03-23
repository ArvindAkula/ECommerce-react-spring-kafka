package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("REST request to process payment for order: {}", paymentRequest.getOrderId());
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String paymentId) {
        log.info("REST request to get payment by ID: {}", paymentId);
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable String orderId) {
        log.info("REST request to get payments by order ID: {}", orderId);
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable String userId) {
        log.info("REST request to get payments by user ID: {}", userId);
        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable String paymentId) {
        log.info("REST request to refund payment: {}", paymentId);
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}