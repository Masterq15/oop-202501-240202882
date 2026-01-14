package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.CartItem;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * PosView - Main View untuk Point of Sale System
 *
 * Integrasi lengkap antara:
 * - Product Management (CRUD produk)
 * - Shopping Cart (keranjang belanja)
 * - Checkout System
 *
 * Layout menggunakan BorderPane dengan sections:
 * - TOP: Title
 * - LEFT: Product Management
 * - CENTER: Shopping Cart
 * - BOTTOM: Summary & Checkout
 */
public class PosView extends BorderPane {

    // ===== PRODUCT MANAGEMENT COMPONENTS =====
    private TextField txtProductCode, txtProductName, txtProductPrice, txtProductStock;
    private Button btnAddProduct, btnDeleteProduct, btnRefreshProduct;
    private TableView<Product> productTable;
    private TableColumn<Product, String> colProductCode, colProductName;
    private TableColumn<Product, Double> colProductPrice;
    private TableColumn<Product, Integer> colProductStock;

    // ===== CART MANAGEMENT COMPONENTS =====
    private TextField txtAddQty;
    private Button btnAddToCart, btnRemoveFromCart, btnClearCart, btnCheckout;
    private TableView<CartItem> cartTable;
    private TableColumn<CartItem, String> colCartName, colCartCode;
    private TableColumn<CartItem, Integer> colCartQty;
    private TableColumn<CartItem, Double> colCartSubtotal;

    // ===== SUMMARY COMPONENTS =====
    private Label lblTotalItems, lblTotalQty, lblGrandTotal;

    public PosView() {
        initComponents();
        layoutComponents();
        setupTableColumns();
    }

    private void initComponents() {
        // ===== PRODUCT FORM COMPONENTS =====
        txtProductCode = new TextField();
        txtProductCode.setPromptText("Kode Produk");

        txtProductName = new TextField();
        txtProductName.setPromptText("Nama Produk");

        txtProductPrice = new TextField();
        txtProductPrice.setPromptText("Harga");

        txtProductStock = new TextField();
        txtProductStock.setPromptText("Stok");

        btnAddProduct = new Button("➕ Tambah Produk");
        btnAddProduct.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        btnDeleteProduct = new Button("🗑️ Hapus Produk");
        btnDeleteProduct.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        btnRefreshProduct = new Button("🔄 Refresh");
        btnRefreshProduct.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        // ===== PRODUCT TABLE =====
        productTable = new TableView<>();
        productTable.setPrefHeight(250);

        // ===== CART COMPONENTS =====
        txtAddQty = new TextField();
        txtAddQty.setPromptText("Qty");
        txtAddQty.setPrefWidth(60);

        btnAddToCart = new Button("🛒 Tambah ke Keranjang");
        btnAddToCart.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");

        btnRemoveFromCart = new Button("❌ Hapus dari Keranjang");
        btnRemoveFromCart.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");

        btnClearCart = new Button("🗑️ Kosongkan Keranjang");
        btnClearCart.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-weight: bold;");

        btnCheckout = new Button("💰 CHECKOUT");
        btnCheckout.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCheckout.setPrefWidth(150);

        // ===== CART TABLE =====
        cartTable = new TableView<>();
        cartTable.setPrefHeight(200);

        // ===== SUMMARY LABELS =====
        lblTotalItems = new Label("Total Items: 0 jenis");
        lblTotalItems.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        lblTotalQty = new Label("Total Qty: 0 pcs");
        lblTotalQty.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        lblGrandTotal = new Label("TOTAL: Rp 0.00");
        lblGrandTotal.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblGrandTotal.setStyle("-fx-text-fill: #4CAF50;");
    }

    private void setupTableColumns() {
        // ===== PRODUCT TABLE COLUMNS =====
        colProductCode = new TableColumn<>("Kode");
        colProductCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colProductCode.setPrefWidth(80);

        colProductName = new TableColumn<>("Nama Produk");
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProductName.setPrefWidth(150);

        colProductPrice = new TableColumn<>("Harga");
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProductPrice.setPrefWidth(100);
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
        colProductStock.setPrefWidth(70);

        productTable.getColumns().addAll(colProductCode, colProductName, colProductPrice, colProductStock);

        // ===== CART TABLE COLUMNS =====
        colCartCode = new TableColumn<>("Kode");
        colCartCode.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct().getCode()));
        colCartCode.setPrefWidth(80);

        colCartName = new TableColumn<>("Nama Produk");
        colCartName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct().getName()));
        colCartName.setPrefWidth(150);

        colCartQty = new TableColumn<>("Qty");
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartQty.setPrefWidth(60);

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

        // ===== TOP: TITLE =====
        Label lblTitle = new Label("🛒 AGRI-POS - Point of Sale System");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitle.setStyle("-fx-text-fill: #2E7D32;");
        setTop(lblTitle);
        BorderPane.setMargin(lblTitle, new Insets(0, 0, 15, 0));

        // ===== LEFT: PRODUCT MANAGEMENT =====
        VBox productSection = createProductSection();
        setLeft(productSection);
        BorderPane.setMargin(productSection, new Insets(0, 15, 0, 0));

        // ===== CENTER: CART MANAGEMENT =====
        VBox cartSection = createCartSection();
        setCenter(cartSection);

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
        form.add(new Label("Harga:"), 0, 2);
        form.add(txtProductPrice, 1, 2);
        form.add(new Label("Stok:"), 0, 3);
        form.add(txtProductStock, 1, 3);

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

    private VBox createCartSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(400);

        // Title
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

        section.getChildren().addAll(
            lblCartTitle,
            addToCartBox,
            cartButtons,
            new Separator(),
            lblCartTable,
            cartTable
        );

        return section;
    }

    private HBox createSummarySection() {
        HBox section = new HBox(30);
        section.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 15; -fx-background-radius: 5;");

        // Summary info
        VBox summaryInfo = new VBox(5);
        summaryInfo.getChildren().addAll(lblTotalItems, lblTotalQty);

        // Total and checkout
        HBox checkoutBox = new HBox(20);
        checkoutBox.getChildren().addAll(lblGrandTotal, btnCheckout);

        section.getChildren().addAll(summaryInfo, new Region(), checkoutBox);
        HBox.setHgrow(section.getChildren().get(1), Priority.ALWAYS);

        return section;
    }

    // ===== GETTER METHODS =====

    // Product Management Getters
    public TextField getTxtProductCode() { return txtProductCode; }
    public TextField getTxtProductName() { return txtProductName; }
    public TextField getTxtProductPrice() { return txtProductPrice; }
    public TextField getTxtProductStock() { return txtProductStock; }
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

    // Summary Getters
    public Label getLblTotalItems() { return lblTotalItems; }
    public Label getLblTotalQty() { return lblTotalQty; }
    public Label getLblGrandTotal() { return lblGrandTotal; }

    // ===== UTILITY METHODS =====

    public void clearProductForm() {
        txtProductCode.clear();
        txtProductName.clear();
        txtProductPrice.clear();
        txtProductStock.clear();
    }

    public Product getSelectedProduct() {
        return productTable.getSelectionModel().getSelectedItem();
    }

    public CartItem getSelectedCartItem() {
        return cartTable.getSelectionModel().getSelectedItem();
    }
}
