package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Login View - JavaFX Form untuk login user
 * Mendukung 2 role: KASIR dan ADMIN
 * Person D - Frontend Week 15
 */
public class LoginView {
    
    private Stage stage;
    private AuthController authController;
    private Scene scene;
    
    @FunctionalInterface
    public interface NavigationCallback {
        void onLoginSuccess(User user);
    }
    
    private static NavigationCallback navCallback;
    
    public static void setNavCallback(NavigationCallback callback) {
        navCallback = callback;
    }
    
    public LoginView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
    }
    
    /**
     * Create dan return login scene
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        
        // Center: Login form
        VBox centerBox = createLoginForm();
        root.setCenter(centerBox);
        
        // Bottom: Footer
        Label footerLabel = new Label("AGRI-POS System v1.0 | Week 15 Project");
        footerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);
        footerBox.getChildren().add(footerLabel);
        root.setBottom(footerBox);
        
        this.scene = new Scene(root, 500, 600);
        return scene;
    }
    
    /**
     * Create login form with username, password, role selector
     */
    private VBox createLoginForm() {
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-padding: 50;");
        
        // Title
        Label titleLabel = new Label("🔐 AGRI-POS LOGIN");
        titleLabel.setFont(new Font("System", 24));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Subtitle
        Label subtitleLabel = new Label("Sistem Point of Sale untuk Pertanian");
        subtitleLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");
        
        // Form fields
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        
        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Masukkan password");
        passwordVisibleField.setPrefWidth(300);
        passwordVisibleField.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        
        // Use StackPane to prevent column shift when showing/hiding password
        javafx.scene.layout.StackPane passwordStackPane = new javafx.scene.layout.StackPane();
        passwordStackPane.setPrefWidth(300);
        passwordStackPane.setPrefHeight(40);
        passwordStackPane.getChildren().addAll(passwordField, passwordVisibleField);
        passwordVisibleField.setVisible(false);
        
        CheckBox showPasswordCheck = new CheckBox("Tampilkan Password");
        showPasswordCheck.setStyle("-fx-font-size: 11;");
        showPasswordCheck.setOnAction(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
            }
        });
        
        // Role selector
        Label roleLabel = new Label("Login sebagai:");
        roleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        HBox roleBox = new HBox(15);
        roleBox.setAlignment(Pos.CENTER);
        
        RadioButton kasirRadio = new RadioButton("Kasir");
        kasirRadio.setPrefWidth(100);
        kasirRadio.setStyle("-fx-font-size: 12;");
        kasirRadio.setSelected(true);
        
        RadioButton adminRadio = new RadioButton("Admin");
        adminRadio.setPrefWidth(100);
        adminRadio.setStyle("-fx-font-size: 12;");
        
        ToggleGroup roleGroup = new ToggleGroup();
        kasirRadio.setToggleGroup(roleGroup);
        adminRadio.setToggleGroup(roleGroup);
        
        roleBox.getChildren().addAll(kasirRadio, adminRadio);
        
        // Demo credentials hint
        Label hintLabel = new Label("Demo: username='ismi', password='password123' (Kasir)\n" +
                                   "       username='firly', password='admin123' (Admin)");
        hintLabel.setStyle("-fx-text-fill: #0066cc; -fx-font-size: 10; -fx-wrap-text: true;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button resetButton = new Button("Reset");
        resetButton.setPrefWidth(120);
        resetButton.setPrefHeight(40);
        resetButton.setStyle("-fx-font-size: 14; -fx-background-color: #f44336; -fx-text-fill: white;");
        
        buttonBox.getChildren().addAll(loginButton, resetButton);
        
        // Status message
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #ff6600; -fx-font-size: 11;");
        
        // Event handlers
        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = showPasswordCheck.isSelected() ? passwordVisibleField.getText().trim() : passwordField.getText().trim();
            String role = kasirRadio.isSelected() ? "KASIR" : "ADMIN";
            
            System.out.println("[LOGIN] Username: " + username + ", Role: " + role);
            
            User loggedInUser = authController.handleLogin(username, password, role);
            if (loggedInUser != null) {
                System.out.println("[LOGIN] Login success! User: " + loggedInUser.getFullName() + ", Role: " + loggedInUser.getRole());
                statusLabel.setText("✓ Login berhasil!");
                statusLabel.setStyle("-fx-text-fill: #4CAF50;");
                // Nanti akan pindah ke PosView atau AdminDashboard
                System.out.println("[LOGIN] Calling showPosOrAdminView with user: " + loggedInUser.getFullName());
                showPosOrAdminView(loggedInUser);
            } else {
                System.out.println("[LOGIN] Login failed!");
                statusLabel.setText("✗ Login gagal! Username atau password salah.");
                statusLabel.setStyle("-fx-text-fill: #ff0000;");
                passwordField.clear();
            }
        });
        
        resetButton.setOnAction(event -> {
            usernameField.clear();
            passwordField.clear();
            passwordVisibleField.clear();
            showPasswordCheck.setSelected(false);
            passwordField.setVisible(true);
            passwordVisibleField.setVisible(false);
            kasirRadio.setSelected(true);
            statusLabel.setText("");
        });
        
        // Allow Enter key to login
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                loginButton.fire();
            }
        });
        
        passwordVisibleField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                loginButton.fire();
            }
        });
        
        // Add all components to form
        formBox.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Separator(),
            usernameLabel,
            usernameField,
            passwordLabel,
            new VBox(3, passwordStackPane, showPasswordCheck),
            roleLabel,
            roleBox,
            hintLabel,
            new Separator(),
            buttonBox,
            statusLabel
        );
        
        return formBox;
    }
    
    /**
     * Navigate ke PosView atau AdminDashboard berdasarkan role
     */
    private void showPosOrAdminView(User user) {
        System.out.println("[NAV] showPosOrAdminView called");
        System.out.println("[NAV] User: " + user.getFullName() + ", Role: " + user.getRole());
        System.out.println("[NAV] navCallback is null? " + (navCallback == null));
        
        if (navCallback != null) {
            System.out.println("[NAV] Calling navCallback.onLoginSuccess()");
            navCallback.onLoginSuccess(user);
            System.out.println("[NAV] navCallback executed");
        } else {
            System.out.println("[NAV] navCallback is NULL!");
            if (user.isAdmin()) {
                System.out.println("→ Menampilkan Admin Dashboard");
            } else {
                System.out.println("→ Menampilkan POS View");
            }
        }
    }
    
    /**
     * Show error dialog
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Get scene
     */
    public Scene getScene() {
        return scene;
    }
}
