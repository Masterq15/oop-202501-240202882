package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.service.CartService;
import com.upb.agripos.view.PosView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.Optional;

/**
 * PosController - Controller Utama Aplikasi POS
 * 
 * Implementasi:
 * - MVC Pattern dengan SOLID Principles
 * - Lambda Expression untuk event handling (Week 13)
 * - Exception Handling (Week 9)
 * - Integrasi Product + Cart (Week 14)
 * 
 * Sequence Flow:
 * View → Controller → Service → DAO → Database
 */
public class PosController {
    
    private PosView view;
    private ProductService productService;
    private CartService cartService;
    
    private ObservableList<Product> productList;
    private ObservableList<CartItem> cartList;
    
    public PosController(PosView view) {
        this.view = view;
        this.productService = new ProductService();
        this.cartService = CartService.getInstance(); // Singleton
        
        this.productList = FXCollections.observableArrayList();
        this.cartList = FXCollections.observableArrayList();
        
        initEventHandlers();
        loadProducts();
        updateCartDisplay();
    }
    
    /**
     * Inisialisasi semua event handlers menggunakan LAMBDA EXPRESSION
     */
    private void initEventHandlers() {
        
        // ===== PRODUCT HANDLERS =====
        
        // Lambda: Tambah Produk
        view.getBtnAddProduct().setOnAction(e -> handleAddProduct());
        
        // Lambda: Hapus Produk
        view.getBtnDeleteProduct().setOnAction(e -> handleDeleteProduct());
        
        // Lambda: Refresh Product List
        view.getBtnRefreshProduct().setOnAction(e -> loadProducts());
        
        // ===== CART HANDLERS =====
        
        // Lambda: Tambah ke Keranjang
        view.getBtnAddToCart().setOnAction(e -> handleAddToCart());
        
        // Lambda: Hapus dari Keranjang
        view.getBtnRemoveFromCart().setOnAction(e -> handleRemoveFromCart());
        
        // Lambda: Kosongkan Keranjang
        view.getBtnClearCart().setOnAction(e -> handleClearCart());
        
        // Lambda: Checkout
        view.getBtnCheckout().setOnAction(e -> handleCheckout());
    }
    
    // ==========================================
    // PRODUCT MANAGEMENT HANDLERS
    // ==========================================
    
