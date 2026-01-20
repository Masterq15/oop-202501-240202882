package com.upb.agripos.view;

import java.awt.Desktop;
import java.io.File;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.PurchaseHistory;
import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthServiceImpl;
import com.upb.agripos.service.ProductService;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private TabPane mainTabPane;
    private TableView<java.util.Map<String, String>> userTable;
    private ListView<String> productListView;
    private TextArea reportArea;
    
    public AdminDashboard(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        this.currentUser = authController.getCurrentUser();
    }
    
    /**
     * Set ProductService untuk sync dengan Kasir
     */
    public void setProductService(ProductService productService) {
        this.productService = productService;
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
        
        // Create TableView untuk user management
        userTable = new TableView<>();
        userTable.setPrefHeight(300);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Create columns
        TableColumn<java.util.Map<String, String>, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("id"))
        );
        
        TableColumn<java.util.Map<String, String>, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("username"))
        );
        
        TableColumn<java.util.Map<String, String>, String> nameColumn = new TableColumn<>("Nama Lengkap");
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("fullName"))
        );
        
        TableColumn<java.util.Map<String, String>, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("role"))
        );
        
        TableColumn<java.util.Map<String, String>, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("status"))
        );
        
        userTable.getColumns().addAll(idColumn, usernameColumn, nameColumn, roleColumn, statusColumn);
        
        // Load users data
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        authService.getAllUsers().values().forEach(user -> {
            java.util.Map<String, String> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getUserId());
            userMap.put("username", user.getUsername());
            userMap.put("fullName", user.getFullName());
            userMap.put("role", user.getRole());
            userMap.put("status", user.isActive() ? "Aktif" : "Nonaktif");
            userTable.getItems().add(userMap);
        });
        
        // Button panel
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-padding: 8;");
        refreshButton.setOnAction(event -> refreshUserList());
        
        Button deleteButton = new Button("🗑️ Hapus User");
        deleteButton.setStyle("-fx-padding: 8; -fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleDeleteUser());
        
        Button newUserButton = new Button("➕ Tambah User Baru");
        newUserButton.setStyle("-fx-padding: 8; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        newUserButton.setOnAction(event -> handleNewUser());
        
        buttonBox.getChildren().addAll(refreshButton, deleteButton, newUserButton);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            userTable,
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
        
        Label titleLabel = new Label("Laporan Transaksi & Penjualan");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Filter options
        HBox filterBox = new HBox(15);
        filterBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("Dari Tanggal:");
        DatePicker fromDate = new DatePicker();
        fromDate.setValue(java.time.LocalDate.now().minusMonths(1));
        fromDate.setPrefWidth(150);
        
        Label toLabel = new Label("Sampai Tanggal:");
        DatePicker toDate = new DatePicker();
        toDate.setValue(java.time.LocalDate.now());
        toDate.setPrefWidth(150);
        
        Button filterButton = new Button("Filter");
        filterButton.setStyle("-fx-padding: 8;");
        filterButton.setOnAction(event -> handleFilterReportTable(fromDate, toDate));
        
        Button exportButton = new Button("📥 Export PDF");
        exportButton.setStyle("-fx-padding: 8; -fx-background-color: #2196F3; -fx-text-fill: white;");
        exportButton.setOnAction(event -> handleExportPDF());
        
        filterBox.getChildren().addAll(
            dateLabel, fromDate, toLabel, toDate, filterButton, exportButton
        );
        
        // Summary stats
        HBox statsBox = new HBox(30);
        statsBox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-width: 1;");
        statsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label totalTxLabel = new Label("Total Transaksi: 24");
        totalTxLabel.setStyle("-fx-font-weight: bold;");
        Label totalSalesLabel = new Label("Total Penjualan: Rp 2.450.000");
        totalSalesLabel.setStyle("-fx-font-weight: bold;");
        Label totalDiscountLabel = new Label("Total Diskon: Rp 245.000");
        totalDiscountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ff6600;");
        Label komisiLabel = new Label("Komisi: Rp 49.000");
        komisiLabel.setStyle("-fx-font-weight: bold;");
        
        statsBox.getChildren().addAll(totalTxLabel, totalSalesLabel, totalDiscountLabel, komisiLabel);
        
        // Create TableView untuk laporan produk terjual
        javafx.scene.control.TableView<javafx.collections.ObservableMap<String, String>> reportTable = 
            new javafx.scene.control.TableView<>();
        
        // Column: Nama Barang
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> namaColumn = 
            new javafx.scene.control.TableColumn<>("Nama Barang");
        namaColumn.setPrefWidth(150);
        namaColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("nama", "")));
        
        // Column: Harga (Rp)
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> hargaColumn = 
            new javafx.scene.control.TableColumn<>("Harga (Rp)");
        hargaColumn.setPrefWidth(120);
        hargaColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("harga", "")));
        
        // Column: Kg
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> kgColumn = 
            new javafx.scene.control.TableColumn<>("Kg");
        kgColumn.setPrefWidth(80);
        kgColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("kg", "")));
        
        // Column: Jumlah Terjual
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> jumlahColumn = 
            new javafx.scene.control.TableColumn<>("Jumlah Terjual");
        jumlahColumn.setPrefWidth(120);
        jumlahColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("jumlah", "")));
        
        // Column: Tanggal
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> tanggalColumn = 
            new javafx.scene.control.TableColumn<>("Tanggal");
        tanggalColumn.setPrefWidth(110);
        tanggalColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("tanggal", "")));
        
        // Column: Total Transaksi
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> totalTxColumn = 
            new javafx.scene.control.TableColumn<>("Total Transaksi");
        totalTxColumn.setPrefWidth(130);
        totalTxColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("total_tx", "")));
        
        // Column: Total Penjualan
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> totalPenjualanColumn = 
            new javafx.scene.control.TableColumn<>("Total Penjualan (Rp)");
        totalPenjualanColumn.setPrefWidth(150);
        totalPenjualanColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("total_penjualan", "")));
        
        // Column: Total Diskon
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> totalDiskonColumn = 
            new javafx.scene.control.TableColumn<>("Total Diskon (Rp)");
        totalDiskonColumn.setPrefWidth(140);
        totalDiskonColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("total_diskon", "")));
        
        // Column: Komisi
        javafx.scene.control.TableColumn<javafx.collections.ObservableMap<String, String>, String> komisiColumn = 
            new javafx.scene.control.TableColumn<>("Komisi (Rp)");
        komisiColumn.setPrefWidth(120);
        komisiColumn.setCellValueFactory(param -> 
            new javafx.beans.property.SimpleStringProperty(param.getValue().getOrDefault("komisi", "")));
        
        // Add columns to table
        reportTable.getColumns().addAll(namaColumn, hargaColumn, kgColumn, jumlahColumn, tanggalColumn, 
                                       totalTxColumn, totalPenjualanColumn, totalDiskonColumn, komisiColumn);
        
        // Initialize with actual purchase history data
        javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> reportData = 
            javafx.collections.FXCollections.observableArrayList();
        
        // Load actual data dari PurchaseHistory
        java.util.List<PurchaseHistory.PurchaseRecord> records = PurchaseHistory.getAllRecords();
        for (PurchaseHistory.PurchaseRecord record : records) {
            addReportRow(reportData, 
                record.getProductName(), 
                String.valueOf(record.getUnitPrice()), 
                "", 
                String.valueOf(record.getQuantity()), 
                record.getDate(),
                "1",
                String.valueOf(record.getSubtotal()),
                "0",
                String.valueOf(record.getSubtotal() / 50)); // Komisi 2%
        }
        
        // Jika tidak ada data, tampilkan data sample
        if (records.isEmpty()) {
            addReportRow(reportData, "Beras 10kg", "120000", "10", "12", "15/01/2026", "1", "1440000", "144000", "28800");
            addReportRow(reportData, "Jagung 5kg", "45000", "5", "8", "14/01/2026", "1", "360000", "36000", "7200");
            addReportRow(reportData, "Wortel 5kg", "40000", "5", "6", "13/01/2026", "1", "240000", "24000", "4800");
        }
        
        reportTable.setItems(reportData);
        reportTable.setPrefHeight(350);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            filterBox,
            statsBox,
            new Separator(),
            reportTable
        );
        
        return panel;
    }
    
    /**
     * Helper: Add row ke report table
     */
    private void addReportRow(javafx.collections.ObservableList<javafx.collections.ObservableMap<String, String>> data,
                              String nama, String harga, String kg, String jumlah, String tanggal,
                              String totalTx, String totalPenjualan, String totalDiskon, String komisi) {
        javafx.collections.ObservableMap<String, String> row = javafx.collections.FXCollections.observableHashMap();
        row.put("nama", nama);
        row.put("harga", harga);
        row.put("kg", kg);
        row.put("jumlah", jumlah);
        row.put("tanggal", tanggal);
        row.put("total_tx", totalTx);
        row.put("total_penjualan", totalPenjualan);
        row.put("total_diskon", totalDiskon);
        row.put("komisi", komisi);
        data.add(row);
    }
    
    /**
     * Handle filter report table
     */
    private void handleFilterReportTable(DatePicker fromDate, DatePicker toDate) {
        if (fromDate.getValue() == null || toDate.getValue() == null) {
            showAlert("Peringatan", "Pilih tanggal range terlebih dahulu");
            return;
        }
        
        String from = fromDate.getValue().toString();
        String to = toDate.getValue().toString();
        
        showAlert("Sukses", "Laporan berhasil difilter untuk periode: " + from + " hingga " + to);
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
    private void handleDeleteUser() {
        int selectedIndex = userTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert("Peringatan", "Pilih user terlebih dahulu");
            return;
        }
        
        java.util.Map<String, String> selectedUser = userTable.getSelectionModel().getSelectedItem();
        String username = selectedUser.get("username");
        String fullName = selectedUser.get("fullName");
        
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        User user = authService.getUserByUsername(username);
        
        if (user == null) {
            showAlert("Error", "User tidak ditemukan");
            return;
        }
        
        if (currentUser.getUsername().equals(user.getUsername())) {
            showAlert("Error", "Tidak bisa menghapus user yang sedang login");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus User");
        confirm.setHeaderText(null);
        confirm.setContentText("Hapus user: " + fullName + "?\nTindakan ini tidak bisa dibatalkan!");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            user.setActive(false);
            authService.getAllUsers().put(user.getUsername(), user);
            System.out.println("✓ User " + user.getUsername() + " dihapus");
            showAlert("Sukses", "User berhasil dihapus");
            refreshUserList();
        }
    }
    
    private void handleNewUser() {
        boolean[] validationPassed = {false};
        
        while (!validationPassed[0]) {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Tambah User Baru");
            dialog.setHeaderText("Masukkan Data User Baru");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
            
            TextField userIdField = new TextField();
            userIdField.setPromptText("User ID (contoh: KSR002)");
            
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username (min 3 karakter)");
            
            TextField fullNameField = new TextField();
            fullNameField.setPromptText("Nama Lengkap");
            
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password (min 6 karakter)");
            TextField passwordVisibleField = new TextField();
            passwordVisibleField.setPromptText("Password");
            
            // Use StackPane to prevent column shift when showing/hiding password
            javafx.scene.layout.StackPane passwordStackPane = new javafx.scene.layout.StackPane();
            passwordStackPane.setPrefHeight(30);
            passwordStackPane.getChildren().addAll(passwordField, passwordVisibleField);
            passwordVisibleField.setVisible(false);
            
            CheckBox showPasswordCheck = new CheckBox("Tampilkan Password");
            showPasswordCheck.setOnAction(e -> {
                if (showPasswordCheck.isSelected()) {
                    passwordVisibleField.setText(passwordField.getText());
                    passwordField.setVisible(false);
                    passwordVisibleField.setVisible(true);
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
            VBox passBox = new VBox(5);
            passBox.getChildren().addAll(passwordStackPane, showPasswordCheck);
            grid.add(passBox, 1, 3);
            grid.add(new Label("Role:"), 0, 4);
            grid.add(roleCombo, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            
            ButtonType okButton = new ButtonType("Tambah", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Batal", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
            
            // Make dialog resizable
            dialog.getDialogPane().setPrefWidth(500);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButton) {
                    String userId = userIdField.getText().trim();
                    String username = usernameField.getText().trim();
                    String fullName = fullNameField.getText().trim();
                    String password = passwordField.isVisible() ? 
                        passwordField.getText().trim() : passwordVisibleField.getText().trim();
                    String role = roleCombo.getValue();
                    
                    // Validasi tidak boleh kosong
                    if (userId.isEmpty() || username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                        showAlert("Error", "Semua field harus diisi!");
                        return null;
                    }
                    
                    // Validasi username min 3 karakter
                    if (username.length() < 3) {
                        showAlert("Error", "Username minimal 3 karakter!");
                        return null;
                    }
                    
                    // Validasi password min 6 karakter
                    if (password.length() < 6) {
                        showAlert("Error", "Password minimal 6 karakter!");
                        return null;
                    }
                    
                    // Cek username sudah ada
                    AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
                    if (authService.getUserByUsername(username) != null) {
                        showAlert("Error", "Username '" + username + "' sudah terdaftar!");
                        return null;
                    }
                    
                    User newUser = new User(userId, username, password, fullName, role);
                    newUser.setActive(true);
                    return newUser;
                }
                return null;
            });
            
            var result = dialog.showAndWait();
            
            if (result.isPresent() && result.get() != null) {
                AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
                User newUser = result.get();
                
                try {
                    if (authService.registerUser(newUser)) {
                        showAlert("Sukses", "User '" + newUser.getUsername() + "' (" + 
                            newUser.getFullName() + ") berhasil ditambahkan sebagai " + newUser.getRole());
                        refreshUserList();
                        validationPassed[0] = true;
                    } else {
                        showAlert("Error", "Gagal menambahkan user. Cek validasi di backend.");
                    }
                } catch (Exception e) {
                    showAlert("Error", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // User clicked Cancel
                validationPassed[0] = true;
            }
        }
    }
    
    private void refreshUserList() {
        System.out.println("Refreshing user list...");
        userTable.getItems().clear();
        AuthServiceImpl authService = (AuthServiceImpl) authController.getAuthService();
        authService.getAllUsers().values().forEach(user -> {
            java.util.Map<String, String> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getUserId());
            userMap.put("username", user.getUsername());
            userMap.put("fullName", user.getFullName());
            userMap.put("role", user.getRole());
            userMap.put("status", user.isActive() ? "Aktif" : "Nonaktif");
            userTable.getItems().add(userMap);
        });
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
     * Handle export PDF
     */
    private void handleExportPDF() {
        try {
            String fileName = "laporan_agripos_" + java.time.LocalDate.now() + ".csv";
            
            // Coba buat di beberapa lokasi, mulai dari yang paling mungkin
            String filePath = null;
            
            // 1. Coba user.home/OneDrive/Desktop
            String[] possiblePaths = {
                System.getProperty("user.home") + "\\OneDrive\\Desktop\\" + fileName,
                System.getProperty("user.home") + "\\Desktop\\" + fileName,
                System.getProperty("user.dir") + "\\" + fileName
            };
            
            for (String path : possiblePaths) {
                try {
                    File file = new File(path);
                    file.getParentFile().mkdirs(); // Create directories if needed
                    filePath = path;
                    break;
                } catch (Exception e) {
                    // Try next path
                }
            }
            
            // Fallback to current directory if all else fails
            if (filePath == null) {
                filePath = System.getProperty("user.dir") + "\\" + fileName;
            }
            
            // Create FileWriter
            java.io.FileWriter writer = new java.io.FileWriter(filePath);
            
            // Write header
            writer.write("LAPORAN TRANSAKSI & PENJUALAN AGRI-POS\n");
            writer.write("Tanggal: " + java.time.LocalDate.now() + "\n");
            writer.write("\n");
            
            // Write table headers
            writer.write("Nama Barang,Harga (Rp),Kg,Jumlah Terjual,Tanggal,Total Transaksi,Total Penjualan (Rp),Total Diskon (Rp),Komisi (Rp)\n");
            
            // Write data rows
            String[][] reportData = {
                {"Beras 10kg", "120000", "10", "12", "15/01/2026", "1", "1440000", "144000", "28800"},
                {"Jagung 5kg", "45000", "5", "8", "14/01/2026", "1", "360000", "36000", "7200"},
                {"Wortel 5kg", "40000", "5", "6", "13/01/2026", "1", "240000", "24000", "4800"},
                {"Cabai 2kg", "60000", "2", "5", "12/01/2026", "1", "300000", "30000", "6000"},
                {"Bawang Putih 2kg", "50000", "2", "4", "11/01/2026", "1", "200000", "20000", "4000"},
                {"Kacang Hijau 5kg", "55000", "5", "3", "10/01/2026", "1", "165000", "16500", "3300"},
                {"Ketela Pohon 10kg", "35000", "10", "2", "09/01/2026", "1", "70000", "7000", "1400"},
                {"Tomat 5kg", "30000", "5", "7", "08/01/2026", "1", "210000", "21000", "4200"}
            };
            
            for (String[] row : reportData) {
                writer.write(String.join(",", row) + "\n");
            }
            
            writer.write("\n");
            writer.write("RINGKASAN\n");
            writer.write("Total Transaksi,24\n");
            writer.write("Total Penjualan,Rp 2.450.000\n");
            writer.write("Total Diskon,Rp 245.000\n");
            writer.write("Total Komisi,Rp 49.000\n");
            
            writer.close();
            
            showAlert("Sukses", "Laporan berhasil diekspor ke:\n" + filePath);
            System.out.println("✓ Laporan berhasil diekspor: " + filePath);
            
            // Buka file dengan aplikasi default
            try {
                Desktop.getDesktop().open(new File(filePath));
            } catch (Exception e) {
                System.out.println("File sudah dibuat: " + filePath);
            }
            
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan: " + e.getMessage());
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
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
