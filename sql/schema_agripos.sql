-- =====================================================
-- DATABASE SCHEMA untuk AgriPOS
-- Person A - DATABASE MASTER
-- PostgreSQL Version
-- =====================================================

-- =====================================================
-- Table: users
-- Menyimpan data pengguna sistem
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'KASIR' -- ADMIN, KASIR
);

CREATE INDEX IF NOT EXISTS idx_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_role ON users(role);

-- =====================================================
-- Table: products
-- Menyimpan data produk yang dijual (terintegrasi dengan PersonB)
-- =====================================================
CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    min_stock INT DEFAULT 10,
    max_stock INT DEFAULT 1000,
    unit VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_code ON products(code);
CREATE INDEX IF NOT EXISTS idx_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_is_active ON products(is_active);

-- =====================================================
-- Table: discounts
-- Menyimpan data diskon (terintegrasi dengan PersonB Discount Strategy)
-- =====================================================
CREATE TABLE IF NOT EXISTS discounts (
    discount_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    discount_type VARCHAR(50) NOT NULL, -- FIXED, PERCENTAGE
    discount_value DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_discount_product_id ON discounts(product_id);
CREATE INDEX IF NOT EXISTS idx_discount_is_active ON discounts(is_active);

-- =====================================================
-- Table: transactions
-- Menyimpan data transaksi penjualan
-- =====================================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(50), -- CASH, CARD, TRANSFER
    status VARCHAR(20) DEFAULT 'COMPLETED', -- COMPLETED, CANCELLED, PENDING
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_transaction_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transaction_date ON transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transaction_status ON transactions(status);

-- =====================================================
-- Table: transaction_items
-- Detail item dalam setiap transaksi
-- =====================================================
CREATE TABLE IF NOT EXISTS transaction_items (
    item_id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    subtotal DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_item_transaction_id ON transaction_items(transaction_id);
CREATE INDEX IF NOT EXISTS idx_item_product_id ON transaction_items(product_id);

-- =====================================================
-- Table: audit_logs
-- Menyimpan log semua perubahan data penting
-- =====================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT,
    action VARCHAR(50) NOT NULL, -- INSERT, UPDATE, DELETE
    table_name VARCHAR(50) NOT NULL,
    record_id INT,
    old_value TEXT,
    new_value TEXT,
    description VARCHAR(255),
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_table_name ON audit_logs(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp);

-- =====================================================
-- Table: stock_movements
-- Menyimpan history perubahan stok produk
-- =====================================================
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT,
    movement_type VARCHAR(50) NOT NULL, -- IN, OUT, RETURN, ADJUSTMENT
    quantity INT NOT NULL,
    reference_id INT, -- transaction_id atau reference lainnya
    reference_type VARCHAR(50), -- TRANSACTION, PURCHASE_ORDER, dll
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_stock_product_id ON stock_movements(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_user_id ON stock_movements(user_id);
CREATE INDEX IF NOT EXISTS idx_stock_movement_type ON stock_movements(movement_type);
CREATE INDEX IF NOT EXISTS idx_stock_created_at ON stock_movements(created_at);

-- =====================================================
-- Views untuk kemudahan query
-- =====================================================

-- View untuk Product dengan info stok
CREATE OR REPLACE VIEW v_product_stock AS
SELECT 
    p.product_id,
    p.code,
    p.name,
    p.category,
    p.price,
    p.stock,
    p.min_stock,
    p.max_stock,
    CASE 
        WHEN p.stock <= p.min_stock THEN 'LOW'
        WHEN p.stock >= p.max_stock THEN 'HIGH'
        ELSE 'NORMAL'
    END as stock_status,
    CASE WHEN p.stock <= p.min_stock THEN 1 ELSE 0 END as needs_reorder
FROM products p
WHERE p.is_active = TRUE;

-- View untuk transaksi dengan detail user
CREATE OR REPLACE VIEW v_transaction_detail AS
SELECT 
    t.transaction_id,
    t.transaction_date,
    u.username as cashier_name,
    t.total_amount,
    t.discount_amount,
    t.final_amount,
    t.payment_method,
    t.status,
    COUNT(ti.item_id) as item_count
FROM transactions t
JOIN users u ON t.user_id = u.user_id
LEFT JOIN transaction_items ti ON t.transaction_id = ti.transaction_id
GROUP BY t.transaction_id, t.transaction_date, u.username, t.total_amount, 
         t.discount_amount, t.final_amount, t.payment_method, t.status;

-- View untuk Laporan Kasir ke Admin (Daily Summary)
CREATE OR REPLACE VIEW v_cashier_daily_report AS
SELECT 
    DATE(t.transaction_date) as tanggal_transaksi,
    u.user_id,
    u.username as nama_kasir,
    COUNT(t.transaction_id) as jumlah_transaksi,
    SUM(t.total_amount) as total_penjualan,
    SUM(t.discount_amount) as total_diskon,
    SUM(t.final_amount) as total_netto,
    COUNT(CASE WHEN t.payment_method = 'CASH' THEN 1 END) as transaksi_cash,
    COUNT(CASE WHEN t.payment_method = 'CARD' THEN 1 END) as transaksi_card,
    COUNT(CASE WHEN t.payment_method = 'TRANSFER' THEN 1 END) as transaksi_transfer,
    COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as transaksi_berhasil,
    COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) as transaksi_batal
FROM transactions t
JOIN users u ON t.user_id = u.user_id
WHERE u.role = 'CASHIER'
GROUP BY DATE(t.transaction_date), u.user_id, u.username
ORDER BY tanggal_transaksi DESC, nama_kasir;

-- View untuk Riwayat Pembelian (Purchase History) per Kasir
CREATE OR REPLACE VIEW v_purchase_history_detail AS
SELECT 
    t.transaction_id,
    t.transaction_date,
    u.username as kasir,
    p.code as kode_produk,
    p.name as nama_produk,
    p.category as kategori,
    ti.quantity as jumlah,
    p.unit,
    ti.unit_price as harga_satuan,
    ti.discount_percentage as diskon_persen,
    ti.subtotal,
    t.total_amount,
    t.discount_amount,
    t.final_amount,
    t.payment_method,
    t.status
FROM transactions t
JOIN users u ON t.user_id = u.user_id
JOIN transaction_items ti ON t.transaction_id = ti.transaction_id
JOIN products p ON ti.product_id = p.product_id
ORDER BY t.transaction_date DESC, t.transaction_id;

-- View untuk Laporan Stok Berkurang
CREATE OR REPLACE VIEW v_stock_reduction_report AS
SELECT 
    p.product_id,
    p.code,
    p.name,
    p.price,
    sm.movement_type,
    sm.quantity,
    sm.created_at as tanggal_perubahan,
    COALESCE(u.username, 'SYSTEM') as oleh_user,
    sm.reference_type,
    sm.notes
FROM stock_movements sm
JOIN products p ON sm.product_id = p.product_id
LEFT JOIN users u ON sm.user_id = u.user_id
WHERE sm.movement_type IN ('OUT', 'ADJUSTMENT')
ORDER BY sm.created_at DESC;

-- View untuk Ringkasan Stok Produk
CREATE OR REPLACE VIEW v_stock_summary AS
SELECT 
    p.product_id,
    p.code,
    p.name,
    p.category,
    p.price,
    p.stock as stok_saat_ini,
    p.min_stock,
    p.max_stock,
    CASE 
        WHEN p.stock <= p.min_stock THEN 'URGENT'
        WHEN p.stock < (p.min_stock * 1.5) THEN 'LOW'
        WHEN p.stock >= p.max_stock THEN 'OVERSTOCK'
        ELSE 'NORMAL'
    END as status_stok,
    (p.max_stock - p.stock) as jumlah_untuk_max,
    COALESCE(d.discount_type, 'NO DISCOUNT') as jenis_diskon,
    COALESCE(d.discount_value, 0) as nilai_diskon
FROM products p
LEFT JOIN discounts d ON p.product_id = d.product_id AND d.is_active = TRUE
WHERE p.is_active = TRUE
ORDER BY p.category, p.name;
