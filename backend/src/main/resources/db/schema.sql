CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(8,2)  NOT NULL
    );

-- INSERT INTO products (name, brand, quantity, price)
-- VALUES ('Aruba', 'ABW', 1960, 54922.00);

-- DELETE FROM products WHERE name='Aruba' AND brand='ABW';