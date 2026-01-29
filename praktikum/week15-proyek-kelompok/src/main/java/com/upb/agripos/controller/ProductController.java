package com.upb.agripos.controller;

import com.upb.agripos.exception.OutOfStockException;
import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;

import java.util.List;

/**
 * ProductController
 * Controller untuk manajemen produk
 */
public class ProductController {
    private ProductService productService;
    private ProductListener listener;

    public interface ProductListener {
        void onProductsLoaded(List<Product> products);
        void onProductAdded(Product product);
        void onProductUpdated(Product product);
        void onProductDeleted(int productId);
        void onError(String errorMessage);
    }

    public ProductController() {
        this.productService = new ProductService();
    }

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public void setProductListener(ProductListener listener) {
        this.listener = listener;
    }

    /**
     * FR-1: Lihat Daftar Produk
     */
    public void loadAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            if (listener != null) {
                listener.onProductsLoaded(products);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Gagal memuat data produk: " + e.getMessage());
            }
        }
    }

    /**
     * FR-1: Tambah Produk
     */
    public void handleAddProduct(String code, String name, String category, String priceStr, String stockStr) {
        try {
            // Validasi dan konversi input
            if (code == null || code.trim().isEmpty()) {
                throw new ValidationException("Kode produk tidak boleh kosong");
            }
            if (priceStr == null || priceStr.trim().isEmpty()) {
                throw new ValidationException("Harga tidak boleh kosong");
            }
            if (stockStr == null || stockStr.trim().isEmpty()) {
                throw new ValidationException("Stok tidak boleh kosong");
            }

            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            productService.addProduct(code, name, category, price, stock);

            if (listener != null) {
                Product newProduct = productService.findProductByCode(code);
                listener.onProductAdded(newProduct);
            }

        } catch (ValidationException e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (NumberFormatException e) {
            if (listener != null) {
                listener.onError("Format harga atau stok tidak valid");
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error menambah produk: " + e.getMessage());
            }
        }
    }

    /**
     * FR-1: Ubah Produk
     */
    public void handleUpdateProduct(int id, String code, String name, String category, String priceStr, String stockStr) {
        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            productService.updateProduct(id, code, name, category, price, stock);

            if (listener != null) {
                Product updated = productService.findProductById(id);
                listener.onProductUpdated(updated);
            }

        } catch (ValidationException e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error mengubah produk: " + e.getMessage());
            }
        }
    }

    /**
     * FR-1: Hapus Produk
     */
    public void handleDeleteProduct(int id) {
        try {
            productService.deleteProduct(id);
            if (listener != null) {
                listener.onProductDeleted(id);
            }
        } catch (ValidationException e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Error menghapus produk: " + e.getMessage());
            }
        }
    }

    /**
     * FR-2: Validasi stok sebelum add ke keranjang
     */
    public boolean validateProductStock(int productId, int quantity) {
        try {
            productService.validateStock(productId, quantity);
            return true;
        } catch (OutOfStockException e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
            return false;
        }
    }

    /**
     * Dapatkan produk berdasarkan kode
     */
    public Product getProductByCode(String code) {
        try {
            return productService.findProductByCode(code);
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Produk tidak ditemukan: " + code);
            }
            return null;
        }
    }

    /**
     * Dapatkan produk berdasarkan ID
     */
    public Product getProductById(int id) {
        try {
            return productService.findProductById(id);
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("Produk tidak ditemukan");
            }
            return null;
        }
    }
}
