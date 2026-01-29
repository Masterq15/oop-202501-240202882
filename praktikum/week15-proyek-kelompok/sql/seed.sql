-- ========================================
-- AGRI-POS WEEK 15 - Seed Data
-- Database: agripos_database
-- ========================================

-- ========================================
-- SEED DATA: products
-- ========================================
INSERT INTO products (code, name, category, price, stock) VALUES
('P001', 'Pupuk Organik Premium', 'Pupuk', 30000, 50),
('P002', 'Benih Padi Berkualitas', 'Benih', 15000, 100),
('P003', 'Pestisida Organik 500ml', 'Pestisida', 45000, 30),
('P004', 'Pupuk NPK 25kg', 'Pupuk', 125000, 20),
('P005', 'Bibit Cabai Hibrida', 'Benih', 25000, 80),
('P006', 'Pupuk Kompos 10kg', 'Pupuk', 50000, 40),
('P007', 'Insektisida Nabati 250ml', 'Pestisida', 35000, 25),
('P008', 'Benih Jagung Hibrida', 'Benih', 20000, 60),
('P009', 'Fungisida Organik', 'Pestisida', 55000, 15),
('P010', 'Pupuk Urea 50kg', 'Pupuk', 350000, 10);

-- ========================================
-- SEED DATA: users
-- ========================================
INSERT INTO users (username, password, full_name, role, active) VALUES
('admin01', 'admin123', 'Admin System', 'ADMIN', true),
('kasir01', 'kasir123', 'Kasir 1 - Budi Santoso', 'KASIR', true),
('kasir02', 'kasir456', 'Kasir 2 - Rina Wijaya', 'KASIR', true),
('kasir03', 'kasir789', 'Kasir 3 - Ahmad Gunawan', 'KASIR', true),
('admin02', 'admin456', 'Admin Backup', 'ADMIN', true);

-- ========================================
-- SEED DATA: transactions (Optional)
-- ========================================
-- Contoh data transaksi untuk testing laporan
-- Uncomment jika ingin menggunakan

-- INSERT INTO transactions (code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name) VALUES
-- ('TRX001', NOW() - INTERVAL '5 days', 100000, 10000, 90000, 'TUNAI', 'COMPLETED', 'Kasir 1 - Budi Santoso'),
-- ('TRX002', NOW() - INTERVAL '3 days', 250000, 0, 250000, 'EWALLET', 'COMPLETED', 'Kasir 2 - Rina Wijaya'),
-- ('TRX003', NOW() - INTERVAL '1 day', 500000, 50000, 450000, 'TUNAI', 'COMPLETED', 'Kasir 1 - Budi Santoso'),
-- ('TRX004', NOW(), 175000, 0, 175000, 'EWALLET', 'COMPLETED', 'Kasir 3 - Ahmad Gunawan');

-- ========================================
-- SEED DATA: transaction_items (Optional)
-- ========================================
-- INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, subtotal) VALUES
-- (1, 1, 2, 30000, 60000),
-- (1, 3, 1, 45000, 45000),
-- (2, 4, 2, 125000, 250000),
-- (3, 2, 5, 15000, 75000),
-- (3, 5, 4, 25000, 100000),
-- (3, 7, 3, 35000, 105000),
-- (4, 1, 3, 30000, 90000),
-- (4, 6, 1, 50000, 50000),
-- (4, 8, 1, 20000, 20000);

-- ========================================
-- Verify Seed Data
-- ========================================
-- Check products
SELECT 'Products' AS "Data Type", COUNT(*) AS "Total Records" FROM products
UNION ALL
SELECT 'Users', COUNT(*) FROM users;

-- ========================================
-- Seed data loaded successfully
-- ========================================
