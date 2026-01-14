package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.model.Product;

import java.util.List;

/**
 * ProductService - Service Layer
 * 
 * Implementasi SOLID Principles:
 * - Single Responsibility: Hanya mengatur business logic produk
 * - Dependency Inversion: View/Controller tidak langsung akses DAO
 * 
 * Sesuai Class Diagram Bab 6:
 * View → Controller → Service → DAO → Database
 */
public class ProductService {
    
    private ProductDAO productDAO;
    
    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }
    
    /**
     * UC-02: Lihat Daftar Produk
     * Mengambil semua produk dari database
     */
    public List<Product> getAllProducts() {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            System.err.println("Error di ProductService.getAllProducts(): " + e.getMessage());
            throw new RuntimeException("Gagal mengambil data produk", e);
        }
    }
    
    /**
     * UC-03: Tambah Produk
     * Menambahkan produk baru ke database
     */
    public boolean addProduct(Product product) {
        try {
            // Validasi business logic (bisa ditambah sesuai kebutuhan)
            if (product.getCode() == null || product.getCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Kode produk tidak boleh kosong");
            }
            
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Harga tidak boleh negatif");
            }
            
            if (product.getStock() < 0) {
                throw new IllegalArgumentException("Stok tidak boleh negatif");
            }
            
            // Panggil DAO untuk insert ke database
            return productDAO.insert(product);
            
        } catch (Exception e) {
            System.err.println("Error di ProductService.addProduct(): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UC-04: Hapus Produk
     * Menghapus produk dari database berdasarkan kode
     */
    public boolean deleteProduct(String code) {
        try {
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("Kode produk tidak boleh kosong");
            }
            
            // Panggil DAO untuk delete dari database
            return productDAO.delete(code);
            
        } catch (Exception e) {
            System.err.println("Error di ProductService.deleteProduct(): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Method tambahan: Cari produk berdasarkan kode
     */
    public Product findByCode(String code) {
        try {
            return productDAO.findByCode(code);
        } catch (Exception e) {
            System.err.println("Error di ProductService.findByCode(): " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Method tambahan: Update stok produk
     */
    public boolean updateStock(String code, int newStock) {
        try {
            if (newStock < 0) {
                throw new IllegalArgumentException("Stok tidak boleh negatif");
            }
            
            Product product = productDAO.findByCode(code);
            if (product != null) {
                product.setStock(newStock);
                return productDAO.update(product);
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("Error di ProductService.updateStock(): " + e.getMessage());
            return false;
        }
    }
}