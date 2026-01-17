package com.upb.agripos.dao.impl;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import com.upb.agripos.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAOImpl - JDBC Implementation untuk Product DAO
 * Person A - DATABASE MASTER
 * 
 * Implementasi Data Access Object untuk operasi CRUD Product
 * menggunakan JDBC dan PreparedStatement untuk security
 */
public class ProductDAOImpl implements ProductDAO {
    
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insert Product baru ke database
     * @param product - Object Product yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    @Override
    public boolean insert(Product product) {
        String sql = "INSERT INTO products (code, name, category, description, price, stock, min_stock, max_stock, unit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getName()); // description
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getStock());
            ps.setInt(7, 10); // min_stock default
            ps.setInt(8, 1000); // max_stock default
            ps.setString(9, "kg"); // unit default
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error insert product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update Product yang sudah ada
     * @param product - Object Product dengan data baru
     * @return true jika berhasil, false jika gagal
     */
    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET name=?, category=?, price=?, stock=? WHERE product_id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setInt(5, Integer.parseInt(product.getCode()));
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Hapus Product dari database
     * @param productId - ID produk yang akan dihapus
     * @return true jika berhasil, false jika gagal
     */
    @Override
    public boolean delete(String productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(productId));
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cari Product berdasarkan ID
     * @param productId - ID produk yang dicari
     * @return Object Product jika ditemukan, null jika tidak
     */
    @Override
    public Product findById(String productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(productId));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find product by id: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cari Product berdasarkan code/kode produk
     * @param code - Kode produk yang dicari
     * @return Object Product jika ditemukan, null jika tidak
     */
    public Product findByCode(String code) {
        String sql = "SELECT * FROM products WHERE code = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find product by code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Ambil semua Product yang aktif
     * @return List berisi semua Product aktif
     */
    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = TRUE ORDER BY code";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error find all products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Cari Product berdasarkan kategori
     * @param category - Kategori produk yang dicari
     * @return List berisi Product dengan kategori tersebut
     */
    public List<Product> findByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ? AND is_active = TRUE ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, category);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find products by category: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Update stok produk
     * @param productId - ID produk
     * @param newStock - Jumlah stok baru
     * @return true jika berhasil
     */
    public boolean updateStock(String productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, newStock);
            ps.setInt(2, Integer.parseInt(productId));
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tambah stok produk
     * @param productId - ID produk
     * @param quantity - Jumlah stok yang ditambah
     * @return true jika berhasil
     */
    public boolean increaseStock(String productId, int quantity) {
        String sql = "UPDATE products SET stock = stock + ? WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantity);
            ps.setInt(2, Integer.parseInt(productId));
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error increase stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kurangi stok produk
     * @param productId - ID produk
     * @param quantity - Jumlah stok yang dikurangi
     * @return true jika berhasil
     */
    public boolean decreaseStock(String productId, int quantity) {
        String sql = "UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantity);
            ps.setInt(2, Integer.parseInt(productId));
            ps.setInt(3, quantity);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error decrease stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cek apakah stok cukup
     * @param productId - ID produk
     * @param quantity - Jumlah yang dibutuhkan
     * @return true jika stok cukup
     */
    public boolean isStockAvailable(String productId, int quantity) {
        String sql = "SELECT stock FROM products WHERE product_id = ? AND is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(productId));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock") >= quantity;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error check stock availability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Ambil produk yang stoknya rendah (di bawah min_stock)
     * @return List berisi produk dengan stok rendah
     */
    public List<Product> findLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock <= min_stock AND is_active = TRUE ORDER BY stock";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error find low stock products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Helper method untuk mapping ResultSet ke Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product(
            rs.getString("code"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getInt("stock")
        );
        return product;
    }
    
    /**
     * Hitung total harga berdasarkan daftar produk
     */
    public double calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToDouble(p -> p.getPrice() * 1) // default quantity 1
                .sum();
    }
}
