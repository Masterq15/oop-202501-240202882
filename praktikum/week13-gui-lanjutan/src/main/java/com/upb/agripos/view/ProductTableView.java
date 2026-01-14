package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProductTableView extends VBox {
    
    // Form Input
    private TextField txtCode;
    private TextField txtName;
    private TextField txtPrice;
    private TextField txtStock;
    private Button btnAdd;
    private Button btnDelete;
    private Button btnRefresh;
    
    // TableView
    private TableView<Product> tableView;
    private TableColumn<Product, String> colCode;
    private TableColumn<Product, String> colName;
    private TableColumn<Product, Double> colPrice;
    private TableColumn<Product, Integer> colStock;
    
    public ProductTableView() {
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        // Form Input Components
        txtCode = new TextField();
        txtCode.setPromptText("Kode Produk");
        
        txtName = new TextField();
        txtName.setPromptText("Nama Produk");
        
        txtPrice = new TextField();
        txtPrice.setPromptText("Harga");
        
        txtStock = new TextField();
        txtStock.setPromptText("Stok");
        
        btnAdd = new Button("Tambah Produk");
        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        btnDelete = new Button("Hapus Produk");
        btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        btnRefresh = new Button("Refresh");
        btnRefresh.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        // TableView Setup
        tableView = new TableView<>();
        
        // Kolom Kode
        colCode = new TableColumn<>("Kode");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCode.setPrefWidth(100);
        
        // Kolom Nama
        colName = new TableColumn<>("Nama Produk");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);
        
        // Kolom Harga
        colPrice = new TableColumn<>("Harga");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(120);
        
        // Kolom Stok
        colStock = new TableColumn<>("Stok");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(80);
        
        // Tambahkan kolom ke TableView
        tableView.getColumns().addAll(colCode, colName, colPrice, colStock);
    }
    
    private void layoutComponents() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        // Form Section
        Label lblForm = new Label("Form Input Produk");
        lblForm.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        VBox formBox = new VBox(5);
        formBox.getChildren().addAll(
            new Label("Kode:"), txtCode,
            new Label("Nama:"), txtName,
            new Label("Harga:"), txtPrice,
            new Label("Stok:"), txtStock
        );
        
        // Button Section
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(btnAdd, btnDelete, btnRefresh);
        
        // Table Section
        Label lblTable = new Label("Daftar Produk:");
        lblTable.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        tableView.setPrefHeight(300);
        
        // Add all to main layout
        getChildren().addAll(
            lblForm, formBox, buttonBox,
            new Separator(),
            lblTable, tableView
        );
    }
    
    // Getters untuk Controller
    public TextField getTxtCode() { return txtCode; }
    public TextField getTxtName() { return txtName; }
    public TextField getTxtPrice() { return txtPrice; }
    public TextField getTxtStock() { return txtStock; }
    public Button getBtnAdd() { return btnAdd; }
    public Button getBtnDelete() { return btnDelete; }
    public Button getBtnRefresh() { return btnRefresh; }
    public TableView<Product> getTableView() { return tableView; }
    
    // Method untuk clear form
    public void clearForm() {
        txtCode.clear();
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
    }
    
    // Method untuk get selected product
    public Product getSelectedProduct() {
        return tableView.getSelectionModel().getSelectedItem();
    }
}