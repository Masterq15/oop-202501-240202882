-- =====================================================
-- SEED DATA untuk AgriPOS
-- Person A - DATABASE MASTER
-- =====================================================

-- =====================================================
-- Sample Users
-- =====================================================
INSERT INTO users (username, password, full_name, role, email, phone) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/DiO', 'Administrator', 'ADMIN', 'admin@agripos.com', '081234567890'),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/DiO', 'Manager Toko', 'MANAGER', 'manager@agripos.com', '081234567891'),
('cashier1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/DiO', 'Kasir Satu', 'CASHIER', 'cashier1@agripos.com', '081234567892'),
('cashier2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/DiO', 'Kasir Dua', 'CASHIER', 'cashier2@agripos.com', '081234567893');

-- =====================================================
-- Sample Products (Terintegrasi dengan PersonB)
-- =====================================================
INSERT INTO products (code, name, category, description, price, stock, min_stock, max_stock, unit) VALUES
('PROD001', 'Beras Premium 5kg', 'Beras', 'Beras premium pilihan kualitas terbaik', 75000.00, 50, 10, 200, 'kg'),
('PROD002', 'Minyak Goreng 2L', 'Minyak', 'Minyak goreng murni 2 liter', 30000.00, 75, 15, 300, 'liter'),
('PROD003', 'Gula Pasir 1kg', 'Gula', 'Gula pasir putih berkualitas', 12000.00, 100, 20, 500, 'kg'),
('PROD004', 'Telur Ayam 1kg', 'Telur', 'Telur ayam segar berisi 10-12 butir', 28000.00, 60, 10, 200, 'kg'),
('PROD005', 'Tepung Terigu 1kg', 'Tepung', 'Tepung terigu serbaguna premium', 8500.00, 80, 15, 400, 'kg'),
('PROD006', 'Garam Halus 500g', 'Garam', 'Garam halus beryodium', 3500.00, 120, 25, 600, 'kg'),
('PROD007', 'Kacang Tanah 500g', 'Kacang', 'Kacang tanah pilihan 500 gram', 18000.00, 45, 10, 150, 'kg'),
('PROD008', 'Bawang Merah 500g', 'Bawang', 'Bawang merah segar', 22000.00, 55, 10, 180, 'kg'),
('PROD009', 'Bawang Putih 250g', 'Bawang', 'Bawang putih segar 250 gram', 15000.00, 70, 15, 250, 'kg'),
('PROD010', 'Lada Hitam 100g', 'Rempah', 'Lada hitam bubuk halus', 45000.00, 30, 5, 100, 'g');

-- =====================================================
-- Sample Discounts (Terintegrasi dengan PersonB Discount Strategy)
-- =====================================================
INSERT INTO discounts (product_id, discount_type, discount_value, description, start_date, end_date, is_active) VALUES
(1, 'FIXED', 5000.00, 'Diskon Rp 5000 untuk beras premium', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
(2, 'PERCENTAGE', 10.00, 'Diskon 10% untuk minyak goreng', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
(3, 'PERCENTAGE', 15.00, 'Diskon 15% untuk gula pasir', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
(4, 'FIXED', 3000.00, 'Diskon Rp 3000 untuk telur ayam', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE),
(5, 'PERCENTAGE', 5.00, 'Diskon 5% untuk tepung terigu', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE);

-- =====================================================
-- Sample Transactions
-- =====================================================
INSERT INTO transactions (user_id, total_amount, discount_amount, final_amount, payment_method, status) VALUES
(3, 200000.00, 10000.00, 190000.00, 'CASH', 'COMPLETED'),
(3, 150000.00, 7500.00, 142500.00, 'CASH', 'COMPLETED'),
(4, 300000.00, 15000.00, 285000.00, 'CARD', 'COMPLETED'),
(3, 85000.00, 5000.00, 80000.00, 'TRANSFER', 'COMPLETED'),
(4, 500000.00, 25000.00, 475000.00, 'CASH', 'COMPLETED');

-- =====================================================
-- Sample Transaction Items
-- =====================================================
INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, discount_percentage, subtotal) VALUES
(1, 1, 2, 75000.00, 5.0, 142500.00),
(1, 2, 1, 30000.00, 10.0, 27000.00),
(1, 3, 1, 12000.00, 0.0, 12000.00),
(2, 4, 3, 28000.00, 0.0, 84000.00),
(2, 5, 2, 8500.00, 5.0, 16200.00),
(2, 6, 2, 3500.00, 0.0, 7000.00),
(3, 1, 3, 75000.00, 5.0, 213750.00),
(3, 8, 2, 22000.00, 0.0, 44000.00),
(3, 9, 2, 15000.00, 0.0, 30000.00),
(4, 7, 3, 18000.00, 10.0, 48600.00),
(4, 10, 1, 45000.00, 0.0, 45000.00),
(5, 2, 5, 30000.00, 10.0, 135000.00),
(5, 3, 10, 12000.00, 15.0, 102000.00),
(5, 4, 5, 28000.00, 0.0, 140000.00);

-- =====================================================
-- Sample Audit Logs
-- =====================================================
INSERT INTO audit_logs (user_id, action, table_name, record_id, old_value, new_value, description, ip_address) VALUES
(1, 'INSERT', 'products', 1, NULL, 'PROD001|Beras Premium 5kg|75000.00|50', 'Tambah produk baru', '127.0.0.1'),
(1, 'INSERT', 'users', 3, NULL, 'cashier1|Kasir Satu|CASHIER', 'Tambah user kasir baru', '127.0.0.1'),
(3, 'INSERT', 'transactions', 1, NULL, 'Transaction #1 - 200000.00', 'Transaksi penjualan', '127.0.0.1'),
(1, 'UPDATE', 'products', 1, 'stock=50', 'stock=48', 'Update stok produk', '127.0.0.1'),
(2, 'INSERT', 'discounts', 1, NULL, 'PROD001|FIXED|5000', 'Tambah diskon produk', '127.0.0.1');

-- =====================================================
-- Sample Stock Movements
-- =====================================================
INSERT INTO stock_movements (product_id, user_id, movement_type, quantity, reference_id, reference_type, notes) VALUES
(1, 1, 'IN', 50, 0, 'INITIAL', 'Stok awal'),
(2, 1, 'IN', 75, 0, 'INITIAL', 'Stok awal'),
(3, 1, 'IN', 100, 0, 'INITIAL', 'Stok awal'),
(1, 3, 'OUT', 2, 1, 'TRANSACTION', 'Penjualan via transaksi #1'),
(2, 3, 'OUT', 1, 1, 'TRANSACTION', 'Penjualan via transaksi #1'),
(3, 4, 'OUT', 10, 5, 'TRANSACTION', 'Penjualan via transaksi #5');

-- =====================================================
-- COMMIT DATA
-- =====================================================
COMMIT;
