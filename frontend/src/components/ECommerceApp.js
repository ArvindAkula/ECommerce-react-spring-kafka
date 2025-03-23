import React, { useState, useEffect } from 'react';
import { ShoppingCart, User } from 'lucide-react';
import ProductList from './ProductList';
import Cart from './Cart';
import OrderConfirmation from './OrderConfirmation';
import { fetchProducts, placeOrder } from '../services/api';

const ECommerceApp = () => {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [orderPlaced, setOrderPlaced] = useState(false);
  const [orderConfirmation, setOrderConfirmation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [user] = useState({ id: 'usr123', name: 'John Doe', email: 'john@example.com' });

  useEffect(() => {
    const getProducts = async () => {
      try {
        // In a real app, this would fetch from the backend
        // const data = await fetchProducts();
        // setProducts(data);
        
        // For demo purposes, we'll use mock data
        const mockProducts = [
          { id: 'p1', name: 'Wireless Headphones', price: 89.99, stock: 12 },
          { id: 'p2', name: 'Smartphone', price: 699.99, stock: 5 },
          { id: 'p3', name: 'Laptop', price: 1299.99, stock: 3 },
          { id: 'p4', name: 'Smart Watch', price: 249.99, stock: 8 },
          { id: 'p5', name: 'Bluetooth Speaker', price: 59.99, stock: 15 },
        ];
        setProducts(mockProducts);
      } catch (err) {
        setError('Failed to load products. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    getProducts();
  }, []);

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.productId === product.id);
    
    if (existingItem) {
      setCart(cart.map(item => 
        item.productId === product.id 
          ? { ...item, quantity: item.quantity + 1 } 
          : item
      ));
    } else {
      setCart([...cart, { 
        productId: product.id, 
        name: product.name, 
        price: product.price, 
        quantity: 1 
      }]);
    }
  };

  const removeFromCart = (productId) => {
    const existingItem = cart.find(item => item.productId === productId);
    
    if (existingItem.quantity === 1) {
      setCart(cart.filter(item => item.productId !== productId));
    } else {
      setCart(cart.map(item => 
        item.productId === productId 
          ? { ...item, quantity: item.quantity - 1 } 
          : item
      ));
    }
  };

  const calculateTotal = () => {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  };

  const handlePlaceOrder = async () => {
    if (cart.length === 0) return;
    
    const orderDetails = {
      userId: user.id,
      items: cart,
      totalAmount: calculateTotal(),
      paymentMethod: 'Credit Card',
      timestamp: new Date().toISOString()
    };
    
    try {
      setLoading(true);
      // In a real app, this would call the backend API
      // const response = await placeOrder(orderDetails);
      // setOrderConfirmation(response);
      
      // For demo purposes, we'll simulate an API response
      setTimeout(() => {
        const confirmation = {
          orderId: `ORD-${Math.floor(Math.random() * 1000000)}`,
          status: 'PROCESSING',
          estimatedDelivery: '3-5 business days'
        };
        setOrderConfirmation(confirmation);
        setOrderPlaced(true);
        setCart([]);
        setLoading(false);
      }, 1500);
    } catch (err) {
      setError('Failed to place order. Please try again.');
      setLoading(false);
    }
  };

  const resetOrder = () => {
    setOrderPlaced(false);
    setOrderConfirmation(null);
  };

  if (loading && !products.length) {
    return <div className="max-w-6xl mx-auto p-4 text-center">Loading...</div>;
  }

  if (error && !products.length) {
    return <div className="max-w-6xl mx-auto p-4 text-center text-red-600">{error}</div>;
  }

  return (
    <div className="max-w-6xl mx-auto p-4">
      <header className="bg-blue-600 text-white p-4 mb-6 rounded-lg shadow-md">
        <div className="flex justify-between items-center">
          <h1 className="text-2xl font-bold">ReactShop</h1>
          <div className="flex items-center space-x-4">
            <div className="relative">
              <ShoppingCart size={24} />
              {cart.length > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                  {cart.reduce((sum, item) => sum + item.quantity, 0)}
                </span>
              )}
            </div>
            <User size={24} />
          </div>
        </div>
      </header>

      {orderPlaced ? (
        <OrderConfirmation 
          orderConfirmation={orderConfirmation} 
          userEmail={user.email} 
          resetOrder={resetOrder} 
        />
      ) : (
        <div className="flex flex-col md:flex-row gap-6">
          <div className="md:w-2/3">
            <ProductList products={products} addToCart={addToCart} />
          </div>
          
          <div className="md:w-1/3">
            <Cart 
              cart={cart} 
              addToCart={addToCart} 
              removeFromCart={removeFromCart} 
              calculateTotal={calculateTotal} 
              placeOrder={handlePlaceOrder} 
              products={products}
              isLoading={loading}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default ECommerceApp;