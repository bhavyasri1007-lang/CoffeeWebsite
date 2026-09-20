CREATE DATABASE IF NOT EXISTS coffee_shop;
USE coffee_shop;

CREATE TABLE IF NOT EXISTS menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO menu_items (name, description, price, image)
SELECT * FROM (
    SELECT 'Cappuccino', 'Rich espresso with steamed milk foam', 120.00, 'cappuccino.jpg'
    UNION ALL SELECT 'Latte', 'Smooth espresso with creamy milk', 130.00, 'latte.jpg'
    UNION ALL SELECT 'Espresso', 'Strong and classic espresso shot', 90.00, 'espresso.jpg'
    UNION ALL SELECT 'Mocha', 'Chocolate, espresso and steamed milk', 150.00, 'mocha.jpg'
    UNION ALL SELECT 'Cold Coffee', 'Chilled creamy coffee', 140.00, 'cold-coffee.jpg'
    UNION ALL SELECT 'Americano', 'Espresso with hot water', 100.00, 'americano.jpg'
) AS temp
WHERE NOT EXISTS (SELECT 1 FROM menu_items);
