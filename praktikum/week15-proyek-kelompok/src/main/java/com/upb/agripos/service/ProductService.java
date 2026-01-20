package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.model.Product;
import java.sql.Connection;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService(Connection connection) {
        this.productDAO = new ProductDAOImpl(connection);
    }

    // Business Logic: Insert Product dengan validasi
    public boolean addProduct(Product product) {
        // Validasi: Cek apakah kode sudah ada
        Product existing = productDAO.findById(product.getCode());
        if (existing != null) {
            System.out.println("Error: Kode produk sudah ada!");
            return false;
        }

        // Validasi: Harga dan stok tidak boleh negatif
        if (product.getPrice() < 0) {
            System.out.println("Error: Harga tidak boleh negatif!");
            return false;
        }

        if (product.getStock() < 0) {
            System.out.println("Error: Stok tidak boleh negatif!");
            return false;
        }

        // Insert ke database
        return productDAO.insert(product);
    }

    // Get all products
    public List getAllProducts() {
        return productDAO.findAll();
    }

    // Find by code
    public Product getProductByCode(String code) {
        return productDAO.findById(code);
    }

    // Update product
    public boolean updateProduct(Product product) {
        // Validasi sebelum update
        if (product.getPrice() < 0 || product.getStock() < 0) {
            System.out.println("Error: Harga dan stok tidak boleh negatif!");
            return false;
        }
        return productDAO.update(product);
    }

    // Delete product
    public boolean deleteProduct(String code) {
        return productDAO.delete(code);
    }

    // Penambahan stok manual (untuk produk yang habis/stok 0)
    public boolean addStock(String code, int additionalStock) {
        // Validasi stok yang ditambahkan harus positif
        if (additionalStock <= 0) {
            System.out.println("Error: Jumlah stok yang ditambahkan harus lebih dari 0!");
            return false;
        }

        // Cari produk berdasarkan kode
        Product product = productDAO.findById(code);
        if (product == null) {
            System.out.println("Error: Produk dengan kode " + code + " tidak ditemukan!");
            return false;
        }

        // Update stok produk
        int newStock = product.getStock() + additionalStock;
        product.setStock(newStock);

        // Update ke database
        boolean success = productDAO.update(product);
        if (success) {
            System.out.println("Stok produk " + product.getName() + " berhasil ditambahkan!");
            System.out.println("Stok lama: " + (product.getStock() - additionalStock) + 
                             " → Stok baru: " + newStock);
        }
        return success;
    }

    // Cek apakah produk stoknya habis (0)
    public boolean isOutOfStock(String code) {
        Product product = productDAO.findById(code);
        return product != null && product.getStock() == 0;
    }

    // Cek apakah stok produk di bawah batas minimum (misal: < 10)
    public boolean isLowStock(String code, int minStock) {
        Product product = productDAO.findById(code);
        return product != null && product.getStock() < minStock;
    }
}