package com.upb.agripos.service;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.Product;
import java.util.List;
import java.util.ArrayList;

/**
 * ProductServiceImpl - Implementation dari ProductService interface
 * 
 * Business logic untuk product management (FR-1)
 * Uses in-memory storage for demo purposes
 * 
 * Created by: [Person B - Backend]
 * Last modified: 
 */
public class ProductServiceImpl implements ProductService {
    
    private List<Product> products;
    
    public ProductServiceImpl() {
        this.products = new ArrayList<>();
        // Initialize dengan sample data
        initSampleData();
    }
    
    private void initSampleData() {
        products.add(new Product("P001", "Benih Padi", "Biji-bijian", 15000, 50));
        products.add(new Product("P002", "Pupuk Organik", "Rempah", 25000, 100));
    }
    
    @Override
    public void addProduct(Product product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("Produk tidak boleh null");
        }
        if (product.getCode() == null || product.getCode().isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new ValidationException("Nama produk tidak boleh kosong");
        }
        if (product.getPrice() <= 0) {
            throw new ValidationException("Harga produk harus lebih besar dari 0");
        }
        if (product.getStock() < 0) {
            throw new ValidationException("Stok tidak boleh negatif");
        }
        
        // Check apakah kode sudah ada
        if (products.stream().anyMatch(p -> p.getCode().equals(product.getCode()))) {
            throw new ValidationException("Kode produk '" + product.getCode() + "' sudah ada");
        }
        
        products.add(product);
        System.out.println("✅ Produk ditambahkan: " + product.getName());
    }
    
    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    @Override
    public void updateProduct(Product product) throws ValidationException {
        if (product == null || product.getCode() == null) {
            throw new ValidationException("Produk atau kode produk tidak boleh null");
        }
        
        int index = products.stream()
            .map(Product::getCode)
            .toList()
            .indexOf(product.getCode());
        
        if (index == -1) {
            throw new ValidationException("Produk dengan kode '" + product.getCode() + "' tidak ditemukan");
        }
        
        products.set(index, product);
        System.out.println("✅ Produk diupdate: " + product.getName());
    }
    
    @Override
    public void deleteProduct(String productCode) throws ValidationException {
        if (productCode == null || productCode.isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        
        Product product = products.stream()
            .filter(p -> p.getCode().equals(productCode))
            .findFirst()
            .orElse(null);
        
        if (product == null) {
            throw new ValidationException("Produk dengan kode '" + productCode + "' tidak ditemukan");
        }
        
        products.remove(product);
        System.out.println("✅ Produk dihapus: " + product.getName());
    }
    
    @Override
    public Product findByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return products.stream()
            .filter(p -> p.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public void updateStock(String productCode, int newStock) throws ValidationException {
        if (productCode == null || productCode.isEmpty()) {
            throw new ValidationException("Kode produk tidak boleh kosong");
        }
        if (newStock < 0) {
            throw new ValidationException("Stok tidak boleh negatif");
        }
        
        Product product = findByCode(productCode);
        if (product == null) {
            throw new ValidationException("Produk dengan kode '" + productCode + "' tidak ditemukan");
        }
        
        product.setStock(newStock);
        System.out.println("✅ Stok produk diupdate: " + product.getName() + " -> " + newStock);
    }
    
    @Override
    public List<Product> findLowStockProducts() {
        return products.stream()
            .filter(p -> "LOW_STOCK".equals(p.getStatus()) || "DISCONTINUED".equals(p.getStatus()))
            .toList();
    }
}
