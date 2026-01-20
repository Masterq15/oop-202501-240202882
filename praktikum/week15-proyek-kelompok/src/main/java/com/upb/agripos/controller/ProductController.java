package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductController {
    private ProductService productService;
    private ObservableList<Product> productList;

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
    public boolean addProduct(String code, String name, String category, String priceStr, String stockStr) {
        try {
            // Parse input
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            // Create product object
            Product product = new Product(code, name, category, price, stock);

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

    // Tambah stok produk secara manual (untuk produk yang stoknya habis/0)
    public boolean addStockManually(String code, String additionalStockStr) {
        try {
            // Validasi input
            if (code == null || code.trim().isEmpty()) {
                System.out.println("Error: Kode produk tidak boleh kosong!");
                return false;
            }

            int additionalStock = Integer.parseInt(additionalStockStr);

            // Panggil service untuk menambah stok
            boolean success = productService.addStock(code, additionalStock);
            
            if (success) {
                loadProducts(); // Refresh list setelah penambahan stok
            }
            
            return success;
        } catch (NumberFormatException e) {
            System.out.println("Error: Jumlah stok harus berupa angka!");
            return false;
        }
    }

    // Cek apakah produk stoknya habis
    public boolean isOutOfStock(String code) {
        return productService.isOutOfStock(code);
    }

    // Cek apakah stok produk di bawah batas minimum
    public boolean isLowStock(String code, int minStock) {
        return productService.isLowStock(code, minStock);
    }

    // Update product dengan stok baru
    public boolean updateProductWithStock(String code, String stockStr) {
        try {
            Product product = productService.getProductByCode(code);
            if (product == null) {
                System.out.println("Error: Produk tidak ditemukan!");
                return false;
            }

            int newStock = Integer.parseInt(stockStr);
            if (newStock < 0) {
                System.out.println("Error: Stok tidak boleh negatif!");
                return false;
            }

            product.setStock(newStock);
            boolean success = productService.updateProduct(product);
            
            if (success) {
                loadProducts(); // Refresh list
            }
            
            return success;
        } catch (NumberFormatException e) {
            System.out.println("Error: Stok harus berupa angka!");
            return false;
        }
    }
}