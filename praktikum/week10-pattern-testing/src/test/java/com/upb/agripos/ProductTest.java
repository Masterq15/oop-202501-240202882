package com.upb.agripos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import com.upb.agripos.model.Product;
import com.upb.agripos.config.DatabaseConnection;

public class ProductTest {
    
    private Product product;
    
    @BeforeEach
    public void setUp() {
        // Setup yang dijalankan sebelum setiap test
        product = new Product("P01", "Benih Jagung");
    }
    
    @Test
    @DisplayName("Test Product getName() returns correct name")
    public void testProductName() {
        assertEquals("Benih Jagung", product.getName());
    }
    
    @Test
    @DisplayName("Test Product getCode() returns correct code")
    public void testProductCode() {
        assertEquals("P01", product.getCode());
    }
    
    @Test
    @DisplayName("Test Product and its attributes are not null")
    public void testProductNotNull() {
        assertNotNull(product);
        assertNotNull(product.getCode());
        assertNotNull(product.getName());
    }
    
    @Test
    @DisplayName("Test Singleton pattern returns same instance")
    public void testSingletonPattern() {
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        
        // Assert bahwa kedua referensi adalah instance yang sama
        assertSame(db1, db2);
        assertNotNull(db1);
    }
    
    @Test
    @DisplayName("Test Product creation with different values")
    public void testProductCreation() {
        Product testProduct = new Product("P99", "Test Product");
        assertEquals("P99", testProduct.getCode());
        assertEquals("Test Product", testProduct.getName());
    }
    
    @Test
    @DisplayName("Test Product code cannot be empty")
    public void testProductCodeNotEmpty() {
        assertFalse(product.getCode().isEmpty());
        assertTrue(product.getCode().length() > 0);
    }
    
    @Test
    @DisplayName("Test Product name cannot be empty")
    public void testProductNameNotEmpty() {
        assertFalse(product.getName().isEmpty());
        assertTrue(product.getName().length() > 0);
    }
}