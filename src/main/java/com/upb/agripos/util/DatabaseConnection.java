package com.upb.agripos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DatabaseConnection - Connection Pool Management
 * Person A - DATABASE MASTER
 * 
 * Menggunakan HikariCP untuk connection pooling
 * yang efisien dan scalable
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    
    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agripos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection Pool Configuration
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE = 2;
    private static final long CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final long IDLE_TIMEOUT = 600000; // 10 minutes
    private static final long MAX_LIFETIME = 1800000; // 30 minutes
    
    /**
     * Private constructor untuk singleton pattern
     */
    private DatabaseConnection() {
        try {
            initializeDataSource();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal inisialisasi connection pool", e);
        }
    }
    
    /**
     * Inisialisasi HikariCP DataSource
     */
    private void initializeDataSource() throws SQLException {
        // Load JDBC Driver
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan", e);
        }
        
        // Konfigurasi HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(MIN_IDLE);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setIdleTimeout(IDLE_TIMEOUT);
        config.setMaxLifetime(MAX_LIFETIME);
        config.setAutoCommit(true);
        config.setLeakDetectionThreshold(60000); // 60 seconds
        
        // Buat DataSource
        dataSource = new HikariDataSource(config);
    }
    
    /**
     * Mendapatkan instance singleton
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Mendapatkan koneksi dari connection pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource tidak tersedia");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Mendapatkan koneksi dengan timeout kustom
     */
    public Connection getConnection(long timeout) throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource tidak tersedia");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Menutup semua koneksi dalam pool
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    /**
     * Mendapatkan statistik pool
     */
    public void printPoolStats() {
        if (dataSource != null) {
            System.out.println("=== Connection Pool Statistics ===");
            System.out.println("Total Connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Active Connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle Connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Pending Threads: " + dataSource.getHikariPoolMXBean().getPendingThreads());
            System.out.println("====================================");
        }
    }
    
    /**
     * Test koneksi database
     */
    public static boolean testConnection() {
        try (Connection conn = getInstance().getConnection()) {
            return conn.isValid(5); // Test dengan timeout 5 detik
        } catch (SQLException e) {
            System.err.println("Koneksi database gagal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Main method untuk testing
     */
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        
        if (testConnection()) {
            System.out.println("✓ Koneksi database berhasil!");
            DatabaseConnection.getInstance().printPoolStats();
        } else {
            System.out.println("✗ Koneksi database gagal!");
        }
        
        // Cleanup
        DatabaseConnection.getInstance().closePool();
    }
}
