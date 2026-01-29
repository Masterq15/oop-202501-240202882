-- ========================================
-- AGRI-POS WEEK 15 - Database Schema
-- Database: agripos_database
-- ========================================

-- Drop tables if exist (untuk clean slate)
DROP TABLE IF EXISTS transaction_items CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ========================================
-- TABLE: products
-- Menyimpan data produk pertanian
-- ========================================
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index untuk query cepat
CREATE INDEX idx_product_code ON products(code);
CREATE INDEX idx_product_category ON products(category);

-- ========================================
-- TABLE: users
-- Menyimpan data user sistem (admin, kasir)
-- ========================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'KASIR',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index untuk query login
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_role ON users(role);

-- ========================================
-- TABLE: transactions
-- Menyimpan data transaksi penjualan
-- ========================================
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    discount DECIMAL(12, 2) DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    cashier_name VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index untuk query transaksi
CREATE INDEX idx_transaction_code ON transactions(code);
CREATE INDEX idx_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transaction_payment_method ON transactions(payment_method);

-- ========================================
-- TABLE: transaction_items
-- Menyimpan detail item dalam setiap transaksi
-- ========================================
CREATE TABLE transaction_items (
    id SERIAL PRIMARY KEY,
    transaction_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Index untuk query items
CREATE INDEX idx_transaction_items_transaction_id ON transaction_items(transaction_id);
CREATE INDEX idx_transaction_items_product_id ON transaction_items(product_id);

-- ========================================
-- Comments & Documentation
-- ========================================
COMMENT ON TABLE products IS 'Tabel produk pertanian yang dijual';
COMMENT ON TABLE users IS 'Tabel user sistem (admin dan kasir)';
COMMENT ON TABLE transactions IS 'Tabel transaksi penjualan';
COMMENT ON TABLE transaction_items IS 'Tabel detail item dalam transaksi';

COMMENT ON COLUMN products.code IS 'Kode produk unik (PXX)';
COMMENT ON COLUMN products.category IS 'Kategori: Pupuk, Benih, Pestisida, dll';
COMMENT ON COLUMN products.stock IS 'Jumlah stok saat ini';

COMMENT ON COLUMN users.role IS 'Role: ADMIN atau KASIR';
COMMENT ON COLUMN users.active IS 'Status user aktif atau tidak';

COMMENT ON COLUMN transactions.code IS 'Kode transaksi unik';
COMMENT ON COLUMN transactions.payment_method IS 'Metode pembayaran: TUNAI, EWALLET, dll';
COMMENT ON COLUMN transactions.payment_status IS 'Status pembayaran: COMPLETED, PENDING, dll';

-- ========================================
-- Schema initialization completed
-- ========================================
