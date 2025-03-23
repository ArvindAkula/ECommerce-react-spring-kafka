import React from 'react';
import { DollarSign } from 'lucide-react';

const Cart = ({ cart, addToCart, removeFromCart, calculateTotal, placeOrder, products, isLoading }) => {
  const findProduct = (productId) => products.find(p => p.id === productId);
  
  return (
    <div className="border rounded-lg shadow-sm p-4 sticky top-4">
      <h2 className="text-xl font-semibold mb-4">Your Cart</h2>
      
      {cart.length === 0 ? (
        <p className="text-gray-500">Your cart is empty</p>
      ) : (
        <>
          <div className="space-y-3 mb-4">
            {cart.map(item => (
              <div key={item.productId} className="flex justify-between items-center border-b pb-2">
                <div>
                  <p className="font-medium">{item.name}</p>
                  <p className="text-sm text-gray-600">${item.price.toFixed(2)} × {item.quantity}</p>
                </div>
                <div className="flex items-center space-x-2">
                  <button 
                    onClick={() => removeFromCart(item.productId)}
                    className="text-gray-500 hover:text-red-500"
                  >
                    -
                  </button>
                  <span>{item.quantity}</span>
                  <button 
                    onClick={() => addToCart(findProduct(item.productId))}
                    className="text-gray-500 hover:text-green-500"
                  >
                    +
                  </button>
                </div>
              </div>
            ))}
          </div>
          
          <div className="border-t pt-3 mb-4">
            <div className="flex justify-between font-semibold">
              <span>Total:</span>
              <span>${calculateTotal().toFixed(2)}</span>
            </div>
          </div>
          
          <button
            onClick={placeOrder}
            disabled={isLoading}
            className={`w-full ${isLoading ? 'bg-gray-400' : 'bg-green-600 hover:bg-green-700'} text-white py-2 px-4 rounded flex items-center justify-center gap-2`}
          >
            {isLoading ? 'Processing...' : (
              <>
                <DollarSign size={18} />
                Place Order
              </>
            )}
          </button>
        </>
      )}
    </div>
  );
};

export default Cart;