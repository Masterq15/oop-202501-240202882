package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import com.upb.agripos.exception.DatabaseException;
import java.util.List;

/**
 * ProductDAO Interface - Abstraction untuk Product CRUD
 * 
 * Implementer: ProductDAOImpl (JDBC + PostgreSQL)
 * 
 * Created by: [Person A - Database Layer]
 * Last modified: 
 */
public interface ProductDAO {

    /**
     * Tambah produk baru
     */
    boolean insert(Product product) throws DatabaseException;

    /**
     * Cari produk by kode
     */
    Product findByCode(String code) throws DatabaseException;

    /**
     * Cari semua produk
     */
    List<Product> findAll() throws DatabaseException;

    /**
     * Update produk (termasuk stok)
     */
    boolean update(Product product) throws DatabaseException;

    /**
     * Hapus produk by kode
     */
    boolean delete(String code) throws DatabaseException;

    /**
     * Cari produk dengan status tertentu (OFR-4)
     */
    List<Product> findByStatus(String status) throws DatabaseException;
}
