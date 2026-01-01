package main.java.com.upb.agripos;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCartMap {
    // Map untuk menyimpan Product sebagai key dan quantity sebagai value
    private final Map<Product, Integer> items = new HashMap<>();

    // Menambah produk (jika sudah ada, tambah quantity-nya)
    public void addProduct(Product p) { 
        items.put(p, items.getOrDefault(p, 0) + 1); 
    }

    // Menghapus/mengurangi produk
    public void removeProduct(Product p) {
        if (!items.containsKey(p)) return;
        int qty = items.get(p);
        if (qty > 1) items.put(p, qty - 1);  // Kurangi quantity
        else items.remove(p);                 // Hapus jika qty = 1
    }

    // Hitung total dengan mengalikan harga × quantity
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void printCart() {
        System.out.println("Isi Keranjang (Map):");
        for (Map.Entry<Product, Integer> e : items.entrySet()) {
            System.out.println("- " + e.getKey().getCode() + " " + 
                               e.getKey().getName() + " x" + e.getValue());
        }
        System.out.println("Total: " + getTotal());
    }
    
}