    /**
     * UC-Produk-01: Tambah Produk
     * Sequence: Controller → Service → DAO → DB
     */
    private void handleAddProduct() {
        String code = view.getTxtProductCode().getText().trim();
        String name = view.getTxtProductName().getText().trim();
        String priceStr = view.getTxtProductPrice().getText().trim();
        String stockStr = view.getTxtProductStock().getText().trim();

        // Validasi input kosong
        if (code.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Semua field harus diisi!");
            return;
        }

        // Parse dan validasi angka
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Format Salah",
                     "Harga dan Stok harus berupa angka!");
            return;
        }

        if (price <= 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Harga harus lebih dari 0!");
            return;
        }

        if (stock < 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Stok tidak boleh negatif!");
            return;
        }

        // Buat objek Product
        Product product = new Product(code, name, price, stock);

        // Simpan ke database via service
        boolean success = productService.addProduct(product);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                     "Produk berhasil ditambahkan!");
            view.clearProductForm();
            loadProducts();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal",
                     "Gagal menambahkan produk. Kode mungkin sudah ada.");
        }
    }
    
    /**
     * UC-Produk-02: Hapus Produk
     * Sequence: Controller → Service → DAO → DB
     */
    private void handleDeleteProduct() {
        Product selected = view.getSelectedProduct();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", 
                     "Silakan pilih produk yang akan dihapus!");
            return;
        }
        
        // Konfirmasi
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Produk: " + selected.getName());
        confirm.setContentText("Apakah Anda yakin?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = productService.deleteProduct(selected.getCode());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Produk berhasil dihapus!");
                loadProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", 
                         "Gagal menghapus produk!");
            }
        }
    }
    
    /**
     * UC-Produk-03: Lihat Daftar Produk
     * Load data dari database
     */
    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productList.clear();
            productList.addAll(products);
            view.getProductTable().setItems(productList);
            
            System.out.println("✓ Products loaded: " + products.size());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Load Data", 
                     "Gagal memuat data produk: " + e.getMessage());
        }
    }
    
    // ==========================================
    // CART MANAGEMENT HANDLERS
    // ==========================================
    
    /**
     * UC-Cart-01: Tambah ke Keranjang
     * Activity: Pilih produk → Input qty → Validasi → Tambah ke cart → Update display
     */
    private void handleAddToCart() {
        Product selected = view.getSelectedProduct();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan",
                     "Silakan pilih produk dari tabel!");
            return;
        }

        // Parse quantity
        String qtyStr = view.getTxtAddQty().getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Format Salah",
                     "Quantity harus berupa angka!");
            return;
        }

        // Tambah ke cart via service (dengan validasi)
        boolean success = cartService.addToCart(selected, quantity);

        if (success) {
            // Update display
            updateCartDisplay();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                     String.format("%s x%d ditambahkan ke keranjang!",
                                   selected.getName(), quantity));
        }
        // Error messages sudah ditampilkan oleh CartService
    }
    
    /**
     * UC-Cart-02: Hapus dari Keranjang
     */
    private void handleRemoveFromCart() {
        CartItem selected = view.getSelectedCartItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan",
                     "Silakan pilih item dari keranjang!");
            return;
        }

        boolean success = cartService.removeFromCart(selected.getProduct().getCode());

        if (success) {
            updateCartDisplay();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                     "Item dihapus dari keranjang!");
        }
        // Error messages sudah ditampilkan oleh CartService
    }
    
    /**
     * UC-Cart-03: Kosongkan Keranjang
     */
    private void handleClearCart() {
        if (cartService.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Info", 
                     "Keranjang sudah kosong!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Kosongkan Keranjang");
        confirm.setContentText("Apakah Anda yakin ingin mengosongkan keranjang?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartService.clearCart();
            updateCartDisplay();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                     "Keranjang berhasil dikosongkan!");
        }
    }
    
    /**
     * UC-Cart-04: Checkout
     * Activity: Validasi → Generate receipt → Clear cart → Reload products
     */
    private void handleCheckout() {
        // Validasi checkout
        boolean isValid = cartService.validateCheckout();

        if (!isValid) {
            // Error messages sudah ditampilkan oleh CartService
            return;
        }

        // Generate receipt
        String receipt = cartService.checkout();

        if (receipt != null) {
            // Tampilkan receipt
            Alert receiptAlert = new Alert(Alert.AlertType.INFORMATION);
            receiptAlert.setTitle("Checkout Berhasil");
            receiptAlert.setHeaderText("Transaksi Selesai");

            TextArea textArea = new TextArea(receipt);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(400);
            textArea.setFont(javafx.scene.text.Font.font("Courier New", 12));

            receiptAlert.getDialogPane().setContent(textArea);
            receiptAlert.showAndWait();

            // Update display
            updateCartDisplay();

            // Reload products (stok mungkin berubah di implementasi real)
            loadProducts();
        }
        // Error messages sudah ditampilkan oleh CartService
    }
    
    // ==========================================
    // HELPER METHODS
    // ==========================================
    
    /**
     * Update tampilan keranjang dan summary
     */
    private void updateCartDisplay() {
        // Update cart table
        List<CartItem> items = cartService.getItems();
        cartList.clear();
        cartList.addAll(items);
        view.getCartTable().setItems(cartList);
        
        // Update summary labels
        int totalItems = cartService.getItemCount();
        int totalQty = 0;
        for (CartItem item : items) {
            totalQty += item.getQuantity();
        }
        double grandTotal = cartService.getTotal();
        
        view.getLblTotalItems().setText("Total Items: " + totalItems + " jenis");
        view.getLblTotalQty().setText("Total Qty: " + totalQty + " pcs");
        view.getLblGrandTotal().setText(String.format("TOTAL: Rp %,.2f", grandTotal));
    }
    
    /**
     * Tampilkan alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}