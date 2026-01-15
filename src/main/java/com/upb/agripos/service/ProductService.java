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
}