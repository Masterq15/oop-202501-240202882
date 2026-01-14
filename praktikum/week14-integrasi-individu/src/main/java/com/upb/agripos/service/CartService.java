package com.upb.agripos.service;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CartService - Service Layer untuk Cart Management
 *
 * Implementasi Singleton Pattern (Bab 6):
 * - Satu instance CartService untuk seluruh aplikasi
 * - Mengelola state keranjang belanja global
 *
 * Business Logic:
 * - Validasi produk sebelum ditambah ke keranjang
 * - Validasi stok sebelum checkout
 * - Hitung total dan summary
 * - Reset keranjang
 */
public class CartService {

    private static CartService instance;
    private Cart cart;
    private ProductService productService;

    /**
     * Private constructor untuk Singleton
     */
    private CartService() {
        this.cart = new Cart();
        this.productService = new ProductService();
    }

    /**
     * Get singleton instance
     */
    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    /**
     * Menambah produk ke keranjang dengan validasi
     * Return true jika berhasil, false jika gagal
     */
    public boolean addToCart(Product product, int quantity) {
        // Validasi produk
        if (product == null) {
            showAlert("Error", "Produk tidak boleh null");
            return false;
        }

        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            showAlert("Error", "Kode produk tidak valid");
            return false;
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            showAlert("Error", "Nama produk tidak valid");
            return false;
        }

        if (quantity <= 0) {
            showAlert("Error", "Quantity harus lebih dari 0");
            return false;
        }

        // Validasi stok
        if (quantity > product.getStock()) {
            showAlert("Stok Tidak Cukup",
                "Stok tidak cukup untuk produk: " + product.getName() +
                " (Diminta: " + quantity + ", Tersedia: " + product.getStock() + ")");
            return false;
        }

        // Tambah ke keranjang
        cart.addItem(product, quantity);
        return true;
    }

    /**
     * Menghapus item dari keranjang
     */
    public boolean removeFromCart(String productCode) {
        if (cart.isEmpty()) {
            showAlert("Keranjang Kosong", "Keranjang kosong, tidak ada item yang bisa dihapus");
            return false;
        }

        return cart.removeItem(productCode);
    }

    /**
     * Mengosongkan keranjang
     */
    public void clearCart() {
        cart.clear();
    }

    /**
     * 
     * Business Logic:
     * 1. Validasi keranjang tidak kosong
     * 2. Validasi stok semua item tersedia
     * 3. Update stok di database untuk setiap item (PENTING!)
     * 4. Generate receipt
     * 5. Clear keranjang
     */
    public String checkout() {
        if (cart.isEmpty()) {
            showAlert("Keranjang Kosong", "Keranjang kosong, tidak bisa checkout");
            return null;
        }

        // Validasi stok semua item
        String stockError = cart.validateStock();
        if (stockError != null) {
            showAlert("Stok Tidak Cukup", stockError);
            return null;
        }

        // Hitung total
        double total = cart.calculateTotal();

        // Update stok di database untuk setiap item yang di-checkout
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();
            
            if (!productService.updateStock(product.getCode(), newStock)) {
                showAlert("Error Update Stok", 
                    "Gagal update stok untuk produk: " + product.getName());
                return null;
            }
        }

        // Generate receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== STRUK PEMBELIAN ===\n");
        receipt.append("AGRI-POS System\n");
        receipt.append("Tanggal: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        for (CartItem item : cart.getItems()) {
            receipt.append(String.format("%s (x%d) - Rp %,.0f\n",
                item.getProduct().getName(),
                item.getQuantity(),
                item.getSubtotal()));
        }

        receipt.append("\n");
        receipt.append(String.format("Total: Rp %,.0f\n", total));
        receipt.append("=======================\n");
        receipt.append("Terima Kasih!\n");

        // Kosongkan keranjang setelah checkout berhasil
        cart.clear();

        return receipt.toString();
    }

    /**
     * Validasi checkout
     * Return true jika valid, false jika tidak valid
     */
    public boolean validateCheckout() {
        if (cart.isEmpty()) {
            showAlert("Keranjang Kosong", "Keranjang kosong, tidak bisa checkout");
            return false;
        }

        String stockError = cart.validateStock();
        if (stockError != null) {
            showAlert("Stok Tidak Cukup", stockError);
            return false;
        }
        return true;
    }

    /**
     * Get semua items di keranjang (untuk test compatibility)
     */
    public List<CartItem> getItems() {
        return cart.getItems();
    }

    /**
     * Hitung total harga keranjang (untuk test compatibility)
     */
    public double getTotal() {
        return cart.calculateTotal();
    }

    /**
     * Get jumlah jenis produk di keranjang (untuk test compatibility)
     */
    public int getItemCount() {
        return cart.getTotalItems();
    }

    /**
     * Get total quantity semua produk di keranjang
     */
    public int getTotalQuantity() {
        return cart.getTotalQuantity();
    }

    /**
     * Cek apakah keranjang kosong
     */
    public boolean isEmpty() {
        return cart.isEmpty();
    }

    /**
     * Reset keranjang (untuk testing)
     */
    public void reset() {
        cart.clear();
    }

    /**
     * Get cart object (untuk testing)
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Helper method untuk menampilkan alert
     */
    private void showAlert(String title, String message) {
        // Untuk sekarang, kita print ke console
        // Nanti bisa diintegrasikan dengan JavaFX Alert
        System.err.println("ALERT [" + title + "]: " + message);
    }
}