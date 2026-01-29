package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

/**
 * KasirView
 * View untuk halaman kasir - penjualan
 */
public class KasirView extends BorderPane {
    private ComboBox<Product> productCombo;
    private Spinner<Integer> quantitySpinner;
    private Button addToCartButton;
    private TableView<CartItem> cartTable;
    private TextField totalField;
    private TextField paymentMethodField;
    private TextField accountField;
    private Button checkoutButton;
    private Button printButton;
    private Button logoutButton;
    private Label userLabel;
    private Label statusLabel;

    public KasirView() {
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #f5f5f5;");

        // Top - Header
        VBox headerBox = createHeaderBox();
        this.setTop(headerBox);

        // Left - Product selection
        VBox leftBox = createLeftBox();
        this.setLeft(leftBox);

        // Center - Cart table
        VBox centerBox = createCenterBox();
        this.setCenter(centerBox);

        // Right - Checkout
        VBox rightBox = createRightBox();
        this.setRight(rightBox);
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox();
        headerBox.setPadding(new Insets(10));
        headerBox.setStyle("-fx-background-color: #2c5f2d;");
        headerBox.setSpacing(10);

        Label titleLabel = new Label("KASIR DASHBOARD - Penjualan");
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

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #5cb85c; -fx-font-weight: bold;");

        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.SPACE_BETWEEN);
        contentBox.getChildren().addAll(titleLabel, statusLabel);

        headerBox.getChildren().addAll(contentBox, userBox);
        return headerBox;
    }

    private VBox createLeftBox() {
        VBox leftBox = new VBox();
        leftBox.setPadding(new Insets(10));
        leftBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 3; -fx-background-color: white;");
        leftBox.setSpacing(10);
        leftBox.setMaxWidth(250);

        Label selectLabel = new Label("Pilih Produk");
        selectLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Label productLabel = new Label("Produk:");
        productCombo = new ComboBox<>();
        productCombo.setPromptText("Pilih produk...");
        productCombo.setMaxWidth(Double.MAX_VALUE);

        Label quantityLabel = new Label("Jumlah:");
        quantitySpinner = new Spinner<>(1, 1000, 1);
        quantitySpinner.setEditable(true);

        addToCartButton = new Button("TAMBAH KE KERANJANG");
        addToCartButton.setStyle("-fx-padding: 10 15; -fx-background-color: #5cb85c; -fx-text-fill: white;");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);

        leftBox.getChildren().addAll(
            selectLabel,
            productLabel,
            productCombo,
            quantityLabel,
            quantitySpinner,
            addToCartButton
        );

        return leftBox;
    }

    private VBox createCenterBox() {
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(10));
        centerBox.setSpacing(10);

        Label cartLabel = new Label("Keranjang Belanja");
        cartLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        cartTable = new TableView<>();

        TableColumn<CartItem, Integer> idCol = new TableColumn<>("ID Produk");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProductId()));
        idCol.setPrefWidth(80);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProductName()));
        nameCol.setPrefWidth(120);

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        qtyCol.setPrefWidth(60);

        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceCol.setPrefWidth(100);

        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSubtotal()));
        subtotalCol.setPrefWidth(100);

        cartTable.getColumns().addAll(idCol, nameCol, qtyCol, priceCol, subtotalCol);

        centerBox.getChildren().addAll(cartLabel, cartTable);
        VBox.setVgrow(cartTable, javafx.scene.layout.Priority.ALWAYS);

        return centerBox;
    }

    private VBox createRightBox() {
        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(10));
        rightBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 3; -fx-background-color: white;");
        rightBox.setSpacing(10);
        rightBox.setMaxWidth(250);

        Label checkoutLabel = new Label("Checkout");
        checkoutLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Label totalLabel = new Label("Total (Rp):");
        totalField = new TextField();
        totalField.setEditable(false);
        totalField.setText("0");

        Label methodLabel = new Label("Metode Bayar:");
        paymentMethodField = new TextField();
        paymentMethodField.setPromptText("CASH / EWALLET");

        Label accountLabel = new Label("No. Akun (EWallet):");
        accountField = new TextField();
        accountField.setPromptText("ex: 082xxxxxxxx");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(5);

        checkoutButton = new Button("CHECKOUT");
        checkoutButton.setStyle("-fx-padding: 10 15; -fx-background-color: #0275d8; -fx-text-fill: white;");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);

        printButton = new Button("PRINT");
        printButton.setStyle("-fx-padding: 10 15; -fx-background-color: #5bc0de; -fx-text-fill: white;");
        printButton.setMaxWidth(Double.MAX_VALUE);

        rightBox.getChildren().addAll(
            checkoutLabel,
            new Label(""), // Spacer
            totalLabel,
            totalField,
            methodLabel,
            paymentMethodField,
            accountLabel,
            accountField,
            new Label(""), // Spacer
            checkoutButton,
            printButton
        );

        return rightBox;
    }

    // Getters
    public ComboBox<Product> getProductCombo() {
        return productCombo;
    }

    public Spinner<Integer> getQuantitySpinner() {
        return quantitySpinner;
    }

    public Button getAddToCartButton() {
        return addToCartButton;
    }

    public TableView<CartItem> getCartTable() {
        return cartTable;
    }

    public TextField getTotalField() {
        return totalField;
    }

    public TextField getPaymentMethodField() {
        return paymentMethodField;
    }

    public TextField getAccountField() {
        return accountField;
    }

    public Button getCheckoutButton() {
        return checkoutButton;
    }

    public Button getPrintButton() {
        return printButton;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    public Label getUserLabel() {
        return userLabel;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public void updateTotal(double total) {
        totalField.setText(String.format("%.0f", total));
    }

    public void clearCart() {
        cartTable.getItems().clear();
        paymentMethodField.clear();
        accountField.clear();
        totalField.setText("0");
    }
}
