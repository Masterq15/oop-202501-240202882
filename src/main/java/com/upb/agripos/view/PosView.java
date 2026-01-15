package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    
    @FunctionalInterface
    public interface LogoutCallback {
        void onLogout();
    }
    
    private static LogoutCallback logoutCallback;
    
    public static void setNavCallback(LogoutCallback callback) {
        logoutCallback = callback;
    }
    
    // UI Components
    private ListView<String> productListView;
    private ListView<String> cartListView;
    private Label totalLabel;
    private Label userLabel;
    
    public PosView(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.currentUser = authController.getCurrentUser();
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
        productPanel.setPrefWidth(300);
        
        Label productLabel = new Label("📦 Daftar Produk");
        productLabel.setFont(new Font("System", 14));
        productLabel.setStyle("-fx-font-weight: bold;");
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Cari produk...");
        searchField.setStyle("-fx-padding: 8;");
        
        // Product list (dummy data untuk simulasi)
        productListView = new ListView<>();
        productListView.setPrefHeight(500);
        productListView.getItems().addAll(
            "P001 - Beras 10kg - Rp 120.000",
            "P002 - Jagung 5kg - Rp 45.000",
            "P003 - Kacang Hijau 5kg - Rp 55.000",
            "P004 - Ketela Pohon 10kg - Rp 35.000",
            "P005 - Wortel 5kg - Rp 40.000",
            "P006 - Tomat 5kg - Rp 30.000",
            "P007 - Cabai 2kg - Rp 60.000",
            "P008 - Bawang Putih 2kg - Rp 50.000"
        );
        
        Button addButton = new Button("+ Tambah ke Keranjang");
        addButton.setPrefWidth(280);
        addButton.setStyle("-fx-padding: 10; -fx-font-size: 11;");
        addButton.setOnAction(event -> handleAddToCart(productListView.getSelectionModel().getSelectedItem()));
        
        productPanel.getChildren().addAll(
            productLabel,
            searchField,
            new Separator(),
            productListView,
            addButton
        );
        
        return productPanel;
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
        
        cartListView = new ListView<>();
        cartListView.setPrefHeight(400);
        
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("Edit Qty");
        editButton.setStyle("-fx-padding: 8;");
        editButton.setOnAction(event -> handleEditCart());
        
        Button deleteButton = new Button("Hapus");
        deleteButton.setStyle("-fx-padding: 8; -fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleRemoveFromCart());
        
        Button clearButton = new Button("Kosongkan Keranjang");
        clearButton.setStyle("-fx-padding: 8; -fx-background-color: #ff9800; -fx-text-fill: white;");
        clearButton.setOnAction(event -> handleClearCart());
        
        actionBox.getChildren().addAll(editButton, deleteButton, clearButton);
        
        cartPanel.getChildren().addAll(
            cartLabel,
            cartListView,
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
        
        Label itemsLabel = new Label("Total Item: 0");
        itemsLabel.setStyle("-fx-font-size: 11;");
        
        Label subtotalLabel = new Label("Subtotal: Rp 0");
        subtotalLabel.setStyle("-fx-font-size: 11;");
        
        Label discountLabel = new Label("Diskon: Rp 0");
        discountLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #ff6600;");
        
        Separator sep1 = new Separator();
        
        totalLabel = new Label("TOTAL: Rp 0");
        totalLabel.setFont(new Font("System", 18));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Discount options
        Label discountOptionLabel = new Label("Terapkan Diskon:");
        discountOptionLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        
        HBox discountBox = new HBox(5);
        RadioButton noDiscount = new RadioButton("Tidak Ada");
        noDiscount.setSelected(true);
        RadioButton percent10 = new RadioButton("10%");
        RadioButton percent20 = new RadioButton("20%");
        
        ToggleGroup discountGroup = new ToggleGroup();
        noDiscount.setToggleGroup(discountGroup);
        percent10.setToggleGroup(discountGroup);
        percent20.setToggleGroup(discountGroup);
        
        discountBox.getChildren().addAll(noDiscount, percent10, percent20);
        
        // Payment method
        Label paymentLabel = new Label("Metode Bayar:");
        paymentLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        
        ComboBox<String> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Tunai", "E-Wallet", "Debit");
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
        
        Separator sep2 = new Separator();
        
        // Checkout button
        Button checkoutButton = new Button("✓ CHECKOUT");
        checkoutButton.setPrefWidth(220);
        checkoutButton.setPrefHeight(50);
        checkoutButton.setFont(new Font("System", 14));
        checkoutButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutButton.setOnAction(event -> handleCheckout());
        
        actionPanel.getChildren().addAll(
            summaryLabel,
            itemsLabel,
            subtotalLabel,
            discountLabel,
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
     * Handle add to cart
     */
    private void handleAddToCart(String product) {
        if (product == null) {
            showAlert("Peringatan", "Pilih produk terlebih dahulu!");
            return;
        }
        
        if (cartListView.getItems().isEmpty()) {
            cartListView.getItems().add(product + " x1");
        } else {
            // Cek apakah produk sudah ada
            boolean found = false;
            for (int i = 0; i < cartListView.getItems().size(); i++) {
                String item = cartListView.getItems().get(i);
                if (item.contains(product.substring(0, 4))) { // Compare by product code
                    // Increase quantity
                    String[] parts = item.split(" x");
                    int qty = Integer.parseInt(parts[1]) + 1;
                    cartListView.getItems().set(i, product + " x" + qty);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                cartListView.getItems().add(product + " x1");
            }
        }
    }
    
    /**
     * Handle edit cart quantity
     */
    private void handleEditCart() {
        System.out.println("Edit cart - TODO");
    }
    
    /**
     * Handle remove from cart
     */
    private void handleRemoveFromCart() {
        int selected = cartListView.getSelectionModel().getSelectedIndex();
        if (selected >= 0) {
            cartListView.getItems().remove(selected);
        }
    }
    
    /**
     * Handle clear cart
     */
    private void handleClearCart() {
        if (!cartListView.getItems().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi");
            confirm.setHeaderText(null);
            confirm.setContentText("Kosongkan keranjang?");
            if (confirm.showAndWait().get() == ButtonType.OK) {
                cartListView.getItems().clear();
            }
        }
    }
    
    /**
     * Handle checkout
     */
    private void handleCheckout() {
        if (cartListView.getItems().isEmpty()) {
            showAlert("Peringatan", "Keranjang masih kosong!");
            return;
        }
        
        System.out.println("→ Processing checkout...");
        showAlert("Sukses", "Transaksi berhasil disimpan!");
        cartListView.getItems().clear();
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
     * Get scene
     */
    public Scene getScene() {
        return scene;
    }
}
