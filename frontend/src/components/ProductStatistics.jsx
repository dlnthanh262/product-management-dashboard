import React, { useEffect, useState } from "react";
import { getProductStatisticsByBrand } from "../api/productApi";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

export default function ProductStatistics() {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterOption, setFilterOption] = useState("all"); // all, top5-high, top5-low

  useEffect(() => {
    getProductStatisticsByBrand()
      .then((res) => {
        setData(res);
        setFilteredData(res); // mặc định hiển thị all
      })
      .catch((e) => console.error(e))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (filterOption === "all") {
      setFilteredData(data);
    } else if (filterOption === "top5-high") {
      const sorted = [...data]
        .sort((a, b) => b.productCount - a.productCount)
        .slice(0, 5);
      setFilteredData(sorted);
    } else if (filterOption === "top5-low") {
      const sorted = [...data]
        .sort((a, b) => a.productCount - b.productCount)
        .slice(0, 5);
      setFilteredData(sorted);
    }
  }, [filterOption, data]);

  if (loading) return <div>Loading statistics...</div>;

  const totalBrands = data.length;
  const totalProducts = data.reduce((sum, item) => sum + item.productCount, 0);
  const maxBrand = filteredData.reduce(
    (max, item) => (item.productCount > (max?.productCount ?? 0) ? item : max),
    null
  );

  const minBrand = filteredData.reduce((min, item) => {
    if (min === null) return item;
    if (item.productCount < min.productCount) return item;
    return min;
  }, null);

  return (
    <div>
      <div className="flex gap-6 mb-6">
        <div className="p-4 bg-white rounded shadow text-center flex-1">
          <div className="text-3xl font-semibold">{totalProducts}</div>
          <div className="text-gray-600">Total Products</div>
        </div>
        <div className="p-4 bg-white rounded shadow text-center flex-1">
          <div className="text-3xl font-semibold">{totalBrands}</div>
          <div className="text-gray-600">Total Brands</div>
        </div>
      </div>

      <div className="mb-4">
        <select
          className="border p-2 rounded"
          value={filterOption}
          onChange={(e) => setFilterOption(e.target.value)}
        >
          <option value="all">All Brands</option>
          <option value="top5-high">
            Top 5 Brands (Highest Product Count)
          </option>
          <option value="top5-low">Top 5 Brands (Lowest Product Count)</option>
        </select>
      </div>

      <ResponsiveContainer width="100%" height={300}>
        <BarChart
          data={filteredData}
          margin={{ top: 20, right: 10, left: -35, bottom: 5 }}
          title="Count Product By Brand"
        >
          <XAxis dataKey="brandName" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="productCount" fill="#8884d8" />
        </BarChart>
      </ResponsiveContainer>

      <div className="p-4 space-y-2">
        <div className="">
          Highest Product Count:{" "}
          <span className="text-green-700">
            {maxBrand
              ? maxBrand.productCount + " - " + maxBrand.brandName
              : "-"}
          </span>
        </div>
        <div className="">
          Lowest Product Count:{" "}
          <span className="text-orange-700">
            {minBrand
              ? minBrand.productCount + " - " + minBrand.brandName
              : "-"}
          </span>
        </div>
      </div>
    </div>
  );
}
