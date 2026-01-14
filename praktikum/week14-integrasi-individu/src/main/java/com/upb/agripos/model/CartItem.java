package com.upb.agripos.model;

/**
 * CartItem - Model Item di Keranjang
 * 
 * Merepresentasikan satu item belanja yang berisi:
 * - Produk (Product)
 * - Quantity (jumlah yang dibeli)
 * - Subtotal (harga x quantity)
 * 
 * Sesuai Class Diagram Bab 6:
 * - CartItem memiliki relasi many-to-one dengan Product
 * - CartItem menghitung subtotal berdasarkan harga produk
 */
public class CartItem {
    
    private Product product;
    private int quantity;
    
    /**
     * Constructor
     */
    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product tidak boleh null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0");
        }
        
        this.product = product;
        this.quantity = quantity;
    }
    
    /**
     * Menghitung subtotal (harga × quantity)
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
    
    /**
     * Validasi apakah quantity tidak melebihi stok
     */
    public boolean isValidStock() {
        return quantity <= product.getStock();
    }
    
    // Getters
    public Product getProduct() {
        return product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    // Setters
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity tidak boleh negatif");
        }
        this.quantity = quantity;
    }
    
    /**
     * Increment quantity
     */
    public void incrementQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount harus positif");
        }
        this.quantity += amount;
    }
    
    /**
     * Decrement quantity
     */
    public void decrementQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount harus positif");
        }
        if (this.quantity - amount < 0) {
            throw new IllegalArgumentException("Quantity tidak boleh negatif");
        }
        this.quantity -= amount;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s x %d @ Rp %.2f = Rp %.2f",
            product.getCode(),
            product.getName(),
            quantity,
            product.getPrice(),
            getSubtotal()
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CartItem cartItem = (CartItem) obj;
        return product.getCode().equals(cartItem.product.getCode());
    }
    
    @Override
    public int hashCode() {
        return product.getCode().hashCode();
    }
}