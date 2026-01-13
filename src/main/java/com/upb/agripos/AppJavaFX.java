package com.upb.agripos;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.controller.ProductController;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.ProductFormView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class AppJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=".repeat(60));
        System.out.println("AGRI-POS System - JavaFX GUI");
        System.out.println("Nama: Risky Dimas Nugroho");
        System.out.println("NIM: 240202882");
        System.out.println("Week 12 - GUI Dasar JavaFX");
        System.out.println("=".repeat(60));

        try {
            // 1. Get Database Connection (Singleton)
            DatabaseConnection dbConfig = DatabaseConnection.getInstance();
            Connection connection = dbConfig.getConnection();
            System.out.println("✓ Database connection berhasil");

            // 2. Initialize Service
            ProductService productService = new ProductService(connection);
            System.out.println("✓ ProductService initialized");

            // 3. Initialize Controller
            ProductController productController = new ProductController(productService);
            System.out.println("✓ ProductController initialized");

            // 4. Initialize View
            ProductFormView view = new ProductFormView(productController);
            Scene scene = view.createScene(primaryStage);
            System.out.println("✓ ProductFormView initialized");

            // 5. Setup Stage
            primaryStage.setTitle("AGRI-POS - Manajemen Produk");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("✓ GUI displayed successfully");
            System.out.println("=".repeat(60));

            // Cleanup on close
            primaryStage.setOnCloseRequest(event -> {
                try {
                    connection.close();
                    System.out.println("Database connection closed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.err.println("✗ Error initializing application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}