package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductController {
    private ProductService productService;
    private ObservableList productList;

    public ProductController(ProductService productService) {
        this.productService = productService;
        this.productList = FXCollections.observableArrayList();
        loadProducts(); // Load initial data
    }

    // Load products from database
    public void loadProducts() {
        productList.clear();
        productList.addAll(productService.getAllProducts());
    }

    // Add new product
    public boolean addProduct(String code, String name, String priceStr, String stockStr) {
        try {
            // Parse input
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            // Create product object
            Product product = new Product(code, name, price, stock);

            // Add through service
            boolean success = productService.addProduct(product);
            
            if (success) {
                loadProducts(); // Refresh list
            }
            
            return success;
        } catch (NumberFormatException e) {
            System.out.println("Error: Format angka tidak valid!");
            return false;
        }
    }

    // Get observable list for ListView binding
    public ObservableList getProductList() {
        return productList;
    }

    // Get product by code
    public Product getProductByCode(String code) {
        return productService.getProductByCode(code);
    }

    // Delete product
    public boolean deleteProduct(String code) {
        boolean success = productService.deleteProduct(code);
        if (success) {
            loadProducts();
        }
        return success;
    }
}