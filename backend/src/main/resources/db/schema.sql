CREATE TABLE IF NOT EXISTS brand (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(255),
    founded_year INT,
    website VARCHAR(255),
    description TEXT
    );

CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand_id INT NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(8,2)  NOT NULL,
    CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brand(id)
    );

-- INSERT INTO product (name, brand, quantity, price)
-- VALUES ('Aruba', 'ABW', 1960, 54922.00);

-- DELETE FROM product WHERE name='Aruba' AND brand='ABW';