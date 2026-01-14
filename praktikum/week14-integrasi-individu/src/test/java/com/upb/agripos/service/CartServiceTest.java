package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.exception.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * CartServiceTest - Unit Test untuk CartService
 *
 * Implementasi Bab 10: Unit Testing
 *
 * Test Coverage:
 * - Tambah item ke keranjang
 * - Hapus item dari keranjang
 * - Hitung total
 * - Validasi stok
 * - Checkout flow
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartServiceTest {

    private CartService cartService;
    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void setUp() {
        // Get singleton instance
        cartService = CartService.getInstance();

        // Reset cart sebelum setiap test
        cartService.reset();

        // Setup sample products
        product1 = new Product("P01", "Pupuk Organik", 30000, 10);
        product2 = new Product("P02", "Benih Padi", 15000, 50);
        product3 = new Product("P03", "Pestisida", 75000, 5);
    }

    @Test
    @Order(1)
    @DisplayName("Test: Tambah item ke keranjang berhasil")
    public void testAddToCart_Success() throws Exception {
        // Act
        cartService.addToCart(product1, 2);

        // Assert
        assertFalse(cartService.isEmpty(), "Keranjang tidak boleh kosong");
        assertEquals(1, cartService.getItemCount(), "Harus ada 1 jenis item");
        assertEquals(60000, cartService.getTotal(), 0.01, "Total harus 60000");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Tambah multiple items ke keranjang")
    public void testAddToCart_MultipleItems() throws Exception {
        // Act
        cartService.addToCart(product1, 2);  // 60000
        cartService.addToCart(product2, 5);  // 75000
        cartService.addToCart(product3, 1);  // 75000

        // Assert
        assertEquals(3, cartService.getItemCount(), "Harus ada 3 jenis item");
        assertEquals(210000, cartService.getTotal(), 0.01, "Total harus 210000");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Tambah item yang sama menambah quantity")
    public void testAddToCart_SameProductIncreasesQuantity() throws Exception {
        // Act
        cartService.addToCart(product1, 2);
        cartService.addToCart(product1, 3);  // Total qty = 5

        // Assert
        assertEquals(1, cartService.getItemCount(), "Harus tetap 1 jenis item");

        List<CartItem> items = cartService.getItems();
        CartItem item = items.get(0);

        assertEquals(5, item.getQuantity(), "Quantity harus 5");
        assertEquals(150000, item.getSubtotal(), 0.01, "Subtotal harus 150000");
    }

    @Test
    @Order(4)
    @DisplayName("Test: Tambah dengan quantity invalid throws exception")
    public void testAddToCart_InvalidQuantity_ThrowsException() {
        // Assert & Act
        assertThrows(InvalidProductException.class, () -> {
            cartService.addToCart(product1, 0);
        }, "Harus throw InvalidProductException untuk quantity 0");

        assertThrows(InvalidProductException.class, () -> {
            cartService.addToCart(product1, -5);
        }, "Harus throw InvalidProductException untuk quantity negatif");
    }

    @Test
    @Order(5)
    @DisplayName("Test: Tambah melebihi stok throws exception")
    public void testAddToCart_InsufficientStock_ThrowsException() {
        // Assert & Act
        InsufficientStockException exception = assertThrows(
            InsufficientStockException.class,
            () -> cartService.addToCart(product3, 10),  // Stok hanya 5
            "Harus throw InsufficientStockException"
        );

        assertTrue(exception.getMessage().contains("Stok tidak cukup"));
    }

    @Test
    @Order(6)
    @DisplayName("Test: Hapus item dari keranjang")
    public void testRemoveFromCart() throws Exception {
        // Arrange
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 3);

        // Act
        boolean removed = cartService.removeFromCart("P01");

        // Assert
        assertTrue(removed, "Remove harus berhasil");
        assertEquals(1, cartService.getItemCount(), "Harus tersisa 1 item");
        assertEquals(45000, cartService.getTotal(), 0.01, "Total harus 45000");
    }

    @Test
    @Order(7)
    @DisplayName("Test: Hapus item yang tidak ada")
    public void testRemoveFromCart_NonExistent() throws Exception {
        // Arrange
        cartService.addToCart(product1, 2);

        // Act
        boolean removed = cartService.removeFromCart("P99");

        // Assert
        assertFalse(removed, "Remove harus return false");
        assertEquals(1, cartService.getItemCount(), "Item count tidak berubah");
    }

    @Test
    @Order(8)
    @DisplayName("Test: Clear keranjang")
    public void testClearCart() throws Exception {
        // Arrange
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 3);

        // Act
        cartService.clearCart();

        // Assert
        assertTrue(cartService.isEmpty(), "Keranjang harus kosong");
        assertEquals(0, cartService.getItemCount());
        assertEquals(0, cartService.getTotal(), 0.01);
    }

    @Test
    @Order(9)
    @DisplayName("Test: Validate checkout dengan keranjang kosong")
    public void testValidateCheckout_EmptyCart_ThrowsException() {
        // Assert & Act
        assertThrows(EmptyCartException.class, () -> {
            cartService.validateCheckout();
        }, "Harus throw EmptyCartException");
    }

    @Test
    @Order(10)
    @DisplayName("Test: Checkout berhasil")
    public void testCheckout_Success() throws Exception {
        // Arrange
        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 3);

        // Act
        String receipt = cartService.checkout();

        // Assert
        assertNotNull(receipt, "Receipt tidak boleh null");
        assertTrue(receipt.contains("AGRI-POS RECEIPT"), "Receipt harus ada header");
        assertTrue(receipt.contains("Pupuk Organik"), "Receipt harus ada nama produk");
        assertTrue(receipt.contains("105,000"), "Receipt harus ada total"); // 60k + 45k = 105k
    }

    @Test
    @Order(11)
    @DisplayName("Test: Singleton pattern - same instance")
    public void testSingleton_SameInstance() {
        // Act
        CartService instance1 = CartService.getInstance();
        CartService instance2 = CartService.getInstance();

        // Assert
        assertSame(instance1, instance2, "Harus return instance yang sama");
    }

    @Test
    @Order(12)
    @DisplayName("Test: Calculate total dengan multiple items")
    public void testCalculateTotal() throws Exception {
        // Arrange & Act
        cartService.addToCart(product1, 2);  // 60000
        cartService.addToCart(product2, 5);  // 75000
        cartService.addToCart(product3, 1);  // 75000

        double total = cartService.getTotal();

        // Assert
        assertEquals(210000, total, 0.01, "Total calculation salah");
    }

    @AfterEach
    public void tearDown() {
        // Clean up setelah setiap test
        cartService.reset();
    }

    @AfterAll
    public static void tearDownAll() {
        System.out.println("✓ Semua test CartService selesai");
    }
}