package com.upb.agripos.view;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ProductFormView {
    private ProductController controller;
    
    // UI Components
    private TextField txtCode;
    private TextField txtName;
    private TextField txtCategory;
    private TextField txtPrice;
    private TextField txtStock;
    private ListView<Product> listView;
    private Label lblStatus;

    public ProductFormView(ProductController controller) {
        this.controller = controller;
    }

    public Scene createScene(Stage stage) {
        // Main Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Header
        Label lblTitle = new Label("AGRI-POS - Manajemen Produk");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox headerBox = new HBox(lblTitle);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 15, 0));
        root.setTop(headerBox);

        // Form Panel (Left)
        VBox formPanel = createFormPanel();
        root.setLeft(formPanel);

        // List Panel (Center)
        VBox listPanel = createListPanel();
        root.setCenter(listPanel);

        // Status Bar (Bottom)
        lblStatus = new Label("Status: Siap");
        lblStatus.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 5px;");
        root.setBottom(lblStatus);

        return new Scene(root, 800, 500);
    }

    private VBox createFormPanel() {
        VBox formPanel = new VBox(10);
        formPanel.setPadding(new Insets(10));
        formPanel.setStyle("-fx-border-color: #BDBDBD; -fx-border-width: 1px; -fx-background-color: #F5F5F5;");
        formPanel.setMaxWidth(300);

        // Form Title
        Label lblFormTitle = new Label("Form Input Produk");
        lblFormTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Input Fields
        txtCode = new TextField();
        txtCode.setPromptText("Contoh: P001");
        
        txtName = new TextField();
        txtName.setPromptText("Contoh: Pupuk Organik");
        
        txtCategory = new TextField();
        txtCategory.setPromptText("Contoh: Pupuk");
        
        txtPrice = new TextField();
        txtPrice.setPromptText("Contoh: 50000");
        
        txtStock = new TextField();
        txtStock.setPromptText("Contoh: 100");

        // Buttons
        Button btnAdd = new Button("Tambah Produk");
        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(event -> handleAddProduct());

        Button btnClear = new Button("Clear Form");
        btnClear.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(event -> clearForm());

        Button btnDelete = new Button("Hapus Produk");
        btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setOnAction(event -> handleDeleteProduct());

        // Add to panel
        formPanel.getChildren().addAll(
            lblFormTitle,
            new Label("Kode Produk:"), txtCode,
            new Label("Nama Produk:"), txtName,
            new Label("Kategori:"), txtCategory,
            new Label("Harga (Rp):"), txtPrice,
            new Label("Stok:"), txtStock,
            btnAdd, btnClear, btnDelete
        );

        return formPanel;
    }

    private VBox createListPanel() {
        VBox listPanel = new VBox(10);
        listPanel.setPadding(new Insets(10));

        // List Title
        Label lblListTitle = new Label("Daftar Produk");
        lblListTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // ListView
        listView = new ListView<>(controller.getProductList());
        listView.setPrefHeight(400);

        // Info Label
        Label lblInfo = new Label("Total Produk: 0");
        controller.getProductList().addListener((javafx.collections.ListChangeListener.Change c) -> {
            lblInfo.setText("Total Produk: " + controller.getProductList().size());
        });

        listPanel.getChildren().addAll(lblListTitle, listView, lblInfo);

        return listPanel;
    }

    // Event Handler: Add Product
    private void handleAddProduct() {
        // Validasi input
        if (!validateInput()) {
            return;
        }

        // Get input values
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String category = txtCategory.getText().trim();
        String price = txtPrice.getText().trim();
        String stock = txtStock.getText().trim();

        // Call controller
        boolean success = controller.addProduct(code, name, category, price, stock);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                     "Produk berhasil ditambahkan!");
            clearForm();
            lblStatus.setText("Status: Produk " + code + " berhasil ditambahkan");
            lblStatus.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 5px;");
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", 
                     "Gagal menambahkan produk. Cek console untuk detail error.");
            lblStatus.setText("Status: Gagal menambahkan produk");
            lblStatus.setStyle("-fx-background-color: #FFCDD2; -fx-padding: 5px;");
        }
    }

    // Event Handler: Delete Product
    private void handleDeleteProduct() {
        Product selected = listView.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", 
                     "Silakan pilih produk yang akan dihapus!");
            return;
        }

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Produk?");
        confirm.setContentText("Anda yakin ingin menghapus: " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = controller.deleteProduct(selected.getCode());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                         "Produk berhasil dihapus!");
                lblStatus.setText("Status: Produk " + selected.getCode() + " berhasil dihapus");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", 
                         "Gagal menghapus produk!");
            }
        }
    }

    // Validasi Input
    private boolean validateInput() {
        // Check empty fields
        if (txtCode.getText().trim().isEmpty() ||
            txtName.getText().trim().isEmpty() ||
            txtPrice.getText().trim().isEmpty() ||
            txtStock.getText().trim().isEmpty()) {
            
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", 
                     "Semua field harus diisi!");
            return false;
        }

        // Validate price
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            if (price < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi Gagal", 
                         "Harga tidak boleh negatif!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", 
                     "Harga harus berupa angka yang valid!");
            return false;
        }

        // Validate stock
        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi Gagal", 
                         "Stok tidak boleh negatif!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", 
                     "Stok harus berupa angka bulat yang valid!");
            return false;
        }

        return true;
    }

    // Clear form
    private void clearForm() {
        txtCode.clear();
        txtName.clear();
        txtCategory.clear();
        txtPrice.clear();
        txtStock.clear();
        txtCode.requestFocus();
    }

    // Show Alert Dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}