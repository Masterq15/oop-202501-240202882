package com.upb.agripos.config;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        System.out.println("DatabaseConnection instance dibuat");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public void connect() {
        System.out.println("Terhubung ke database Agri-POS");
    }
    
    public void disconnect() {
        System.out.println("Disconnected dari database");
    }
}