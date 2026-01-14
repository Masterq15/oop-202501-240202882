package com.upb.agripos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart - Model Keranjang Belanja
 * 
 * Implementasi Collections (Bab 7):
 * - Menggunakan List<CartItem> untuk menyimpan item
 * - Menyediakan operasi tambah, hapus, clear, dan hitung total
 * 
 * Sesuai Class Diagram Bab 6:
 * - Cart memiliki relasi 1-to-many dengan CartItem
 * - Cart bertanggung jawab mengelola koleksi item belanja
 */
public class Cart {
    
    private List<CartItem> items;
    
    public Cart() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Menambah item ke keranjang
     * Jika produk sudah ada, quantity akan ditambahkan
     */
    public void addItem(Product product, int quantity) {
        // Cek apakah produk sudah ada di keranjang
        CartItem existingItem = findItemByCode(product.getCode());
        
        if (existingItem != null) {
            // Jika sudah ada, tambahkan quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Jika belum ada, buat CartItem baru
            CartItem newItem = new CartItem(product, quantity);
            items.add(newItem);
        }
    }
    
    /**
     * Menghapus item dari keranjang berdasarkan kode produk
     */
    public boolean removeItem(String productCode) {
        CartItem item = findItemByCode(productCode);
        if (item != null) {
            items.remove(item);
            return true;
        }
        return false;
    }
    
    /**
     * Update quantity item di keranjang
     */
    public boolean updateQuantity(String productCode, int newQuantity) {
        CartItem item = findItemByCode(productCode);
        if (item != null) {
            if (newQuantity <= 0) {
                return removeItem(productCode);
            } else {
                item.setQuantity(newQuantity);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Mencari item berdasarkan kode produk
     */
    private CartItem findItemByCode(String productCode) {
        for (CartItem item : items) {
            if (item.getProduct().getCode().equals(productCode)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Menghitung total harga keranjang
     * Implementasi dari requirement Bab 7: Hitung total belanja
     */
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    /**
     * Menghitung total item (jumlah jenis produk)
     */
    public int getTotalItems() {
        return items.size();
    }
    
    /**
     * Menghitung total quantity (jumlah barang)
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    /**
     * Mengosongkan keranjang
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Cek apakah keranjang kosong
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Get semua items (untuk ditampilkan di GUI)
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items); // Return copy untuk encapsulation
    }
    
    /**
     * Validasi stok sebelum checkout
     * Return null jika stok cukup, return error message jika tidak cukup
     */
    public String validateStock() {
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                return "Stok tidak cukup untuk produk: " + item.getProduct().getName() +
                       " (Diminta: " + item.getQuantity() + ", Tersedia: " + item.getProduct().getStock() + ")";
            }
        }
        return null; // Valid
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== KERANJANG BELANJA ===\n");
        
        if (isEmpty()) {
            sb.append("Keranjang kosong\n");
        } else {
            for (CartItem item : items) {
                sb.append(item.toString()).append("\n");
            }
            sb.append("------------------------\n");
            sb.append(String.format("Total: Rp %.2f\n", calculateTotal()));
            sb.append(String.format("Total Item: %d jenis\n", getTotalItems()));
            sb.append(String.format("Total Quantity: %d pcs\n", getTotalQuantity()));
        }
        
        return sb.toString();
    }
}