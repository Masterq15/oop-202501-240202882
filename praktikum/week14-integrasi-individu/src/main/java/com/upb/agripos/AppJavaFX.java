package com.upb.agripos;

import com.upb.agripos.controller.PosController;
import com.upb.agripos.view.PosView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AppJavaFX - Main Application
 * Week 14: Integrasi Individu - POS System Lengkap
 *
 * Integrasi lengkap dari semua modul:
 * - Week 11: DAO + JDBC Database
 * - Week 12: GUI Dasar
 * - Week 13: TableView + Lambda Expression
 * - Week 14: POS Integration + Cart Management
 *
 * Features:
 * - Product Management (CRUD)
 * - Shopping Cart dengan Collections
 * - Checkout System
 * - Exception Handling
 * - Unit Testing
 */
public class AppJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inisialisasi View (POS System)
            PosView view = new PosView();

            // Inisialisasi Controller dengan View
            // Controller akan:
            // 1. Setup event handlers dengan lambda expression
            // 2. Load data awal dari database via Service → DAO
            // 3. Manage cart operations
            PosController controller = new PosController(view);

            // Setup Scene
            Scene scene = new Scene(view, 900, 700);

            // Setup Stage
            primaryStage.setTitle("🛒 AGRI-POS - Point of Sale System (Week 14)");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

            System.out.println("═══════════════════════════════════════════════");
            System.out.println("     🛒 AGRI-POS Week 14 - POS Integration");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("✓ GUI berhasil dimuat dengan PosView");
            System.out.println("✓ Event handlers menggunakan lambda expression");
            System.out.println("✓ Product Management + Shopping Cart terintegrasi");
            System.out.println("✓ Data terintegrasi dengan PostgreSQL");
            System.out.println("✓ Exception handling dan validation");
            System.out.println("═══════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("✗ Error saat menjalankan aplikasi:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}