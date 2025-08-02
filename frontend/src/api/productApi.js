// src/api/productApi.js
import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const getProducts = async () => {
  const res = await axios.get(`${BASE_URL}/products`);
  return res.data;
};

export const getBrands = async () => {
  const res = await axios.get(`${BASE_URL}/brands`);
  return res.data;
};

export const createProduct = async (product) => {
  return await axios.post(`${BASE_URL}/products`, product);
};

export const updateProduct = async (id, product) => {
  return await axios.put(`${BASE_URL}/products/${id}`, product);
};

export const deleteProduct = async (id) => {
  return await axios.delete(`${BASE_URL}/products/${id}`);
};