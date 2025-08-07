import { Fragment, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";

export default function ProductCreateModal({ isOpen, onClose, onCreate, brandOptions }) {
  const [formData, setFormData] = useState({
    name: "",
    brandId: "",
    quantity: "",
    price: ""
  });

  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { name, brandId, quantity, price } = formData;

    // Simple validation
    if (!name || !brandId || !quantity || !price) {
      setError("All fields are required.");
      return;
    }

    if (Number(price) <= 0 || Number(quantity) < 0) {
      setError("Invalid quantity or price.");
      return;
    }

    setError("");

    try {
      var res = await onCreate({
        name,
        brandId: Number(brandId), // Gửi đúng tên trường như DTO backend yêu cầu
        quantity: Number(quantity),
        price: Number(price),
      });
      onClose();
      setFormData({ name: "", brandId: "", quantity: "", price: "" });
    } catch (err) {
      const errorMessage = typeof err.response?.data?.message === 'object'
        ? Object.values(err.response.data.message)[0]
        : err.response?.data?.message || 'Something went wrong.';
      setError(errorMessage);
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
              <Dialog.Title className="text-lg font-semibold">Create New Product</Dialog.Title>

              <form onSubmit={handleSubmit} className="mt-4 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Name</label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className="mt-1 w-full rounded border border-gray-300 p-2"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Brand</label>
                  <select
                    name="brandId"
                    value={formData.brandId}
                    onChange={handleChange}
                    className="mt-1 w-full rounded border border-gray-300 p-2 pr-8 appearance-none"
                  >
                    <option value="" disabled hidden>Select brand</option>
                    {brandOptions.map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Quantity</label>
                  <input
                    type="number"
                    name="quantity"
                    value={formData.quantity}
                    min="1"
                    onChange={handleChange}
                    className="mt-1 w-full rounded border border-gray-300 p-2"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Price</label>
                  <input
                    type="number"
                    step="0.01"
                    min="1.00"
                    name="price"
                    value={formData.price}
                    onChange={handleChange}
                    className="mt-1 w-full rounded border border-gray-300 p-2"
                  />
                </div>

                {error && <p className="text-sm text-red-600">{error}</p>}

                <div className="flex justify-end space-x-2 pt-4">
                  <button
                    type="button"
                    onClick={onClose}
                    className="rounded bg-gray-200 px-4 py-2 text-sm hover:bg-gray-300"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="rounded bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700"
                  >
                    Create
                  </button>
                </div>
              </form>
            </Dialog.Panel>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
}