package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * ReceiptDialog
 * Dialog untuk tampil struk/receipt
 */
public class ReceiptDialog extends Stage {
    private TextArea receiptTextArea;
    private Button printButton;
    private Button closeButton;

    public ReceiptDialog(String receiptContent) {
        this.setTitle("STRUK PENJUALAN");
        this.setWidth(500);
        this.setHeight(600);
        this.setResizable(false);

        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: #f5f5f5;");

        // Top - Header
        VBox headerBox = new VBox();
        headerBox.setPadding(new Insets(10));
        headerBox.setStyle("-fx-background-color: #2c5f2d;");
        headerBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("STRUK PENJUALAN");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label("Agri-POS Week 15");
        subtitleLabel.setFont(Font.font("Segoe UI", 11));
        subtitleLabel.setStyle("-fx-text-fill: #cccccc;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        mainPane.setTop(headerBox);

        // Center - Receipt content
        VBox centerBox = new VBox();
        centerBox.setPadding(new Insets(10));
        centerBox.setSpacing(10);

        receiptTextArea = new TextArea();
        receiptTextArea.setText(receiptContent);
        receiptTextArea.setEditable(false);
        receiptTextArea.setWrapText(true);
        receiptTextArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; -fx-padding: 10;");

        ScrollPane scrollPane = new ScrollPane(receiptTextArea);
        scrollPane.setFitToWidth(true);

        centerBox.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        mainPane.setCenter(centerBox);

        // Bottom - Buttons
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10));
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: white;");

        printButton = new Button("CETAK");
        printButton.setStyle("-fx-padding: 8 30; -fx-font-size: 11; -fx-background-color: #5cb85c; -fx-text-fill: white;");

        closeButton = new Button("TUTUP");
        closeButton.setStyle("-fx-padding: 8 30; -fx-font-size: 11; -fx-background-color: #6c757d; -fx-text-fill: white;");

        closeButton.setOnAction(e -> this.close());

        buttonBox.getChildren().addAll(printButton, closeButton);
        mainPane.setBottom(buttonBox);

        Scene scene = new Scene(mainPane);
        this.setScene(scene);
    }

    public TextArea getReceiptTextArea() {
        return receiptTextArea;
    }

    public Button getPrintButton() {
        return printButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void setReceiptContent(String content) {
        receiptTextArea.setText(content);
    }
}
