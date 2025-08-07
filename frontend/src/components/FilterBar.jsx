import Select from "react-select";
import React, { useEffect, useState, useCallback } from "react";
import { ArrowPathIcon } from "@heroicons/react/24/outline";

const FilterBar = ({ brands, onFilterChange, onReset }) => {
  const [name, setName] = useState("");
  const [brand, setBrand] = useState(null);
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const brandOptions = [
    { value: "", label: "All brands" },
    ...brands.map((b) => ({
      value: b.name,
      label: b.name,
    })),
  ];

  const handleFilter = () => {
    const min = minPrice !== "" ? parseFloat(minPrice) : null;
    const max = maxPrice !== "" ? parseFloat(maxPrice) : null;

    if (min !== null && max !== null && max < min) {
      alert("❌ Max price must be greater than or equal to Min price.");
      handleReset(); // clear input luôn
      return;
    }

    const filters = {
      name: name.trim() || null,
      brand: brand?.value || null,
      minPrice: min,
      maxPrice: max,
    };
    onFilterChange(filters);
  };

  const handleReset = () => {
    setName("");
    setBrand(null);
    setMinPrice("");
    setMaxPrice("");
    onReset();
  };

  return (
    <div className="flex flex-wrap items-center gap-4 mb-4">
      <input
        type="text"
        placeholder="Search by name"
        className="border px-3 py-2 rounded w-48"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <div className="w-36">
        <Select
          options={brandOptions}
          value={
            brandOptions.find((opt) => opt.value === brand?.value) ||
            brandOptions[0]
          }
          onChange={setBrand}
          className=""
          styles={{
            menu: (provided) => ({
              ...provided,
              maxHeight: "300px",
              overflowY: "auto",
            }),
            option: (provided, state) => ({
              ...provided,
              textAlign: "left",
            }),
            singleValue: (provided) => ({
              ...provided,
              textAlign: "left",
              paddingTop: "0.4rem",
              paddingBottom: "0.4rem"
            }),
          }}
        />
      </div>

      <input
        type="number"
        placeholder="Min price"
        className="border px-3 py-2 rounded w-32"
        value={minPrice}
        onChange={(e) => {
          const val = parseFloat(e.target.value);
          setMinPrice(val >= 0 ? e.target.value : "0");
        }}
        min="0"
        step="10"
      />

      <input
        type="number"
        placeholder="Max price"
        className="border px-3 py-2 rounded w-32"
        value={maxPrice}
        onChange={(e) => {
          const val = parseFloat(e.target.value);
          setMaxPrice(val >= 0 ? e.target.value : "0");
        }}
        min="0"
        step="10"
      />

      <button
        onClick={handleFilter}
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Filter
      </button>

      <button
        onClick={handleReset}
        className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
      >
        <ArrowPathIcon className="h-6 w-6" />
      </button>
    </div>
  );
};

export default FilterBar;
