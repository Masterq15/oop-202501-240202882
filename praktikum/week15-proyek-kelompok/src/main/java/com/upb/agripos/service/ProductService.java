package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import com.upb.agripos.exception.ValidationException;
import java.util.List;

/**
 * ProductService Interface - Business Logic untuk Product Management
 * 
 * Implementer: ProductServiceImpl
 * 
 * Created by: [Person B - Backend]
 * Last modified: 
 */
public interface ProductService {

    /**
     * FR-1: Tambah produk dengan validasi
     */
    void addProduct(Product product) throws ValidationException;

    /**
     * FR-1: Ambil semua produk
     */
    List<Product> getAllProducts();

    /**
     * FR-1: Update produk dengan validasi
     */
    void updateProduct(Product product) throws ValidationException;

    /**
     * FR-1: Hapus produk
     */
    void deleteProduct(String code) throws ValidationException;

    /**
     * FR-1: Cari produk by kode
     */
    Product findByCode(String code);

    /**
     * Update stok produk (digunakan saat checkout FR-2)
     */
    void updateStock(String productCode, int newStock) throws ValidationException;

    /**
     * OFR-4: Cari produk dengan stok rendah
     */
    List<Product> findLowStockProducts();
}
