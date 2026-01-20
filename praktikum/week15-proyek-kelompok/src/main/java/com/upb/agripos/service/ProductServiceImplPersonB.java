package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementasi dari ProductService
 * Menangani operasi CRUD untuk Product dengan validasi
 * Person B - SERVICE & DISCOUNT
 */
public class ProductServiceImplPersonB implements ProductServicePersonB {
    private List<Product> products;

    public ProductServiceImplPersonB() {
        this.products = new ArrayList<>();
    }

    /**
     * Validasi produk
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak boleh null");
        }
        if (product.getCode() == null || product.getCode().isEmpty()) {
            throw new IllegalArgumentException("ID Produk tidak boleh kosong");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Nama Produk tidak boleh kosong");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Harga Produk tidak boleh negatif");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stok Produk tidak boleh negatif");
        }
        if (product.getCategory() == null || product.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Kategori Produk tidak boleh kosong");
        }
    }

    /**
     * Cek duplikasi ID produk (kecuali saat update)
     */
    private boolean isDuplicateId(String productId, boolean isUpdate, String updateId) {
        for (Product p : products) {
            if (p.getCode().equals(productId)) {
                if (isUpdate && productId.equals(updateId)) {
                    return false; // Sama dengan ID yang diupdate
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void addProduct(Product product) {
        validateProduct(product);

        // Cek duplikasi ID
        if (isDuplicateId(product.getCode(), false, null)) {
            throw new IllegalArgumentException("Produk dengan ID " + product.getCode() + " sudah ada");
        }

        products.add(product);
    }

    @Override
    public void updateProduct(Product product) {
        validateProduct(product);

        Product existing = getProduct(product.getCode());
        if (existing == null) {
            throw new IllegalArgumentException("Produk dengan ID " + product.getCode() + " tidak ditemukan");
        }

        // Update data
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setCategory(product.getCategory());
    }

    @Override
    public void deleteProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("ID Produk tidak boleh kosong");
        }

        Product product = getProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("Produk dengan ID " + productId + " tidak ditemukan");
        }

        products.remove(product);
    }

    @Override
    public Product getProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            return null;
        }

        for (Product p : products) {
            if (p.getCode().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return new ArrayList<>();
        }

        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
}
