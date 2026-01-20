package com.upb.agripos.dao.impl;

import com.upb.agripos.model.Product;
import com.upb.agripos.util.DatabaseConnection;
import java.util.List;
import java.util.Map;

/**
 * DatabaseIntegrationTest - Demonstrasi integrasi dengan tugas PersonB
 * Person A - DATABASE MASTER
 * 
 * Test class untuk mendemonstrasikan:
 * 1. Connection pooling
 * 2. Product CRUD operations
 * 3. User management
 * 4. Integrasi dengan discount strategy PersonB
 */
public class DatabaseIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== AgriPOS Database Integration Test ===\n");
        
        // Test 1: Connection Pool
        testConnectionPool();
        
        // Test 2: Product Operations
        testProductOperations();
        
        // Test 3: User Operations
        testUserOperations();
        
        // Test 4: Stock Management
        testStockManagement();
        
        // Test 5: Integration dengan PersonB
        testDiscountIntegration();
        
        System.out.println("\n=== All Tests Completed ===");
        
        // Cleanup
        DatabaseConnection.getInstance().closeConnection();
    }
    
    /**
     * Test 1: Cek connection pool
     */
    private static void testConnectionPool() {
        System.out.println("TEST 1: Database Connection Status");
        System.out.println("------------------------------");
        
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connection successful");
        } else {
            System.out.println("✗ Database connection failed");
        }
        System.out.println();
    }
    
    /**
     * Test 2: Product CRUD operations
     */
    private static void testProductOperations() {
        System.out.println("TEST 2: Product CRUD Operations");
        System.out.println("------------------------------");
        
        ProductDAOImpl productDao = new ProductDAOImpl();
        
        // Ambil semua produk
        List<Product> allProducts = productDao.findAll();
        System.out.println("Total products in database: " + allProducts.size());
        System.out.println("Sample products:");
        for (int i = 0; i < Math.min(3, allProducts.size()); i++) {
            Product p = allProducts.get(i);
            System.out.println("  - " + p.toString());
        }
        
        // Cari by ID
        Product product = productDao.findById("1");
        if (product != null) {
            System.out.println("\nProduct found by ID:");
            System.out.println("  Name: " + product.getName());
            System.out.println("  Price: Rp " + product.getPrice());
            System.out.println("  Stock: " + product.getStock());
        }
        
        System.out.println();
    }
    
    /**
     * Test 3: User Management
     */
    private static void testUserOperations() {
        System.out.println("TEST 3: User Management");
        System.out.println("------------------------------");
        
        UserDAOImpl userDao = new UserDAOImpl();
        
        // Ambil semua user
        List<UserDAOImpl.User> allUsers = userDao.findAll();
        System.out.println("Total users in database: " + allUsers.size());
        System.out.println("Users by role:");
        
        for (String role : new String[]{"ADMIN", "MANAGER", "CASHIER"}) {
            List<UserDAOImpl.User> usersInRole = userDao.findByRole(role);
            System.out.println("  " + role + ": " + usersInRole.size() + " user(s)");
        }
        
        // Test authentication
        UserDAOImpl.User user = userDao.findByUsername("cashier1");
        if (user != null) {
            System.out.println("\nUser found:");
            System.out.println("  Username: " + user.getUsername());
            System.out.println("  Full Name: " + user.getFullName());
            System.out.println("  Role: " + user.getRole());
            System.out.println("  Status: " + (user.isActive() ? "Active" : "Inactive"));
        }
        
        // User statistics
        Map<String, Object> stats = userDao.getUserStatistics();
        System.out.println("\nUser Statistics:");
        System.out.println("  Total: " + stats.get("totalUsers"));
        System.out.println("  Active: " + stats.get("activeUsers"));
        System.out.println("  Admins: " + stats.get("adminCount"));
        System.out.println("  Managers: " + stats.get("managerCount"));
        System.out.println("  Cashiers: " + stats.get("cashierCount"));
        
        System.out.println();
    }
    
    /**
     * Test 4: Stock Management
     */
    private static void testStockManagement() {
        System.out.println("TEST 4: Stock Management");
        System.out.println("------------------------------");
        
        ProductDAOImpl productDao = new ProductDAOImpl();
        
        // Cari produk dengan stok rendah
        List<Product> lowStockProducts = productDao.findLowStockProducts();
        System.out.println("Products with low stock: " + lowStockProducts.size());
        for (Product p : lowStockProducts) {
            System.out.println("  - " + p.getName() + " (Stock: " + p.getStock() + ")");
        }
        
        // Cek stok availability
        System.out.println("\nStock availability check:");
        String testProductId = "1";
        if (productDao.isStockAvailable(testProductId, 5)) {
            System.out.println("  ✓ Product ID " + testProductId + " has enough stock for 5 units");
        } else {
            System.out.println("  ✗ Product ID " + testProductId + " does NOT have enough stock for 5 units");
        }
        
        System.out.println();
    }
    
    /**
     * Test 5: Integration dengan PersonB (Discount Strategy)
     */
    private static void testDiscountIntegration() {
        System.out.println("TEST 5: Integration dengan PersonB (Discount Strategy)");
        System.out.println("-----------------------------------------------------");
        
        ProductDAOImpl productDao = new ProductDAOImpl();
        
        System.out.println("Integration Points:");
        System.out.println("  1. Product model terintegrasi dengan PersonB Product");
        System.out.println("  2. Database support untuk DiscountStrategyPersonB:");
        System.out.println("     - FIXED discount type");
        System.out.println("     - PERCENTAGE discount type");
        System.out.println("  3. Product stock management untuk cart operations");
        System.out.println();
        
        System.out.println("Sample products for PersonB:");
        List<Product> products = productDao.findAll();
        for (int i = 0; i < Math.min(3, products.size()); i++) {
            Product p = products.get(i);
            System.out.println("  - ID: " + p.getCode() + 
                             ", Name: " + p.getName() + 
                             ", Price: Rp " + String.format("%.2f", p.getPrice()));
        }
        
        System.out.println();
    }
}
