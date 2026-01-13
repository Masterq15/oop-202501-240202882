-- ===============================================
-- SQL Script untuk Week 11 - Agri-POS Database
-- ===============================================

-- Drop table jika sudah ada (untuk clean setup)
DROP TABLE IF EXISTS products;

-- Buat tabel products
CREATE TABLE products (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

-- Insert sample data (optional untuk testing manual)
INSERT INTO products (code, name, price, stock) VALUES
('P01', 'Pupuk Organik', 25000, 10),
('P02', 'Benih Padi', 15000, 50),
('P03', 'Pestisida Alami', 45000, 20),
('P04', 'Pupuk Kimia', 35000, 30),
('P05', 'Benih Jagung', 18000, 40);

-- Verify data
SELECT * FROM products ORDER BY code;

-- Query untuk melihat struktur tabel
SELECT 
    column_name, 
    data_type, 
    character_maximum_length, 
    is_nullable
FROM 
    information_schema.columns
WHERE 
    table_name = 'products'
ORDER BY 
    ordinal_position;