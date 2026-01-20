package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.User;
import com.upb.agripos.service.ProductService;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * KasirView - Point of Sale Interface untuk Role KASIR
 * 
 * Class ini menyediakan interface khusus untuk Kasir dengan fitur:
 * - Product selection dan cart management
 * - Transaction processing dan payment
 * - Receipt generation
 * - Real-time inventory updates
 * 
 * Week 15 - Proyek Kelompok
 * Person D - Frontend/UI
 */
public class KasirView {
    
    private Stage stage;
    private AuthController authController;
    private User currentUser;
    private ProductService productService;
    private PosView posView;
    private Scene scene;
    
    // Menggunakan LogoutCallback dari PosView
    public static void setNavCallback(PosView.LogoutCallback callback) {
        PosView.setNavCallback(callback);
    }
    
    /**
     * Constructor untuk KasirView
     */
    public KasirView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.currentUser = authController.getCurrentUser();
    }
    
    /**
     * Set ProductService untuk database operations
     */
    public void setProductService(ProductService productService) {
        this.productService = productService;
        if (this.posView != null) {
            this.posView.setProductService(productService);
        }
    }
    
    /**
     * Create dan return KasirView scene dengan delegasi ke PosView
     */
    public Scene createScene() {
        // Delegate to PosView untuk actual UI rendering
        this.posView = new PosView(stage, authController);
        
        if (this.productService != null) {
            this.posView.setProductService(productService);
        }
        
        // Create scene dari PosView
        this.scene = this.posView.createScene();
        
        System.out.println("✓ KasirView scene created");
        return this.scene;
    }
    
    /**
     * Get the underlying PosView instance
     */
    public PosView getPosView() {
        return this.posView;
    }
    
    /**
     * Get current scene
     */
    public Scene getScene() {
        return this.scene;
    }
    
    /**
     * Get current user
     */
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    /**
     * Validate kasir role
     */
    public boolean isKasir() {
        return currentUser != null && "KASIR".equalsIgnoreCase(currentUser.getRole());
    }
    
    /**
     * Refresh product list dari database
     */
    public void refreshProducts() {
        if (this.posView != null) {
            this.posView.refreshProductList();
        }
    }
}
