package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.exception.OutOfStockException;
import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.Product;

import java.util.List;

/**
 * ProductService
 * Service layer untuk business logic produk
 * 
 * Implementasi SOLID:
 * - SRP: Hanya menangani logika bisnis produk
 * - DIP: Menggunakan interface ProductDAO, bukan implementasi
 */
public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    // Untuk testing: allow dependency injection
    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * FR-1: Manajemen Produk - Lihat Daftar Produk
     */
    public List<Product> getAllProducts() throws Exception {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            throw new Exception("Gagal mengambil data produk: " + e.getMessage(), e);
        }
    }

    /**
     * FR-1: Manajemen Produk - Tambah Produk
     */
    public void addProduct(String code, String name, String category, double price, int stock) 
            throws ValidationException {
        try {
            // Validasi input
            if (code == null || code.trim().isEmpty()) {
                throw new ValidationException("Kode produk tidak boleh kosong");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nama produk tidak boleh kosong");
            }
            if (category == null || category.trim().isEmpty()) {
                throw new ValidationException("Kategori produk tidak boleh kosong");
            }
            if (price <= 0) {
                throw new ValidationException("Harga harus lebih dari 0");
            }
            if (stock < 0) {
                throw new ValidationException("Stok tidak boleh negatif");
            }

            // Cek duplikasi kode
            if (productDAO.findByCode(code) != null) {
                throw new ValidationException("Kode produk sudah ada: " + code);
            }

            Product product = new Product(code, name, category, price, stock);
            if (!productDAO.save(product)) {
                throw new ValidationException("Gagal menyimpan produk");
            }

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Error saat menambah produk: " + e.getMessage(), e);
        }
    }

    /**
     * FR-1: Manajemen Produk - Ubah Produk
     */
    public void updateProduct(int id, String code, String name, String category, double price, int stock) 
            throws ValidationException {
        try {
            // Validasi input
            if (code == null || code.trim().isEmpty()) {
                throw new ValidationException("Kode produk tidak boleh kosong");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nama produk tidak boleh kosong");
            }
            if (price <= 0) {
                throw new ValidationException("Harga harus lebih dari 0");
            }
            if (stock < 0) {
                throw new ValidationException("Stok tidak boleh negatif");
            }

            Product product = new Product(id, code, name, category, price, stock);
            if (!productDAO.update(product)) {
                throw new ValidationException("Gagal mengubah produk");
            }

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Error saat mengubah produk: " + e.getMessage(), e);
        }
    }

    /**
     * FR-1: Manajemen Produk - Hapus Produk
     */
    public void deleteProduct(int id) throws ValidationException {
        try {
            if (!productDAO.delete(id)) {
                throw new ValidationException("Gagal menghapus produk");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Error saat menghapus produk: " + e.getMessage(), e);
        }
    }

    /**
     * FR-2: Transaksi Penjualan - Validasi Stok
     */
    public void validateStock(int productId, int requestedQuantity) throws OutOfStockException {
        try {
            Product product = productDAO.findById(productId);
            if (product == null) {
                throw new OutOfStockException("Produk tidak ditemukan");
            }
            if (product.getStock() < requestedQuantity) {
                throw new OutOfStockException(
                    "Stok tidak cukup untuk produk " + product.getName() + 
                    ". Tersedia: " + product.getStock() + ", diminta: " + requestedQuantity
                );
            }
        } catch (OutOfStockException e) {
            throw e;
        } catch (Exception e) {
            throw new OutOfStockException("Error validasi stok: " + e.getMessage(), e);
        }
    }

    /**
     * FR-2: Transaksi Penjualan - Update Stok
     */
    public void updateProductStock(int productId, int quantitySold) throws Exception {
        try {
            Product product = productDAO.findById(productId);
            if (product == null) {
                throw new Exception("Produk tidak ditemukan");
            }
            
            int newStock = product.getStock() - quantitySold;
            if (newStock < 0) {
                throw new Exception("Stok akan negatif");
            }

            if (!productDAO.updateStock(productId, newStock)) {
                throw new Exception("Gagal update stok");
            }

        } catch (Exception e) {
            throw new Exception("Error update stok: " + e.getMessage(), e);
        }
    }

    /**
     * Pencarian produk berdasarkan kode
     */
    public Product findProductByCode(String code) throws Exception {
        return productDAO.findByCode(code);
    }

    /**
     * Pencarian produk berdasarkan ID
     */
    public Product findProductById(int id) throws Exception {
        return productDAO.findById(id);
    }
}
