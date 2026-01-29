package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Configuration
 * Singleton pattern untuk manajemen koneksi database
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/agripos_database";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "150603";
    private static final String DB_DRIVER = "org.postgresql.Driver";

    private DatabaseConfig() {}

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver tidak ditemukan: " + e.getMessage());
        }
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                // Create table Products (IF NOT EXISTS - jangan DROP untuk menjaga data)
                String createProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY," +
                    "code VARCHAR(50) UNIQUE NOT NULL," +
                    "name VARCHAR(200) NOT NULL," +
                    "category VARCHAR(100) NOT NULL," +
                    "price DECIMAL(12, 2) NOT NULL," +
                    "stock INTEGER NOT NULL DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

                // Create table Users
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "full_name VARCHAR(200) NOT NULL," +
                    "role VARCHAR(20) NOT NULL DEFAULT 'KASIR'," +
                    "active BOOLEAN DEFAULT true," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

                // Create table Transactions
                String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id SERIAL PRIMARY KEY," +
                    "code VARCHAR(50) UNIQUE NOT NULL," +
                    "transaction_date TIMESTAMP NOT NULL," +
                    "subtotal DECIMAL(12, 2) NOT NULL," +
                    "discount DECIMAL(12, 2) DEFAULT 0," +
                    "total DECIMAL(12, 2) NOT NULL," +
                    "payment_method VARCHAR(50) NOT NULL," +
                    "payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED'," +
                    "cashier_name VARCHAR(200)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

                // Create table Transaction Items
                String createTransactionItemsTable = "CREATE TABLE IF NOT EXISTS transaction_items (" +
                    "id SERIAL PRIMARY KEY," +
                    "transaction_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "unit_price DECIMAL(12, 2) NOT NULL," +
                    "subtotal DECIMAL(12, 2) NOT NULL," +
                    "FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                    ")";

                stmt.execute(createProductsTable);
                stmt.execute(createUsersTable);
                stmt.execute(createTransactionsTable);
                stmt.execute(createTransactionItemsTable);
                System.out.println("✓ Database tables initialized (data persisted)");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            throw e;
        }
    }

    public void seedInitialData() throws SQLException {
        try (Connection conn = getConnection()) {
            // Seed products
            String checkProducts = "SELECT COUNT(*) FROM products";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkProducts)) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertProducts = "INSERT INTO products (code, name, category, price, stock) VALUES " +
                        "('P001', 'Pupuk Organik Premium', 'Pupuk', 30000, 50)," +
                        "('P002', 'Benih Padi Berkualitas', 'Benih', 15000, 100)," +
                        "('P003', 'Pestisida Organik 500ml', 'Pestisida', 45000, 30)," +
                        "('P004', 'Pupuk NPK 25kg', 'Pupuk', 125000, 20)," +
                        "('P005', 'Bibit Cabai Hibrida', 'Benih', 25000, 80)";
                    try (Statement insertStmt = conn.createStatement()) {
                        insertStmt.execute(insertProducts);
                        System.out.println("Seed products data successfully");
                    }
                }
            }

            // Seed users
            String checkUsers = "SELECT COUNT(*) FROM users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkUsers)) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertUsers = "INSERT INTO users (username, password, full_name, role, active) VALUES " +
                        "('admin01', 'admin123', 'Admin System', 'ADMIN', true)," +
                        "('kasir01', 'kasir123', 'Kasir 1', 'KASIR', true)," +
                        "('kasir02', 'kasir456', 'Kasir 2', 'KASIR', true)";
                    try (Statement insertStmt = conn.createStatement()) {
                        insertStmt.execute(insertUsers);
                        System.out.println("Seed users data successfully");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error seeding data: " + e.getMessage());
        }
    }
}
