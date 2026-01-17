package com.upb.agripos;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.service.AuthServiceImpl;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.util.DatabaseConnection;
import com.upb.agripos.view.AdminDashboard;
import com.upb.agripos.view.LoginView;
import com.upb.agripos.view.PosView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

/**
 * Main Application Entry Point - Week 15 Collaborative Project
 * Person D - Frontend/UI Layer
 * 
 * Features:
 * - Authentication dengan LoginView
 * - Role-based routing (KASIR -> PosView, ADMIN -> AdminDashboard)
 * - Service layer integration
 */
public class AppJavaFX extends Application {
    
    private Stage primaryStage;
    private AuthController authController;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        System.out.println("=".repeat(70));
        System.out.println("AGRI-POS System - Point of Sale untuk Pertanian");
        System.out.println("Week 15 - Collaborative Project");
        System.out.println("Person D - Frontend/UI");
        System.out.println("=".repeat(70));
        
        try {
            // Initialize AuthService
            AuthServiceImpl authService = new AuthServiceImpl();
            
            // Initialize AuthController
            authController = new AuthController(authService);
            System.out.println("✓ AuthController initialized");
            
            // Setup primary stage
            primaryStage.setTitle("🛒 AGRI-POS - Point of Sale System");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            
            // Show LoginView first
            showLoginView();
            
            primaryStage.show();
            System.out.println("✓ Application started successfully");
            System.out.println("=".repeat(70));
            
        } catch (Exception e) {
            System.err.println("✗ Error initializing application:");
            e.printStackTrace();
        }
    }
    
    /**
     * Display LoginView
     */
    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage, authController);
        LoginView.setNavCallback((user) -> {
            if (user.isAdmin()) {
                showAdminDashboard();
            } else {
                showPosView();
            }
        });
        Scene scene = loginView.createScene();
        primaryStage.setScene(scene);
        System.out.println("→ LoginView displayed");
    }
    
    /**
     * Display PosView untuk KASIR
     */
    private void showPosView() {
        try {
            System.out.println("[NAV] showPosView() called");
            System.out.println("[NAV] Current user: " + (authController.getCurrentUser() != null ? authController.getCurrentUser().getFullName() : "NULL"));
            
            ProductService productService = null;
            
            // Try to get database connection and create ProductService
            try {
                System.out.println("[NAV] Getting database connection...");
                Connection connection = DatabaseConnection.getInstance().getConnection();
                System.out.println("[NAV] Connection obtained: " + (connection != null ? "SUCCESS" : "NULL"));
                
                if (connection != null) {
                    productService = new ProductService(connection);
                    System.out.println("[NAV] ProductService created");
                }
            } catch (Exception dbException) {
                System.err.println("[NAV] Database connection failed: " + dbException.getMessage());
                System.err.println("[NAV] Continuing without ProductService (database unavailable)");
            }
            
            // Create PosView and inject ProductService if available
            PosView posView = new PosView(primaryStage, authController);
            if (productService != null) {
                posView.setProductService(productService);
                System.out.println("[NAV] ProductService injected into PosView");
            }
            
            PosView.setNavCallback(() -> showLoginView());
            System.out.println("[NAV] Creating PosView scene...");
            Scene scene = posView.createScene();
            System.out.println("[NAV] Setting scene to primaryStage...");
            primaryStage.setScene(scene);
            
            String userName = authController.getCurrentUser() != null ? authController.getCurrentUser().getFullName() : "Unknown";
            System.out.println("✓ PosView displayed for: " + userName);
        } catch (Exception e) {
            System.err.println("✗ Error displaying PosView: " + e.getMessage());
            System.err.println("✗ Full error:");
            e.printStackTrace();
            // Fallback: show LoginView
            showLoginView();
        }
    }
    
    /**
     * Display AdminDashboard untuk ADMIN
     */
    private void showAdminDashboard() {
        try {
            System.out.println("[NAV] showAdminDashboard() called");
            System.out.println("[NAV] Current user: " + (authController.getCurrentUser() != null ? authController.getCurrentUser().getFullName() : "NULL"));
            
            ProductService productService = null;
            
            // Try to get database connection and create ProductService
            try {
                System.out.println("[NAV] Getting database connection...");
                Connection connection = DatabaseConnection.getInstance().getConnection();
                System.out.println("[NAV] Connection obtained: " + (connection != null ? "SUCCESS" : "NULL"));
                
                if (connection != null) {
                    productService = new ProductService(connection);
                    System.out.println("[NAV] ProductService created");
                }
            } catch (Exception dbException) {
                System.err.println("[NAV] Database connection failed: " + dbException.getMessage());
                System.err.println("[NAV] Continuing without ProductService (database unavailable)");
            }
            
            // Create AdminDashboard and inject ProductService if available
            AdminDashboard adminDashboard = new AdminDashboard(primaryStage, authController);
            if (productService != null) {
                adminDashboard.setProductService(productService);
                System.out.println("[NAV] ProductService injected into AdminDashboard");
            }
            
            AdminDashboard.setNavCallback(() -> showLoginView());
            System.out.println("[NAV] Creating AdminDashboard scene...");
            Scene scene = adminDashboard.createScene();
            System.out.println("[NAV] Setting scene to primaryStage...");
            primaryStage.setScene(scene);
            
            String userName = authController.getCurrentUser() != null ? authController.getCurrentUser().getFullName() : "Unknown";
            System.out.println("✓ AdminDashboard displayed for: " + userName);
        } catch (Exception e) {
            System.err.println("✗ Error displaying AdminDashboard: " + e.getMessage());
            System.err.println("✗ Full error:");
            e.printStackTrace();
            // Fallback: show LoginView
            showLoginView();
        }
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}