package com.upb.agripos.view;

import com.upb.agripos.model.Product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * AdminView
 * View untuk halaman admin - manajemen produk
 */
public class AdminView extends BorderPane {
    private TableView<Product> productTable;
    private TextField productCodeField;
    private TextField productNameField;
    private TextField priceField;
    private TextField stockField;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button logoutButton;
    private Label userLabel;

    public AdminView() {
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #f5f5f5;");

        // Top - Header
        VBox headerBox = createHeaderBox();
        this.setTop(headerBox);

        // Center - Product table
        VBox centerBox = createCenterBox();
        this.setCenter(centerBox);

        // Right - Form input
        VBox rightBox = createRightBox();
        this.setRight(rightBox);
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox();
        headerBox.setPadding(new Insets(10));
        headerBox.setStyle("-fx-background-color: #2c5f2d;");
        headerBox.setSpacing(10);

        Label titleLabel = new Label("ADMIN DASHBOARD - Manajemen Produk");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: white;");

        HBox userBox = new HBox();
        userBox.setAlignment(Pos.CENTER_RIGHT);
        userBox.setSpacing(10);

        userLabel = new Label("User: ");
        userLabel.setStyle("-fx-text-fill: white;");

        logoutButton = new Button("LOGOUT");
        logoutButton.setStyle("-fx-padding: 5 20; -fx-background-color: #d9534f; -fx-text-fill: white;");

        userBox.getChildren().addAll(userLabel, logoutButton);

        headerBox.getChildren().addAll(titleLabel, userBox);
        return headerBox;
    }

    private VBox createCenterBox() {
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(10));
        centerBox.setSpacing(10);

        Label tableLabel = new Label("Daftar Produk");
        tableLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        productTable = new TableView<>();

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        idCol.setPrefWidth(50);

        TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCode()));
        codeCol.setPrefWidth(100);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceCol.setPrefWidth(100);

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStock()));
        stockCol.setPrefWidth(80);

        productTable.getColumns().addAll(idCol, codeCol, nameCol, priceCol, stockCol);

        centerBox.getChildren().addAll(tableLabel, productTable);
        VBox.setVgrow(productTable, javafx.scene.layout.Priority.ALWAYS);

        return centerBox;
    }

    private VBox createRightBox() {
        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(10));
        rightBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 3; -fx-background-color: white;");
        rightBox.setSpacing(10);
        rightBox.setMaxWidth(250);

        Label formLabel = new Label("Form Produk");
        formLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Label codeLabel = new Label("Kode Produk:");
        productCodeField = new TextField();
        productCodeField.setPromptText("ex: P001");

        Label nameLabel = new Label("Nama Produk:");
        productNameField = new TextField();
        productNameField.setPromptText("ex: Pupuk");

        Label priceLabel = new Label("Harga:");
        priceField = new TextField();
        priceField.setPromptText("ex: 30000");

        Label stockLabel = new Label("Stok:");
        stockField = new TextField();
        stockField.setPromptText("ex: 50");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(5);
        addButton = new Button("Tambah Produk");
        addButton.setStyle("-fx-padding: 8 15; -fx-background-color: #5cb85c; -fx-text-fill: white;");

        editButton = new Button("Edit Produk");
        editButton.setStyle("-fx-padding: 8 15; -fx-background-color: #0275d8; -fx-text-fill: white;");

        deleteButton = new Button("Hapus Produk");
        deleteButton.setStyle("-fx-padding: 8 15; -fx-background-color: #d9534f; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);

        rightBox.getChildren().addAll(
            formLabel,
            new Label(""), // Spacer
            codeLabel, productCodeField,
            nameLabel, productNameField,
            priceLabel, priceField,
            stockLabel, stockField,
            buttonBox
        );

        return rightBox;
    }

    // Getters
    public TableView<Product> getProductTable() {
        return productTable;
    }

    public TextField getProductCodeField() {
        return productCodeField;
    }

    public TextField getProductNameField() {
        return productNameField;
    }

    public TextField getPriceField() {
        return priceField;
    }

    public TextField getStockField() {
        return stockField;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    public Label getUserLabel() {
        return userLabel;
    }

    public void clearForm() {
        productCodeField.clear();
        productNameField.clear();
        priceField.clear();
        stockField.clear();
    }
}
