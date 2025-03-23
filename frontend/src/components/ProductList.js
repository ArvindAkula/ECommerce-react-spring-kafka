import React from 'react';
import { Package } from 'lucide-react';

const ProductList = ({ products, addToCart }) => {
  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Products</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {products.map(product => (
          <div key={product.id} className="border rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow">
            <div className="bg-gray-100 h-40 flex items-center justify-center">
              <Package size={64} className="text-gray-400" />
            </div>
            <div className="p-4">
              <h3 className="font-medium">{product.name}</h3>
              <div className="flex justify-between items-center mt-2">
                <p className="font-semibold">${product.price.toFixed(2)}</p>
                <p className="text-sm text-gray-500">{product.stock} in stock</p>
              </div>
              <button
                onClick={() => addToCart(product)}
                className="mt-3 w-full bg-blue-600 hover:bg-blue-700 text-white py-1 px-2 rounded text-sm"
              >
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProductList;