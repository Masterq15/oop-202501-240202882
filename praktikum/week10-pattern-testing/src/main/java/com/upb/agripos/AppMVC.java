package com.upb.agripos;

import com.upb.agripos.model.Product;
import com.upb.agripos.view.ConsoleView;
import com.upb.agripos.controller.ProductController;
import com.upb.agripos.config.DatabaseConnection;

public class AppMVC {
    public static void main(String[] args) {
        System.out.println("Hello, I am Risky Dimas Nugroho-240202882 (Week10)");
        System.out.println();
        
        // Test Singleton Pattern
        System.out.println("=== Test Singleton Pattern ===");
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        
        System.out.println("db1 == db2: " + (db1 == db2));
        db1.connect();
        System.out.println();
        
        // Test MVC Pattern
        System.out.println("=== Test MVC Pattern ===");
        Product product = new Product("P01", "Pupuk Organik");
        ConsoleView view = new ConsoleView();
        ProductController controller = new ProductController(product, view);
        
        controller.showProduct();
        System.out.println();
        
        Product product2 = new Product("P02", "Benih Padi");
        ProductController controller2 = new ProductController(product2, view);
        controller2.showProduct();
    }
}