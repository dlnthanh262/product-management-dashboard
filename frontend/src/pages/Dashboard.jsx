// src/pages/Dashboard.jsx
import React, { useEffect, useState } from 'react';
import { getProducts, getBrands, deleteProduct } from '../api/productApi';
import ProductTable from '../components/ProductTable';
// import FilterBar from '../components/FilterBar';

const Dashboard = () => {
  const [products, setProducts] = useState([]);
  const [brands, setBrands] = useState([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [productList, brandList] = await Promise.all([
      getProducts(),
      getBrands()
    ]);
    setProducts(productList);
    setBrands(brandList);
  };

  const handleDelete = async (id) => {
    await deleteProduct(id);
    loadData();
  };

  return (
    <div className="p-4 max-w-screen-xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Product Dashboard</h1>
      {/* <FilterBar brands={brands} setProducts={setProducts} /> */}
      <ProductTable products={products} onDelete={handleDelete} />
    </div>
  );
};

export default Dashboard;