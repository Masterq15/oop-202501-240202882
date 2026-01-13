package com.upb.agripos.view;

public class ConsoleView {
    public void showMessage(String message) {
        System.out.println(message);
    }
    
    public void showProduct(String code, String name) {
        System.out.println("Produk: " + code + " - " + name);
    }
}