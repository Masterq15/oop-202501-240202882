package com.upb.agripos;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.service.AuthServiceImpl;
import com.upb.agripos.view.AdminDashboard;
import com.upb.agripos.view.LoginView;
import com.upb.agripos.view.PosView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        PosView posView = new PosView(primaryStage, authController);
        PosView.setNavCallback(() -> showLoginView());
        Scene scene = posView.createScene();
        primaryStage.setScene(scene);
        System.out.println("→ PosView displayed for: " + authController.getCurrentUser().getFullName());
    }
    
    /**
     * Display AdminDashboard untuk ADMIN
     */
    private void showAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(primaryStage, authController);
        AdminDashboard.setNavCallback(() -> showLoginView());
        Scene scene = adminDashboard.createScene();
        primaryStage.setScene(scene);
        System.out.println("→ AdminDashboard displayed for: " + authController.getCurrentUser().getFullName());
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}