package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.model.Product;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        // Menggunakan implementasi DAO dari pertemuan sebelumnya
        this.productDAO = new ProductDAOImpl();
    }

    public void addProduct(Product product) throws Exception {
        productDAO.insert(product);
    }

    public List<Product> getAllProducts() throws Exception {
        return productDAO.findAll();
    }
}