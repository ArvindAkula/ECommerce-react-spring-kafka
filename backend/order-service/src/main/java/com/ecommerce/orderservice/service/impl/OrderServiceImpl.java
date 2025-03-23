package com.ecommerce.orderservice.service.impl;

import com.ecommerce.common.config.KafkaTopics;
import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.common.model.OrderItem;
import com.ecommerce.orderservice.dto.OrderItemRequest;
import com.ecommerce.orderservice.dto.OrderItemResponse;
import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderLineItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating order for user: {}", orderRequest.getUserId());
        
        // Create order entity
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(orderRequest.getUserId())
                .totalAmount(orderRequest.getTotalAmount())
                .paymentMethod(orderRequest.getPaymentMethod())
                .status(OrderEvent.OrderStatus.CREATED)
                .build();
        
        // Add order items
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            OrderLineItem lineItem = OrderLineItem.builder()
                    .productId(itemRequest.getProductId())
                    .name(itemRequest.getName())
                    .price(itemRequest.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .build();
            order.addItem(lineItem);
        }
        
        // Save order to database
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with ID: {}", savedOrder.getId());
        
        // Publish order created event to Kafka
        publishOrderEvent(savedOrder);
        
        // Return order response
        return mapToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        log.info("Getting order with ID: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(String userId) {
        log.info("Getting orders for user: {}", userId);
        
        List<Order> orders = orderRepository.findByUserId(userId);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Getting all orders");
        
        List<Order> orders = orderRepository.findAll();
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    private void publishOrderEvent(Order order) {
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .timestamp(LocalDateTime.now())
                .items(mapToOrderItems(order.getItems()))
                .build();
        
        log.info("Publishing order event to Kafka: {}", orderEvent);
        kafkaTemplate.send(KafkaTopics.ORDERS_TOPIC, order.getId(), orderEvent);
    }
    
    private List<OrderItem> mapToOrderItems(List<OrderLineItem> lineItems) {
        return lineItems.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .status(order.getStatus())
                .estimatedDelivery("3-5 business days") // This could be calculated based on shipping method
                .build();
    }
}