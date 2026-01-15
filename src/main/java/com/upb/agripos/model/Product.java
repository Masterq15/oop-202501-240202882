package com.upb.agripos.model;

/**
 * Product Model - Collaborative Project Week 15
 * Attributes:
 * - code: kode produk (PK)
 * - name: nama produk
 * - category: kategori produk
 * - price: harga jual
 * - stock: stok tersedia
 */
public class Product {
    private String code;
    private String name;
    private String category;
    private double price;
    private int stock;

    // Constructor for insert (tanpa ID dari DB)
    public Product(String code, String name, String category, double price, int stock) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    // Setters
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return code + " - " + name + " (" + category + ") - Rp " + price + ", Stok: " + stock;
    }
}