package main.java.com.upb.agripos;

public class ProductService {
    // Instance tunggal (static)
    private static ProductService instance;
    
    // Constructor private (tidak bisa new dari luar)
    private ProductService() {}

    // Method untuk dapatkan instance
    public static ProductService getInstance() {
        if (instance == null) { 
            instance = new ProductService(); 
        }
        return instance;
    }
    
    // Contoh method untuk manage produk
    public void registerProduct(Product p) {
        System.out.println("Produk " + p.getName() + " terdaftar");
    }
}
