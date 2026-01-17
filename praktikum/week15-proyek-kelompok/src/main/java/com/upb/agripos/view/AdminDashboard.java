package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.User;
import com.upb.agripos.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * AdminDashboard - View untuk halaman admin
 * 
 * Fitur:
 * - Product Management (CRUD)
 * - Product Reports (stok rendah, dll)
 * - Transaction Reports (FR-4)
 * - Audit Log (FR-6)
 * - System Settings
 * 
 * Role-based: ADMIN ONLY
 * 
 * Created by: [Person D - Frontend]
 * Last modified: 2026-01-15
 */
public class AdminDashboard extends TabPane {
    
    // ===== SERVICES =====
    private ProductService productService;
    private TransactionService transactionService;
    private AuditLogService auditLogService;
    private User currentUser;
    
    // ===== TAB: PRODUCT MANAGEMENT =====
    private TextField txtProductCode, txtProductName, txtProductPrice, txtProductStock, txtReorderLevel;
    private ComboBox<String> cmbProductCategory, cmbProductStatus;
    private Button btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnRefreshProducts;
    private TableView<Product> productTable;
    private TableColumn<Product, String> colProductCode, colProductName, colProductCategory, colProductStatus;
    private TableColumn<Product, Double> colProductPrice;
    private TableColumn<Product, Integer> colProductStock, colProductReorder;
    
    // ===== TAB: REPORTS =====
    private DatePicker dpStartDate, dpEndDate;
    private Button btnGenerateReport, btnExportReport;
    private TextArea txtReportArea;
    private Label lblReportSummary;
    
    // ===== TAB: AUDIT LOG =====
    private TableView<String> auditTable;
    private TableColumn<String, String> colAuditLog;
    private Button btnRefreshAudit, btnClearAudit;
    
    // ===== TAB: SETTINGS =====
    private Label lblSystemStatus, lblLastSync;
    private Button btnBackup, btnRestore;
    
