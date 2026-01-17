package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.CartItem;
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
 * PosView - Main View untuk Point of Sale System
 *
 * Integrasi lengkap antara:
 * - Product Management (CRUD produk dari ProductService)
 * - Shopping Cart (keranjang belanja dengan CartItem)
 * - Checkout System (dengan TransactionService & PaymentMethod)
 * - Discount System (DiscountStrategy)
 *
 * Layout menggunakan BorderPane dengan sections:
 * - TOP: Title + User Info
 * - LEFT: Product Management
 * - CENTER: Shopping Cart
 * - BOTTOM: Summary & Checkout
 * 
 * Created by: [Person D - Frontend]
 * Last modified: 2026-01-15 - Integration dengan services
 */
public class PosView extends BorderPane {

    // ===== SERVICES =====
    private ProductService productService;
    private TransactionService transactionService;
    private AuthService authService;
    private DiscountStrategy discountStrategy;
    private PaymentMethod paymentMethod;
    
    // Current user
    private User currentUser;
    
    // ===== CART DATA =====
    private ObservableList<CartItem> cartItems;
    
    // ===== PRODUCT MANAGEMENT COMPONENTS =====
    private TextField txtProductCode, txtProductName, txtProductPrice, txtProductStock;
    private ComboBox<String> cmbProductCategory;
    private Button btnAddProduct, btnDeleteProduct, btnRefreshProduct;
    private TableView<Product> productTable;
    private TableColumn<Product, String> colProductCode, colProductName, colProductCategory, colProductStatus;
    private TableColumn<Product, Double> colProductPrice;
    private TableColumn<Product, Integer> colProductStock;

    // ===== CART MANAGEMENT COMPONENTS =====
    private TextField txtAddQty;
    private Button btnAddToCart, btnRemoveFromCart, btnClearCart, btnCheckout;
    private TableView<CartItem> cartTable;
    private TableColumn<CartItem, String> colCartName, colCartCode;
    private TableColumn<CartItem, Integer> colCartQty;
    private TableColumn<CartItem, Double> colCartSubtotal;

    // ===== PAYMENT COMPONENTS =====
    private ComboBox<String> cmbPaymentMethod;
    private ComboBox<String> cmbDiscount;
    private TextField txtDiscount;
    private Label lblDiscount;

    // ===== SUMMARY COMPONENTS =====
    private Label lblTotalItems, lblTotalQty, lblSubtotal, lblDiscount2, lblGrandTotal;
    private Label lblUserInfo, lblStockWarning;

    public PosView(ProductService productService, 
                   TransactionService transactionService,
                   AuthService authService,
                   User currentUser) {
        this.productService = productService;
        this.transactionService = transactionService;
        this.authService = authService;
        this.currentUser = currentUser;
        this.cartItems = FXCollections.observableArrayList();
        
        try {
            initComponents();
            layoutComponents();
            setupTableColumns();
            loadProducts();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("ERROR initializing PosView: " + ex.getMessage());
        }
    }

    private void initComponents() {
        // ===== PRODUCT FORM COMPONENTS =====
        txtProductCode = new TextField();
        txtProductCode.setPromptText("Kode Produk");
        txtProductCode.setPrefHeight(35);

        txtProductName = new TextField();
        txtProductName.setPromptText("Nama Produk");
        txtProductName.setPrefHeight(35);

        txtProductPrice = new TextField();
        txtProductPrice.setPromptText("Harga (Rp)");
        txtProductPrice.setPrefHeight(35);

        txtProductStock = new TextField();
        txtProductStock.setPromptText("Stok");
        txtProductStock.setPrefHeight(35);
        
        cmbProductCategory = new ComboBox<>();
        cmbProductCategory.setItems(FXCollections.observableArrayList(
            "Sayuran", "Buah", "Biji-bijian", "Rempah", "Lainnya"
        ));
        cmbProductCategory.setPrefHeight(35);

        btnAddProduct = new Button("➕ Tambah Produk");
        btnAddProduct.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnAddProduct.setPrefHeight(35);
        btnAddProduct.setDisable(!currentUser.isAdmin());  // Only admin

        btnDeleteProduct = new Button("🗑️ Hapus");
        btnDeleteProduct.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnDeleteProduct.setPrefHeight(35);
        btnDeleteProduct.setDisable(!currentUser.isAdmin());  // Only admin

        btnRefreshProduct = new Button("🔄 Refresh");
        btnRefreshProduct.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnRefreshProduct.setPrefHeight(35);

        // ===== PRODUCT TABLE =====
        productTable = new TableView<>();
        productTable.setPrefHeight(250);

        // ===== CART COMPONENTS =====
        txtAddQty = new TextField();
        txtAddQty.setPromptText("Qty");
        txtAddQty.setPrefWidth(60);
        txtAddQty.setPrefHeight(35);

        btnAddToCart = new Button("🛒 Tambah ke Keranjang");
        btnAddToCart.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnAddToCart.setPrefHeight(35);

        btnRemoveFromCart = new Button("❌ Hapus dari Keranjang");
        btnRemoveFromCart.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnRemoveFromCart.setPrefHeight(35);

        btnClearCart = new Button("🗑️ Kosongkan");
        btnClearCart.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        btnClearCart.setPrefHeight(35);

        btnCheckout = new Button("💰 CHECKOUT");
        btnCheckout.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCheckout.setPrefWidth(150);
        btnCheckout.setPrefHeight(40);

        // ===== CART TABLE =====
        cartTable = new TableView<>();
        cartTable.setItems(cartItems);
        cartTable.setPrefHeight(200);

        // ===== PAYMENT COMPONENTS =====
        cmbPaymentMethod = new ComboBox<>();
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("TUNAI", "E-WALLET"));
        cmbPaymentMethod.setValue("TUNAI");
        cmbPaymentMethod.setPrefHeight(35);
        
