package main.java.com.upb.agripos;

public class MainCart {
    public static void main(String[] args) {
        System.out.println("Hello, I am Risky Dimas Nugroho - 240202882 (Week7)");

        // Buat produk
        Product p1 = new Product("P01", "Beras", 50000);
        Product p2 = new Product("P02", "Pupuk", 30000);
        Product p3 = new Product("P03", "Bakterisida", 120000);

        // Test ArrayList
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(p1);
        cart.addProduct(p2);
        cart.addProduct(p3);
        
        cart.printCart();

        System.out.println("\n=== Setelah hapus Beras === :");
        cart.removeProduct(p1);
        cart.printCart();

        // Test HashMap (OPSIONAL)
        System.out.println("\n=== Test dengan Map ===");
        ShoppingCartMap cartMap = new ShoppingCartMap();
        cartMap.addProduct(p1);
        cartMap.addProduct(p2);  // Tambah beras lagi (quantity +1)
        cartMap.addProduct(p3);
        cartMap.printCart();
    }
    
}
