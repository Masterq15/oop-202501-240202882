package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoginView
 * View untuk halaman login Agri-POS
 */
public class LoginView extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label titleLabel;
    private Label errorLabel;

    public LoginView() {
        this.setStyle("-fx-background-color: #f5f5f5;");
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        // Title
        titleLabel = new Label("AGRI-POS WEEK 15");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #2c5f2d;");

        // Subtitle
        Label subtitleLabel = new Label("Sistem Manajemen Penjualan Pertanian");
        subtitleLabel.setFont(Font.font("Segoe UI", 12));
        subtitleLabel.setStyle("-fx-text-fill: #666666;");

        // Form container
        VBox formBox = new VBox();
        formBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: white;");
        formBox.setPadding(new Insets(30));
        formBox.setSpacing(15);
        formBox.setMaxWidth(350);

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Segoe UI", 12));
        usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        usernameField.setStyle("-fx-font-size: 11; -fx-padding: 8;");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Segoe UI", 12));
        passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setStyle("-fx-font-size: 11; -fx-padding: 8;");

        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 11;");
        errorLabel.setWrapText(true);

        // Login button
        loginButton = new Button("LOGIN");
        loginButton.setStyle("-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #2c5f2d; -fx-text-fill: white;");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // Add components to form
        formBox.getChildren().addAll(
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            errorLabel,
            loginButton
        );

        // Add to main layout
        this.getChildren().addAll(titleLabel, subtitleLabel, formBox);
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setText("");
    }
}
