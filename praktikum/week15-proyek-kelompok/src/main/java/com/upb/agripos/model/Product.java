package com.upb.agripos.model;

/**
 * Product Model
 * 
 * Attributes:
 * - code: kode produk (PK)
 * - name: nama produk
 * - category: kategori produk
 * - price: harga jual
 * - stock: stok tersedia
 * - reorderLevel: stok minimum (OFR-4)
 * - status: NORMAL / LOW_STOCK / DISCONTINUED (OFR-4)
 * 
 * Created by: [Person A - Database Layer]
 * Last modified: 
 */
public class Product {
    private String code;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int reorderLevel;    // OFR-4
    private String status;       // OFR-4: NORMAL, LOW_STOCK, DISCONTINUED

    // Constructor
    public Product(String code, String name, String category, double price, int stock) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.reorderLevel = 10;  // Default
        this.status = "NORMAL";   // Default
    }

    // Constructor dengan reorder level
    public Product(String code, String name, String category, double price, int stock, int reorderLevel) {
        this(code, name, category, price, stock);
        this.reorderLevel = reorderLevel;
        updateStatus();
    }

    // Getters & Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { 
        this.stock = stock;
        updateStatus();  // Update status saat stok berubah
    }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Helper: Update status berdasarkan stok
    private void updateStatus() {
        if (stock <= 0) {
            this.status = "DISCONTINUED";
        } else if (stock <= reorderLevel) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "NORMAL";
        }
    }

    @Override
    public String toString() {
        return code + " - " + name + " (" + category + ") | Rp " + price + " | Stok: " + stock + " | Status: " + status;
    }
}
