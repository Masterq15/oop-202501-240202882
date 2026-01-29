package com.upb.agripos;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk CartService
 * Test FR-2: Transaksi Penjualan - Keranjang Belanja
 */
@DisplayName("CartService Tests")
class CartServiceTest {
    private CartService cartService;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
        product1 = new Product(1, "P001", "Pupuk Organik", "Pupuk", 30000, 50);
        product2 = new Product(2, "P002", "Benih Padi", "Benih", 15000, 100);
    }

    @Test
    @DisplayName("TC-01: Tambah item ke keranjang kosong")
    void testAddItemToEmptyCart() {
        cartService.addItem(product1, 2);
        
        assertEquals(1, cartService.getItemCount());
        assertEquals(60000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-02: Tambah item yang sama (update qty)")
    void testAddDuplicateItemUpdatesQuantity() {
        cartService.addItem(product1, 2);
        cartService.addItem(product1, 3);
        
        assertEquals(1, cartService.getItemCount());
        assertEquals(5, cartService.getItems().get(0).getQuantity());
        assertEquals(150000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-03: Tambah multiple items berbeda")
    void testAddMultipleItems() {
        cartService.addItem(product1, 1);
        cartService.addItem(product2, 2);
        
        assertEquals(2, cartService.getItemCount());
        assertEquals(30000 + 30000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-04: Hitung subtotal dengan multiple items")
    void testCalculateSubtotal() {
        cartService.addItem(product1, 2); // 60000
        cartService.addItem(product2, 3); // 45000
        
        assertEquals(105000, cartService.getSubtotal());
        assertEquals(105000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-05: Hapus item dari keranjang")
    void testRemoveItem() throws Exception {
        cartService.addItem(product1, 2);
        cartService.addItem(product2, 1);
        
        cartService.removeItem(product1.getId());
        
        assertEquals(1, cartService.getItemCount());
        assertEquals(15000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-06: Ubah kuantitas item")
    void testUpdateItemQuantity() throws Exception {
        cartService.addItem(product1, 2);
        cartService.updateItemQuantity(product1.getId(), 5);
        
        assertEquals(5, cartService.getItems().get(0).getQuantity());
        assertEquals(150000, cartService.getTotal());
    }

    @Test
    @DisplayName("TC-07: Kosongkan keranjang")
    void testClearCart() {
        cartService.addItem(product1, 2);
        cartService.addItem(product2, 1);
        
        cartService.clear();
        
        assertEquals(0, cartService.getItemCount());
        assertTrue(cartService.isEmpty());
    }

    @Test
    @DisplayName("TC-08: Reject invalid quantity")
    void testRejectInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItem(product1, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItem(product1, -5);
        });
    }

    @Test
    @DisplayName("TC-09: Reject null product")
    void testRejectNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItem(null, 1);
        });
    }

    @Test
    @DisplayName("TC-10: Remove non-existent item throws exception")
    void testRemoveNonExistentItem() {
        cartService.addItem(product1, 1);
        
        Exception exception = assertThrows(Exception.class, () -> {
            cartService.removeItem(999);
        });
        
        assertTrue(exception.getMessage().contains("tidak ditemukan"));
    }
}
