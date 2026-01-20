package com.upb.agripos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - PostgreSQL Connection Management untuk Week 15
 * Terhubung dengan pgAdmin
 * 
 * Person A - DATABASE MASTER
 * Week 15 - Proyek Kelompok
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database Configuration - PostgreSQL with pgAdmin
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/agripos";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "150603";
    private static final String DB_DRIVER = "org.postgresql.Driver";
    
    /**
     * Private constructor untuk singleton pattern
     */
    private DatabaseConnection() {
        try {
            initializeConnection();
        } catch (SQLException e) {
            System.err.println("❌ Gagal inisialisasi PostgreSQL connection: " + e.getMessage());
            throw new RuntimeException("Koneksi database gagal", e);
        }
    }
    
    /**
     * Inisialisasi JDBC Connection ke PostgreSQL
     */
    private void initializeConnection() throws SQLException {
        // Load JDBC Driver
        try {
            Class.forName(DB_DRIVER);
            System.out.println("✓ PostgreSQL Driver loaded");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver tidak ditemukan. Pastikan postgresql-xx.jar di classpath", e);
        }
        
        // Create Connection
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ PostgreSQL Connection established: " + DB_URL);
            System.out.println("✓ Connected to pgAdmin database 'agripos'");
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to PostgreSQL: " + e.getMessage());
            System.err.println("   Make sure PostgreSQL is running on localhost:5432");
            System.err.println("   Database: agripos | User: postgres");
            throw e;
        }
    }
    
    /**
     * Mendapatkan instance singleton DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Mendapatkan koneksi database yang aktif
     */
    public Connection getConnection() {
        if (connection == null) {
            try {
                initializeConnection();
            } catch (SQLException e) {
                System.err.println("❌ Error getting connection: " + e.getMessage());
                return null;
            }
        }
        return connection;
    }
    
    /**
     * Close koneksi database
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test koneksi database
     */
    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Database connection is active");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Main method untuk test koneksi
     */
    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("PostgreSQL Connection Test");
        System.out.println("========================================");
        System.out.println("Target: localhost:5432/agripos");
        System.out.println("User: postgres");
        System.out.println("========================================\n");
        
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn.testConnection()) {
                System.out.println("\n✓ CONNECTION SUCCESSFUL!");
                System.out.println("Ready to connect with pgAdmin");
            } else {
                System.out.println("\n❌ CONNECTION FAILED!");
            }
        } catch (Exception e) {
            System.out.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.getInstance().closeConnection();
        }
        
        System.out.println("\n========================================\n");
    }
}
