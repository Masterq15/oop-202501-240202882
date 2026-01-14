package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.util.List;

/**
 * ProductDAO - Data Access Object Interface
 *
 * Bertanggung jawab untuk semua operasi CRUD ke database PostgreSQL
 * Sesuai dengan prinsip Separation of Concerns dan Dependency Inversion
 */
public interface ProductDAO {

    /**
     * Menyimpan produk baru ke database
     */
    boolean insert(Product product) throws Exception;

    /**
     * Mengambil produk berdasarkan kode
     */
    Product findByCode(String code) throws Exception;

    /**
     * Mengambil semua produk dari database
     */
    List<Product> findAll() throws Exception;

    /**
     * Mengupdate produk yang sudah ada
     */
    boolean update(Product product) throws Exception;

    /**
     * Menghapus produk berdasarkan kode
     */
    boolean delete(String code) throws Exception;
}