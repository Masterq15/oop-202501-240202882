package com.upb.agripos;

import com.upb.agripos.service.*;
import com.upb.agripos.model.User;
import com.upb.agripos.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * AppJavaFX - Main Entry Point untuk Agri-POS GUI
 * 
 * Fitur:
 * - Role-based routing (KASIR -> PosView, ADMIN -> AdminDashboard)
 * - Session management
 * - Service layer integration
 * 
 * Week 15 - Proyek Kelompok
 * Created by: [Person D - Frontend]
 * Last modified: 2026-01-15
 */
public class AppJavaFX extends Application {

    private Stage primaryStage;
    private Scene loginScene, posScene, adminScene;
    private AuthService authService;
    private ProductService productService;
    private TransactionService transactionService;
    private AuditLogService auditLogService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize services (TODO: Connect to real database)
        initializeServices();
        
        // Initialize main window
        primaryStage.setTitle("🛒 Agri-POS - Point of Sale System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setOnCloseRequest(e -> handleLogout());
        
        // Create scenes
        createLoginScene();
        
        // Show login scene first
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void initializeServices() {
        // Initialize services dengan hardcoded data untuk demo
        authService = new AuthServiceImpl();
        // productService = new ProductServiceImpl();  // TODO: Uncomment saat database siap
        // transactionService = new TransactionServiceImpl();  // TODO: Uncomment saat database siap
        // auditLogService = new AuditLogServiceImpl();  // TODO: Uncomment saat database siap
        
        // For now, create mock services untuk demo
        productService = new ProductServiceImpl();  // Will use in-memory storage
        transactionService = new TransactionServiceImpl();  // Will use in-memory storage
        auditLogService = new AuditLogServiceImpl();  // Will use in-memory storage
    }

    private void createLoginScene() {
        BorderPane loginRoot = new BorderPane();
        LoginView loginView = new LoginView(authService);
        loginRoot.setCenter(loginView);
        
        // Setup login callback
        loginView.setLoginCallback(user -> {
            if (user.isAdmin()) {
                showAdminDashboard(user);
            } else if (user.isKasir()) {
                showPosView(user);
            }
        });
        
        loginScene = new Scene(loginRoot, 1200, 800);
    }

    private void showPosView(User user) {
        try {
            PosView posView = new PosView(productService, transactionService, authService, user);
            
            // Setup PosView actions (TODO: Connect dengan business logic)
            setupPosViewActions(posView);
            
            BorderPane root = new BorderPane();
            root.setCenter(posView);
            
            // Add logout button
            root.setTop(createTopBar(user, () -> handleLogout()));
            
            posScene = new Scene(root, 1200, 800);
            primaryStage.setScene(posScene);
            System.out.println("✅ PosView loaded successfully for: " + user.getFullName());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("❌ ERROR loading PosView: " + ex.getMessage());
            showErrorAlert("Error", "Gagal memuat PosView: " + ex.getMessage());
        }
    }

    private void showAdminDashboard(User user) {
        try {
            AdminDashboard adminDash = new AdminDashboard(
                productService, 
                transactionService, 
                auditLogService, 
                user
            );
            
            // Setup AdminDashboard actions (TODO: Connect dengan business logic)
            setupAdminDashboardActions(adminDash);
            
            BorderPane root = new BorderPane();
            root.setCenter(adminDash);
            
            // Add logout button
            root.setTop(createTopBar(user, () -> handleLogout()));
            
            adminScene = new Scene(root, 1200, 800);
            primaryStage.setScene(adminScene);
            System.out.println("✅ AdminDashboard loaded successfully for: " + user.getFullName());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("❌ ERROR loading AdminDashboard: " + ex.getMessage());
            showErrorAlert("Error", "Gagal memuat AdminDashboard: " + ex.getMessage());
        }
    }

    private javafx.scene.layout.HBox createTopBar(User user, Runnable logoutCallback) {
        javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox(20);
        topBar.setPadding(new javafx.geometry.Insets(10));
        topBar.setStyle("-fx-background-color: #2E7D32; -fx-padding: 10;");
        
        javafx.scene.control.Label lblUser = new javafx.scene.control.Label(
            "👤 " + user.getFullName() + " (" + user.getRole() + ")"
        );
        lblUser.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        javafx.scene.control.Button btnLogout = new javafx.scene.control.Button("🔐 Logout");
        btnLogout.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11;");
        btnLogout.setPrefHeight(30);
        btnLogout.setOnAction(e -> logoutCallback.run());
        
        topBar.getChildren().addAll(lblUser, spacer, btnLogout);
        return topBar;
    }

    private void setupPosViewActions(PosView posView) {
        // TODO: Setup event handlers untuk:
        // 1. Add product to cart
        // 2. Remove product from cart
        // 3. Clear cart
        // 4. Checkout
        // 5. Update product (only admin)
        
        System.out.println("✅ PosView initialized untuk: " + posView.getCurrentUser().getFullName());
    }

    private void setupAdminDashboardActions(AdminDashboard adminDash) {
        // TODO: Setup event handlers untuk:
        // 1. Add product
        // 2. Update product
        // 3. Delete product
        // 4. Generate reports
        // 5. Export reports
        // 6. View audit logs
        // 7. Backup/Restore database
        
        System.out.println("✅ AdminDashboard initialized");
    }

    private void handleLogout() {
        authService.logout();
        createLoginScene();
        primaryStage.setScene(loginScene);
        System.out.println("✅ Logout berhasil");
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}
