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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;

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
    private java.util.List<String> allProducts;
    
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
        
        // Product list - load dari AdminDashboard shared list atau dari default
        productListView = new ListView<>();
        productListView.setPrefHeight(500);
        
        // Initialize products dari shared list
        allProducts = new java.util.ArrayList<>(AdminDashboard.getSharedProductList());
        if (allProducts.isEmpty()) {
            // Jika shared list kosong, gunakan default products
            allProducts = java.util.Arrays.asList(
                "P001 - Beras 10kg - Rp 120.000",
                "P002 - Jagung 5kg - Rp 45.000",
                "P003 - Kacang Hijau 5kg - Rp 55.000",
                "P004 - Ketela Pohon 10kg - Rp 35.000",
                "P005 - Wortel 5kg - Rp 40.000",
                "P006 - Tomat 5kg - Rp 30.000",
                "P007 - Cabai 2kg - Rp 60.000",
                "P008 - Bawang Putih 2kg - Rp 50.000"
            );
        }
        productListView.getItems().addAll(allProducts);
        
        // Event handler untuk search
        searchField.setOnKeyReleased(e -> {
            String searchText = searchField.getText().toLowerCase();
            productListView.getItems().clear();
            
            if (searchText.isEmpty()) {
                // Tampilkan semua produk jika search kosong
                productListView.getItems().addAll(allProducts);
            } else {
                // Filter produk berdasarkan pencarian
                for (String product : allProducts) {
                    if (product.toLowerCase().contains(searchText)) {
                        productListView.getItems().add(product);
                    }
                }
            }
        });
        
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
        
        // Event handler untuk diskon
        noDiscount.setOnAction(e -> {
            itemsLabel.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabel, subtotalLabel, discountLabel, totalLabel, 0);
        });
        
        percent10.setOnAction(e -> {
            itemsLabel.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabel, subtotalLabel, discountLabel, totalLabel, 10);
        });
        
        percent20.setOnAction(e -> {
            itemsLabel.setText(String.format("Total Item: %d", getTotalItems()));
            updateSummary(itemsLabel, subtotalLabel, discountLabel, totalLabel, 20);
        });
        
        discountBox.getChildren().addAll(noDiscount, percent10, percent20);
        
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
        checkoutButton.setOnAction(event -> handleCheckout(paymentCombo, amountField, changeLabel, noDiscount, itemsLabel, subtotalLabel, discountLabel));
        
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
        
        // Update total price display
        updateTotalPrice();
    }
    
    /**
     * Update total price based on cart items
     */
    private void updateTotalPrice() {
        double total = 0;
        
        for (String item : cartListView.getItems()) {
            // Parse format: "PXXX - Nama - Rp XXX.XXX" x QTY
            String[] parts = item.split(" x");
            if (parts.length >= 2) {
                // Extract quantity
                int qty = Integer.parseInt(parts[parts.length - 1]);
                
                // Extract price from format "... - Rp XXX.XXX"
                String itemInfo = parts[0];
                int priceStartIdx = itemInfo.lastIndexOf("Rp ");
                if (priceStartIdx != -1) {
                    String priceStr = itemInfo.substring(priceStartIdx + 3).replaceAll("[^0-9]", "");
                    if (!priceStr.isEmpty()) {
                        double price = Double.parseDouble(priceStr);
                        total += price * qty;
                    }
                }
            }
        }
        
        totalLabel.setText(String.format("TOTAL: Rp %,d", (long)total));
    }
    
    /**
     * Handle edit cart quantity
     */
    private void handleEditCart() {
        int selected = cartListView.getSelectionModel().getSelectedIndex();
        if (selected < 0) {
            showAlert("Peringatan", "Pilih item yang ingin diedit!");
            return;
        }
        
        String item = cartListView.getItems().get(selected);
        String[] parts = item.split(" x");
        String currentQty = parts[parts.length - 1];
        
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
                String updatedItem = parts[0] + " x" + newQty;
                cartListView.getItems().set(selected, updatedItem);
                updateTotalPrice();
                showAlert("Sukses", "Quantity berhasil diperbarui!");
            } catch (NumberFormatException ex) {
                showAlert("Error", "Quantity harus berupa angka!");
            }
        }
    }
    
    /**
     * Handle remove from cart
     */
    private void handleRemoveFromCart() {
        int selected = cartListView.getSelectionModel().getSelectedIndex();
        if (selected >= 0) {
            cartListView.getItems().remove(selected);
            updateTotalPrice();
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
                updateTotalPrice();
            }
        }
    }
    
    /**
     * Handle checkout
     */
    private void handleCheckout(ComboBox<String> paymentCombo, TextField amountField, Label changeLabel, 
                                RadioButton noDiscount, Label itemsLabel, Label subtotalLabel, Label discountLabel) {
        if (cartListView.getItems().isEmpty()) {
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
        
        // List item belanja
        for (String item : cartListView.getItems()) {
            receipt.append(item).append("\n");
        }
        
        receipt.append("───────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("TOTAL: Rp %,d\n", (long)total));
        receipt.append(String.format("Metode Pembayaran: %s\n", paymentMethod));
        
        if ("E-Wallet".equals(paymentMethod)) {
            receipt.append(String.format("Ref: %s\n", ewalletRef));
            receipt.append(String.format("Jumlah Pembayaran: Rp %,d\n", (long)total));
        } else {
            receipt.append(String.format("Jumlah Pembayaran: Rp %,d\n", (long)amountPaid));
            if (change >= 0) {
                receipt.append(String.format("Kembalian: Rp %,d\n", (long)change));
            } else {
                receipt.append(String.format("Kurang: Rp %,d\n", (long)Math.abs(change)));
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
        
        // Reset semua UI components setelah transaksi berhasil
        cartListView.getItems().clear();
        amountField.clear();
        changeLabel.setText("Kembalian: Rp 0");
        changeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        paymentCombo.setValue("Tunai");
        noDiscount.setSelected(true);
        itemsLabel.setText("Total Item: 0");
        updateTotalPrice();
        updateSummary(itemsLabel, subtotalLabel, discountLabel, totalLabel, 0);
        
        showAlert("Sukses", "Transaksi berhasil disimpan!");
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
        return cartListView.getItems().size();
    }
    
    /**
     * Update ringkasan dengan diskon
     */
    private void updateSummary(Label itemsLabel, Label subtotalLabel, 
                               Label discountLabel, Label totalLabel, int discountPercent) {
        double subtotal = 0;
        
        // Hitung subtotal dari semua item di keranjang
        for (String item : cartListView.getItems()) {
            // Parse format: "PXXX - Nama - Rp XXX.XXX" x QTY
            String[] parts = item.split(" x");
            if (parts.length >= 2) {
                int qty = Integer.parseInt(parts[parts.length - 1]);
                
                String itemInfo = parts[0];
                int priceStartIdx = itemInfo.lastIndexOf("Rp ");
                if (priceStartIdx != -1) {
                    String priceStr = itemInfo.substring(priceStartIdx + 3).replaceAll("[^0-9]", "");
                    if (!priceStr.isEmpty()) {
                        double price = Double.parseDouble(priceStr);
                        subtotal += price * qty;
                    }
                }
            }
        }
        
        // Hitung diskon
        double discountAmount = (subtotal * discountPercent) / 100;
        double total = subtotal - discountAmount;
        
        // Update labels
        subtotalLabel.setText(String.format("Subtotal: Rp %,d", (long)subtotal));
        discountLabel.setText(String.format("Diskon (%d%%): -Rp %,d", discountPercent, (long)discountAmount));
        totalLabel.setText(String.format("TOTAL: Rp %,d", (long)total));
    }
    
    /**
     * Get scene
     */
    public Scene getScene() {
        return scene;
    }
}
