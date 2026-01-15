-- =====================================================
-- DATABASE SCHEMA untuk AgriPOS
-- Person A - DATABASE MASTER
-- =====================================================

-- =====================================================
-- Table: users
-- Menyimpan data pengguna sistem
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CASHIER', -- ADMIN, CASHIER, MANAGER
    email VARCHAR(100),
    phone VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_is_active (is_active)
);

-- =====================================================
-- Table: products
-- Menyimpan data produk yang dijual (terintegrasi dengan PersonB)
-- =====================================================
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
);

-- =====================================================
-- Table: discounts
-- Menyimpan data diskon (terintegrasi dengan PersonB Discount Strategy)
-- =====================================================
CREATE TABLE IF NOT EXISTS discounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    discount_type VARCHAR(50) NOT NULL, -- FIXED, PERCENTAGE
    discount_value DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    start_date DATETIME,
    end_date DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_is_active (is_active)
);

-- =====================================================
-- Table: transactions
-- Menyimpan data transaksi penjualan
-- =====================================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(50), -- CASH, CARD, TRANSFER
    status VARCHAR(20) DEFAULT 'COMPLETED', -- COMPLETED, CANCELLED, PENDING
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_status (status)
);

-- =====================================================
-- Table: transaction_items
-- Detail item dalam setiap transaksi
-- =====================================================
CREATE TABLE IF NOT EXISTS transaction_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    subtotal DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_product_id (product_id)
);

-- =====================================================
-- Table: audit_logs
-- Menyimpan log semua perubahan data penting
-- =====================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(50) NOT NULL, -- INSERT, UPDATE, DELETE
    table_name VARCHAR(50) NOT NULL,
    record_id INT,
    old_value TEXT,
    new_value TEXT,
    description VARCHAR(255),
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_table_name (table_name),
    INDEX idx_timestamp (timestamp)
);

-- =====================================================
-- Table: stock_movements
-- Menyimpan history perubahan stok produk
-- =====================================================
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT,
    movement_type VARCHAR(50) NOT NULL, -- IN, OUT, RETURN, ADJUSTMENT
    quantity INT NOT NULL,
    reference_id INT, -- transaction_id atau reference lainnya
    reference_type VARCHAR(50), -- TRANSACTION, PURCHASE_ORDER, dll
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_movement_type (movement_type),
    INDEX idx_created_at (created_at)
);

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
GROUP BY t.transaction_id;
