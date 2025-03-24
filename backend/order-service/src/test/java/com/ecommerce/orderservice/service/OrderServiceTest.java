package com.ecommerce.orderservice.service;

import com.ecommerce.common.event.OrderEvent;
import com.ecommerce.orderservice.dto.OrderItemRequest;
import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderLineItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, kafkaTemplate);
    }



    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        String orderId = UUID.randomUUID().toString();
        String userId = "test-user";
        Order order = createOrder(userId);
        order.setId(orderId);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // Act
        OrderResponse response = orderService.getOrderById(orderId);
        
        // Assert
        assertNotNull(response);
        assertEquals(orderId, response.getOrderId());
        assertEquals(userId, response.getUserId());
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        // Arrange
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Arrange
        String userId = "test-user";
        List<Order> userOrders = new ArrayList<>();
        userOrders.add(createOrder(userId));
        userOrders.add(createOrder(userId));
        
        when(orderRepository.findByUserId(userId)).thenReturn(userOrders);
        
        // Act
        List<OrderResponse> responses = orderService.getOrdersByUserId(userId);
        
        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(userId, responses.get(0).getUserId());
        assertEquals(userId, responses.get(1).getUserId());
    }

    private OrderRequest createOrderRequest(String userId) {
        OrderItemRequest item1 = OrderItemRequest.builder()
                .productId("p1")
                .name("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();
        
        OrderItemRequest item2 = OrderItemRequest.builder()
                .productId("p2")
                .name("Product 2")
                .price(new BigDecimal("20.49"))
                .quantity(1)
                .build();
        
        return OrderRequest.builder()
                .userId(userId)
                .items(List.of(item1, item2))
                .totalAmount(new BigDecimal("42.47"))
                .paymentMethod("Credit Card")
                .build();
    }

    private Order createOrder(String userId) {
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .totalAmount(new BigDecimal("42.47"))
                .paymentMethod("Credit Card")
                .status(OrderEvent.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        OrderLineItem item1 = OrderLineItem.builder()
                .productId("p1")
                .name("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .order(order)
                .build();
        
        OrderLineItem item2 = OrderLineItem.builder()
                .productId("p2")
                .name("Product 2")
                .price(new BigDecimal("20.49"))
                .quantity(1)
                .order(order)
                .build();
        
        order.setItems(List.of(item1, item2));
        
        return order;
    }
}