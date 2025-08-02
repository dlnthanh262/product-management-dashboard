// src/components/ProductTable.jsx
import React from 'react';

const ProductTable = ({ products, onDelete }) => {
  return (
    <table className="w-full border-collapse border border-gray-300">
      <thead>
        <tr className="bg-gray-100">
          <th className="p-2 border">Name</th>
          <th className="p-2 border">Brand</th>
          <th className="p-2 border">Price</th>
          <th className="p-2 border">Quantity</th>
          <th className="p-2 border">Actions</th>
        </tr>
      </thead>
      <tbody>
        {products.map((p) => (
          <tr key={p.id}>
            <td className="p-2 border">{p.name}</td>
            <td className="p-2 border">{p.brand.name}</td>
            <td className="p-2 border">${p.price}</td>
            <td className="p-2 border">{p.quantity}</td>
            <td className="p-2 border">
              <button onClick={() => onDelete(p.id)} className="text-red-600">Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default ProductTable;