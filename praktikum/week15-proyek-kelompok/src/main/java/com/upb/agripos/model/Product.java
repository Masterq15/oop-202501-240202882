package com.upb.agripos.model;

import java.io.Serializable;

/**
 * Product Model
 * Merepresentasikan produk dalam sistem Agri-POS
 * 
 * Atribut: kode, nama, kategori, harga, stok
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String code;
    private String name;
    private String category;
    private double price;
    private int stock;

    // Constructor tanpa ID (untuk insert baru)
    public Product(String code, String name, String category, double price, int stock) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Constructor lengkap (dari database)
    public Product(int id, String code, String name, String category, double price, int stock) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return code + " - " + name + " (" + category + ") - Rp " + String.format("%,.0f", price) + " - Stok: " + stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id == product.id || (code != null && code.equals(product.code));
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
