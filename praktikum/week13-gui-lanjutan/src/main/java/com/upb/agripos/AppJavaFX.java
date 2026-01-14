package com.upb.agripos;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.view.ProductTableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AppJavaFX - Main Application
 * Week 13: GUI Lanjutan dengan TableView dan Lambda Expression
 * 
 * Integrasi lengkap:
 * - Week 11: DAO + JDBC
 * - Week 12: GUI Dasar
 * - Week 13: TableView + Lambda
 */
public class AppJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inisialisasi View (TableView)
            ProductTableView view = new ProductTableView();
            
            // Inisialisasi Controller dengan View
            // Controller akan:
            // 1. Setup event handlers dengan lambda expression
            // 2. Load data awal dari database via Service → DAO
            ProductController controller = new ProductController(view);
            
            // Setup Scene
            Scene scene = new Scene(view, 600, 650);
            
            // Setup Stage
            primaryStage.setTitle("Agri-POS - Kelola Produk (Week 13 - TableView)");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("  AGRI-POS Week 13 - TableView + Lambda");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("✓ GUI berhasil dimuat");
            System.out.println("✓ Event handlers menggunakan lambda expression");
            System.out.println("✓ Data terintegrasi dengan PostgreSQL");
            System.out.println("═══════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.err.println("✗ Error saat menjalankan aplikasi:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}