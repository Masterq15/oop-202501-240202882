package com.upb.agripos;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;

import com.upb.agripos.controller.AuthController;
import com.upb.agripos.controller.CartController;
import com.upb.agripos.controller.ProductController;
import com.upb.agripos.dao.DatabaseConfig;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.User;
import com.upb.agripos.service.ReportService;
import com.upb.agripos.service.UserService;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AppJavaFX - Aplikasi Utama Agri-POS Week 15
 * 
 * Arsitektur: View → Controller → Service → DAO → Database
 * Mengimplementasikan SOLID principles dan design patterns
 */
public class AppJavaFX extends Application {
    private AuthController authController;
    private ProductController productController;
    private CartController cartController;

    private User currentUser;

    // UI Components
    private StackPane mainContainer;
    private BorderPane adminDashboard;
    private BorderPane kasirDashboard;

    @Override
    public void start(Stage stage) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  AGRI-POS WEEK 15 - Proyek Kelompok");
        System.out.println("  NIM: 240202882 - Risky Dimas Nugroho");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Inisialisasi database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            dbConfig.initializeDatabase();
            dbConfig.seedInitialData();
            System.out.println("✓ Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("✗ Database initialization failed: " + e.getMessage());
            showErrorAndExit("Database Error", e.getMessage());
            return;
        }

        // Inisialisasi controllers
        authController = new AuthController();
        productController = new ProductController();
        cartController = new CartController();

        // Setup main container
        mainContainer = new StackPane();
        mainContainer.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        // Show login screen
        showLoginScreen();

