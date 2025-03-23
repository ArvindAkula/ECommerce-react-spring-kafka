import React from 'react';

const OrderConfirmation = ({ orderConfirmation, userEmail, resetOrder }) => {
  if (!orderConfirmation) return null;
  
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-6">
      <h2 className="text-xl font-semibold text-green-800 mb-4">Order Confirmation</h2>
      <div className="bg-white p-4 rounded shadow mb-4">
        <p className="font-medium">Order ID: {orderConfirmation.orderId}</p>
        <p>Status: {orderConfirmation.status}</p>
        <p>Estimated Delivery: {orderConfirmation.estimatedDelivery}</p>
      </div>
      <p className="mb-4">Thank you for your purchase! We've sent a confirmation email to {userEmail}.</p>
      <button 
        onClick={resetOrder}
        className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded"
      >
        Continue Shopping
      </button>
    </div>
  );
};

export default OrderConfirmation;