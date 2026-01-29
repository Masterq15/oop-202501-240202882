package com.upb.agripos.dao;

import com.upb.agripos.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAOImpl
 * Implementasi akses data untuk Product menggunakan JDBC dan PostgreSQL
 */
public class ProductDAOImpl implements ProductDAO {
    private DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    @Override
    public List<Product> findAll() throws Exception {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, code, name, category, price, stock FROM products ORDER BY code";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching products: " + e.getMessage(), e);
        }
        return products;
    }

    @Override
    public Product findById(int id) throws Exception {
        String sql = "SELECT id, code, name, category, price, stock FROM products WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching product by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Product findByCode(String code) throws Exception {
        String sql = "SELECT id, code, name, category, price, stock FROM products WHERE code = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching product by code: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean save(Product product) throws Exception {
        String sql = "INSERT INTO products (code, name, category, price, stock) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStock());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error saving product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Product product) throws Exception {
        String sql = "UPDATE products SET code = ?, name = ?, category = ?, price = ?, stock = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStock());
            pstmt.setInt(6, product.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error updating product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error deleting product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStock(int id, int newStock) throws Exception {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newStock);
            pstmt.setInt(2, id);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error updating stock: " + e.getMessage(), e);
        }
    }
}
