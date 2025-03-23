import axios from 'axios';

// Base URLs for different services
const API_BASE_URL = 'http://localhost:8080/api';
const PAYMENT_API_URL = 'http://localhost:8083/api';

// Create API clients for each service
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const paymentApi = axios.create({
  baseURL: PAYMENT_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Product service APIs
export const fetchProducts = async () => {
  try {
    const response = await api.get('/products');
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error);
    throw error;
  }
};

// Order service APIs
export const placeOrder = async (orderData) => {
  try {
    const response = await api.post('/orders', orderData);
    return response.data;
  } catch (error) {
    console.error('Error placing order:', error);
    throw error;
  }
};

export const getOrderById = async (orderId) => {
  try {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching order ${orderId}:`, error);
    throw error;
  }
};

export const getOrdersByUser = async (userId) => {
  try {
    const response = await api.get(`/orders/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching orders for user ${userId}:`, error);
    throw error;
  }
};

// Payment service APIs
export const processPayment = async (paymentData) => {
  try {
    const response = await paymentApi.post('/payments', paymentData);
    return response.data;
  } catch (error) {
    console.error('Error processing payment:', error);
    throw error;
  }
};

export const getPaymentById = async (paymentId) => {
  try {
    const response = await paymentApi.get(`/payments/${paymentId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching payment ${paymentId}:`, error);
    throw error;
  }
};

export const getPaymentsByOrder = async (orderId) => {
  try {
    const response = await paymentApi.get(`/payments/order/${orderId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching payments for order ${orderId}:`, error);
    throw error;
  }
};

export default api;