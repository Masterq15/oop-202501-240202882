package com.upb.agripos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Connection Management
 * Person A - DATABASE MASTER
 * 
 * Simple JDBC connection tanpa HikariCP
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agripos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Private constructor untuk singleton pattern
     */
    private DatabaseConnection() {
        try {
            initializeConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal inisialisasi connection", e);
        }
    }
    
    /**
     * Inisialisasi JDBC Connection
     */
    private void initializeConnection() throws SQLException {
        // Load JDBC Driver
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan", e);
        }
        
        // Create Connection
        this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
     * Mendapatkan koneksi database
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }
    
    /**
     * Menutup koneksi
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
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
        } else {
            System.out.println("✗ Koneksi database gagal!");
        }
        
        // Cleanup
        DatabaseConnection.getInstance().closeConnection();
    }
}
