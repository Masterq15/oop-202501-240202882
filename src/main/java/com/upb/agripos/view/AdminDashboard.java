package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthServiceImpl;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * AdminDashboard - Interface untuk Admin
 * Menampilkan: User management, Product management, Reports
 * Person D - Frontend Week 15
 */
public class AdminDashboard {
    
    // Static list for sharing products between Admin and Kasir
    private static final java.util.List<String> sharedProductList = new java.util.ArrayList<>();
    
    public static java.util.List<String> getSharedProductList() {
        return sharedProductList;
    }
    
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
    private TabPane mainTabPane;
    private ListView<String> userListView;
    private ListView<String> productListView;
    private TextArea reportArea;
    
    public AdminDashboard(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.currentUser = authController.getCurrentUser();
    }
    
    /**
     * Create dan return admin dashboard scene
     */
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9;");
        
        // Top: Header
        root.setTop(createHeader());
        
        // Center: Tabbed content
        root.setCenter(createMainContent());
        
        // Bottom: Footer
        root.setBottom(createStatusBar());
        
        this.scene = new Scene(root, 1400, 800);
        return scene;
    }
    
    /**
     * Create header
     */
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-padding: 15; -fx-background-color: #34495e; -fx-border-color: #2c3e50; -fx-border-width: 0 0 2 0;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("⚙️ AGRI-POS - Admin Dashboard");
        titleLabel.setFont(new Font("System", 20));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label userLabel = new Label("Admin: " + currentUser.getFullName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-padding: 8;");
        logoutButton.setOnAction(event -> handleLogout());
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        
        return header;
    }
    
    /**
     * Create tabbed main content
     */
    private TabPane createMainContent() {
        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainTabPane.setStyle("-fx-padding: 10;");
        
        // Tab 1: User Management
        Tab userTab = new Tab("👥 Manajemen User", createUserManagementPanel());
        userTab.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        // Tab 2: Product Management
        Tab productTab = new Tab("📦 Manajemen Produk", createProductManagementPanel());
        productTab.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        // Tab 3: Reports
        Tab reportTab = new Tab("📊 Laporan", createReportsPanel());
        reportTab.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        // Tab 4: Settings
        Tab settingsTab = new Tab("⚙️ Pengaturan", createSettingsPanel());
        settingsTab.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        mainTabPane.getTabs().addAll(userTab, productTab, reportTab, settingsTab);
        
        return mainTabPane;
    }
    
    /**
     * Create user management panel
     */
    private VBox createUserManagementPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 15;");
        
        // Title
        Label titleLabel = new Label("Daftar Pengguna");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // User list
        userListView = new ListView<>();
        userListView.setPrefHeight(300);
        
        // Load users dari AuthService
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        authService.getAllUsers().values().forEach(user ->
            userListView.getItems().add(user.getUserId() + " - " + user.getFullName() + " (" + user.getRole() + ")")
        );
        
        // Button panel
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-padding: 8;");
        refreshButton.setOnAction(event -> refreshUserList());
        
        Button deactivateButton = new Button("❌ Nonaktifkan User");
        deactivateButton.setStyle("-fx-padding: 8; -fx-background-color: #ff9800; -fx-text-fill: white;");
        deactivateButton.setOnAction(event -> handleDeactivateUser());
        
        Button newUserButton = new Button("➕ Tambah User Baru");
        newUserButton.setStyle("-fx-padding: 8; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        newUserButton.setOnAction(event -> handleNewUser());
        
        buttonBox.getChildren().addAll(refreshButton, deactivateButton, newUserButton);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            userListView,
            buttonBox
        );
        
        return panel;
    }
    
    /**
     * Create product management panel dengan TableView
     */
    private VBox createProductManagementPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 15;");
        
        Label titleLabel = new Label("Daftar Produk");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Create TableView untuk produk management
        javafx.scene.control.TableView<javafx.collections.ObservableMap<String, String>> productTable = 
            new javafx.scene.control.TableView<>();
        
        // Column: Kode Produk
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> codeColumn = 
            new javafx.scene.control.TableColumn<>("Kode Produk");
        codeColumn.setPrefWidth(100);
        codeColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("kode", "")));
        
        // Column: Nama Produk
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> nameColumn = 
            new javafx.scene.control.TableColumn<>("Nama Produk");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("nama", "")));
        
        // Column: Jenis Produk
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> typeColumn = 
            new javafx.scene.control.TableColumn<>("Jenis Produk");
        typeColumn.setPrefWidth(120);
        typeColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("jenis", "")));
        
        // Column: Harga
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> priceColumn = 
            new javafx.scene.control.TableColumn<>("Harga (Rp)");
        priceColumn.setPrefWidth(120);
        priceColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("harga", "")));
        
        // Column: Stok
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> stockColumn = 
            new javafx.scene.control.TableColumn<>("Stok");
        stockColumn.setPrefWidth(80);
        stockColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("stok", "")));
        
        // Column: Berat (kg)
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> weightColumn = 
            new javafx.scene.control.TableColumn<>("Berat (kg)");
        weightColumn.setPrefWidth(100);
        weightColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("berat", "")));
        
        // Add columns to table
        productTable.getColumns().addAll(codeColumn, nameColumn, typeColumn, priceColumn, stockColumn, weightColumn);
        
        // Initialize dummy data
        javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> productData = 
            javafx.collections.FXCollections.observableArrayList();
        
        // Add sample products
        addProductRow(productData, "P001", "Beras", "Padi-padian", "120000", "45", "10");
        addProductRow(productData, "P002", "Jagung", "Padi-padian", "45000", "32", "5");
        addProductRow(productData, "P003", "Kacang Hijau", "Kacang-kacangan", "55000", "28", "5");
        addProductRow(productData, "P004", "Ketela Pohon", "Umbi-umbian", "35000", "50", "10");
        addProductRow(productData, "P005", "Wortel", "Sayuran", "40000", "15", "5");
        addProductRow(productData, "P006", "Tomat", "Sayuran", "30000", "22", "3");
        addProductRow(productData, "P007", "Cabai", "Sayuran", "60000", "8", "2");
        addProductRow(productData, "P008", "Bawang Putih", "Sayuran", "50000", "18", "2");
        
        productTable.setItems(productData);
        productTable.setPrefHeight(350);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addButton = new Button("➕ Tambah Produk");
        addButton.setStyle("-fx-padding: 8; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(event -> handleAddProductTable(productTable, productData));
        
        Button editButton = new Button("✏️ Edit Produk");
        editButton.setStyle("-fx-padding: 8;");
        editButton.setOnAction(event -> handleEditProductTable(productTable, productData));
        
        Button deleteButton = new Button("❌ Hapus Produk");
        deleteButton.setStyle("-fx-padding: 8; -fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleDeleteProductTable(productTable, productData));
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            productTable,
            buttonBox
        );
        
        return panel;
    }
    
    /**
     * Helper: Add row ke product table
     */
    private void addProductRow(javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> data,
                               String kode, String nama, String jenis, String harga, String stok, String berat) {
        javafx.collections.ObservableMap<String, String> row = javafx.collections.FXCollections.observableHashMap();
        row.put("kode", kode);
        row.put("nama", nama);
        row.put("jenis", jenis);
        row.put("harga", harga);
        row.put("stok", stok);
        row.put("berat", berat);
        data.add(row);
    }
    
    /**
     * Handle add product ke table
     */
    private void handleAddProductTable(javafx.scene.control.TableView<javafx.collections.ObservableMap<String, String>> table,
                                       javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> data) {
        Dialog<javafx.collections.ObservableMap<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Tambah Produk Baru");
        dialog.setHeaderText("Masukkan Data Produk");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField codeField = new TextField();
        codeField.setPromptText("P001");
        TextField nameField = new TextField();
        nameField.setPromptText("Nama Produk");
        TextField typeField = new TextField();
        typeField.setPromptText("Jenis (Padi-padian, Sayuran, etc)");
        TextField priceField = new TextField();
        priceField.setPromptText("Harga (Rp)");
        TextField stockField = new TextField();
        stockField.setPromptText("Stok");
        TextField weightField = new TextField();
        weightField.setPromptText("Berat (kg)");
        
        grid.add(new Label("Kode:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Jenis:"), 0, 2);
        grid.add(typeField, 1, 2);
        grid.add(new Label("Harga:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Stok:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new Label("Berat:"), 0, 5);
        grid.add(weightField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType okButton = new ButtonType("Tambah", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Batal", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                if (codeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                    showAlert("Error", "Kode dan Nama produk tidak boleh kosong");
                    return null;
                }
                javafx.collections.ObservableMap<String, String> row = javafx.collections.FXCollections.observableHashMap();
                row.put("kode", codeField.getText().trim());
                row.put("nama", nameField.getText().trim());
                row.put("jenis", typeField.getText().trim());
                row.put("harga", priceField.getText().trim());
                row.put("stok", stockField.getText().trim());
                row.put("berat", weightField.getText().trim());
                return row;
            }
            return null;
        });
        
        var result = dialog.showAndWait();
        if (result.isPresent() && result.get() != null) {
            data.add(result.get());
            // Tambah ke shared list juga untuk kasir
            String product = result.get().get("kode") + " - " + result.get().get("nama") + " - Rp " + result.get().get("harga");
            sharedProductList.add(product);
            showAlert("Sukses", "Produk berhasil ditambahkan");
        }
    }
    
    /**
     * Handle edit product di table
     */
    private void handleEditProductTable(javafx.scene.control.TableView<javafx.collections.ObservableMap<String, String>> table,
                                        javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> data) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Peringatan", "Pilih produk terlebih dahulu");
            return;
        }
        
        javafx.collections.ObservableMap<String, String> selected = data.get(selectedIndex);
        
        Dialog<javafx.collections.ObservableMap<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Produk");
        dialog.setHeaderText("Ubah Data Produk");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField codeField = new TextField(selected.get("kode"));
        TextField nameField = new TextField(selected.get("nama"));
        TextField typeField = new TextField(selected.get("jenis"));
        TextField priceField = new TextField(selected.get("harga"));
        TextField stockField = new TextField(selected.get("stok"));
        TextField weightField = new TextField(selected.get("berat"));
        
        grid.add(new Label("Kode:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Jenis:"), 0, 2);
        grid.add(typeField, 1, 2);
        grid.add(new Label("Harga:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Stok:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new Label("Berat:"), 0, 5);
        grid.add(weightField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType okButton = new ButtonType("Update", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Batal", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                selected.put("kode", codeField.getText().trim());
                selected.put("nama", nameField.getText().trim());
                selected.put("jenis", typeField.getText().trim());
                selected.put("harga", priceField.getText().trim());
                selected.put("stok", stockField.getText().trim());
                selected.put("berat", weightField.getText().trim());
                return selected;
            }
            return null;
        });
        
        var result = dialog.showAndWait();
        if (result.isPresent()) {
            table.refresh();
            showAlert("Sukses", "Produk berhasil diupdate");
        }
    }
    
    /**
     * Handle delete product dari table
     */
    private void handleDeleteProductTable(javafx.scene.control.TableView<javafx.collections.ObservableMap<String, String>> table,
                                          javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> data) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Peringatan", "Pilih produk terlebih dahulu");
            return;
        }
        
        javafx.collections.ObservableMap<String, String> selected = data.get(selectedIndex);
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText(null);
        confirm.setContentText("Hapus produk: " + selected.get("nama") + "?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            data.remove(selectedIndex);
            showAlert("Sukses", "Produk berhasil dihapus");
        }
    }
    
    /**
     * Create reports panel
     */
    private VBox createReportsPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 15;");
        
        Label titleLabel = new Label("Laporan Transaksi");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Filter options
        HBox filterBox = new HBox(15);
        filterBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("Dari Tanggal:");
        TextField fromDate = new TextField();
        fromDate.setPromptText("yyyy-MM-dd");
        fromDate.setPrefWidth(120);
        
        Label toLabel = new Label("Sampai Tanggal:");
        TextField toDate = new TextField();
        toDate.setPromptText("yyyy-MM-dd");
        toDate.setPrefWidth(120);
        
        Button filterButton = new Button("Filter");
        filterButton.setStyle("-fx-padding: 8;");
        filterButton.setOnAction(event -> handleFilterReport(fromDate, toDate));
        
        Button exportButton = new Button("📥 Export PDF");
        exportButton.setStyle("-fx-padding: 8; -fx-background-color: #2196F3; -fx-text-fill: white;");
        exportButton.setOnAction(event -> handleExportPDF());
        
        filterBox.getChildren().addAll(
            dateLabel, fromDate, toLabel, toDate, filterButton, exportButton
        );
        
        // Report content
        reportArea = new TextArea();
        reportArea.setPrefHeight(400);
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setText(
            "LAPORAN TRANSAKSI AGRI-POS\n" +
            "=".repeat(50) + "\n\n" +
            "Tanggal: 15/01/2026\n" +
            "Total Transaksi: 24\n" +
            "Total Penjualan: Rp 2.450.000\n" +
            "Total Diskon: Rp 245.000\n" +
            "Komisi: Rp 49.000\n\n" +
            "Top 5 Produk Terjual:\n" +
            "1. Beras 10kg - 12 unit\n" +
            "2. Jagung 5kg - 8 unit\n" +
            "3. Wortel 5kg - 6 unit\n" +
            "4. Cabai 2kg - 5 unit\n" +
            "5. Bawang Putih 2kg - 4 unit\n"
        );
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            filterBox,
            new Separator(),
            reportArea
        );
        
        return panel;
    }
    
    /**
     * Create settings panel
     */
    private VBox createSettingsPanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-padding: 15;");
        
        Label titleLabel = new Label("Pengaturan Sistem");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Database settings
        VBox dbBox = new VBox(8);
        dbBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        Label dbLabel = new Label("Koneksi Database");
        dbLabel.setStyle("-fx-font-weight: bold;");
        Label dbStatus = new Label("✓ Terhubung ke PostgreSQL (localhost:5432)");
        dbStatus.setStyle("-fx-text-fill: #27ae60;");
        dbBox.getChildren().addAll(dbLabel, dbStatus);
        
        // Application settings
        VBox appBox = new VBox(8);
        appBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        Label appLabel = new Label("Pengaturan Aplikasi");
        appLabel.setStyle("-fx-font-weight: bold;");
        
        HBox taxBox = new HBox(15);
        Label taxLabel = new Label("Pajak (%):");
        taxLabel.setPrefWidth(100);
        TextField taxField = new TextField("10");
        taxField.setPrefWidth(100);
        taxBox.getChildren().addAll(taxLabel, taxField);
        
        HBox discountBox = new HBox(15);
        Label discountLabel = new Label("Diskon Default (%):");
        discountLabel.setPrefWidth(100);
        TextField discountField = new TextField("0");
        discountField.setPrefWidth(100);
        discountBox.getChildren().addAll(discountLabel, discountField);
        
        CheckBox auditLogCheck = new CheckBox("Aktifkan Audit Log");
        auditLogCheck.setSelected(true);
        
        appBox.getChildren().addAll(appLabel, taxBox, discountBox, auditLogCheck);
        
        Button saveButton = new Button("💾 Simpan Pengaturan");
        saveButton.setStyle("-fx-padding: 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(event -> handleSaveSettings(taxField, discountField, auditLogCheck));
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            dbBox,
            new Separator(),
            appBox,
            saveButton
        );
        
        return panel;
    }
    
    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setStyle("-fx-padding: 10; -fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("✓ Admin logged in - Sistem normal");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    /**
     * Handle user actions
     */
    private void handleDeactivateUser() {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih user terlebih dahulu");
            return;
        }
        
        // Parse: "USERID - Nama (ROLE)" -> extract username dari selected item
        // Format: "KSR001 - Ismi (KASIR)" kita perlu username
        String[] parts = selected.split(" - ");
        String userId = parts[0].trim();
        
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        
        // Cari user yang sesuai dengan userId
        User userToDeactivate = null;
        for (User user : authService.getAllUsers().values()) {
            if (user.getUserId().equals(userId)) {
                userToDeactivate = user;
                break;
            }
        }
        
        if (userToDeactivate == null) {
            showAlert("Error", "User tidak ditemukan");
            return;
        }
        
        if (!userToDeactivate.isActive()) {
            showAlert("Info", "User sudah nonaktif");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Nonaktifkan User");
        confirm.setHeaderText(null);
        confirm.setContentText("Nonaktifkan user: " + userToDeactivate.getFullName() + "?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            userToDeactivate.setActive(false);
            System.out.println("✓ User " + userToDeactivate.getUsername() + " berhasil dinonaktifkan");
            showAlert("Sukses", "User berhasil dinonaktifkan");
            refreshUserList();
        }
    }
    
    private void handleNewUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Tambah User Baru");
        dialog.setHeaderText("Masukkan Data User Baru");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID (contoh: USR001)");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Nama Lengkap");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setVisible(false);
        CheckBox showPasswordCheck = new CheckBox("Tampilkan Password");
        showPasswordCheck.setOnAction(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
            }
        });
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("KASIR", "ADMIN");
        roleCombo.setValue("KASIR");
        
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Nama Lengkap:"), 0, 2);
        grid.add(fullNameField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        VBox passBox = new VBox(3);
        HBox passFieldBox = new HBox();
        passFieldBox.getChildren().addAll(passwordField, passwordVisibleField);
        passBox.getChildren().addAll(passFieldBox, showPasswordCheck);
        grid.add(passBox, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType okButton = new ButtonType("Tambah", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Batal", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                String userId = userIdField.getText().trim();
                String username = usernameField.getText().trim();
                String fullName = fullNameField.getText().trim();
                String password = showPasswordCheck.isSelected() ? passwordVisibleField.getText().trim() : passwordField.getText().trim();
                String role = roleCombo.getValue();
                
                if (userId.isEmpty() || username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                    showAlert("Error", "Semua field harus diisi");
                    return null;
                }
                
                AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
                if (authService.getUserByUsername(username) != null) {
                    showAlert("Error", "Username " + username + " sudah digunakan");
                    return null;
                }
                
                User newUser = new User(userId, username, fullName, password, role);
                newUser.setActive(true);
                return newUser;
            }
            return null;
        });
        
        var result = dialog.showAndWait();
        if (result.isPresent() && result.get() != null) {
            AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
            User newUser = result.get();
            if (authService.registerUser(newUser)) {
                showAlert("Sukses", "User " + newUser.getUsername() + " berhasil ditambahkan");
                refreshUserList();
            } else {
                showAlert("Error", "Gagal menambahkan user");
            }
        }
    }
    
    private void refreshUserList() {
        System.out.println("Refreshing user list...");
        userListView.getItems().clear();
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        authService.getAllUsers().values().forEach(user ->
            userListView.getItems().add(user.getUserId() + " - " + user.getFullName() + " (" + user.getRole() + ")")
        );
    }
    
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
     * Handle reports
     */
    private void handleFilterReport(TextField fromDate, TextField toDate) {
        String from = fromDate.getText().trim();
        String to = toDate.getText().trim();
        
        if (from.isEmpty() || to.isEmpty()) {
            showAlert("Peringatan", "Masukkan tanggal range terlebih dahulu");
            return;
        }
        
        String report = "LAPORAN TRANSAKSI AGRI-POS\n" +
                       "=".repeat(50) + "\n\n" +
                       "Periode: " + from + " hingga " + to + "\n" +
                       "Total Transaksi: 18\n" +
                       "Total Penjualan: Rp 1.850.000\n" +
                       "Total Diskon: Rp 185.000\n" +
                       "Komisi: Rp 37.000\n\n" +
                       "Top 5 Produk Terjual:\n" +
                       "1. Beras 10kg - 9 unit\n" +
                       "2. Jagung 5kg - 6 unit\n" +
                       "3. Wortel 5kg - 4 unit\n" +
                       "4. Cabai 2kg - 3 unit\n" +
                       "5. Bawang Putih 2kg - 2 unit\n";
        
        reportArea.setText(report);
        showAlert("Sukses", "Laporan berhasil difilter");
    }
    
    private void handleExportPDF() {
        String reportContent = reportArea.getText();
        if (reportContent.isEmpty()) {
            showAlert("Peringatan", "Tidak ada laporan untuk diekspor");
            return;
        }
        
        // Simulasi export PDF
        String fileName = "laporan_agripos_" + java.time.LocalDate.now() + ".pdf";
        showAlert("Sukses", "Laporan berhasil diekspor ke file:\n" + fileName);
        System.out.println("→ Exporting report to: " + fileName);
    }
    
    /**
     * Handle settings
     */
    private void handleSaveSettings(TextField taxField, TextField discountField, CheckBox auditCheck) {
        try {
            double tax = Double.parseDouble(taxField.getText());
            double discount = Double.parseDouble(discountField.getText());
            
            if (tax < 0 || discount < 0 || tax > 100 || discount > 100) {
                showAlert("Error", "Nilai persentase harus antara 0-100");
                return;
            }
            
            System.out.println("✓ Settings saved:");
            System.out.println("  - Tax: " + tax + "%");
            System.out.println("  - Discount: " + discount + "%");
            System.out.println("  - Audit Log: " + (auditCheck.isSelected() ? "Enabled" : "Disabled"));
            
            showAlert("Sukses", "Pengaturan berhasil disimpan");
        } catch (NumberFormatException e) {
            showAlert("Error", "Format input tidak valid");
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
