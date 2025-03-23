package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    /**
     * Creates a new order based on the request
     *
     * @param orderRequest the order details
     * @return the created order
     */
    OrderResponse createOrder(OrderRequest orderRequest);
    
    /**
     * Retrieves an order by its ID
     *
     * @param orderId the order ID
     * @return the order if found
     */
    OrderResponse getOrderById(String orderId);
    
    /**
     * Retrieves all orders for a user
     *
     * @param userId the user ID
     * @return list of orders
     */
    List<OrderResponse> getOrdersByUserId(String userId);
    
    /**
     * Retrieves all orders in the system
     *
     * @return list of orders
     */
    List<OrderResponse> getAllOrders();
}