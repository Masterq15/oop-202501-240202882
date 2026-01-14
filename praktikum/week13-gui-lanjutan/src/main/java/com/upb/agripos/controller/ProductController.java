package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.ProductTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

public class ProductController {
    
    private ProductTableView view;
    private ProductService productService;
    private ObservableList<Product> productList;
    
    public ProductController(ProductTableView view) {
        this.view = view;
        this.productService = new ProductService();
        this.productList = FXCollections.observableArrayList();
        
        initEventHandlers();
        loadData();
    }
    
    /**
     * Inisialisasi Event Handlers menggunakan LAMBDA EXPRESSION
     * Sesuai requirement Week 13
     */
    private void initEventHandlers() {
        
        // LAMBDA: Event handler untuk tombol Tambah Produk
        view.getBtnAdd().setOnAction(e -> handleAddProduct());
        
        // LAMBDA: Event handler untuk tombol Hapus Produk
        view.getBtnDelete().setOnAction(e -> handleDeleteProduct());
        
        // LAMBDA: Event handler untuk tombol Refresh
        view.getBtnRefresh().setOnAction(e -> loadData());
        
        // LAMBDA: Event handler untuk double-click pada table (opsional)
        view.getTableView().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Product selected = view.getSelectedProduct();
                if (selected != null) {
                    showProductDetail(selected);
                }
            }
        });
    }
    
    /**
     * UC-03: Tambah Produk
     * Sequence: View → Controller → Service → DAO → DB
     */
    private void handleAddProduct() {
        try {
            // Validasi input
            String code = view.getTxtCode().getText().trim();
            String name = view.getTxtName().getText().trim();
            String priceStr = view.getTxtPrice().getText().trim();
            String stockStr = view.getTxtStock().getText().trim();
            
            if (code.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", 
                         "Harap isi semua field!");
                return;
            }
            
            // Parse dan validasi angka
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            
            if (price < 0 || stock < 0) {
                showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", 
                         "Harga dan stok harus positif!");
                return;
            }
            
            // Buat objek Product
            Product product = new Product(code, name, price, stock);
            
            // Panggil service untuk menyimpan ke database
            boolean success = productService.addProduct(product);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Produk berhasil ditambahkan!");
                view.clearForm();
                loadData(); // Reload data dari database
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", 
                         "Gagal menambahkan produk. Kode mungkin sudah ada.");
            }
            
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", 
                     "Harga dan Stok harus berupa angka!");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Terjadi kesalahan: " + ex.getMessage());
        }
    }
    
    /**
     * UC-04: Hapus Produk
     * Sequence: View → Controller → Service → DAO → DB
     * Sesuai Activity Diagram: pilih item → konfirmasi → hapus → reload
     */
    private void handleDeleteProduct() {
        Product selected = view.getSelectedProduct();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", 
                     "Silakan pilih produk yang akan dihapus!");
            return;
        }
        
        // Konfirmasi penghapusan (sesuai Activity Diagram)
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Produk: " + selected.getName());
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus produk ini?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Panggil service untuk hapus dari database
            boolean success = productService.deleteProduct(selected.getCode());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Produk berhasil dihapus!");
                loadData(); // Reload data dari database
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", 
                         "Gagal menghapus produk!");
            }
        }
    }
    
    /**
     * UC-02: Lihat Daftar Produk
     * Sequence: Controller → Service → DAO → DB
     * Method ini mengambil semua data dari database dan mengisi TableView
     */
    public void loadData() {
        try {
            // Ambil data dari database melalui service
            List<Product> products = productService.getAllProducts();
            
            // Clear dan isi ObservableList
            productList.clear();
            productList.addAll(products);
            
            // Set data ke TableView
            view.getTableView().setItems(productList);
            
            System.out.println("✓ Data berhasil dimuat: " + products.size() + " produk");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Load Data", 
                     "Gagal memuat data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Method helper untuk menampilkan detail produk (opsional)
     */
    private void showProductDetail(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detail Produk");
        alert.setHeaderText(product.getName());
        alert.setContentText(
            "Kode: " + product.getCode() + "\n" +
            "Nama: " + product.getName() + "\n" +
            "Harga: Rp " + String.format("%,.2f", product.getPrice()) + "\n" +
            "Stok: " + product.getStock()
        );
        alert.showAndWait();
    }
    
    /**
     * Method helper untuk menampilkan alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Getter untuk testing (jika diperlukan)
    public ObservableList<Product> getProductList() {
        return productList;
    }
}