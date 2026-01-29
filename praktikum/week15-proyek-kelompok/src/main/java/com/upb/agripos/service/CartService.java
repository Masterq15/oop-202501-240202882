package com.upb.agripos.service;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * CartService
 * Service untuk mengelola keranjang belanja
 * 
 * FR-2: Transaksi Penjualan
 * - Tambah produk ke keranjang, ubah qty, hapus item keranjang
 * - Hitung total belanja dari isi keranjang
 */
public class CartService {
    private List<CartItem> items;

    public CartService() {
        this.items = new ArrayList<>();
    }

    /**
     * FR-2: Tambah produk ke keranjang atau update qty jika sudah ada
     */
    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Product dan quantity harus valid");
        }

        // Cek apakah produk sudah ada di keranjang
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Jika belum ada, tambah item baru
        items.add(new CartItem(product, quantity));
    }

    /**
     * FR-2: Ubah kuantitas item dalam keranjang
     */
    public void updateItemQuantity(int productId, int newQuantity) throws Exception {
        if (newQuantity < 0) {
            throw new Exception("Kuantitas tidak boleh negatif");
        }

        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                if (newQuantity == 0) {
                    removeItem(productId);
                } else {
                    item.setQuantity(newQuantity);
                }
                return;
            }
        }

        throw new Exception("Item tidak ditemukan di keranjang");
    }

    /**
     * FR-2: Hapus item dari keranjang
     */
    public void removeItem(int productId) throws Exception {
        boolean removed = items.removeIf(item -> item.getProduct().getId() == productId);
        if (!removed) {
            throw new Exception("Item tidak ditemukan di keranjang");
        }
    }

    /**
     * FR-2: Dapatkan semua item dalam keranjang
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * FR-2: Hitung subtotal (total sebelum diskon)
     */
    public double getSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    /**
     * FR-2: Hitung total (setelah diskon jika ada)
     */
    public double getTotal() {
        return getSubtotal();
    }

    /**
     * Kosongkan keranjang
     */
    public void clear() {
        items.clear();
    }

    /**
     * Dapatkan jumlah item unik dalam keranjang
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Cek apakah keranjang kosong
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
