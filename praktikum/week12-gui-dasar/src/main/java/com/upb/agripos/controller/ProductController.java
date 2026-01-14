package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.ProductFormView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductController {
    private ProductService productService;
    private ProductFormView view;
    private ObservableList<Product> productList;

    // Konstruktor menerima View agar bisa mengambil data dari TextField
    public ProductController(ProductFormView view) {
        this.view = view;
        this.productService = new ProductService();
        // Mengambil data awal dari database melalui service
        try {
            this.productList = FXCollections.observableArrayList(productService.getAllProducts());
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
            this.productList = FXCollections.observableArrayList();
        }
    }

    // Method untuk menangani klik tombol "Tambah"
    public void handleAddProduct() {
        try {
            // 1. Ambil data dari TextField di View
            String code = view.getTxtCode().getText();
            String name = view.getTxtName().getText();
            double price = Double.parseDouble(view.getTxtPrice().getText());
            int stock = Integer.parseInt(view.getTxtStock().getText());

            // 2. Buat objek Product baru
            Product p = new Product(code, name, price, stock);
            
            // 3. Simpan ke database melalui Service (Sesuai prinsip DIP & Layering)
            productService.addProduct(p);
            
            // 4. Update daftar di memori agar tampilan sinkron
            productList.add(p);
            
            // 5. Bersihkan form
            view.clearFields();
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Harga dan Stok harus angka!");
        } catch (Exception e) {
            System.err.println("Error saving product: " + e.getMessage());
        }
    }

    public ObservableList<Product> getProductList() {
        return productList;
    }
}