package main.java.com.upb.agripos;

public class MainExceptionDemo {
    public static void main(String[] args) {
        System.out.println("Hello, I am Risky Dimas Nugroho-240202882 (Week9)");

        ShoppingCart cart = new ShoppingCart();
        Product p1 = new Product("P01", "Pupuk Organik", 25000, 3);

        // Test 1: Quantity invalid (negatif)
        try {
            cart.addProduct(p1, -1);
        } catch (InvalidQuantityException e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }

        // Test 2: Hapus produk yang belum ada di keranjang
        try {
            cart.removeProduct(p1);
        } catch (ProductNotFoundException e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }

        // Test 3: Checkout keranjang kosong
        try {
            cart.checkout();
        } catch (EmptyCartException | InsufficientStockException e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }

        // Test 4: Checkout dengan stok tidak cukup
        try {
            cart.addProduct(p1, 5);  // Minta 5, stok cuma 3
            cart.checkout();
        } catch (Exception e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }
    }
}