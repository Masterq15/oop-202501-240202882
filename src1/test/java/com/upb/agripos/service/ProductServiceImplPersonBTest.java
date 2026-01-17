package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk ProductServiceImpl PersonB
 * Menguji semua operasi CRUD dan validasi
 * Person B - SERVICE & DISCOUNT
 */
@DisplayName("ProductService PersonB Tests")
class ProductServiceImplPersonBTest {
    private ProductServicePersonB productService;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImplPersonB();
        sampleProduct = new Product("PRD-001", "Benih Padi", 25000, 100, "Benih");
    }

    // ==================== ADD PRODUCT TESTS ====================

    @Test
    @DisplayName("Should add product successfully with valid data")
    void testAddProductSuccess() {
        productService.addProduct(sampleProduct);
        
        Product retrieved = productService.getProduct("PRD-001");
        assertNotNull(retrieved);
        assertEquals("PRD-001", retrieved.getProductId());
        assertEquals("Benih Padi", retrieved.getName());
        assertEquals(25000, retrieved.getPrice());
        assertEquals(100, retrieved.getStock());
        assertEquals("Benih", retrieved.getCategory());
    }

    @Test
    @DisplayName("Should throw exception when adding null product")
    void testAddProductNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when product ID is empty")
    void testAddProductEmptyId() {
        Product product = new Product("", "Benih Padi", 25000, 100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when product name is empty")
    void testAddProductEmptyName() {
        Product product = new Product("PRD-001", "", 25000, 100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when product price is negative")
    void testAddProductNegativePrice() {
        Product product = new Product("PRD-001", "Benih Padi", -25000, 100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when product stock is negative")
    void testAddProductNegativeStock() {
        Product product = new Product("PRD-001", "Benih Padi", 25000, -100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when product category is empty")
    void testAddProductEmptyCategory() {
        Product product = new Product("PRD-001", "Benih Padi", 25000, 100, "");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate product ID")
    void testAddProductDuplicateId() {
        productService.addProduct(sampleProduct);
        
        Product duplicate = new Product("PRD-001", "Benih Jagung", 30000, 50, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(duplicate);
        });
    }

    // ==================== GET PRODUCT TESTS ====================

    @Test
    @DisplayName("Should get product by ID successfully")
    void testGetProductSuccess() {
        productService.addProduct(sampleProduct);
        
        Product retrieved = productService.getProduct("PRD-001");
        assertNotNull(retrieved);
        assertEquals("Benih Padi", retrieved.getName());
    }

    @Test
    @DisplayName("Should return null when product ID not found")
    void testGetProductNotFound() {
        Product retrieved = productService.getProduct("PRD-999");
        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should return null when product ID is empty")
    void testGetProductEmptyId() {
        Product retrieved = productService.getProduct("");
        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should return null when product ID is null")
    void testGetProductNullId() {
        Product retrieved = productService.getProduct(null);
        assertNull(retrieved);
    }

    // ==================== UPDATE PRODUCT TESTS ====================

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProductSuccess() {
        productService.addProduct(sampleProduct);
        
        Product updated = new Product("PRD-001", "Benih Padi Premium", 30000, 150, "Benih");
        productService.updateProduct(updated);
        
        Product retrieved = productService.getProduct("PRD-001");
        assertEquals("Benih Padi Premium", retrieved.getName());
        assertEquals(30000, retrieved.getPrice());
        assertEquals(150, retrieved.getStock());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        Product product = new Product("PRD-999", "Benih Padi", 25000, 100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(product);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with null product")
    void testUpdateProductNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid data")
    void testUpdateProductInvalidPrice() {
        productService.addProduct(sampleProduct);
        
        Product updated = new Product("PRD-001", "Benih Padi", -10000, 100, "Benih");
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(updated);
        });
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProductSuccess() {
        productService.addProduct(sampleProduct);
        
        productService.deleteProduct("PRD-001");
        
        Product retrieved = productService.getProduct("PRD-001");
        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDeleteProductNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct("PRD-999");
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting with empty ID")
    void testDeleteProductEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct("");
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting with null ID")
    void testDeleteProductNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct(null);
        });
    }

    // ==================== GET ALL PRODUCTS TESTS ====================

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProductsSuccess() {
        productService.addProduct(sampleProduct);
        productService.addProduct(new Product("PRD-002", "Benih Jagung", 30000, 80, "Benih"));
        productService.addProduct(new Product("PRD-003", "Pupuk", 50000, 200, "Pupuk"));
        
        List<Product> products = productService.getAllProducts();
        assertEquals(3, products.size());
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testGetAllProductsEmpty() {
        List<Product> products = productService.getAllProducts();
        assertTrue(products.isEmpty());
    }

    @Test
    @DisplayName("Should not modify original list when getting all products")
    void testGetAllProductsImmutable() {
        productService.addProduct(sampleProduct);
        
        List<Product> products1 = productService.getAllProducts();
        products1.add(new Product("PRD-002", "Benih Jagung", 30000, 80, "Benih"));
        
        List<Product> products2 = productService.getAllProducts();
        assertEquals(1, products2.size());
    }

    // ==================== GET PRODUCTS BY CATEGORY TESTS ====================

    @Test
    @DisplayName("Should get products by category successfully")
    void testGetProductsByCategorySuccess() {
        productService.addProduct(sampleProduct);
        productService.addProduct(new Product("PRD-002", "Benih Jagung", 30000, 80, "Benih"));
        productService.addProduct(new Product("PRD-003", "Pupuk Urea", 50000, 200, "Pupuk"));
        
        List<Product> benih = productService.getProductsByCategory("Benih");
        assertEquals(2, benih.size());
        
        List<Product> pupuk = productService.getProductsByCategory("Pupuk");
        assertEquals(1, pupuk.size());
    }

    @Test
    @DisplayName("Should return empty list when category not found")
    void testGetProductsByCategoryNotFound() {
        productService.addProduct(sampleProduct);
        
        List<Product> products = productService.getProductsByCategory("Pestisida");
        assertTrue(products.isEmpty());
    }

    @Test
    @DisplayName("Should handle case-insensitive category search")
    void testGetProductsByCategoryCaseInsensitive() {
        productService.addProduct(sampleProduct);
        
        List<Product> products1 = productService.getProductsByCategory("benih");
        List<Product> products2 = productService.getProductsByCategory("BENIH");
        
        assertEquals(1, products1.size());
        assertEquals(1, products2.size());
    }

    @Test
    @DisplayName("Should return empty list for null category")
    void testGetProductsByCategoryNull() {
        productService.addProduct(sampleProduct);
        
        List<Product> products = productService.getProductsByCategory(null);
        assertTrue(products.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for empty category")
    void testGetProductsByCategoryEmpty() {
        productService.addProduct(sampleProduct);
        
        List<Product> products = productService.getProductsByCategory("");
        assertTrue(products.isEmpty());
    }
}