    public AdminDashboard(ProductService productService, 
                         TransactionService transactionService,
                         AuditLogService auditLogService,
                         User currentUser) {
        this.productService = productService;
        this.transactionService = transactionService;
        this.auditLogService = auditLogService;
        this.currentUser = currentUser;
        
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        
        try {
            initComponents();
            createTabs();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("ERROR initializing AdminDashboard: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        // ===== PRODUCT MANAGEMENT COMPONENTS =====
        txtProductCode = new TextField();
        txtProductCode.setPromptText("Kode Produk");
        txtProductCode.setPrefHeight(35);
        
        txtProductName = new TextField();
        txtProductName.setPromptText("Nama Produk");
        txtProductName.setPrefHeight(35);
        
        cmbProductCategory = new ComboBox<>();
        cmbProductCategory.setItems(FXCollections.observableArrayList(
            "Sayuran", "Buah", "Biji-bijian", "Rempah", "Lainnya"
        ));
        cmbProductCategory.setPrefHeight(35);
        
        txtProductPrice = new TextField();
        txtProductPrice.setPromptText("Harga (Rp)");
        txtProductPrice.setPrefHeight(35);
        
        txtProductStock = new TextField();
        txtProductStock.setPromptText("Stok Awal");
        txtProductStock.setPrefHeight(35);
        
        txtReorderLevel = new TextField();
        txtReorderLevel.setPromptText("Reorder Level");
        txtReorderLevel.setPrefHeight(35);
        
        cmbProductStatus = new ComboBox<>();
        cmbProductStatus.setItems(FXCollections.observableArrayList(
            "NORMAL", "LOW_STOCK", "DISCONTINUED"
        ));
        cmbProductStatus.setValue("NORMAL");
        cmbProductStatus.setPrefHeight(35);
        
        btnAddProduct = new Button("➕ Tambah Produk");
        btnAddProduct.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddProduct.setPrefHeight(35);
        
        btnUpdateProduct = new Button("✏️ Update");
        btnUpdateProduct.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnUpdateProduct.setPrefHeight(35);
        
        btnDeleteProduct = new Button("🗑️ Hapus");
        btnDeleteProduct.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeleteProduct.setPrefHeight(35);
        
        btnRefreshProducts = new Button("🔄 Refresh");
        btnRefreshProducts.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRefreshProducts.setPrefHeight(35);
        btnRefreshProducts.setOnAction(e -> loadProducts());
        
        productTable = new TableView<>();
        productTable.setPrefHeight(400);
        setupProductTableColumns();
        
        // ===== REPORT COMPONENTS =====
        dpStartDate = new DatePicker();
        dpStartDate.setPrefHeight(35);
        
        dpEndDate = new DatePicker();
        dpEndDate.setPrefHeight(35);
        
        btnGenerateReport = new Button("📊 Generate Report");
        btnGenerateReport.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnGenerateReport.setPrefHeight(35);
        
        btnExportReport = new Button("💾 Export to CSV");
        btnExportReport.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnExportReport.setPrefHeight(35);
        
        txtReportArea = new TextArea();
        txtReportArea.setWrapText(true);
        txtReportArea.setEditable(false);
        txtReportArea.setPrefHeight(400);
        
        lblReportSummary = new Label("Report Summary:");
        lblReportSummary.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // ===== AUDIT LOG COMPONENTS =====
        auditTable = new TableView<>();
        auditTable.setPrefHeight(400);
        colAuditLog = new TableColumn<>("Audit Log");
        colAuditLog.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        colAuditLog.setPrefWidth(800);
        auditTable.getColumns().add(colAuditLog);
        
        btnRefreshAudit = new Button("🔄 Refresh");
        btnRefreshAudit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRefreshAudit.setPrefHeight(35);
        btnRefreshAudit.setOnAction(e -> loadAuditLogs());
        
        btnClearAudit = new Button("🗑️ Clear Logs");
        btnClearAudit.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnClearAudit.setPrefHeight(35);
        
        // ===== SETTINGS COMPONENTS =====
        lblSystemStatus = new Label("System Status: ✅ RUNNING");
        lblSystemStatus.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblSystemStatus.setStyle("-fx-text-fill: #4CAF50;");
        
        lblLastSync = new Label("Last Sync: " + java.time.LocalDateTime.now());
        lblLastSync.setFont(Font.font("Arial", 12));
        
        btnBackup = new Button("💾 Backup Database");
        btnBackup.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnBackup.setPrefHeight(35);
        
        btnRestore = new Button("📂 Restore Database");
        btnRestore.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRestore.setPrefHeight(35);
    }
    
    private void setupProductTableColumns() {
        colProductCode = new TableColumn<>("Kode");
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colProductCode.setPrefWidth(80);
        
        colProductName = new TableColumn<>("Nama Produk");
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductName.setPrefWidth(120);
        
        colProductCategory = new TableColumn<>("Kategori");
        colProductCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colProductCategory.setPrefWidth(100);
        
        colProductPrice = new TableColumn<>("Harga");
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProductPrice.setPrefWidth(100);
        colProductPrice.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("Rp %,.0f", price));
            }
        });
        
        colProductStock = new TableColumn<>("Stok");
        colProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProductStock.setPrefWidth(70);
        
        colProductReorder = new TableColumn<>("Reorder Level");
        colProductReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
        colProductReorder.setPrefWidth(100);
        
        colProductStatus = new TableColumn<>("Status");
        colProductStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colProductStatus.setPrefWidth(100);
        colProductStatus.setCellFactory(col -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("LOW_STOCK".equals(status)) {
                        setStyle("-fx-text-fill: #FF9800;");
                    } else if ("DISCONTINUED".equals(status)) {
                        setStyle("-fx-text-fill: #f44336;");
                    }
                }
            }
        });
        
        productTable.getColumns().addAll(
            colProductCode, colProductName, colProductCategory, 
            colProductPrice, colProductStock, colProductReorder, colProductStatus
        );
    }
    
    private void createTabs() {
        // Tab 1: Product Management
        Tab tabProducts = new Tab("📦 Manajemen Produk", createProductManagementTab());
        tabProducts.setClosable(false);
        getTabs().add(tabProducts);
        
        // Tab 2: Reports
        Tab tabReports = new Tab("📊 Laporan", createReportsTab());
        tabReports.setClosable(false);
        getTabs().add(tabReports);
        
        // Tab 3: Audit Log
        Tab tabAudit = new Tab("📋 Audit Log", createAuditLogTab());
        tabAudit.setClosable(false);
        getTabs().add(tabAudit);
        
        // Tab 4: Settings
        Tab tabSettings = new Tab("⚙️ Pengaturan", createSettingsTab());
        tabSettings.setClosable(false);
        getTabs().add(tabSettings);
    }
    
    private VBox createProductManagementTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(15));
        
        Label title = new Label("📦 Manajemen Produk");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1976D2;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Kode:"), 0, 0);
        form.add(txtProductCode, 1, 0);
        form.add(new Label("Nama:"), 2, 0);
        form.add(txtProductName, 3, 0);
        form.add(new Label("Kategori:"), 0, 1);
        form.add(cmbProductCategory, 1, 1);
        form.add(new Label("Status:"), 2, 1);
        form.add(cmbProductStatus, 3, 1);
        form.add(new Label("Harga:"), 0, 2);
        form.add(txtProductPrice, 1, 2);
        form.add(new Label("Stok:"), 2, 2);
        form.add(txtProductStock, 3, 2);
        form.add(new Label("Reorder Level:"), 0, 3);
        form.add(txtReorderLevel, 1, 3);
        
        HBox buttons = new HBox(5);
        buttons.getChildren().addAll(btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnRefreshProducts);
        
        Label tableTitle = new Label("Daftar Produk:");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        tab.getChildren().addAll(
            title,
            form,
            buttons,
            new Separator(),
            tableTitle,
            productTable
        );
        
        return tab;
    }
    
    private VBox createReportsTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(15));
        
        Label title = new Label("📊 Laporan Penjualan");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1976D2;");
        
        HBox dateRange = new HBox(10);
        dateRange.getChildren().addAll(
            new Label("Dari:"), dpStartDate,
            new Label("Hingga:"), dpEndDate,
            btnGenerateReport, btnExportReport
        );
        
        tab.getChildren().addAll(
            title,
            dateRange,
            new Separator(),
            lblReportSummary,
            txtReportArea
        );
        
        return tab;
    }
    
    private VBox createAuditLogTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(15));
        
        Label title = new Label("📋 Audit Log");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1976D2;");
        
        HBox buttons = new HBox(5);
        buttons.getChildren().addAll(btnRefreshAudit, btnClearAudit);
        
        tab.getChildren().addAll(
            title,
            buttons,
            auditTable
        );
        
        return tab;
    }
    
    private VBox createSettingsTab() {
        VBox tab = new VBox(15);
        tab.setPadding(new Insets(15));
        
        Label title = new Label("⚙️ Pengaturan Sistem");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1976D2;");
        
        VBox statusBox = new VBox(10);
        statusBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 15;");
        statusBox.getChildren().addAll(lblSystemStatus, lblLastSync);
        
        HBox backupBox = new HBox(10);
        backupBox.getChildren().addAll(btnBackup, btnRestore);
        
        tab.getChildren().addAll(
            title,
            new Separator(),
            new Label("Status Sistem:"),
            statusBox,
            new Separator(),
            new Label("Database:"),
            backupBox
        );
        
        return tab;
    }
    
    private void loadProducts() {
        try {
            java.util.List<Product> products = productService.getAllProducts();
            ObservableList<Product> observableProducts = FXCollections.observableArrayList(products);
            productTable.setItems(observableProducts);
        } catch (Exception ex) {
            showAlert("Error", "Gagal memuat produk: " + ex.getMessage());
        }
    }
    
    private void loadAuditLogs() {
        // TODO: Load dari auditLogService
        ObservableList<String> logs = FXCollections.observableArrayList(
            "[2026-01-15 10:30] Admin login: " + currentUser.getUsername(),
            "[2026-01-15 10:35] Product added: PRD001",
            "[2026-01-15 10:40] Stock updated: PRD002 (100 -> 95)"
        );
        auditTable.setItems(logs);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ===== GETTER METHODS =====
    public Button getBtnAddProduct() { return btnAddProduct; }
    public Button getBtnUpdateProduct() { return btnUpdateProduct; }
    public Button getBtnDeleteProduct() { return btnDeleteProduct; }
    public Button getBtnGenerateReport() { return btnGenerateReport; }
    public Button getBtnExportReport() { return btnExportReport; }
    public Button getBtnBackup() { return btnBackup; }
    public Button getBtnRestore() { return btnRestore; }
    
    public TextField getTxtProductCode() { return txtProductCode; }
    public TextField getTxtProductName() { return txtProductName; }
    public TextField getTxtProductPrice() { return txtProductPrice; }
    public TextField getTxtProductStock() { return txtProductStock; }
    public TextField getTxtReorderLevel() { return txtReorderLevel; }
    
    public ComboBox<String> getCmbProductCategory() { return cmbProductCategory; }
    public ComboBox<String> getCmbProductStatus() { return cmbProductStatus; }
    
    public TableView<Product> getProductTable() { return productTable; }
    public TableView<String> getAuditTable() { return auditTable; }
    
    public TextArea getTxtReportArea() { return txtReportArea; }
    public Label getLblReportSummary() { return lblReportSummary; }
    
    public Product getSelectedProduct() {
        return productTable.getSelectionModel().getSelectedItem();
    }
}
