package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.PurchaseHistory;
import com.upb.agripos.model.User;
import com.upb.agripos.service.ProductService;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * PosView - Main interface untuk Kasir
 * Menampilkan: Product list, Cart, Checkout, Payment
 * Person D - Frontend Week 15
 */
public class PosView {
    
    private Stage stage;
    private AuthController authController;
    private User currentUser;
    private Scene scene;
    private ProductService productService;
    
    @FunctionalInterface
    public interface LogoutCallback {
        void onLogout();
    }
    
    private static LogoutCallback logoutCallback;
    
    public static void setNavCallback(LogoutCallback callback) {
        logoutCallback = callback;
    }
    
    // UI Components
    private TableView<java.util.Map<String, String>> productTableView;
    private TableView<java.util.Map<String, String>> cartTableView;
    private Label totalLabel;
    private Label userLabel;
    private java.util.List<java.util.Map<String, String>> allProducts;
    
    // UI References untuk diskon otomatis
    private Label itemsLabelRef;
    private Label subtotalLabelRef;
    private Label discountLabelRef;
    private ToggleGroup discountGroupRef;
    private RadioButton noDiscountRef;
    private RadioButton percent10Ref;
    private RadioButton percent20Ref;
    
    public PosView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.currentUser = authController.getCurrentUser();
    }
    
    // Set ProductService untuk database operations
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Create dan return POS scene
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9;");
        
        // Top: Header dengan user info dan logout
        root.setTop(createHeader());
        
        // Left: Product list
        root.setLeft(createProductPanel());
        
        // Center: Cart
        root.setCenter(createCartPanel());
        
        // Right: Total dan action buttons
        root.setRight(createActionPanel());
        
        // Bottom: Status bar
        root.setBottom(createStatusBar());
        
        this.scene = new Scene(root, 1200, 700);
        return scene;
    }
    
    /**
     * Create header dengan user info
     */
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-padding: 15; -fx-background-color: #2c3e50; -fx-border-color: #34495e; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("🛒 AGRI-POS - Kasir");
        titleLabel.setFont(new Font("System", 18));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        userLabel = new Label("Kasir: " + currentUser.getFullName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        logoutButton.setOnAction(event -> handleLogout());
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        
        return header;
    }
    
    /**
     * Create product list panel (left side)
     */
    private VBox createProductPanel() {
        VBox productPanel = new VBox(10);
        productPanel.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");
        productPanel.setPrefWidth(400);
        
        Label productLabel = new Label("📦 Daftar Produk");
        productLabel.setFont(new Font("System", 14));
        productLabel.setStyle("-fx-font-weight: bold;");
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Cari produk...");
        searchField.setStyle("-fx-padding: 8;");
        
        // Create TableView untuk produk
        productTableView = new TableView<>();
        productTableView.setPrefHeight(500);
        productTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Create columns
        TableColumn<java.util.Map<String, String>, String> codeColumn = new TableColumn<>("Kode");
        codeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("kode"))
        );
        codeColumn.setPrefWidth(60);
        
        TableColumn<java.util.Map<String, String>, String> nameColumn = new TableColumn<>("Nama");
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("nama"))
        );
        nameColumn.setPrefWidth(100);
        
        TableColumn<java.util.Map<String, String>, String> beratColumn = new TableColumn<>("Berat");
        beratColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("berat"))
        );
        beratColumn.setPrefWidth(70);
        
        TableColumn<java.util.Map<String, String>, String> stokColumn = new TableColumn<>("Stok");
        stokColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("stok"))
        );
        stokColumn.setPrefWidth(60);
        
        TableColumn<java.util.Map<String, String>, String> priceColumn = new TableColumn<>("Harga (Rp)");
        priceColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("harga"))
        );
        priceColumn.setPrefWidth(100);
        
        productTableView.getColumns().addAll(codeColumn, nameColumn, beratColumn, stokColumn, priceColumn);
        
        // Initialize products: default + shared list (tidak replace)
        java.util.List<java.util.Map<String, String>> defaultProducts = java.util.Arrays.asList(
            createProductMap("P001", "Beras", "10kg", "120.000", "50"),
            createProductMap("P002", "Jagung", "5kg", "45.000", "30"),
            createProductMap("P003", "Kacang Hijau", "5kg", "55.000", "25"),
            createProductMap("P004", "Ketela Pohon", "10kg", "35.000", "40"),
            createProductMap("P005", "Wortel", "5kg", "40.000", "35"),
            createProductMap("P006", "Tomat", "5kg", "30.000", "45"),
            createProductMap("P007", "Cabai", "2kg", "60.000", "20"),
            createProductMap("P008", "Bawang Putih", "2kg", "50.000", "28")
        );
        
        allProducts = new java.util.ArrayList<>();
        for (java.util.Map<String, String> product : defaultProducts) {
            allProducts.add(new java.util.HashMap<>(product));
            productTableView.getItems().add(new java.util.HashMap<>(product));
        }
        
        // Event handler untuk search
        searchField.setOnKeyReleased(e -> {
            String searchText = searchField.getText().toLowerCase();
            productTableView.getItems().clear();
            
            if (searchText.isEmpty()) {
                // Tampilkan semua produk jika search kosong
                for (java.util.Map<String, String> product : allProducts) {
                    productTableView.getItems().add(new java.util.HashMap<>(product));
                }
            } else {
                // Filter produk berdasarkan pencarian
                for (java.util.Map<String, String> product : allProducts) {
                    if (product.get("nama").toLowerCase().contains(searchText) || 
                        product.get("kode").toLowerCase().contains(searchText)) {
                        productTableView.getItems().add(new java.util.HashMap<>(product));
                    }
                }
            }
        });
        
        Button addButton = new Button("🛒 Tambah ke Keranjang");
        addButton.setPrefWidth(185);
        addButton.setStyle("-fx-padding: 10; -fx-font-size: 11; -fx-background-color: #FF9800; -fx-text-fill: white;");
        addButton.setOnAction(event -> {
            java.util.Map<String, String> selected = productTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleAddToCart(selected);
            } else {
                showAlert("Peringatan", "Pilih produk terlebih dahulu!");
            }
        });
        
        Button addStockButton = new Button("📦 Tambah Stok");
        addStockButton.setPrefWidth(185);
        addStockButton.setStyle("-fx-padding: 10; -fx-font-size: 11; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        addStockButton.setOnAction(event -> {
            java.util.Map<String, String> selected = productTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleAddStock(selected);
            } else {
                showAlert("Peringatan", "Pilih produk terlebih dahulu!");
            }
        });
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.getChildren().addAll(addButton, addStockButton);
        
        productPanel.getChildren().addAll(
            productLabel,
            searchField,
            new Separator(),
            productTableView,
            buttonPanel
        );
        
        return productPanel;
    }
    
    private java.util.Map<String, String> createProductMap(String kode, String nama, String berat, String harga, String stok) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("kode", kode);
        map.put("nama", nama);
        map.put("berat", berat);
        map.put("harga", harga);
        map.put("stok", stok);
        return map;
    }
    
    /**
     * Create cart panel (center)
     */
    private VBox createCartPanel() {
        VBox cartPanel = new VBox(10);
        cartPanel.setStyle("-fx-padding: 15;");
        
        Label cartLabel = new Label("🛍️ Keranjang");
        cartLabel.setFont(new Font("System", 14));
        cartLabel.setStyle("-fx-font-weight: bold;");
        
        // Create TableView untuk keranjang
        cartTableView = new TableView<>();
        cartTableView.setPrefHeight(400);
        cartTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Create columns
        TableColumn<java.util.Map<String, String>, String> cartCodeColumn = new TableColumn<>("Kode");
        cartCodeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("kode"))
        );
        cartCodeColumn.setPrefWidth(50);
        
        TableColumn<java.util.Map<String, String>, String> cartNameColumn = new TableColumn<>("Nama");
        cartNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("nama"))
        );
        cartNameColumn.setPrefWidth(80);
        
        TableColumn<java.util.Map<String, String>, String> cartUkuranColumn = new TableColumn<>("Berat");
        cartUkuranColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("berat"))
        );
        cartUkuranColumn.setPrefWidth(60);
        
        TableColumn<java.util.Map<String, String>, String> qtyColumn = new TableColumn<>("Qty");
        qtyColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("qty"))
        );
        qtyColumn.setPrefWidth(40);
        
        TableColumn<java.util.Map<String, String>, String> subtotalColumn = new TableColumn<>("Subtotal (Rp)");
        subtotalColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("subtotal"))
        );
        subtotalColumn.setPrefWidth(90);
        
        cartTableView.getColumns().addAll(cartCodeColumn, cartNameColumn, cartUkuranColumn, qtyColumn, subtotalColumn);
        
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("✏️ Edit Qty");
        editButton.setStyle("-fx-padding: 8;");
        editButton.setOnAction(event -> handleEditCart());
        
        Button deleteButton = new Button("🗑️ Hapus");
        deleteButton.setStyle("-fx-padding: 8; -fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleRemoveFromCart());
        
        Button clearButton = new Button("🔄 Kosongkan Keranjang");
        clearButton.setStyle("-fx-padding: 8; -fx-background-color: #ff9800; -fx-text-fill: white;");
        clearButton.setOnAction(event -> handleClearCart());
        
        actionBox.getChildren().addAll(editButton, deleteButton, clearButton);
        
        cartPanel.getChildren().addAll(
            cartLabel,
            cartTableView,
            new Separator(),
            actionBox
        );
        
        return cartPanel;
    }
    
    /**
     * Create action panel (right side) dengan total dan checkout
     */
    private VBox createActionPanel() {
        VBox actionPanel = new VBox(15);
        actionPanel.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-width: 0 0 0 1;");
        actionPanel.setPrefWidth(250);
        actionPanel.setAlignment(Pos.TOP_CENTER);
        
        // Summary
        Label summaryLabel = new Label("💰 RINGKASAN");
        summaryLabel.setFont(new Font("System", 12));
        summaryLabel.setStyle("-fx-font-weight: bold;");
        
        itemsLabelRef = new Label("Total Item: 0");
        itemsLabelRef.setStyle("-fx-font-size: 11;");
        
        subtotalLabelRef = new Label("Subtotal: Rp 0");
        subtotalLabelRef.setStyle("-fx-font-size: 11;");
        
        discountLabelRef = new Label("Diskon: Rp 0");
        discountLabelRef.setStyle("-fx-font-size: 11; -fx-text-fill: #ff6600;");
        
        Separator sep1 = new Separator();
        
        totalLabel = new Label("TOTAL: Rp 0");
        totalLabel.setFont(new Font("System", 18));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Discount options
        Label discountOptionLabel = new Label("Terapkan Diskon:");
        discountOptionLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        
        HBox discountBox = new HBox(5);
        noDiscountRef = new RadioButton("Tidak Ada");
        noDiscountRef.setSelected(true);
        percent10Ref = new RadioButton("10%");
        percent20Ref = new RadioButton("20%");
        
        discountGroupRef = new ToggleGroup();
        noDiscountRef.setToggleGroup(discountGroupRef);
        percent10Ref.setToggleGroup(discountGroupRef);
        percent20Ref.setToggleGroup(discountGroupRef);
        
        // Event handler untuk diskon
        noDiscountRef.setOnAction(e -> {
            itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, 0);
        });
        
        percent10Ref.setOnAction(e -> {
            itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, 10);
        });
        
        percent20Ref.setOnAction(e -> {
            itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, 20);
        });
        
        discountBox.getChildren().addAll(noDiscountRef, percent10Ref, percent20Ref);
        
        // Payment method
        Label paymentLabel = new Label("Metode Bayar:");
        paymentLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        
        ComboBox<String> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Tunai", "E-Wallet");
        paymentCombo.setValue("Tunai");
        paymentCombo.setPrefWidth(220);
        
        // Amount paid field
        Label amountLabel = new Label("Jumlah Bayar:");
        amountLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        TextField amountField = new TextField();
        amountField.setPromptText("Rp");
        amountField.setPrefWidth(220);
        
        // Change label
        Label changeLabel = new Label("Kembalian: Rp 0");
        changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        // Event handler untuk hitung kembalian
        amountField.setOnKeyReleased(e -> {
            double total = extractTotal(totalLabel.getText());
            try {
                double paid = Double.parseDouble(amountField.getText().replaceAll("[^0-9]", ""));
                double change = paid - total;
                if (change >= 0) {
                    changeLabel.setText(String.format("Kembalian: Rp %,d", (long)change));
                    changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
                } else {
                    changeLabel.setText(String.format("Kurang: Rp %,d", (long)Math.abs(change)));
                    changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                changeLabel.setText("Kembalian: Rp 0");
                changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
            }
        });
        
        Separator sep2 = new Separator();
        
        // Checkout button
        Button checkoutButton = new Button("✓ CHECKOUT");
        checkoutButton.setPrefWidth(220);
        checkoutButton.setPrefHeight(50);
        checkoutButton.setFont(new Font("System", 14));
        checkoutButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutButton.setOnAction(event -> handleCheckout(paymentCombo, amountField, changeLabel, noDiscountRef, itemsLabelRef, subtotalLabelRef, discountLabelRef));
        
        actionPanel.getChildren().addAll(
            summaryLabel,
            itemsLabelRef,
            subtotalLabelRef,
            discountLabelRef,
            sep1,
            totalLabel,
            new Separator(),
            discountOptionLabel,
            discountBox,
            paymentLabel,
            paymentCombo,
            amountLabel,
            amountField,
            changeLabel,
            sep2,
            checkoutButton
        );
        
        return actionPanel;
    }
    
    /**
     * Create status bar (bottom)
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setStyle("-fx-padding: 10; -fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("✓ Siap melayani pelanggan");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    /**
     * Handle add to cart - dengan auto update diskon
     */
    private void handleAddToCart(java.util.Map<String, String> product) {
        if (product == null) {
            showAlert("Peringatan", "Pilih produk terlebih dahulu!");
            return;
        }
        
        String productCode = product.get("kode");
        String productName = product.get("nama");
        String productBerat = product.get("berat");
        String productPrice = product.get("harga").replaceAll("[^0-9]", "");
        String productStok = product.get("stok");
        
        // ❌ VALIDASI: Blokir jika stok = 0 (HABIS)
        int stok = Integer.parseInt(productStok.replaceAll("[^0-9]", ""));
        if (stok == 0) {
            showAlert(
                "❌ STOK HABIS!",
                String.format("Produk '%s' (Kode: %s) stoknya sudah habis.\n\n" +
                    "Silakan gunakan tombol 'Tambah Stok' untuk menambahkan stok terlebih dahulu!",
                    productName, productCode)
            );
            return;
        }
        
        boolean found = false;
        
        // Cek apakah produk sudah ada di keranjang
        for (java.util.Map<String, String> cartItem : cartTableView.getItems()) {
            if (cartItem.get("kode").equals(productCode)) {
                // Increase quantity
                int currentQty = Integer.parseInt(cartItem.get("qty"));
                int newQty = currentQty + 1;
                long subtotal = Long.parseLong(productPrice) * newQty;
                
                cartItem.put("qty", String.valueOf(newQty));
                cartItem.put("subtotal", String.format("%,d", subtotal).replace(",", "."));
                
                cartTableView.refresh();
                found = true;
                break;
            }
        }
        
        // Jika produk belum ada, tambahkan ke keranjang
        if (!found) {
            java.util.Map<String, String> cartItem = new java.util.HashMap<>();
            cartItem.put("kode", productCode);
            cartItem.put("nama", productName);
            cartItem.put("berat", productBerat);
            cartItem.put("harga", product.get("harga"));
            cartItem.put("stok", productStok);
            cartItem.put("qty", "1");
            long subtotal = Long.parseLong(productPrice);
            cartItem.put("subtotal", String.format("%,d", subtotal).replace(",", "."));
            
            cartTableView.getItems().add(cartItem);
        }
        
        // Update total price display
        updateTotalPrice();
        
        // AUTO UPDATE DISKON saat item ditambah
        int discountPercent = 0;
        if (percent10Ref.isSelected()) {
            discountPercent = 10;
        } else if (percent20Ref.isSelected()) {
            discountPercent = 20;
        }
        
        // Update summary otomatis dengan diskon
        itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
        updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, discountPercent);
    }
    
    /**
     * Update total price based on cart items
     */
    private void updateTotalPrice() {
        long total = 0;
        
        for (java.util.Map<String, String> item : cartTableView.getItems()) {
            String subtotalStr = item.get("subtotal").replaceAll("[^0-9]", "");
            if (!subtotalStr.isEmpty()) {
                total += Long.parseLong(subtotalStr);
            }
        }
        
        totalLabel.setText(String.format("TOTAL: Rp %,d", total).replace(",", "."));
    }
    
    /**
     * Handle edit cart quantity - dengan auto update diskon
     */
    private void handleEditCart() {
        java.util.Map<String, String> selected = cartTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih item yang ingin diedit!");
            return;
        }
        
        String currentQty = selected.get("qty");
        String productPrice = selected.get("harga");
        
        // Dialog untuk input quantity baru dengan TextField
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Ubah Jumlah Item");
        
        VBox content = new VBox(10);
        Label label = new Label("Masukkan quantity baru:");
        TextField qtyField = new TextField(currentQty);
        qtyField.setPromptText("Contoh: 5");
        qtyField.setPrefWidth(150);
        
        content.getChildren().addAll(label, qtyField);
        dialog.getDialogPane().setContent(content);
        
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                return qtyField.getText();
            }
            return null;
        });
        
        var result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newQty = Integer.parseInt(result.get().trim());
                if (newQty <= 0) {
                    showAlert("Error", "Quantity harus lebih dari 0!");
                    return;
                }
                
                // Update quantity and recalculate subtotal
                String priceStr = selected.get("harga").replaceAll("[^0-9]", "");
                if (!priceStr.isEmpty()) {
                    long price = Long.parseLong(priceStr);
                    long newSubtotal = price * newQty;
                    
                    selected.put("qty", String.valueOf(newQty));
                    selected.put("subtotal", String.format("%,d", newSubtotal).replace(",", "."));
                    
                    cartTableView.refresh();
                    updateTotalPrice();
                    
                    // AUTO UPDATE DISKON - Get current discount percentage
                    int discountPercent = 0;
                    if (percent10Ref.isSelected()) {
                        discountPercent = 10;
                    } else if (percent20Ref.isSelected()) {
                        discountPercent = 20;
                    }
                    
                    // Update summary otomatis dengan diskon
                    itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
                    updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, discountPercent);
                    
                    showAlert("Sukses", "Quantity berhasil diperbarui dan diskon otomatis diupdate!");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Quantity harus berupa angka!");
            }
        }
    }
    
    /**
     * Handle remove from cart - dengan auto update diskon
     */
    private void handleRemoveFromCart() {
        java.util.Map<String, String> selected = cartTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartTableView.getItems().remove(selected);
            updateTotalPrice();
            
            // AUTO UPDATE DISKON saat item dihapus
            int discountPercent = 0;
            if (percent10Ref.isSelected()) {
                discountPercent = 10;
            } else if (percent20Ref.isSelected()) {
                discountPercent = 20;
            }
            
            // Update summary otomatis dengan diskon
            itemsLabelRef.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabelRef, subtotalLabelRef, discountLabelRef, totalLabel, discountPercent);
        }
    }
    
    /**
     * Handle clear cart
     */
    private void handleClearCart() {
        if (!cartTableView.getItems().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi");
            confirm.setHeaderText(null);
            confirm.setContentText("Kosongkan keranjang?");
            if (confirm.showAndWait().get() == ButtonType.OK) {
                cartTableView.getItems().clear();
                updateTotalPrice();
            }
        }
    }
    
    /**
     * Handle checkout
     */
    private void handleCheckout(ComboBox<String> paymentCombo, TextField amountField, Label changeLabel, 
                                RadioButton noDiscount, Label itemsLabel, Label subtotalLabel, Label discountLabel) {
        if (cartTableView.getItems().isEmpty()) {
            showAlert("Peringatan", "Keranjang masih kosong!");
            return;
        }
        
        double total = extractTotal(totalLabel.getText());
        
        // Get payment details
        String paymentMethod = paymentCombo.getValue();
        String amountText = amountField.getText().replaceAll("[^0-9]", "");
        
        if (amountText.isEmpty()) {
            showAlert("Peringatan", "Masukkan jumlah pembayaran!");
            return;
        }
        
        double amountPaid = Double.parseDouble(amountText);
        double change = amountPaid - total;
        
        // Process E-Wallet payment if selected
        String ewalletRef = "";
        if ("E-Wallet".equals(paymentMethod)) {
            ewalletRef = processEWalletPayment(total);
            if (ewalletRef == null) {
                return; // User cancelled E-Wallet payment
            }
        }
        
        // Generate receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("═══════════════════════════════════════════════════════════════════════\n");
        receipt.append("                    AGRI-POS - STRUK PEMBAYARAN\n");
        receipt.append("═══════════════════════════════════════════════════════════════════════\n\n");
        receipt.append(String.format("Kasir: %s\n", currentUser.getFullName()));
        receipt.append(String.format("Tanggal: %s\n", java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        receipt.append("\n───────────────────────────────────────────────────────────────────────\n");
        receipt.append("ITEM BELANJA:\n");
        receipt.append("───────────────────────────────────────────────────────────────────────\n");
        
        // List item belanja dari TableView
        for (java.util.Map<String, String> item : cartTableView.getItems()) {
            String line = String.format("%s %-20s %4s x Rp %10s = Rp %10s", 
                item.get("kode"), item.get("nama"), item.get("qty"), 
                item.get("harga"), item.get("subtotal"));
            receipt.append(line).append("\n");
        }
        
        receipt.append("───────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("TOTAL: Rp %,d\n", (long)total).replace(",", "."));
        receipt.append(String.format("Metode Pembayaran: %s\n", paymentMethod));
        
        if ("E-Wallet".equals(paymentMethod)) {
            receipt.append(String.format("Ref: %s\n", ewalletRef));
            receipt.append(String.format("Jumlah Pembayaran: Rp %,d\n", (long)total).replace(",", "."));
        } else {
            receipt.append(String.format("Jumlah Pembayaran: Rp %,d\n", (long)amountPaid).replace(",", "."));
            if (change >= 0) {
                receipt.append(String.format("Kembalian: Rp %,d\n", (long)change).replace(",", "."));
            } else {
                receipt.append(String.format("Kurang: Rp %,d\n", (long)Math.abs(change)).replace(",", "."));
            }
        }
        
        receipt.append("═══════════════════════════════════════════════════════════════════════\n");
        receipt.append("           Terima kasih atas pembelian Anda\n");
        receipt.append("═══════════════════════════════════════════════════════════════════════\n");
        
        // Tampilkan receipt dalam dialog
        Alert receiptAlert = new Alert(Alert.AlertType.INFORMATION);
        receiptAlert.setTitle("Struk Pembayaran");
        receiptAlert.setHeaderText("Transaksi Berhasil");
        
        TextArea receiptArea = new TextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setWrapText(false);
        receiptArea.setPrefHeight(400);
        receiptArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        
        receiptAlert.getDialogPane().setContent(receiptArea);
        receiptAlert.showAndWait();
        
        System.out.println("→ Processing checkout...");
        System.out.println(receipt.toString());
        
        // Generate transaction ID
        String transactionId = "TRX" + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // SAVE PURCHASE HISTORY dan REDUCE STOCK untuk setiap item yang di-checkout
        for (java.util.Map<String, String> cartItem : cartTableView.getItems()) {
            String productCode = cartItem.get("kode");
            String productName = cartItem.get("nama");
            int qtyCheckout = Integer.parseInt(cartItem.get("qty"));
            long unitPrice = Long.parseLong(cartItem.get("harga").replaceAll("[^0-9]", ""));
            long subtotalPrice = Long.parseLong(cartItem.get("subtotal").replaceAll("[^0-9]", ""));
            
            // Simpan ke purchase history
            PurchaseHistory.addRecord(new PurchaseHistory.PurchaseRecord(
                transactionId,
                productCode,
                productName,
                qtyCheckout,
                unitPrice,
                subtotalPrice,
                currentUser.getFullName(),
                paymentMethod
            ));
            
            // Update stok di allProducts
            for (java.util.Map<String, String> product : allProducts) {
                if (product.get("kode").equals(productCode)) {
                    int currentStok = Integer.parseInt(product.get("stok"));
                    int newStok = currentStok - qtyCheckout;
                    product.put("stok", String.valueOf(Math.max(0, newStok)));
                    System.out.println("→ Stok " + productCode + " berkurang dari " + currentStok + " menjadi " + newStok);
                    break;
                }
            }
            
            // Update stok di productTableView items juga (PENTING!)
            for (java.util.Map<String, String> tableItem : productTableView.getItems()) {
                if (tableItem.get("kode").equals(productCode)) {
                    int currentStok = Integer.parseInt(tableItem.get("stok"));
                    int newStok = currentStok - qtyCheckout;
                    tableItem.put("stok", String.valueOf(Math.max(0, newStok)));
                    break;
                }
            }
        }
        
        // Refresh product table untuk menampilkan stok terbaru
        productTableView.refresh();
        
        // Reset semua UI components setelah transaksi berhasil
        cartTableView.getItems().clear();
        amountField.clear();
        changeLabel.setText("Kembalian: Rp 0");
        changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        paymentCombo.setValue("Tunai");
        noDiscount.setSelected(true);
        itemsLabel.setText("Total Item: 0");
        updateTotalPrice();
        updateSummary(itemsLabel, subtotalLabel, discountLabel, totalLabel, 0);
        
        showAlert("Sukses", "Transaksi berhasil disimpan! Stok produk telah diperbarui.");
    }
    
    /**
     * Process E-Wallet payment dengan QRIS scan
     */
    private String processEWalletPayment(double total) {
        // Dialog QRIS Scan
        Alert qrisAlert = new Alert(Alert.AlertType.INFORMATION);
        qrisAlert.setTitle("Pembayaran E-Wallet");
        qrisAlert.setHeaderText("Scan QRIS untuk melanjutkan pembayaran");
        
        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));
        content.setAlignment(Pos.CENTER);
        
        Label qrLabel = new Label("📱 SCAN QRIS");
        qrLabel.setFont(new Font("System", 14));
        qrLabel.setStyle("-fx-font-weight: bold;");
        
        Label amountLabel = new Label(String.format("Nominal: Rp %,d", (long)total));
        amountLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #2c3e50;");
        
        Label noteLabel = new Label("Tunjukkan QRIS code ke customer untuk scan");
        noteLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        // Simulasi QRIS (bisa diganti dengan QR code library jika perlu)
        Label qrcodeSimulation = new Label("█████████████████████████████████\n" +
                                          "█  Simulate QRIS QR Code Scan  █\n" +
                                          "█████████████████████████████████");
        qrcodeSimulation.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; -fx-text-fill: #000;");
        
        content.getChildren().addAll(qrLabel, amountLabel, new Separator(), 
                                     noteLabel, qrcodeSimulation);
        
        qrisAlert.getDialogPane().setContent(content);
        
        // Buttons: Pembayaran Berhasil / Batal
        ButtonType successButton = new ButtonType("✓ Pembayaran Berhasil", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("✗ Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        qrisAlert.getDialogPane().getButtonTypes().setAll(successButton, cancelButton);
        
        var result = qrisAlert.showAndWait();
        
        if (result.isPresent() && result.get() == successButton) {
            // Generate reference number
            String refNumber = "EW" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            // Show success notification
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("E-Wallet Berhasil");
            successAlert.setHeaderText("Pembayaran E-Wallet Berhasil");
            successAlert.setContentText("✓ Pembayaran sebesar Rp " + String.format("%,d", (long)total) + 
                                       " telah diterima\n\n" +
                                       "Ref: " + refNumber);
            successAlert.showAndWait();
            
            return refNumber;
        }
        
        return null; // User cancelled
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText(null);
        confirm.setContentText("Logout dari sistem?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            authController.handleLogout();
            System.out.println("→ Kembali ke login screen");
            if (logoutCallback != null) {
                logoutCallback.onLogout();
            }
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Extract total value dari label text
     */
    private double extractTotal(String totalText) {
        try {
            String numericText = totalText.replaceAll("[^0-9]", "");
            return Double.parseDouble(numericText);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Get total items di keranjang
     */
    private int getTotalItems() {
        int total = 0;
        for (java.util.Map<String, String> item : cartTableView.getItems()) {
            total += Integer.parseInt(item.get("qty"));
        }
        return total;
    }
    
    /**
     * Update ringkasan dengan diskon
     */
    private void updateSummary(Label itemsLabel, Label subtotalLabel, 
                               Label discountLabel, Label totalLabel, int discountPercent) {
        long subtotal = 0;
        
        // Hitung subtotal dari semua item di keranjang
        for (java.util.Map<String, String> item : cartTableView.getItems()) {
            String subtotalStr = item.get("subtotal").replaceAll("[^0-9]", "");
            if (!subtotalStr.isEmpty()) {
                subtotal += Long.parseLong(subtotalStr);
            }
        }
        
        // Hitung diskon
        long discountAmount = (subtotal * discountPercent) / 100;
        long total = subtotal - discountAmount;
        
        // Update labels
        subtotalLabel.setText(String.format("Subtotal: Rp %,d", subtotal).replace(",", "."));
        discountLabel.setText(String.format("Diskon (%d%%): -Rp %,d", discountPercent, discountAmount).replace(",", "."));
        totalLabel.setText(String.format("TOTAL: Rp %,d", total).replace(",", "."));
    }
    
    /**
     * Get scene
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * Handle tambah stok - menampilkan dialog input untuk menambah stok produk
     */
    private void handleAddStock(java.util.Map<String, String> product) {
        if (product == null) {
            showAlert("Peringatan", "Pilih produk terlebih dahulu!");
            return;
        }
        
        String productCode = product.get("kode");
        String productName = product.get("nama");
        String productStok = product.get("stok");
        int currentStock = Integer.parseInt(productStok.replaceAll("[^0-9]", ""));
        
        // Create dialog untuk input jumlah stok yang ingin ditambahkan
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Tambah Stok Produk");
        dialog.setHeaderText(String.format("Tambah Stok: %s (Kode: %s)\nStok Saat Ini: %d", 
            productName, productCode, currentStock));
        
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");
        
        Label label = new Label("Jumlah stok yang ditambahkan:");
        TextField textField = new TextField();
        textField.setPromptText("Contoh: 50");
        textField.setPrefWidth(300);
        
        content.getChildren().addAll(label, textField);
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType okButtonType = new ButtonType("Tambah", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        
        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });
        
        // Show dialog dan process hasilnya
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();
            
            // Validasi input
            if (input.trim().isEmpty()) {
                showAlert("Error", "Jumlah stok tidak boleh kosong!");
                return;
            }
            
            try {
                int additionalStock = Integer.parseInt(input);
                
                if (additionalStock <= 0) {
                    showAlert("Error", "Jumlah stok harus lebih dari 0!");
                    return;
                }
                
                // Update stok di product list DAN di database
                int newStock = currentStock + additionalStock;
                
                // Update product di table
                for (java.util.Map<String, String> prod : productTableView.getItems()) {
                    if (prod.get("kode").equals(productCode)) {
                        prod.put("stok", String.valueOf(newStock));
                        productTableView.refresh();
                        break;
                    }
                }
                
                // Update ke database via ProductService
                if (productService != null) {
                    try {
                        com.upb.agripos.model.Product productObj = productService.getProductByCode(productCode);
                        if (productObj != null) {
                            productObj.setStock(newStock);
                            boolean dbSuccess = productService.updateProduct(productObj);
                            
                            if (dbSuccess) {
                                System.out.println("[KASIR] Stok produk " + productCode + " ditambahkan: +" + 
                                    additionalStock + " (Database updated)");
                            } else {
                                showAlert("Warning", "Perubahan stok tidak tersimpan ke database!");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating stock to database: " + e.getMessage());
                        showAlert("Warning", "Perubahan stok tidak tersimpan ke database!\n" + e.getMessage());
                    }
                } else {
                    System.out.println("[WARNING] ProductService tidak tersedia, hanya UI yang terupdate");
                }
                
                showAlert(
                    "✅ Sukses!",
                    String.format("Stok produk '%s' berhasil ditambahkan!\n" +
                        "Stok sebelumnya: %d\n" +
                        "Stok ditambahkan: %d\n" +
                        "Stok sekarang: %d\n\n" +
                        "(Stok Admin berkurang sesuai dengan penambahan ini)",
                        productName, currentStock, additionalStock, newStock)
                );
                
            } catch (NumberFormatException e) {
                showAlert("Error", "Jumlah stok harus berupa angka!");
            }
        }
    }
}
