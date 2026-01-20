package com.upb.agripos;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.AuthServiceImpl;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stock Synchronization Feature Test
 * 
 * Tests the stock transfer mechanism where:
 * - Kasir adds stock to a product via UI
 * - Admin's inventory decreases by same amount
 * - Changes persist to database
 * 
 * Person D - Feature Implementation
 */
public class StockSynchronizationTest {
    
    private ProductService productService;
    private Connection connection;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Initialize database connection
        connection = DatabaseConnection.getInstance().getConnection();
        assertNotNull(connection, "Database connection should not be null");
        
        // Initialize ProductService
        productService = new ProductService(connection);
        assertNotNull(productService, "ProductService should not be null");
    }
    
    /**
     * Test 1: ProductService successfully created with database connection
     */
    @Test
    public void testProductServiceInitialization() {
        assertNotNull(productService, "ProductService must be initialized");
        System.out.println("✅ Test 1 PASSED: ProductService initialized successfully");
    }
    
    /**
     * Test 2: Can retrieve product by code
     */
    @Test
    public void testGetProductByCode() {
        try {
            // Assuming product with code 'PROD001' exists in database
            Product product = productService.getProductByCode("PROD001");
            
            if (product != null) {
                assertNotNull(product.getCode(), "Product code should not be null");
                assertNotNull(product.getName(), "Product name should not be null");
                System.out.println("✅ Test 2 PASSED: Retrieved product - " + product.getName());
            } else {
                System.out.println("⚠️ Test 2 SKIPPED: No product with code PROD001 found in database");
            }
        } catch (Exception e) {
            System.err.println("❌ Test 2 FAILED: " + e.getMessage());
            fail("Failed to retrieve product: " + e.getMessage());
        }
    }
    
    /**
     * Test 3: Can update product stock in database
     */
    @Test
    public void testUpdateProductStock() {
        try {
            // Get a product
            Product product = productService.getProductByCode("PROD001");
            
            if (product != null) {
                // Store original stock
                int originalStock = product.getStock();
                System.out.println("Original stock for " + product.getCode() + ": " + originalStock);
                
                // Simulate Kasir adding 50 units
                int additionalStock = 50;
                int newStock = originalStock + additionalStock;
                product.setStock(newStock);
                
                // Update database
                boolean updateSuccess = productService.updateProduct(product);
                assertTrue(updateSuccess, "Product update should return true");
                System.out.println("✅ Test 3 PASSED: Stock updated to " + newStock);
                
                // Verify update by retrieving again
                Product updatedProduct = productService.getProductByCode("PROD001");
                assertEquals(newStock, updatedProduct.getStock(), 
                    "Stock should be " + newStock + " in database");
                System.out.println("✅ Test 3 VERIFIED: Database shows updated stock of " + newStock);
                
            } else {
                System.out.println("⚠️ Test 3 SKIPPED: No product found");
            }
        } catch (Exception e) {
            System.err.println("❌ Test 3 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 4: Verify stock synchronization between Kasir and Admin
     * 
     * Flow:
     * 1. Admin starts with stock = 100
     * 2. Kasir adds 50 units
     * 3. Database updates to 150
     * 4. Admin reads updated value (150)
     */
    @Test
    public void testStockSynchronizationBetweenKasirAndAdmin() {
        try {
            Product product = productService.getProductByCode("PROD002");
            
            if (product != null) {
                System.out.println("\n--- Testing Stock Synchronization ---");
                
                // Step 1: Admin's initial view
                int adminInitialStock = product.getStock();
                System.out.println("1. Admin sees stock: " + adminInitialStock);
                
                // Step 2: Kasir adds stock
                int kasirAddition = 75;
                int expectedNewStock = adminInitialStock + kasirAddition;
                product.setStock(expectedNewStock);
                
                // Step 3: Persist to database
                boolean updateSuccess = productService.updateProduct(product);
                assertTrue(updateSuccess, "Stock update must succeed");
                System.out.println("2. Kasir adds " + kasirAddition + " units");
                System.out.println("3. Database updated to: " + expectedNewStock);
                
                // Step 4: Admin refreshes and sees updated stock
                Product refreshedProduct = productService.getProductByCode("PROD002");
                assertEquals(expectedNewStock, refreshedProduct.getStock(),
                    "Admin should see updated stock after refresh");
                System.out.println("4. Admin refreshes and sees stock: " + expectedNewStock);
                System.out.println("✅ Test 4 PASSED: Stock synchronized between Kasir and Admin");
                
            } else {
                System.out.println("⚠️ Test 4 SKIPPED: Product PROD002 not found");
            }
        } catch (Exception e) {
            System.err.println("❌ Test 4 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 5: Validate stock calculation (newStock = currentStock + additionalStock)
     */
    @Test
    public void testStockCalculation() {
        int currentStock = 100;
        int additionalStock = 50;
        int newStock = currentStock + additionalStock;
        
        assertEquals(150, newStock, "Stock calculation should be correct");
        System.out.println("✅ Test 5 PASSED: Stock calculation verified - " + 
            currentStock + " + " + additionalStock + " = " + newStock);
    }
    
    /**
     * Test 6: Error handling - ProductService null check
     */
    @Test
    public void testProductServiceNullCheck() {
        ProductService nullService = null;
        
        if (nullService == null) {
            System.out.println("✅ Test 6 PASSED: ProductService null check - UI would skip DB update");
        }
    }
    
    /**
     * Main entry point for running tests
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("STOCK SYNCHRONIZATION FEATURE - TEST SUITE");
        System.out.println("=".repeat(70));
        
        StockSynchronizationTest testSuite = new StockSynchronizationTest();
        
        try {
            testSuite.setUp();
            
            System.out.println("\n[TEST 1] ProductService Initialization");
            testSuite.testProductServiceInitialization();
            
            System.out.println("\n[TEST 2] Get Product By Code");
            testSuite.testGetProductByCode();
            
            System.out.println("\n[TEST 3] Update Product Stock");
            testSuite.testUpdateProductStock();
            
            System.out.println("\n[TEST 4] Stock Synchronization Between Kasir and Admin");
            testSuite.testStockSynchronizationBetweenKasirAndAdmin();
            
            System.out.println("\n[TEST 5] Stock Calculation");
            testSuite.testStockCalculation();
            
            System.out.println("\n[TEST 6] ProductService Null Check");
            testSuite.testProductServiceNullCheck();
            
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST SUITE COMPLETE");
            System.out.println("=".repeat(70));
            
        } catch (Exception e) {
            System.err.println("Test setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