        Scene scene = new Scene(mainContainer, 1200, 700);
        stage.setTitle("AGRI-POS - Point of Sale System (Week 15)");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * FR-5: Tampilan Login
     */
    private void showLoginScreen() {
        VBox loginContainer = new VBox(20);
        loginContainer.setPadding(new Insets(40));
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setStyle("-fx-background-color: linear-gradient(to right, #2ecc71, #27ae60);");

        // Card login
        VBox loginCard = new VBox(20);
        loginCard.setPadding(new Insets(30));
        loginCard.setStyle("-fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 5);");
        loginCard.setPrefWidth(400);

        Label title = new Label("AGRI-POS LOGIN");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Contoh: admin01 atau kasir01");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");

        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle("-fx-font-size: 14; -fx-padding: 10; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        loginBtn.setPrefWidth(Double.MAX_VALUE);

        Label infoLabel = new Label("Demo Akun:\nAdmin: admin01 / admin123\nKasir: kasir01 / kasir123");
        infoLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666; -fx-padding: 10; -fx-border-color: #eee; -fx-border-radius: 5;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        loginBtn.setOnAction(e -> {
            errorLabel.setText("");
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            authController.setAuthListener(new AuthController.AuthListener() {
                @Override
                public void onLoginSuccess(User user) {
                    currentUser = user;
                    System.out.println("✓ Login berhasil: " + user.getFullName() + " (" + user.getRole() + ")");
                    showMainDashboard();
                }

                @Override
                public void onLoginFailed(String errorMessage) {
                    errorLabel.setText("❌ " + errorMessage);
                    System.err.println("✗ Login failed: " + errorMessage);
                }
            });

            authController.handleLogin(username, password);
        });

        loginCard.getChildren().addAll(
            title,
            new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            loginBtn,
            infoLabel,
            errorLabel
        );

        loginContainer.getChildren().add(loginCard);
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(loginContainer);
    }

    /**
     * Tampilkan dashboard utama sesuai role
     */
    private void showMainDashboard() {
        mainContainer.getChildren().clear();

        if (authController.isAdmin()) {
            showAdminDashboard();
        } else if (authController.isKasir()) {
            showKasirDashboard();
        }
    }

    /**
     * FR-5: Dashboard Admin - Manajemen Produk
     */
    private void showAdminDashboard() {
        adminDashboard = new BorderPane();

        // Top: Header dengan logout
        HBox header = createHeader("ADMIN DASHBOARD - Manajemen Produk");
        adminDashboard.setTop(header);

        // Center: Tabs untuk Admin
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Manajemen User
        Tab userTab = new Tab("Manajemen User", createUserManagementPanel());
        userTab.setClosable(false);

        // Tab 2: Manajemen Produk
        Tab productTab = new Tab("Manajemen Produk", createProductManagementPanel());
        productTab.setClosable(false);

        // Tab 3: Laporan Penjualan
        Tab reportTab = new Tab("Laporan", createReportPanel());
        reportTab.setClosable(false);

        tabPane.getTabs().addAll(userTab, productTab, reportTab);
        adminDashboard.setCenter(tabPane);

        mainContainer.getChildren().add(adminDashboard);
    }

    /**
     * Panel Manajemen Produk untuk Admin
     */
    private VBox createProductManagementPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Form Input Produk
        HBox formRow1 = new HBox(10);
        Label codeLabel = new Label("Kode:");
        TextField codeField = new TextField();
        codeField.setPrefWidth(100);

        Label nameLabel = new Label("Nama:");
        TextField nameField = new TextField();
        nameField.setPrefWidth(200);

        Label categoryLabel = new Label("Kategori:");
        TextField categoryField = new TextField();
        categoryField.setPrefWidth(100);

        formRow1.getChildren().addAll(codeLabel, codeField, nameLabel, nameField, categoryLabel, categoryField);

        HBox formRow2 = new HBox(10);
        Label priceLabel = new Label("Harga:");
        TextField priceField = new TextField();
        priceField.setPrefWidth(120);

        Label stockLabel = new Label("Stok:");
        TextField stockField = new TextField();
        stockField.setPrefWidth(100);

        Button addBtn = new Button("Tambah Produk");
        addBtn.setStyle("-fx-padding: 8; -fx-font-weight: bold;");
        addBtn.setPrefWidth(150);

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-padding: 8;");
        clearBtn.setPrefWidth(80);

        formRow2.getChildren().addAll(priceLabel, priceField, stockLabel, stockField, addBtn, clearBtn);

        Label formErrorLabel = new Label();
        formErrorLabel.setStyle("-fx-text-fill: #e74c3c;");

        // Tabel Produk
        TableView<Product> productTable = new TableView<>();
        TableColumn<Product, String> codeColumn = new TableColumn<>("Kode");
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCode()));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Nama");
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Kategori");
        categoryColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory()));

        TableColumn<Product, String> priceColumn = new TableColumn<>("Harga");
        priceColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp " + String.format("%,.0f", cell.getValue().getPrice())));

        TableColumn<Product, String> stockColumn = new TableColumn<>("Stok");
        stockColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getStock())));

        productTable.getColumns().addAll(codeColumn, nameColumn, categoryColumn, priceColumn, stockColumn);

        // Setup listeners
        productController.setProductListener(new ProductController.ProductListener() {
            @Override
            public void onProductsLoaded(java.util.List<Product> products) {
                productTable.getItems().clear();
                productTable.getItems().addAll(products);
            }

            @Override
            public void onProductAdded(Product product) {
                formErrorLabel.setText("✓ Produk berhasil ditambahkan");
                formErrorLabel.setStyle("-fx-text-fill: #27ae60;");
                codeField.clear(); nameField.clear(); categoryField.clear();
                priceField.clear(); stockField.clear();
                productController.loadAllProducts();
            }

            @Override
            public void onProductUpdated(Product product) {
                formErrorLabel.setText("✓ Produk berhasil diupdate");
                formErrorLabel.setStyle("-fx-text-fill: #27ae60;");
                codeField.clear(); nameField.clear(); categoryField.clear();
                priceField.clear(); stockField.clear();
                codeField.setDisable(false);
                addBtn.setText("Tambah Produk");
                addBtn.setUserData(null);
                productController.loadAllProducts();
            }

            @Override
            public void onProductDeleted(int productId) {
                formErrorLabel.setText("✓ Produk berhasil dihapus");
                formErrorLabel.setStyle("-fx-text-fill: #27ae60;");
                productController.loadAllProducts();
            }

            @Override
            public void onError(String errorMessage) {
                formErrorLabel.setText("❌ " + errorMessage);
                formErrorLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        addBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String price = priceField.getText().trim();
            String stock = stockField.getText().trim();

            if (code.isEmpty() || name.isEmpty() || category.isEmpty() || price.isEmpty() || stock.isEmpty()) {
                formErrorLabel.setText("❌ Semua field harus diisi");
                formErrorLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            // Check if this is update or add
            Object productId = addBtn.getUserData();
            if (productId != null) {
                // Update mode
                productController.handleUpdateProduct((Integer) productId, code, name, category, price, stock);
            } else {
                // Add mode
                productController.handleAddProduct(code, name, category, price, stock);
            }
        });

        clearBtn.setOnAction(e -> {
            codeField.clear();
            codeField.setDisable(false);
            nameField.clear();
            categoryField.clear();
            priceField.clear();
            stockField.clear();
            addBtn.setText("Tambah Produk");
            addBtn.setUserData(null);
            formErrorLabel.setText("");
        });

        // Load produk
        productController.loadAllProducts();

        // Edit and Delete buttons
        HBox deleteRow = new HBox(10);
        Button editBtn = new Button("Edit Produk Terpilih");
        editBtn.setStyle("-fx-padding: 8; -fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                codeField.setText(selected.getCode());
                codeField.setDisable(true);
                nameField.setText(selected.getName());
                categoryField.setText(selected.getCategory());
                priceField.setText(String.valueOf(selected.getPrice()));
                stockField.setText(String.valueOf(selected.getStock()));
                addBtn.setText("Update Produk");
                addBtn.setUserData(selected.getId());
            } else {
                formErrorLabel.setText("❌ Pilih produk yang ingin diedit");
                formErrorLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });
        
        Button deleteBtn = new Button("Hapus Produk Terpilih");
        deleteBtn.setStyle("-fx-padding: 8; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                productController.handleDeleteProduct(selected.getId());
            } else {
                formErrorLabel.setText("❌ Pilih produk yang ingin dihapus");
                formErrorLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });
        deleteRow.getChildren().addAll(editBtn, deleteBtn);

        panel.getChildren().addAll(
            new Label("Input Produk Baru:"),
            formRow1, formRow2, formErrorLabel,
            new Separator(),
            new Label("Daftar Produk:"),
            productTable,
            deleteRow
        );

        return panel;
    }

    /**
     * Panel Laporan Penjualan
     */
    private VBox createReportPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label reportLabel = new Label("Laporan Penjualan");
        reportLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Filter controls dengan date range
        HBox filterBox = new HBox(15);
        filterBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        filterBox.setPrefHeight(60);

        Label fromLabel = new Label("Dari:");
        fromLabel.setStyle("-fx-font-weight: bold;");
        DatePicker fromDatePicker = new DatePicker(java.time.LocalDate.now().minusDays(30));
        fromDatePicker.setPrefWidth(120);

        Label sampaiLabel = new Label("Sampai:");
        sampaiLabel.setStyle("-fx-font-weight: bold;");
        DatePicker sampaiDatePicker = new DatePicker(java.time.LocalDate.now());
        sampaiDatePicker.setPrefWidth(120);

        Button tampilkanBtn = new Button("Tampilkan");
        tampilkanBtn.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        tampilkanBtn.setPrefWidth(100);

        Button downloadExcelBtn = new Button("Download ke Excel");
        downloadExcelBtn.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        downloadExcelBtn.setPrefWidth(120);

        filterBox.getChildren().addAll(
            fromLabel, fromDatePicker,
            sampaiLabel, sampaiDatePicker,
            tampilkanBtn, downloadExcelBtn
        );

        // Summary cards
        HBox summaryBox = new HBox(15);
        summaryBox.setPrefHeight(100);

        VBox totalTrxCard = createSummaryCard("Total Transaksi", "0", "#3498db");
        VBox totalRevenueCard = createSummaryCard("Total Pendapatan", "Rp0,00", "#27ae60");
        VBox tunaiCard = createSummaryCard("Tunai", "Rp0,00", "#ff9800");
        VBox ewalletCard = createSummaryCard("E-Wallet", "Rp0,00", "#9c27b0");

        summaryBox.getChildren().addAll(totalTrxCard, totalRevenueCard, tunaiCard, ewalletCard);

        // Tabel transaksi
        TableView<Transaction> transactionTable = new TableView<>();
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> codeColumn = new TableColumn<>("Kode Transaksi");
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCode()));
        codeColumn.setPrefWidth(150);

        TableColumn<Transaction, String> tanggalColumn = new TableColumn<>("Tanggal");
        tanggalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getTransactionDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        tanggalColumn.setPrefWidth(130);

        TableColumn<Transaction, String> kasirColumn = new TableColumn<>("Kasir");
        kasirColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCashierName()));
        kasirColumn.setPrefWidth(120);

        TableColumn<Transaction, String> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp" + String.format("%,.0f", cell.getValue().getSubtotal())));
        subtotalColumn.setPrefWidth(120);

        TableColumn<Transaction, String> totalColumn = new TableColumn<>("Total Harga");
        totalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp" + String.format("%,.0f", cell.getValue().getTotal())));
        totalColumn.setPrefWidth(120);

        TableColumn<Transaction, String> kembaliColumn = new TableColumn<>("Kembali");
        kembaliColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp" + String.format("%,.0f", cell.getValue().getChange())));
        kembaliColumn.setPrefWidth(120);

        TableColumn<Transaction, String> metodeColumn = new TableColumn<>("Metode");
        metodeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPaymentMethod()));
        metodeColumn.setPrefWidth(100);

        TableColumn<Transaction, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPaymentStatus()));
        statusColumn.setPrefWidth(100);

        transactionTable.getColumns().addAll(codeColumn, tanggalColumn, kasirColumn, subtotalColumn, totalColumn, kembaliColumn, metodeColumn, statusColumn);

        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        // Action handlers
        ReportService reportService = new ReportService();
        
        Runnable loadReport = () -> {
            try {
                java.time.LocalDate fromDate = fromDatePicker.getValue();
                java.time.LocalDate sampaiDate = sampaiDatePicker.getValue();
                
                System.out.println("Loading report from: " + fromDate + " to: " + sampaiDate);
                
                List<Transaction> transactions = reportService.getTransactionsByDateRange(
                    fromDate.toString(), sampaiDate.toString()
                );
                
                System.out.println("Found " + transactions.size() + " transactions");
                
                transactionTable.getItems().clear();
                transactionTable.getItems().addAll(transactions);
                
                // Update summary
                Map<String, Object> summary = reportService.calculateSummary(transactions);
                updateSummaryCards(
                    totalTrxCard, totalRevenueCard, tunaiCard, ewalletCard, summary
                );
            } catch (Exception ex) {
                System.err.println("Error loading report: " + ex.getMessage());
                ex.printStackTrace();
                showAlert("Error", "Gagal memuat laporan: " + ex.getMessage());
            }
        };

        tampilkanBtn.setOnAction(e -> loadReport.run());
        downloadExcelBtn.setOnAction(e -> {
            try {
                java.time.LocalDate fromDate = fromDatePicker.getValue();
                java.time.LocalDate sampaiDate = sampaiDatePicker.getValue();
                
                List<Transaction> transactions = reportService.getTransactionsByDateRange(
                    fromDate.toString(), sampaiDate.toString()
                );
                
                exportToExcel(transactions, fromDate, sampaiDate);
                showAlert("Sukses", "Laporan berhasil di-download ke Excel");
            } catch (Exception ex) {
                System.err.println("Error exporting report: " + ex.getMessage());
                ex.printStackTrace();
                showAlert("Error", "Gagal export laporan: " + ex.getMessage());
            }
        });

        // Auto-load laporan saat panel dibuka (tampilkan hari ini)
        loadReport.run();

        panel.getChildren().addAll(reportLabel, filterBox, summaryBox, transactionTable);
        return panel;
    }

    /**
     * Helper: buat card summary
     */
    private VBox createSummaryCard(String title, String value, String bgColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-radius: 8; -fx-background-color: " + bgColor + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: normal;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white; -fx-font-weight: bold;");
        valueLabel.setId("summaryValue");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    /**
     * Helper: update summary cards dengan data baru
     */
    private void updateSummaryCards(VBox totalTrxCard, VBox totalRevenueCard, VBox tunaiCard, VBox ewalletCard, Map<String, Object> summary) {
        updateCardValue(totalTrxCard, summary.get("totalTransactions").toString());
        updateCardValue(totalRevenueCard, "Rp" + String.format("%,.2f", summary.get("totalRevenue")));
        updateCardValue(tunaiCard, "Rp" + String.format("%,.2f", summary.get("tunaiTotal")));
        updateCardValue(ewalletCard, "Rp" + String.format("%,.2f", summary.get("ewalletTotal")));
    }

    /**
     * Helper: update nilai card
     */
    private void updateCardValue(VBox card, String newValue) {
        for (javafx.scene.Node node : card.getChildren()) {
            if (node instanceof Label && node.getId() != null && node.getId().equals("summaryValue")) {
                ((Label) node).setText(newValue);
            }
        }
    }

    /**
     * FR-5: Dashboard Kasir - Penjualan
     */
    private void showKasirDashboard() {
        kasirDashboard = new BorderPane();

        // Top: Header
        HBox header = createHeader("KASIR DASHBOARD - Transaksi Penjualan");
        kasirDashboard.setTop(header);

        // Left: Daftar Produk
        VBox productPanel = createKasirProductPanel();

        // Right: Keranjang dan Checkout
        VBox cartPanel = createKasirCartPanel();

        // Main content
        HBox mainContent = new HBox(10);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(
            new ScrollPane(productPanel),
            new Separator(javafx.geometry.Orientation.VERTICAL),
            cartPanel
        );

        kasirDashboard.setCenter(mainContent);
        mainContainer.getChildren().add(kasirDashboard);
    }

    /**
     * Panel Produk untuk Kasir
     */
    private VBox createKasirProductPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(400);

        Label title = new Label("PRODUK TERSEDIA");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        TableView<Product> productTable = new TableView<>();
        TableColumn<Product, String> codeColumn = new TableColumn<>("Kode");
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCode()));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Nama");
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        nameColumn.setPrefWidth(150);

        TableColumn<Product, String> priceColumn = new TableColumn<>("Harga");
        priceColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp " + String.format("%,.0f", cell.getValue().getPrice())));

        TableColumn<Product, String> stockColumn = new TableColumn<>("Stok");
        stockColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getStock())));

        productTable.getColumns().addAll(codeColumn, nameColumn, priceColumn, stockColumn);
        productTable.setPrefHeight(400);

        HBox addToCartBox = new HBox(5);
        Label qtyLabel = new Label("Qty:");
        TextField qtyField = new TextField("1");
        qtyField.setPrefWidth(50);

        Button addBtn = new Button("Tambah ke Keranjang");
        addBtn.setStyle("-fx-padding: 8; -fx-background-color: #3498db; -fx-text-fill: white;");
        addBtn.setPrefWidth(150);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 10;");

        addBtn.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                errorLabel.setText("❌ Pilih produk terlebih dahulu");
                return;
            }

            try {
                int qty = Integer.parseInt(qtyField.getText());
                if (qty <= 0) {
                    errorLabel.setText("❌ Kuantitas harus > 0");
                    return;
                }

                if (productController.validateProductStock(selected.getId(), qty)) {
                    cartController.handleAddToCart(selected, qty);
                    errorLabel.setText("✓ Ditambahkan ke keranjang");
                    errorLabel.setStyle("-fx-text-fill: #27ae60;");
                    qtyField.setText("1");
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("❌ Format kuantitas tidak valid");
            }
        });

        addToCartBox.getChildren().addAll(qtyLabel, qtyField, addBtn);

        productController.setProductListener(new ProductController.ProductListener() {
            @Override
            public void onProductsLoaded(java.util.List<Product> products) {
                productTable.getItems().clear();
                productTable.getItems().addAll(products);
            }

            @Override
            public void onProductAdded(Product product) {}

            @Override
            public void onProductUpdated(Product product) {}

            @Override
            public void onProductDeleted(int productId) {}

            @Override
            public void onError(String errorMessage) {
                errorLabel.setText("❌ " + errorMessage);
            }
        });

        productController.loadAllProducts();

        panel.getChildren().addAll(title, productTable, addToCartBox, errorLabel);
        return panel;
    }

    /**
     * Panel Keranjang dan Checkout untuk Kasir
     */
    private VBox createKasirCartPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(500);
        panel.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");

        Label title = new Label("KERANJANG BELANJA");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        TableView<CartItem> cartTable = new TableView<>();
        TableColumn<CartItem, String> productColumn = new TableColumn<>("Produk");
        productColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduct().getName()));

        TableColumn<CartItem, String> qtyColumn = new TableColumn<>("Qty");
        qtyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getQuantity())));

        TableColumn<CartItem, String> priceColumn = new TableColumn<>("Harga/Unit");
        priceColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp " + String.format("%,.0f", cell.getValue().getProduct().getPrice())));

        TableColumn<CartItem, String> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("Rp " + String.format("%,.0f", cell.getValue().getSubtotal())));

        cartTable.getColumns().addAll(productColumn, qtyColumn, priceColumn, subtotalColumn);
        cartTable.setPrefHeight(250);

        // Total display
        HBox totalBox = new HBox(20);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(10));
        Label totalLabel = new Label("TOTAL: Rp 0");
        totalLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        totalBox.getChildren().add(totalLabel);

        // Checkout section
        HBox checkoutBox = new HBox(5);
        Label paymentLabel = new Label("Metode Pembayaran :");
        paymentLabel.setPrefWidth(135);
        ComboBox<String> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Tunai", "E-Wallet");
        paymentCombo.setValue("Tunai");
        paymentCombo.setPrefWidth(90);

        Label inputLabel = new Label("Bayar :");
        inputLabel.setPrefWidth(60);
        TextField inputField = new TextField();
        inputField.setPrefWidth(180);
        inputField.setPromptText("Jumlah uang / Nomor akun");

        Button checkoutBtn = new Button("CHECKOUT");
        checkoutBtn.setStyle("-fx-padding: 10; -fx-font-size: 12; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white;");
        checkoutBtn.setPrefWidth(100);

        checkoutBox.getChildren().addAll(paymentLabel, paymentCombo, inputLabel, inputField, checkoutBtn);

        // Remove button
        HBox removeBox = new HBox(5);
        Button removeBtn = new Button("Hapus Item Terpilih");
        removeBtn.setStyle("-fx-padding: 5;");
        removeBtn.setOnAction(e -> {
            CartItem selected = cartTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartController.handleRemoveFromCart(selected.getProduct().getId());
            }
        });

        Button clearBtn = new Button("Kosongkan Keranjang");
        clearBtn.setStyle("-fx-padding: 5; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> cartController.handleClearCart());

        removeBox.getChildren().addAll(removeBtn, clearBtn);

        // Cart listener
        cartController.setCartListener(new CartController.CartListener() {
            @Override
            public void onItemAdded(CartItem item) {
                updateCartDisplay();
            }

            @Override
            public void onItemRemoved(int productId) {
                updateCartDisplay();
            }

            @Override
            public void onQuantityChanged(CartItem item) {
                updateCartDisplay();
            }

            @Override
            public void onCartCleared() {
                updateCartDisplay();
            }

            @Override
            public void onError(String errorMessage) {
                showAlert("Error", errorMessage);
            }

            @Override
            public void onCheckoutSuccess(String receipt) {
                showReceiptDialog(receipt);
                updateCartDisplay();
            }

            private void updateCartDisplay() {
                cartTable.getItems().clear();
                cartTable.getItems().addAll(cartController.getCartItems());
                totalLabel.setText("TOTAL: Rp " + String.format("%,.0f", cartController.getTotal()));
            }
        });

        checkoutBtn.setOnAction(e -> {
            if (cartController.isCartEmpty()) {
                showAlert("Error", "Keranjang kosong!");
                return;
            }

            String paymentMethod = paymentCombo.getValue();
            String input = inputField.getText().trim();

            if (input.isEmpty()) {
                showAlert("Error", "Masukkan jumlah uang atau nomor akun");
                return;
            }

            cartController.handleCheckout(paymentMethod, input, currentUser.getFullName());
            inputField.clear();
        });

        panel.getChildren().addAll(
            title,
            cartTable,
            totalBox,
            new Separator(),
            checkoutBox,
            removeBox
        );

        return panel;
    }

    /**
     * Buat header dengan logout button
     */
    private HBox createHeader(String title) {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2ecc71; -fx-border-color: #27ae60; -fx-border-width: 0 0 2 0;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("User: " + currentUser.getFullName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button logoutBtn = new Button("LOGOUT");
        logoutBtn.setStyle("-fx-padding: 8; -fx-background-color: white; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            authController.handleLogout();
            showLoginScreen();
        });

        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutBtn);
        return header;
    }

    /**
     * Helper: Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Helper: Show error and exit
     */
    private void showErrorAndExit(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Fatal Error");
        alert.setContentText(message);
        alert.setOnCloseRequest(e -> System.exit(1));
        alert.showAndWait();
    }

    /**
     * Panel Manajemen User untuk Admin
     * CRUD operation untuk user management
     */
    private VBox createUserManagementPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        UserService userService = new UserService();

        // ===== FORM INPUT USER =====
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label formTitle = new Label("Tambah/Edit User");
        formTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        HBox row1 = new HBox(10);
        Label usernameLabel = new Label("Username:");
        usernameLabel.setPrefWidth(100);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(200);
        usernameField.setPromptText("username");

        Label fullNameLabel = new Label("Nama Lengkap:");
        fullNameLabel.setPrefWidth(120);
        TextField fullNameField = new TextField();
        fullNameField.setPrefWidth(250);
        fullNameField.setPromptText("Nama lengkap user");

        row1.getChildren().addAll(usernameLabel, usernameField, fullNameLabel, fullNameField);

        HBox row2 = new HBox(10);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(100);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(200);
        passwordField.setPromptText("password");

        Label roleLabel = new Label("Role:");
        roleLabel.setPrefWidth(50);
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "KASIR");
        roleCombo.setValue("KASIR");
        roleCombo.setPrefWidth(100);

        Label activeLabel = new Label("Aktif:");
        activeLabel.setPrefWidth(50);
        ComboBox<String> activeCombo = new ComboBox<>();
        activeCombo.getItems().addAll("Ya", "Tidak");
        activeCombo.setValue("Ya");
        activeCombo.setPrefWidth(80);

        row2.getChildren().addAll(passwordLabel, passwordField, roleLabel, roleCombo, activeLabel, activeCombo);

        HBox buttonRow = new HBox(10);
        Button addUserBtn = new Button("Tambah User");
        addUserBtn.setStyle("-fx-padding: 8; -fx-font-weight: bold; -fx-font-size: 12;");
        addUserBtn.setPrefWidth(130);

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        clearBtn.setPrefWidth(80);

        Label formStatus = new Label();
        formStatus.setStyle("-fx-text-fill: #2e7d32;");

        buttonRow.getChildren().addAll(addUserBtn, clearBtn, new Region(), formStatus);
        HBox.setHgrow(buttonRow.getChildren().get(2), Priority.ALWAYS);

        formBox.getChildren().addAll(formTitle, row1, row2, buttonRow);
        panel.getChildren().add(formBox);

        // ===== TABLE USER LIST =====
        Label tableTitle = new Label("Daftar User");
        tableTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        TableView<User> userTable = new TableView<>();
        userTable.setPrefHeight(300);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameCol.setPrefWidth(120);

        TableColumn<User, String> fullNameCol = new TableColumn<>("Nama Lengkap");
        fullNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        fullNameCol.setPrefWidth(180);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        roleCol.setPrefWidth(80);

        TableColumn<User, String> activeCol = new TableColumn<>("Status");
        activeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().isActive() ? "Aktif" : "Nonaktif"));
        activeCol.setPrefWidth(80);

        TableColumn<User, String> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(110);

        // Setup action column dengan custom cell factory
        actionCol.setCellFactory(col -> {
            return new javafx.scene.control.TableCell<User, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        HBox actionBox = new HBox(5);
                        actionBox.setAlignment(Pos.CENTER);

                        Button editBtn = new Button("Edit");
                        editBtn.setPrefWidth(50);
                        editBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");

                        Button deleteBtn = new Button("Hapus");
                        deleteBtn.setPrefWidth(55);
                        deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");

                        User user = getTableRow().getItem();

                        editBtn.setOnAction(e -> {
                            usernameField.setText(user.getUsername());
                            usernameField.setDisable(true);
                            fullNameField.setText(user.getFullName());
                            passwordField.setText(user.getPassword());
                            roleCombo.setValue(user.getRole());
                            activeCombo.setValue(user.isActive() ? "Ya" : "Tidak");
                            addUserBtn.setText("Update User");
                            addUserBtn.setUserData(user.getId());
                        });

                        deleteBtn.setOnAction(e -> {
                            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmAlert.setTitle("Konfirmasi");
                            confirmAlert.setHeaderText("Hapus User");
                            confirmAlert.setContentText("Yakin hapus user '" + user.getUsername() + "'?");

                            if (confirmAlert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
                                try {
                                    userService.deleteUser(user.getId());
                                    showAlert("Sukses", "User berhasil dihapus");
                                    loadUserData(userTable, userService);
                                    formStatus.setText("User berhasil dihapus");
                                } catch (Exception ex) {
                                    showAlert("Error", "Gagal hapus user: " + ex.getMessage());
                                }
                            }
                        });

                        actionBox.getChildren().addAll(editBtn, deleteBtn);
                        setGraphic(actionBox);
                    }
                }
            };
        });

        userTable.getColumns().addAll(idCol, usernameCol, fullNameCol, roleCol, activeCol, actionCol);

        // Load user list on init
        loadUserData(userTable, userService);

        // Button handlers
        addUserBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();
                String fullName = fullNameField.getText().trim();
                String password = passwordField.getText().trim();
                String role = roleCombo.getValue();

                if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                    showAlert("Validasi", "Semua field harus diisi");
                    return;
                }

                Object userDataId = addUserBtn.getUserData();
                if (userDataId != null) {
                    // Update existing user
                    int userId = (Integer) userDataId;
                    boolean active = activeCombo.getValue().equals("Ya");
                    userService.updateUser(userId, password, fullName, active);
                    showAlert("Sukses", "User berhasil diupdate");

                    // Reset form
                    resetUserForm(usernameField, fullNameField, passwordField, roleCombo, activeCombo, addUserBtn, formStatus);
                    loadUserData(userTable, userService);
                    formStatus.setText("User berhasil diupdate");
                } else {
                    // Add new user
                    userService.createUser(username, password, fullName, role);
                    showAlert("Sukses", "User berhasil ditambahkan");

                    resetUserForm(usernameField, fullNameField, passwordField, roleCombo, activeCombo, addUserBtn, formStatus);
                    loadUserData(userTable, userService);
                    formStatus.setText("User berhasil ditambahkan");
                }
            } catch (Exception ex) {
                showAlert("Error", "Gagal proses user: " + ex.getMessage());
            }
        });

        clearBtn.setOnAction(e -> {
            resetUserForm(usernameField, fullNameField, passwordField, roleCombo, activeCombo, addUserBtn, formStatus);
        });

        panel.getChildren().addAll(tableTitle, userTable);

        return panel;
    }

    /**
     * Helper: Load data user ke table
     */
    private void loadUserData(TableView<User> userTable, UserService userService) {
        try {
            userTable.getItems().clear();
            List<User> users = userService.getAllUsers();
            userTable.getItems().addAll(users);
        } catch (Exception e) {
            showAlert("Error", "Gagal load user: " + e.getMessage());
        }
    }

    /**
     * Helper: Reset form user
     */
    private void resetUserForm(TextField usernameField, TextField fullNameField, PasswordField passwordField,
                               ComboBox<String> roleCombo, ComboBox<String> activeCombo, Button addUserBtn, Label formStatus) {
        usernameField.clear();
        usernameField.setDisable(false);
        fullNameField.clear();
        passwordField.clear();
        roleCombo.setValue("KASIR");
        activeCombo.setValue("Ya");
        addUserBtn.setText("Tambah User");
        addUserBtn.setUserData(null);
        formStatus.setText("");
    }

    /**
     * Dialog untuk menampilkan struk dengan opsi cetak
     */
    private void showReceiptDialog(String receiptText) {
        // Create dialog window
        javafx.stage.Stage receiptStage = new javafx.stage.Stage();
        receiptStage.setTitle("Struk Pembelian");
        receiptStage.setWidth(400);
        receiptStage.setHeight(600);
        receiptStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox dialogBox = new VBox(15);
        dialogBox.setPadding(new Insets(15));
        dialogBox.setStyle("-fx-border-color: #e0e0e0;");

        // Title
        Label titleLabel = new Label("STRUK PEMBELIAN");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);

        // Receipt content in TextArea
        javafx.scene.control.TextArea receiptArea = new javafx.scene.control.TextArea();
        receiptArea.setText(receiptText);
        receiptArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11;");
        receiptArea.setWrapText(true);
        receiptArea.setEditable(false);
        receiptArea.setPrefHeight(400);

        // Button box
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button printBtn = new Button("🖨 Cetak Struk");
        printBtn.setStyle("-fx-padding: 10; -fx-font-weight: bold; -fx-font-size: 12;");
        printBtn.setPrefWidth(150);
        printBtn.setOnAction(e -> {
            printReceipt(receiptText);
            showAlert("Sukses", "Struk telah dikirim ke printer");
        });

        Button copyBtn = new Button("📋 Salin");
        copyBtn.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        copyBtn.setPrefWidth(100);
        copyBtn.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(receiptText);
            clipboard.setContent(content);
            showAlert("Sukses", "Struk telah disalin ke clipboard");
        });

        Button closeBtn = new Button("Tutup");
        closeBtn.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        closeBtn.setPrefWidth(100);
        closeBtn.setOnAction(e -> receiptStage.close());

        buttonBox.getChildren().addAll(printBtn, copyBtn, closeBtn);

        dialogBox.getChildren().addAll(titleLabel, receiptArea, buttonBox);

        Scene scene = new Scene(dialogBox);
        receiptStage.setScene(scene);
        receiptStage.showAndWait();
    }

    /**
     * Print receipt ke printer default sistem
     */
    private void printReceipt(String receiptText) {
        try {
            // Buat PrinterJob
            javafx.print.PrinterJob printerJob = javafx.print.PrinterJob.createPrinterJob();
            
            if (printerJob != null) {
                // Buat TextArea untuk print
                javafx.scene.control.TextArea printArea = new javafx.scene.control.TextArea();
                printArea.setText(receiptText);
                printArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10;");
                printArea.setWrapText(true);
                printArea.setEditable(false);

                // Show print dialog
                if (printerJob.showPrintDialog(mainContainer.getScene().getWindow())) {
                    // Print ke printer
                    boolean success = printerJob.printPage(printArea);
                    
                    if (success) {
                        printerJob.endJob();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error printing: " + e.getMessage());
        }
    }

    /**
     * Export laporan ke file Excel dengan format rapi
     */
    private void exportToExcel(List<Transaction> transactions, 
                               java.time.LocalDate fromDate, 
                               java.time.LocalDate sampaiDate) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Laporan Penjualan");
        
        // Set column widths
        sheet.setColumnWidth(0, 18 * 256);  // Kode Transaksi
        sheet.setColumnWidth(1, 18 * 256);  // Tanggal
        sheet.setColumnWidth(2, 15 * 256);  // Kasir
        sheet.setColumnWidth(3, 15 * 256);  // Subtotal
        sheet.setColumnWidth(4, 15 * 256);  // Total Harga
        sheet.setColumnWidth(5, 12 * 256);  // Kembali
        sheet.setColumnWidth(6, 15 * 256);  // Metode
        sheet.setColumnWidth(7, 12 * 256);  // Status
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        
        // Create title
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("LAPORAN PENJUALAN");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));
        
        // Create date range row
        XSSFRow dateRow = sheet.createRow(1);
        XSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Periode: " + fromDate + " hingga " + sampaiDate);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 7));
        
        // Create header row for columns
        XSSFRow headerRowData = sheet.createRow(3);
        String[] headers = {"Kode Transaksi", "Tanggal", "Kasir", "Subtotal", "Total Harga", "Kembali", "Metode", "Status"};
        
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRowData.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create number style for currency
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        // Fill data
        int rowNum = 4;
        long totalAmount = 0;
        int totalTransactions = 0;
        
        for (Transaction transaction : transactions) {
            XSSFRow row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(transaction.getCode());
            row.createCell(1).setCellValue(
                transaction.getTransactionDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            );
            row.createCell(2).setCellValue(transaction.getCashierName());
            
            XSSFCell subtotalCell = row.createCell(3);
            subtotalCell.setCellValue(transaction.getSubtotal());
            subtotalCell.setCellStyle(currencyStyle);
            
            XSSFCell totalCell = row.createCell(4);
            totalCell.setCellValue(transaction.getTotal());
            totalCell.setCellStyle(currencyStyle);
            
            XSSFCell changeCell = row.createCell(5);
            changeCell.setCellValue(transaction.getChange());
            changeCell.setCellStyle(currencyStyle);
            
            row.createCell(6).setCellValue(transaction.getPaymentMethod());
            row.createCell(7).setCellValue(transaction.getPaymentStatus());
            
            totalAmount += transaction.getTotal();
            totalTransactions++;
        }
        
        // Add summary rows
        int summaryRow = rowNum + 1;
        XSSFRow summaryHeaderRow = sheet.createRow(summaryRow);
        XSSFCell summaryHeaderCell = summaryHeaderRow.createCell(0);
        summaryHeaderCell.setCellValue("RINGKASAN");
        summaryHeaderCell.setCellStyle(headerStyle);
        
        XSSFRow totalTrxRow = sheet.createRow(summaryRow + 1);
        totalTrxRow.createCell(0).setCellValue("Total Transaksi:");
        XSSFCell totalTrxCell = totalTrxRow.createCell(4);
        totalTrxCell.setCellValue(totalTransactions);
        
        XSSFRow totalAmountRow = sheet.createRow(summaryRow + 2);
        totalAmountRow.createCell(0).setCellValue("Total Pendapatan:");
        XSSFCell totalAmountCell = totalAmountRow.createCell(4);
        totalAmountCell.setCellValue(totalAmount);
        totalAmountCell.setCellStyle(currencyStyle);
        
        // Save file
        String fileName = "Laporan_Penjualan_" + java.time.LocalDate.now() + ".xlsx";
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName;
        
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Excel file created: " + filePath);
        } finally {
            workbook.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
