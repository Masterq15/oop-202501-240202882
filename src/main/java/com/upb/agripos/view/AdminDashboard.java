package com.upb.agripos.view;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthServiceImpl;
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
 * AdminDashboard - Interface untuk Admin
 * Menampilkan: User management, Product management, Reports
 * Person D - Frontend Week 15
 */
public class AdminDashboard {
    
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
        
        // Details panel
        VBox detailsBox = new VBox(5);
        detailsBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        Label detailLabel = new Label("Detail User:");
        detailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        
        TextArea detailsArea = new TextArea();
        detailsArea.setPrefHeight(150);
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        
        // On selection, show details
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String userId = newVal.split(" - ")[0];
                User user = authService.getUserByUsername(userId);
                if (user != null) {
                    StringBuilder details = new StringBuilder();
                    details.append("ID: ").append(user.getUserId()).append("\n");
                    details.append("Username: ").append(user.getUsername()).append("\n");
                    details.append("Nama Lengkap: ").append(user.getFullName()).append("\n");
                    details.append("Role: ").append(user.getRole()).append("\n");
                    details.append("Status: ").append(user.isActive() ? "Aktif" : "Nonaktif");
                    detailsArea.setText(details.toString());
                }
            }
        });
        
        detailsBox.getChildren().addAll(detailLabel, detailsArea);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            userListView,
            buttonBox,
            new Separator(),
            detailsBox
        );
        
        return panel;
    }
    
    /**
     * Create product management panel
     */
    private VBox createProductManagementPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 15;");
        
        Label titleLabel = new Label("Daftar Produk");
        titleLabel.setFont(new Font("System", 14));
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        // Product list (dummy data)
        productListView = new ListView<>();
        productListView.setPrefHeight(300);
        productListView.getItems().addAll(
            "P001 - Beras 10kg - Rp 120.000 (Stok: 45)",
            "P002 - Jagung 5kg - Rp 45.000 (Stok: 32)",
            "P003 - Kacang Hijau 5kg - Rp 55.000 (Stok: 28)",
            "P004 - Ketela Pohon 10kg - Rp 35.000 (Stok: 50)",
            "P005 - Wortel 5kg - Rp 40.000 (Stok: 15)",
            "P006 - Tomat 5kg - Rp 30.000 (Stok: 22)",
            "P007 - Cabai 2kg - Rp 60.000 (Stok: 8)",
            "P008 - Bawang Putih 2kg - Rp 50.000 (Stok: 18)"
        );
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addButton = new Button("➕ Tambah Produk");
        addButton.setStyle("-fx-padding: 8; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(event -> showAlert("Info", "Edit/Add product - TODO"));
        
        Button editButton = new Button("✏️ Edit Produk");
        editButton.setStyle("-fx-padding: 8;");
        editButton.setOnAction(event -> showAlert("Info", "Edit product - TODO"));
        
        Button deleteButton = new Button("❌ Hapus Produk");
        deleteButton.setStyle("-fx-padding: 8; -fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> showAlert("Info", "Delete product - TODO"));
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
        
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            productListView,
            buttonBox
        );
        
        return panel;
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
        filterButton.setOnAction(event -> showAlert("Info", "Filter laporan - TODO"));
        
        Button exportButton = new Button("📥 Export PDF");
        exportButton.setStyle("-fx-padding: 8; -fx-background-color: #2196F3; -fx-text-fill: white;");
        exportButton.setOnAction(event -> showAlert("Info", "Export PDF - TODO"));
        
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
        saveButton.setOnAction(event -> showAlert("Sukses", "Pengaturan tersimpan"));
        
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
        showAlert("Info", "Deactivate user - TODO");
    }
    
    private void handleNewUser() {
        showAlert("Info", "New user dialog - TODO");
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
