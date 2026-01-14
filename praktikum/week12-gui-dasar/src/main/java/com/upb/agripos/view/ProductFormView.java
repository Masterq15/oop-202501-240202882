package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ProductFormView extends VBox {
    private TextField txtCode, txtName, txtPrice, txtStock;
    private Button btnAdd;
    private ListView<String> listView;

    public ProductFormView() {
        setSpacing(10);
        setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        txtCode = new TextField();
        txtName = new TextField();
        txtPrice = new TextField();
        txtStock = new TextField();
        btnAdd = new Button("Tambah Produk");

        grid.add(new Label("Kode:"), 0, 0);
        grid.add(txtCode, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(new Label("Harga:"), 0, 2);
        grid.add(txtPrice, 1, 2);
        grid.add(new Label("Stok:"), 0, 3);
        grid.add(txtStock, 1, 3);
        grid.add(btnAdd, 1, 4);

        listView = new ListView<>();
        
        getChildren().addAll(new Label("Form Input Produk"), grid, new Label("Daftar Produk:"), listView);
    }

    public void clearFields() {
        txtCode.clear();
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
    }

    // Getters for Controller
    public TextField getTxtCode() { return txtCode; }
    public TextField getTxtName() { return txtName; }
    public TextField getTxtPrice() { return txtPrice; }
    public TextField getTxtStock() { return txtStock; }
    public Button getBtnAdd() { return btnAdd; }
    public ListView<String> getListView() { return listView; }
}