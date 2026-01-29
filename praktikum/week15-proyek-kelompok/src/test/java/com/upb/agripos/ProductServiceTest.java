package com.upb.agripos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;

/**
 * Unit Test untuk ProductService
 * Test FR-1: Manajemen Produk
 */
@DisplayName("Product Validation Tests")
class ProductServiceTest {

    @Test
    @DisplayName("TC-19: Validasi kode produk kosong")
    void testEmptyProductCode() {
        ProductService service = new ProductService();
        
        assertThrows(ValidationException.class, () -> {
            service.addProduct("", "Produk Test", "Kategori", 50000, 10);
        });
    }

    @Test
    @DisplayName("TC-20: Validasi harga negatif")
    void testNegativePrice() {
        ProductService service = new ProductService();
        
        assertThrows(ValidationException.class, () -> {
            service.addProduct("P999", "Produk Test", "Kategori", -50000, 10);
        });
    }

    @Test
    @DisplayName("TC-21: Validasi stok negatif")
    void testNegativeStock() {
        ProductService service = new ProductService();
        
        assertThrows(ValidationException.class, () -> {
            service.addProduct("P999", "Produk Test", "Kategori", 50000, -5);
        });
    }

    @Test
    @DisplayName("TC-22: Model - Product toString")
    void testProductToString() {
        Product product = new Product(1, "P001", "Pupuk", "Pupuk", 30000, 50);
        String str = product.toString();
        
        assertTrue(str.contains("P001"));
        assertTrue(str.contains("Pupuk"));
        assertTrue(str.contains("30") && str.contains("000"));
    }

    @Test
    @DisplayName("TC-23: Model - Product equality")
    void testProductEquality() {
        Product p1 = new Product(1, "P001", "Produk A", "Kategori", 50000, 10);
        Product p2 = new Product(1, "P001", "Produk B", "Kategori", 60000, 20);
        
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("TC-24: Model - Product hash consistency")
    void testProductHashConsistency() {
        Product p1 = new Product(1, "P001", "Produk A", "Kategori", 50000, 10);
        Product p2 = new Product(2, "P001", "Produk B", "Kategori", 60000, 20);
        
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