        cmbDiscount = new ComboBox<>();
        cmbDiscount.setItems(FXCollections.observableArrayList("TIDAK ADA", "TETAP (Rp)", "PERSEN (%)"));
        cmbDiscount.setValue("TIDAK ADA");
        cmbDiscount.setPrefHeight(35);
        
        txtDiscount = new TextField();
        txtDiscount.setPromptText("Nominal/Persen");
        txtDiscount.setPrefHeight(35);
        txtDiscount.setDisable(true);

        // ===== SUMMARY LABELS =====
        lblUserInfo = new Label("User: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        lblUserInfo.setFont(Font.font("Arial", 12));
        lblUserInfo.setStyle("-fx-text-fill: #666666;");

        lblTotalItems = new Label("Total Items: 0 jenis");
        lblTotalItems.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        lblTotalQty = new Label("Total Qty: 0 pcs");
        lblTotalQty.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        lblSubtotal = new Label("Subtotal: Rp 0.00");
        lblSubtotal.setFont(Font.font("Arial", 12));

        lblDiscount2 = new Label("Diskon: Rp 0.00");
        lblDiscount2.setFont(Font.font("Arial", 12));

        lblGrandTotal = new Label("TOTAL: Rp 0.00");
        lblGrandTotal.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblGrandTotal.setStyle("-fx-text-fill: #4CAF50;");

        lblStockWarning = new Label();
        lblStockWarning.setFont(Font.font("Arial", 11));
        lblStockWarning.setStyle("-fx-text-fill: #FF9800;");
        
        // Setup event handlers
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // Add product button
        btnAddProduct.setOnAction(e -> handleAddProduct());
        
        // Delete product button
        btnDeleteProduct.setOnAction(e -> handleDeleteProduct());
        
        // Discount type change
        cmbDiscount.setOnAction(e -> {
            boolean enabled = !cmbDiscount.getValue().equals("TIDAK ADA");
            txtDiscount.setDisable(!enabled);
            if (!enabled) {
                txtDiscount.clear();
            }
        });
        
        // Refresh product table
        btnRefreshProduct.setOnAction(e -> loadProducts());
    }

