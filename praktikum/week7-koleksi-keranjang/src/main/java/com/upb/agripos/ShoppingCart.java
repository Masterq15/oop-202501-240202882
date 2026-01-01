package main.java.com.upb.agripos;

import java.util.ArrayList;

public class ShoppingCart {
    // ArrayList untuk menyimpan daftar produk
    private final ArrayList<Product> items = new ArrayList<>();

    // Menambah produk ke keranjang
    public void addProduct(Product p) { 
        items.add(p); 
    }

    // Menghapus produk dari keranjang
    public void removeProduct(Product p) { 
        items.remove(p); 
    }

    // Menghitung total harga semua produk
    public double getTotal() {
        double sum = 0;
        for (Product p : items) {
            sum += p.getPrice();
        }
        return sum;
    }

    // Menampilkan isi keranjang
    public void printCart() {
        System.out.println("Isi Keranjang:");
        for (Product p : items) {
            System.out.println("- " + p.getCode() + " " + p.getName() + " = " + p.getPrice());
        }
        System.out.println("Total: " + getTotal());
    }
    
}
