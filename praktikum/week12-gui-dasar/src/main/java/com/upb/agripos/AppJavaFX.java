package com.upb.agripos;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.view.ProductFormView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProductFormView view = new ProductFormView();
        ProductController controller = new ProductController(view);

        // Menghubungkan tombol di View ke logic di Controller
        view.getBtnAdd().setOnAction(e -> {
            controller.handleAddProduct();
            
            // Refresh tampilan list sederhana
            view.getListView().getItems().clear();
            controller.getProductList().forEach(p -> 
                view.getListView().getItems().add(p.toString())
            );
        });

        // Load data awal dari database
        controller.getProductList().forEach(p -> 
            view.getListView().getItems().add(p.toString())
        );

        Scene scene = new Scene(view, 450, 600);
        primaryStage.setTitle("Agri-POS - Kelola Produk");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}