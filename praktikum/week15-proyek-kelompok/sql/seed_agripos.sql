-- =====================================================
-- SEED DATA untuk AgriPOS
-- Person A - DATABASE MASTER
-- PostgreSQL Version (CLEANED - fokus user management)
-- =====================================================

-- =====================================================
-- Sample Users (ADMIN dan KASIR)
-- =====================================================
INSERT INTO users (username, password, full_name, role) VALUES
-- Admin Users - Password: admin123
('firly', 'admin123', 'Firly', 'ADMIN'),
('admin', 'admin123', 'Administrator', 'ADMIN'),

-- Kasir Users - Password: password123
('ismi', 'password123', 'Ismi', 'KASIR'),
('kasir1', 'password123', 'Budi Santoso', 'KASIR')
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- Sample Products
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
('PROD010', 'Lada Hitam 100g', 'Rempah', 'Lada hitam bubuk halus', 45000.00, 30, 5, 100, 'g')
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- Sample Discounts
-- =====================================================
INSERT INTO discounts (product_id, discount_type, discount_value, description, start_date, end_date, is_active) VALUES
(1, 'FIXED', 5000.00, 'Diskon Rp 5000 untuk beras premium', NOW(), NOW() + INTERVAL '30 days', TRUE),
(2, 'PERCENTAGE', 10.00, 'Diskon 10% untuk minyak goreng', NOW(), NOW() + INTERVAL '30 days', TRUE),
(3, 'PERCENTAGE', 15.00, 'Diskon 15% untuk gula pasir', NOW(), NOW() + INTERVAL '30 days', TRUE),
(4, 'FIXED', 3000.00, 'Diskon Rp 3000 untuk telur ayam', NOW(), NOW() + INTERVAL '30 days', TRUE),
(5, 'PERCENTAGE', 5.00, 'Diskon 5% untuk tepung terigu', NOW(), NOW() + INTERVAL '30 days', FALSE)
ON CONFLICT DO NOTHING;
