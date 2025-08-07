import { Fragment, useState, useEffect } from "react";
import { Dialog, Transition } from "@headlessui/react";

export default function ProductDetailModal({
  isOpen,
  onClose,
  onEdit,
  onDelete,
  product,
  brands,
  refetch
}) {
  const [formData, setFormData] = useState({
    name: "",
    brandId: "",
    quantity: "",
    price: "",
  });

  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (product && brands.length > 0) {
      var brandId = brands.find(b => b.name === product.brandName)?.id || "";
      setFormData({
        name: product.name,
        brandId: brandId,
        quantity: product.quantity.toString(),
        price: product.price.toString(),
      });
      setIsEditing(false);
      setError("");
    }
  }, [product]);

  const handleChange = (e) => {
    if (!isEditing) return;
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCancelEdit = () => {
    if (product) {
      setFormData({
        name: product.name,
        brandId: product.brandId,
        quantity: product.quantity.toString(),
        price: product.price.toString(),
      });
      setIsEditing(false);
      setError("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const { name, brandId, quantity, price } = formData;

    if (!name || !brandId || !quantity || !price) {
      setError("All fields are required.");
      return;
    }

     if (!name && !brandId && !quantity && !price) {
      setError("None of the fields be changed.");
      return;
    }

    if (Number(price) <= 0 || Number(quantity) < 0) {
      setError("Invalid quantity or price.");
      return;
    }

    try {
      await onEdit(product.id, {
        name,
        brandId: Number(brandId),
        quantity: Number(quantity),
        price: Number(price),
      });
      await refetch();
      setIsEditing(false);
      onClose();
    } catch (err) {
      const errorMessage =
        typeof err.response?.data?.message === "object"
          ? Object.values(err.response.data.message)[0]
          : err.response?.data?.message || "Something went wrong.";
      setError(errorMessage);
    }
  };

  const handleDelete = async () => {
    if (confirm("Are you sure you want to delete this product?")) {
      await onDelete(product.id);
      await refetch(); 
      onClose();
    }
  };

  return (
    <Transition show={isOpen} as={Fragment}>
      <Dialog as="div" className="relative z-50" onClose={onClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-200"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-150"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center px-4 py-8">
            <Dialog.Panel className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
              <Dialog.Title className="text-lg font-semibold">
                Product Details
              </Dialog.Title>

              <form onSubmit={handleSubmit} className="mt-4 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Name
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    readOnly={!isEditing}
                    className={`mt-1 w-full rounded border border-gray-300 p-2 ${
                      !isEditing ? "bg-gray-100 cursor-not-allowed" : "bg-white"
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Brand
                  </label>
                  <select
                    name="brandId"
                    value={formData.brandId}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className={`mt-1 w-full rounded border border-gray-300 p-2 appearance-none ${
                      !isEditing ? "bg-gray-100 cursor-not-allowed" : "bg-white"
                    }`}
                  >
                    <option value="" disabled hidden>
                      {product?.brandName || "Select brand"}
                    </option>
                    {brands.map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Quantity
                  </label>
                  <input
                    type="number"
                    name="quantity"
                    min="0"
                    value={formData.quantity}
                    onChange={handleChange}
                    readOnly={!isEditing}
                    className={`mt-1 w-full rounded border border-gray-300 p-2 ${
                      !isEditing ? "bg-gray-100 cursor-not-allowed" : "bg-white"
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Price
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    min="1.00"
                    name="price"
                    value={formData.price}
                    onChange={handleChange}
                    readOnly={!isEditing}
                    className={`mt-1 w-full rounded border border-gray-300 p-2 ${
                      !isEditing ? "bg-gray-100 cursor-not-allowed" : "bg-white"
                    }`}
                  ></input>
                </div>

                {error && <p className="text-sm text-red-600">{error}</p>}

                <div className="flex justify-between pt-4">
                  <button
                    type="button"
                    onClick={onClose}
                    className="rounded bg-gray-200 px-4 py-2 text-sm hover:bg-gray-300"
                  >
                    Close
                  </button>

                  <div className="flex space-x-2">
                    {!isEditing ? (
                      <>
                        <button
                          type="button"
                          onClick={() => setIsEditing(true)}
                          className="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          onClick={handleDelete}
                          className="rounded bg-red-600 px-4 py-2 text-sm text-white hover:bg-red-700"
                        >
                          Delete
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          type="button"
                          onClick={handleCancelEdit}
                          className="rounded bg-yellow-400 px-4 py-2 text-sm text-white hover:bg-yellow-500"
                        >
                          Cancel
                        </button>
                        <button
                          type="submit"
                          className="rounded bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700"
                        >
                          Save
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </form>
            </Dialog.Panel>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
}
