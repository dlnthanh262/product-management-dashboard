// src/api/productApi.js
import axios from 'axios';

const BASE_URL = import.meta.env.API_BASE_URL || 'http://localhost:8080/api';

export const getProducts = async ({
  page = 0,
  deleted = false,
  name = '',
  brand = '',
  minPrice,
  maxPrice,
}) => {
  const params = new URLSearchParams({
    page,
    size: 10,
    deleted,
  });

  if (name) params.append('name', name);
  if (brand) params.append('brand', brand);
  if (minPrice !== null && minPrice !== undefined) params.append('minPrice', minPrice);
  if (maxPrice !== null && maxPrice !== undefined) params.append('maxPrice', maxPrice);

  const res = await axios.get(`${BASE_URL}/products?${params.toString()}`);
  return res.data;
};

export const getBrands = async () => {
  const res = await axios.get(`${BASE_URL}/brands?page=0&size=100`);
  return res.data;
};

export const createProduct = async (product) => {
  const res = await axios.post(`${BASE_URL}/products`, product);
  return res.data;
};

export const updateProduct = async (id, product) => {
  return await axios.put(`${BASE_URL}/products/${id}`, product);
};

export const deleteProduct = async (id) => {
  return await axios.delete(`${BASE_URL}/products/${id}`);
};