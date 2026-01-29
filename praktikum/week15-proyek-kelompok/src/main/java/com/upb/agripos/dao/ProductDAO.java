package com.upb.agripos.dao;

import com.upb.agripos.model.Product;

import java.util.List;

/**
 * ProductDAO Interface
 * Mendefinisikan kontrak untuk akses data produk
 */
public interface ProductDAO {
    List<Product> findAll() throws Exception;
    Product findById(int id) throws Exception;
    Product findByCode(String code) throws Exception;
    boolean save(Product product) throws Exception;
    boolean update(Product product) throws Exception;
    boolean delete(int id) throws Exception;
    boolean updateStock(int id, int newStock) throws Exception;
}
