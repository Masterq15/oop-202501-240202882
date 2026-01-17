package com.upb.agripos.view;

import com.upb.agripos.service.AuthService;
import com.upb.agripos.exception.AuthenticationException;
import com.upb.agripos.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoginView - View untuk halaman login (FR-5)
 * 
 * UI Components: Username field, password field, login button
 * Role-based access untuk KASIR dan ADMIN
 * 
 * Credentials:
 * - KASIR: ismi / password123
 * - ADMIN: firly / admin123
 * 
 * Created by: [Person D - Frontend]
 * Last modified: 2026-01-15
 */
public class LoginView extends VBox {
    
    private AuthService authService;
    
    // UI Components
    private TextField txtUsername;
    private PasswordField txtPassword;
    private Button btnLogin;
    private Label lblMessage;
    private Label lblTitle;
    private Label lblVersion;
    
    // Callback interface untuk login sukses
    public interface LoginCallback {
        void onLoginSuccess(User user);
    }
    
    private LoginCallback loginCallback;
    
    public LoginView(AuthService authService) {
        this.authService = authService;
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        // Title
        lblTitle = new Label("🛒 AGRI-POS");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        lblTitle.setStyle("-fx-text-fill: #2E7D32;");
        
        Label lblSubtitle = new Label("Point of Sale System untuk Pertanian");
        lblSubtitle.setFont(Font.font("Arial", 14));
        lblSubtitle.setStyle("-fx-text-fill: #666666;");
        
        // Username field
        txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        txtUsername.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        txtUsername.setPrefHeight(40);
        
        // Password field
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        txtPassword.setPrefHeight(40);
        
        // Login button
        btnLogin = new Button("🔐 LOGIN");
        btnLogin.setPrefWidth(200);
        btnLogin.setPrefHeight(45);
        btnLogin.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #2E7D32; -fx-text-fill: white;");
        
        // Message label
        lblMessage = new Label();
        lblMessage.setFont(Font.font("Arial", 12));
        lblMessage.setWrapText(true);
        
        // Version label
        lblVersion = new Label("Test Credentials:\n" +
                              "KASIR: ismi / password123\n" +
                              "ADMIN: firly / admin123");
        lblVersion.setFont(Font.font("Arial", 10));
        lblVersion.setStyle("-fx-text-fill: #999999;");
        lblVersion.setWrapText(true);
        
        // Setup button action
        btnLogin.setOnAction(e -> handleLogin());
        
        // Setup enter key untuk submit
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
    }
    
    private void layoutComponents() {
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #FFFFFF;");
        setSpacing(20);
        
        // Center login form
        VBox formContainer = new VBox(15);
        formContainer.setPrefWidth(400);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 30; -fx-border-radius: 10;");
        
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(lblTitle, new Label("Point of Sale System untuk Pertanian"));
        
        VBox inputBox = new VBox(10);
        inputBox.setPrefWidth(Double.MAX_VALUE);
        inputBox.getChildren().addAll(
            new Label("Username:"),
            txtUsername,
            new Label("Password:"),
            txtPassword
        );
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(btnLogin);
        
        formContainer.getChildren().addAll(
            titleBox,
            new Separator(),
            inputBox,
            buttonBox,
            lblMessage,
            new Separator(),
            lblVersion
        );
        
        getChildren().add(formContainer);
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        
        // Clear message
        lblMessage.setText("");
        lblMessage.setStyle("");
        
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password harus diisi!");
            return;
        }
        
        try {
            // Login attempt
            User user = authService.login(username, password);
            
            // Success
            showSuccess("Login berhasil! Selamat datang, " + user.getFullName());
            
            // Clear fields
            txtUsername.clear();
            txtPassword.clear();
            
            // Notify callback dengan delay 1.5 detik agar user bisa lihat success message
            if (loginCallback != null) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1500); // Tunggu 1.5 detik
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    javafx.application.Platform.runLater(() -> {
                        loginCallback.onLoginSuccess(user);
                    });
                }).start();
            }
        } catch (AuthenticationException ex) {
            showError(ex.getMessage());
        }
    }
    
    private void showError(String message) {
        lblMessage.setText("❌ " + message);
        lblMessage.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
    }
    
    private void showSuccess(String message) {
        lblMessage.setText("✅ " + message);
        lblMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-weight: bold;");
    }
    
    // Setter untuk callback
    public void setLoginCallback(LoginCallback callback) {
        this.loginCallback = callback;
    }
    
    // Getter methods
    public TextField getTxtUsername() {
        return txtUsername;
    }
    
    public PasswordField getTxtPassword() {
        return txtPassword;
    }
    
    public Button getBtnLogin() {
        return btnLogin;
    }
}
