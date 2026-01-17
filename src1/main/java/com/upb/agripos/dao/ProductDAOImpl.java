package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAOImpl - Database Access Object untuk Product
 * Implementasi menggunakan JDBC dengan connection pooling
 * 
 * NOTA: Implementation digerakkan ke week15-proyek-kelompok
 * Disini saat ini hanya stub untuk kompilasi collaborative project
 */
public class ProductDAOImpl implements ProductDAO {
    private Connection connection;

    public ProductDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insert(Product product) {
        // TODO: Implement in collaborative project
        return false;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Product findById(String productId) {
        return null;
    }

    @Override
    public boolean update(Product product) {
        // TODO: Implement
        return false;
    }

    @Override
    public boolean delete(String productId) {
        // TODO: Implement
        return false;
    }
}