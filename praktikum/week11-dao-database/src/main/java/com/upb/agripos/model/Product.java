package com.upb.agripos.model;

public class Product {
    private String code;
    private String name;
    private double price;
    private int stock;

    // Constructor
    public Product(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public String getCode() { 
        return code; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getStock() { 
        return stock; 
    }

    // Setters
    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    public void setStock(int stock) { 
        this.stock = stock; 
    }
    
    // toString for easy printing
    @Override
    public String toString() {
        return String.format("Product[code=%s, name=%s, price=%.2f, stock=%d]", 
                             code, name, price, stock);
    }
}