    private void setupTableColumns() {
        // ===== PRODUCT TABLE COLUMNS =====
        colProductCode = new TableColumn<>("Kode");
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colProductCode.setPrefWidth(60);

        colProductName = new TableColumn<>("Nama Produk");
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductName.setPrefWidth(130);

        colProductCategory = new TableColumn<>("Kategori");
        colProductCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colProductCategory.setPrefWidth(80);

        colProductPrice = new TableColumn<>("Harga");
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProductPrice.setPrefWidth(80);
        colProductPrice.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", price));
                }
            }
        });

        colProductStock = new TableColumn<>("Stok");
        colProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProductStock.setPrefWidth(50);

        colProductStatus = new TableColumn<>("Status");
        colProductStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colProductStatus.setPrefWidth(80);

        productTable.getColumns().addAll(colProductCode, colProductName, colProductCategory, colProductPrice, colProductStock, colProductStatus);

        // ===== CART TABLE COLUMNS =====
        colCartCode = new TableColumn<>("Kode");
        colCartCode.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct().getCode()));
        colCartCode.setPrefWidth(60);

        colCartName = new TableColumn<>("Nama Produk");
        colCartName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct().getName()));
        colCartName.setPrefWidth(130);

        colCartQty = new TableColumn<>("Qty");
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartQty.setPrefWidth(50);

        colCartSubtotal = new TableColumn<>("Subtotal");
        colCartSubtotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
            cellData.getValue().getSubtotal()).asObject());
        colCartSubtotal.setPrefWidth(100);
        colCartSubtotal.setCellFactory(col -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", subtotal));
                }
            }
        });

        cartTable.getColumns().addAll(colCartCode, colCartName, colCartQty, colCartSubtotal);
    }

    private void layoutComponents() {
        setPadding(new Insets(15));

        // ===== TOP: TITLE + USER INFO =====
        VBox topBox = new VBox(5);
        Label lblTitle = new Label("🛒 AGRI-POS - Point of Sale System");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitle.setStyle("-fx-text-fill: #2E7D32;");
        topBox.getChildren().addAll(lblTitle, lblUserInfo);
        setTop(topBox);
        BorderPane.setMargin(topBox, new Insets(0, 0, 15, 0));

        // ===== LEFT: PRODUCT MANAGEMENT =====
        VBox productSection = createProductSection();
        setLeft(productSection);
        BorderPane.setMargin(productSection, new Insets(0, 15, 0, 0));

        // ===== CENTER: CART + PAYMENT =====
        VBox centerSection = createCenterSection();
        setCenter(centerSection);

        // ===== BOTTOM: SUMMARY & CHECKOUT =====
        HBox summarySection = createSummarySection();
        setBottom(summarySection);
        BorderPane.setMargin(summarySection, new Insets(15, 0, 0, 0));
    }

    private VBox createProductSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(400);

        // Title
        Label lblProductTitle = new Label("📦 Manajemen Produk");
        lblProductTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblProductTitle.setStyle("-fx-text-fill: #1976D2;");

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(5);
        form.add(new Label("Kode:"), 0, 0);
        form.add(txtProductCode, 1, 0);
        form.add(new Label("Nama:"), 0, 1);
        form.add(txtProductName, 1, 1);
        form.add(new Label("Kategori:"), 0, 2);
        form.add(cmbProductCategory, 1, 2);
        form.add(new Label("Harga:"), 0, 3);
        form.add(txtProductPrice, 1, 3);
        form.add(new Label("Stok:"), 0, 4);
        form.add(txtProductStock, 1, 4);

        // Buttons
        HBox productButtons = new HBox(5);
        productButtons.getChildren().addAll(btnAddProduct, btnDeleteProduct, btnRefreshProduct);

        // Table
        Label lblProductTable = new Label("Daftar Produk:");
        lblProductTable.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        section.getChildren().addAll(
            lblProductTitle,
            form,
            productButtons,
            new Separator(),
            lblProductTable,
            productTable
        );

        return section;
    }

    private VBox createCenterSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(400);

        // Cart title
        Label lblCartTitle = new Label("🛒 Keranjang Belanja");
        lblCartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblCartTitle.setStyle("-fx-text-fill: #FF9800;");

        // Add to cart controls
        HBox addToCartBox = new HBox(10);
        addToCartBox.getChildren().addAll(
            new Label("Qty:"), txtAddQty, btnAddToCart
        );

        // Cart buttons
        HBox cartButtons = new HBox(5);
        cartButtons.getChildren().addAll(btnRemoveFromCart, btnClearCart);

        // Table
        Label lblCartTable = new Label("Isi Keranjang:");
        lblCartTable.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        // Payment section
        Label lblPaymentTitle = new Label("💳 Metode Pembayaran & Diskon");
        lblPaymentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblPaymentTitle.setStyle("-fx-text-fill: #1976D2;");

        GridPane paymentForm = new GridPane();
        paymentForm.setHgap(10);
        paymentForm.setVgap(5);
        paymentForm.add(new Label("Metode:"), 0, 0);
        paymentForm.add(cmbPaymentMethod, 1, 0);
        paymentForm.add(new Label("Diskon:"), 0, 1);
        paymentForm.add(cmbDiscount, 1, 1);
        paymentForm.add(lblDiscount, 0, 2);
        paymentForm.add(txtDiscount, 1, 2);

        section.getChildren().addAll(
            lblCartTitle,
            addToCartBox,
            cartButtons,
            new Separator(),
            lblCartTable,
            cartTable,
            new Separator(),
            lblPaymentTitle,
            paymentForm,
            lblStockWarning
        );

        return section;
    }

    private HBox createSummarySection() {
        HBox section = new HBox(30);
        section.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 15; -fx-background-radius: 5;");

        // Summary info
        VBox summaryInfo = new VBox(5);
        summaryInfo.getChildren().addAll(lblTotalItems, lblTotalQty, lblSubtotal, lblDiscount2);

        // Total and checkout
        HBox checkoutBox = new HBox(20);
        checkoutBox.setAlignment(Pos.CENTER_RIGHT);
        checkoutBox.getChildren().addAll(lblGrandTotal, btnCheckout);

        section.getChildren().addAll(summaryInfo, new Region(), checkoutBox);
        HBox.setHgrow(section.getChildren().get(1), Priority.ALWAYS);

        return section;
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

    private void handleAddProduct() {
        try {
            // Validasi input
            if (txtProductCode.getText().isEmpty() || 
                txtProductName.getText().isEmpty() || 
                txtProductPrice.getText().isEmpty() || 
                txtProductStock.getText().isEmpty() ||
                cmbProductCategory.getValue() == null) {
                showAlert("Validasi", "Semua field harus diisi!");
                return;
            }

            // Buat product baru
            String code = txtProductCode.getText();
            String name = txtProductName.getText();
            String category = cmbProductCategory.getValue();
            double price;
            int stock;
            
            try {
                price = Double.parseDouble(txtProductPrice.getText());
                stock = Integer.parseInt(txtProductStock.getText());
            } catch (NumberFormatException ex) {
                showAlert("Validasi", "Harga dan Stok harus berupa angka!");
                return;
            }

            // Simpan ke service
            Product product = new Product(code, name, category, price, stock);
            productService.addProduct(product);
            showAlert("Sukses", "Produk berhasil ditambahkan!");
            clearProductForm();
            loadProducts();
        } catch (Exception ex) {
            showAlert("Error", "Gagal menambah produk: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleDeleteProduct() {
        try {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                showAlert("Validasi", "Pilih produk yang ingin dihapus!");
                return;
            }

            // Confirm delete
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi");
            confirmAlert.setContentText("Apakah Anda yakin ingin menghapus produk: " + selectedProduct.getName() + "?");
            java.util.Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
            
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                productService.deleteProduct(selectedProduct.getCode());
                showAlert("Sukses", "Produk berhasil dihapus!");
                loadProducts();
            }
        } catch (Exception ex) {
            showAlert("Error", "Gagal menghapus produk: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== GETTER METHODS =====

    // Product Management Getters
    public TextField getTxtProductCode() { return txtProductCode; }
    public TextField getTxtProductName() { return txtProductName; }
    public TextField getTxtProductPrice() { return txtProductPrice; }
    public TextField getTxtProductStock() { return txtProductStock; }
    public ComboBox<String> getCmbProductCategory() { return cmbProductCategory; }
    public Button getBtnAddProduct() { return btnAddProduct; }
    public Button getBtnDeleteProduct() { return btnDeleteProduct; }
    public Button getBtnRefreshProduct() { return btnRefreshProduct; }
    public TableView<Product> getProductTable() { return productTable; }

    // Cart Management Getters
    public TextField getTxtAddQty() { return txtAddQty; }
    public Button getBtnAddToCart() { return btnAddToCart; }
    public Button getBtnRemoveFromCart() { return btnRemoveFromCart; }
    public Button getBtnClearCart() { return btnClearCart; }
    public Button getBtnCheckout() { return btnCheckout; }
    public TableView<CartItem> getCartTable() { return cartTable; }
    public ObservableList<CartItem> getCartItems() { return cartItems; }

    // Payment Getters
    public ComboBox<String> getCmbPaymentMethod() { return cmbPaymentMethod; }
    public ComboBox<String> getCmbDiscount() { return cmbDiscount; }
    public TextField getTxtDiscount() { return txtDiscount; }

    // Summary Getters
    public Label getLblTotalItems() { return lblTotalItems; }
    public Label getLblTotalQty() { return lblTotalQty; }
    public Label getLblSubtotal() { return lblSubtotal; }
    public Label getLblDiscount() { return lblDiscount2; }
    public Label getLblGrandTotal() { return lblGrandTotal; }

    // ===== UTILITY METHODS =====

    public void clearProductForm() {
        txtProductCode.clear();
        txtProductName.clear();
        txtProductPrice.clear();
        txtProductStock.clear();
        cmbProductCategory.setValue(null);
    }

    public Product getSelectedProduct() {
        return productTable.getSelectionModel().getSelectedItem();
    }

    public CartItem getSelectedCartItem() {
        return cartTable.getSelectionModel().getSelectedItem();
    }
    
    public void updateSummary(int totalItems, int totalQty, double subtotal, double discount) {
        lblTotalItems.setText(String.format("Total Items: %d jenis", totalItems));
        lblTotalQty.setText(String.format("Total Qty: %d pcs", totalQty));
        lblSubtotal.setText(String.format("Subtotal: Rp %,.0f", subtotal));
        lblDiscount2.setText(String.format("Diskon: Rp %,.0f", discount));
        lblGrandTotal.setText(String.format("TOTAL: Rp %,.0f", subtotal - discount));
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
