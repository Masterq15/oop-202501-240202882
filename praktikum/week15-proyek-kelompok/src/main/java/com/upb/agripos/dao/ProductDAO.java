package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAO Interface & Base Implementation
 * Person A - DATABASE MASTER
 * 
 * Interface untuk operasi CRUD Product
 * Implementasi menggunakan JDBC dengan connection pooling
 */
public interface ProductDAO {
    
    /**
     * Insert product baru
     */
    boolean insert(Product product);
    
    /**
     * Update product yang sudah ada
     */
    boolean update(Product product);
    
    /**
     * Delete product berdasarkan ID
     */
    boolean delete(String productId);
    
    /**
     * Cari product berdasarkan ID
     */
    Product findById(String productId);
    
    /**
     * Ambil semua product
     */
    List<Product> findAll();
}