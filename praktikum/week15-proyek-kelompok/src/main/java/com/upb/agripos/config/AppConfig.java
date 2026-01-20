package com.upb.agripos.config;

/**
 * AppConfig - Konfigurasi Aplikasi Agri-POS Week 15
 * 
 * File ini menyimpan semua konfigurasi yang diperlukan aplikasi
 * termasuk database, UI, dan business logic settings
 * 
 * Week 15 - Proyek Kelompok
 */
public class AppConfig {
    
    // ========== DATABASE CONFIGURATION ==========
    public static final class Database {
        public static final String DRIVER = "org.postgresql.Driver";
        public static final String URL = "jdbc:postgresql://localhost:5432/agripos";
        public static final String USER = "postgres";
        public static final String PASSWORD = "150603";
        public static final String NAME = "agripos";
        public static final String HOST = "localhost";
        public static final int PORT = 5432;
        
        /**
         * Get database connection info
         */
        public static String getConnectionInfo() {
            return String.format("PostgreSQL @ %s:%d/%s", HOST, PORT, NAME);
        }
    }
    
    // ========== APPLICATION CONFIGURATION ==========
    public static final class App {
        public static final String NAME = "Agri-POS";
        public static final String VERSION = "1.0-FINAL";
        public static final String TITLE = "🛒 AGRI-POS - Point of Sale System untuk Pertanian";
        public static final int DEFAULT_WIDTH = 1200;
        public static final int DEFAULT_HEIGHT = 800;
    }
    
    // ========== ROLE CONFIGURATION ==========
    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String KASIR = "KASIR";
    }
    
    // ========== UI CONFIGURATION ==========
    public static final class UI {
        public static final String STYLESHEET = "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;";
        public static final String COLOR_PRIMARY = "#2196F3";
        public static final String COLOR_SUCCESS = "#4CAF50";
        public static final String COLOR_WARNING = "#FF9800";
        public static final String COLOR_ERROR = "#F44336";
        public static final String COLOR_BACKGROUND = "#f9f9f9";
    }
    
    // ========== PAYMENT METHOD ==========
    public static final class PaymentMethod {
        public static final String CASH = "CASH";
        public static final String EWALLET = "EWALLET";
    }
    
    /**
     * Get semua konfigurasi aplikasi sebagai string
     */
    public static void printConfiguration() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║        AGRI-POS - KONFIGURASI APLIKASI         ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        
        System.out.println("\n📊 DATABASE:");
        System.out.println("  Driver:   " + Database.DRIVER);
        System.out.println("  Host:     " + Database.HOST);
        System.out.println("  Port:     " + Database.PORT);
        System.out.println("  Database: " + Database.NAME);
        System.out.println("  User:     " + Database.USER);
        System.out.println("  Info:     " + Database.getConnectionInfo());
        
        System.out.println("\n📱 APPLICATION:");
        System.out.println("  Name:     " + App.NAME);
        System.out.println("  Version:  " + App.VERSION);
        System.out.println("  Title:    " + App.TITLE);
        System.out.println("  Size:     " + App.DEFAULT_WIDTH + "x" + App.DEFAULT_HEIGHT);
        
        System.out.println("\n🔐 ROLES:");
        System.out.println("  Admin:    " + Role.ADMIN);
        System.out.println("  Kasir:    " + Role.KASIR);
        
        System.out.println("\n💳 PAYMENT METHODS:");
        System.out.println("  Cash:     " + PaymentMethod.CASH);
        System.out.println("  E-Wallet: " + PaymentMethod.EWALLET);
        
        System.out.println("\n✓ Configuration loaded successfully\n");
    }
}
