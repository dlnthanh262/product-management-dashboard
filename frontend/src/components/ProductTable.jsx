const products = [
  { id: 1, name: "Laptop", quantity: 5 },
  { id: 2, name: "Mouse", quantity: 12 },
];

export default function ProductTable() {
  return (
    <table>
      <thead><tr><th>ID</th><th>Name</th><th>Qty</th></tr></thead>
      <tbody>
        {products.map(p => (
          <tr key={p.id}>
            <td>{p.id}</td><td>{p.name}</td><td>{p.quantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}