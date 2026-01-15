package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import java.util.List;

/**
 * Service Interface untuk Product
 * Mendefinisikan kontrak untuk operasi CRUD pada Product
 * Person B - SERVICE & DISCOUNT
 */
public interface ProductServicePersonB {
    /**
     * Menambah produk baru
     * @param product produk yang akan ditambahkan
     * @throws IllegalArgumentException jika validasi gagal
     */
    void addProduct(Product product);

    /**
     * Mengupdate produk yang sudah ada
     * @param product produk dengan data terbaru
     * @throws IllegalArgumentException jika produk tidak ditemukan atau validasi gagal
     */
    void updateProduct(Product product);

    /**
     * Menghapus produk berdasarkan ID
     * @param productId ID produk yang akan dihapus
     * @throws IllegalArgumentException jika produk tidak ditemukan
     */
    void deleteProduct(String productId);

    /**
     * Mendapatkan produk berdasarkan ID
     * @param productId ID produk
     * @return Product atau null jika tidak ditemukan
     */
    Product getProduct(String productId);

    /**
     * Mendapatkan semua produk
     * @return List semua produk
     */
    List<Product> getAllProducts();

    /**
     * Mendapatkan produk berdasarkan kategori
     * @param category kategori produk
     * @return List produk dengan kategori tersebut
     */
    List<Product> getProductsByCategory(String category);
}
