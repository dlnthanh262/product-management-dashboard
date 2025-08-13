import React, { useEffect, useState, useCallback } from "react";
import { getProducts, getBrands } from "../api/productApi";
import ProductTable from "../components/ProductTable";
import FilterBar from "../components/FilterBar";
import ProductCreateModal from "../components/ProductCreateModal";
import { createProduct, updateProduct, deleteProduct } from "../api/productApi";
import ProductDetailModal from "../components/ProductDetailModal";
import ProductStatistics from "../components/ProductStatistics";

const Dashboard = () => {
  const [brands, setBrands] = useState([]);
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  const [filters, setFilters] = useState({
    deleted: false,
    name: "",
    brand: "",
    minPrice: "",
    maxPrice: "",
  });

  useEffect(() => {
    getBrands().then((res) => {
      setBrands(res.content || []);
    });
  }, []);

  const fetchProducts = useCallback(async () => {
    setIsLoading(true);
    try {
      const res = await getProducts({ page, ...filters });
      setProducts(res.content || []);
      setTotalPages(res.totalPages || 0);
    } catch (error) {
      console.error("Failed to fetch products:", error);
    }
    setIsLoading(false);
  }, [page, filters]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  const handleFilterChange = (newFilterValues) => {
    setFilters((prev) => ({
      ...prev,
      ...newFilterValues,
    }));
    setPage(0);
  };

  const handleFilterReset = () => {
    setFilters({
      deleted: false,
      name: "",
      brand: "",
      minPrice: "",
      maxPrice: "",
    });
    setPage(0);
  };

  const handleRowClick = (product) => {
    setSelectedProduct(product);
    setIsDetailModalOpen(true);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className="flex flex-col min-h-screen">
      {/* Header */}
      <header className="bg-blue-600 text-white flex justify-between items-center px-6 py-3">
        <h1 className="text-xl font-bold">Product Dashboard</h1>
        <button
          onClick={handleLogout}
          className="bg-none hover:bg-white hover:text-black px-3 py-1 rounded"
        >
          Logout
        </button>
      </header>

      {/* Main content */}
      <main className="flex flex-1 p-4 gap-6 bg-gray-50">

        {/* Left: Products */}
        <section className="flex-[5] flex flex-col bg-white rounded shadow p-4">
          <div className="flex justify-between items-center mb-4">
            <FilterBar
              brands={brands}
              onFilterChange={handleFilterChange}
              onReset={handleFilterReset}
            />
            <button
              onClick={() => setIsCreateModalOpen(true)}
              className="rounded bg-green-600 px-4 py-2 text-white hover:bg-green-700"
            >
              Create
            </button>
            <ProductCreateModal
              isOpen={isCreateModalOpen}
              onClose={() => setIsCreateModalOpen(false)}
              onCreate={createProduct}
              brandOptions={brands}
            />
            <ProductDetailModal
              isOpen={isDetailModalOpen}
              onClose={() => setIsDetailModalOpen(false)}
              onEdit={updateProduct}
              onDelete={deleteProduct}
              product={selectedProduct}
              brands={brands}
              refetch={fetchProducts}
            />
          </div>

          <ProductTable
            products={products}
            isLoading={isLoading}
            onRowClick={handleRowClick}
          />

          {/* Pagination */}
          <div className="flex justify-center items-center mt-4 gap-2">
            <button
              onClick={() => handlePageChange(page - 1)}
              disabled={page === 0}
              className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
            >
              Prev
            </button>
            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                onClick={() => handlePageChange(i)}
                className={`px-3 py-1 rounded ${
                  page === i ? "bg-blue-500 text-white" : "bg-gray-100"
                }`}
              >
                {i + 1}
              </button>
            ))}
            <button
              onClick={() => handlePageChange(page + 1)}
              disabled={page === totalPages - 1}
              className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </section>

        {/* Right: Statistics */}
        <aside className="flex-[2] bg-white rounded shadow p-4">
          <h2 className="text-xl font-bold mb-4">Product Statistics</h2>
          <ProductStatistics />
        </aside>
      </main>
    </div>
  );
};

export default Dashboard;
