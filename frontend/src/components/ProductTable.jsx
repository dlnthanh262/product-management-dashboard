import React from "react";

const ProductTable = ({ products, isLoading, onRowClick }) => {
  const rowsPerPage = 10;
  const rowHeightClass = "h-12";
  const emptyRows = rowsPerPage - products.length;

  return (
    <div className=" mt-4">
      {isLoading && (
        <div className="absolute inset-0 bg-white bg-opacity-60 flex items-center justify-center z-10">
          <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-blue-500" />
        </div>
      )}

      <table className="w-full border-collapse table-fixed">
        <thead className="bg-gray-100 text-sm">
          <tr className={rowHeightClass}>
            <th className="border">Name</th>
            <th className="border">Brand</th>
            <th className="border">Price</th>
            <th className="border">Quantity</th>
          </tr>
        </thead>
        <tbody className="text-sm relative">
          {products.length === 0 && isLoading === false ? (
            <tr className={rowHeightClass}>
              <td colSpan={4} className="text-center text-gray-500">
                No data available
              </td>
            </tr>
          ) : (
            products.map((p) => (
              <tr
                key={p.id}
                onClick={() => onRowClick(p)}
                tabIndex={0}
                className={`${rowHeightClass} cursor-pointer hover:bg-blue-50 active:bg-blue-200 transition-colors`}
              >
                <td className="border text-center align-middle">{p.name}</td>
                <td className="border text-center align-middle">
                  {p.brandName}
                </td>
                <td className="border text-center align-middle">${p.price}</td>
                <td className="border text-center align-middle">
                  {p.quantity}
                </td>
              </tr>
            ))
          )}

          {Array.from({ length: emptyRows > 0 ? emptyRows : 0 }).map((_, i) => (
            <tr key={`empty-${i}`} className={rowHeightClass}>
              <td colSpan={4}>&nbsp;</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTable;
