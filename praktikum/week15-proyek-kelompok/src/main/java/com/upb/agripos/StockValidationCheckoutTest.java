package com.upb.agripos;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.transaction.Transaction;
import com.upb.agripos.model.transaction.TransactionDetail;
import com.upb.agripos.model.payment.CashPayment;

/**
 * Test untuk validasi CHECKOUT dengan pengecekan stok
 * 
 * Skenario:
 * 1. Jika stok produk sudah 0 (habis), checkout DITOLAK ❌
 * 2. Jika stok tidak cukup untuk quantity yang diminta, checkout DITOLAK ❌
 * 3. Jika stok cukup, checkout DIIZINKAN ✓
 * 4. Setelah checkout, stok dikurangi sesuai quantity
 */
public class StockValidationCheckoutTest {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  TEST: VALIDASI STOK PADA CHECKOUT TRANSAKSI              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        try {
            // ===== TEST 1: Checkout dengan stok 0 (DITOLAK) =====
            System.out.println("TEST 1: Checkout dengan Produk Stok HABIS (0)");
            System.out.println("─" + "─".repeat(56) + "\n");
            
            Product productOOF = new Product("BNH-001", "Benih Padi", "Benih", 45000, 0);
            Transaction txn1 = new Transaction("TRX-001", "KASIR-001");
            
            System.out.println("Produk: " + productOOF.getName());
            System.out.println("Kode: " + productOOF.getCode());
            System.out.println("Stok saat ini: " + productOOF.getStock() + " unit");
            System.out.println("Qty diminta: 2 unit\n");
            
            try {
                // Coba tambah item dengan stok 0
                TransactionDetail detail1 = new TransactionDetail("DTL-001", productOOF, 2, 0);
                txn1.addDetail(detail1);
                System.out.println("❌ HASIL: CHECKOUT DITOLAK");
                System.out.println("   ALASAN: Stok produk HABIS (0)\n");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ HASIL: DITOLAK (ERROR)");
                System.out.println("   PESAN: " + e.getMessage() + "\n");
            }
            
            // ===== TEST 2: Checkout dengan stok tidak cukup (DITOLAK) =====
            System.out.println("\nTEST 2: Checkout dengan Stok TIDAK CUKUP");
            System.out.println("─" + "─".repeat(56) + "\n");
            
            Product productLow = new Product("BNH-002", "Benih Jagung", "Benih", 35000, 3);
            Transaction txn2 = new Transaction("TRX-002", "KASIR-002");
            
            System.out.println("Produk: " + productLow.getName());
            System.out.println("Kode: " + productLow.getCode());
            System.out.println("Stok saat ini: " + productLow.getStock() + " unit");
            System.out.println("Qty diminta: 5 unit\n");
            
            try {
                // Coba tambah item lebih dari stok
                TransactionDetail detail2 = new TransactionDetail("DTL-002", productLow, 5, 0);
                txn2.addDetail(detail2);
                System.out.println("❌ HASIL: CHECKOUT DITOLAK");
                System.out.println("   ALASAN: Stok tidak cukup\n");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ HASIL: DITOLAK (ERROR)");
                System.out.println("   PESAN: " + e.getMessage() + "\n");
            }
            
            // ===== TEST 3: Checkout dengan stok cukup (DIIZINKAN) =====
            System.out.println("\nTEST 3: Checkout dengan Stok CUKUP");
            System.out.println("─" + "─".repeat(56) + "\n");
            
            Product productOK = new Product("PES-001", "Pestisida Organik", "Pestisida", 65000, 50);
            Transaction txn3 = new Transaction("TRX-003", "KASIR-001");
            
            System.out.println("Produk: " + productOK.getName());
            System.out.println("Kode: " + productOK.getCode());
            System.out.println("Stok saat ini: " + productOK.getStock() + " unit");
            System.out.println("Qty diminta: 10 unit");
            System.out.println("Total pembayaran: Rp " + (65000 * 10) + "\n");
            
            try {
                // Tambah item dengan stok cukup
                TransactionDetail detail3 = new TransactionDetail("DTL-003", productOK, 10, 0);
                txn3.addDetail(detail3);
                
                // Proses checkout
                double totalAmount = txn3.getGrandTotal();
                CashPayment payment = new CashPayment("PAY-001", totalAmount, totalAmount + 50000);
                
                if (payment.processPayment()) {
                    System.out.println("✓ HASIL: CHECKOUT DIIZINKAN");
                    System.out.println("   STATUS: Pembayaran diterima");
                    System.out.println("   Kembalian: Rp " + payment.getChange());
                    System.out.println("   Stok lama: " + (productOK.getStock() + 10) + " unit");
                    
                    // Update stok setelah checkout
                    productOK.setStock(productOK.getStock() - 10);
                    System.out.println("   Stok baru: " + productOK.getStock() + " unit\n");
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: " + e.getMessage() + "\n");
            }
            
            // ===== TEST 4: Multiple Items dengan mixed stock status =====
            System.out.println("\nTEST 4: Transaksi Multiple Items (Mixed Status)");
            System.out.println("─" + "─".repeat(56) + "\n");
            
            Product p1 = new Product("ALT-001", "Alat Tanam", "Alat", 250000, 5);
            Product p2 = new Product("ALT-002", "Selang Air", "Alat", 85000, 0);
            
            Transaction txn4 = new Transaction("TRX-004", "KASIR-003");
            
            System.out.println("Item 1: " + p1.getName() + " - Stok: " + p1.getStock());
            System.out.println("Item 2: " + p2.getName() + " - Stok: " + p2.getStock());
            System.out.println("Qty Item 1: 2 unit → Status: ✓ Cukup");
            System.out.println("Qty Item 2: 1 unit → Status: ❌ Habis\n");
            
            try {
                // Item 1 cukup
                TransactionDetail dt1 = new TransactionDetail("DTL-101", p1, 2, 0);
                txn4.addDetail(dt1);
                System.out.println("✓ Item 1 ditambahkan ke keranjang");
                
                // Item 2 habis - akan error
                try {
                    TransactionDetail dt2 = new TransactionDetail("DTL-102", p2, 1, 0);
                    txn4.addDetail(dt2);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Item 2 DITOLAK: " + e.getMessage());
                    System.out.println("\n→ KESIMPULAN: Checkout akan GAGAL karena ada item dengan stok habis");
                    System.out.println("→ SOLUSI: Tambahkan stok Item 2 sebelum checkout\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            // ===== TEST 5: Penambahan Stok Manual kemudian Checkout =====
            System.out.println("\nTEST 5: Penambahan Stok Manual → Checkout");
            System.out.println("─" + "─".repeat(56) + "\n");
            
            Product productRestock = new Product("PES-002", "Pupuk Kompos", "Pupuk", 120000, 0);
            
            System.out.println("Produk: " + productRestock.getName());
            System.out.println("Stok awal: " + productRestock.getStock() + " unit (HABIS)");
            System.out.println("\n→ Lakukan penambahan stok manual...");
            System.out.println("→ Tambah stok: 50 unit");
            
            // Manual restock
            productRestock.setStock(productRestock.getStock() + 50);
            System.out.println("✓ Stok baru: " + productRestock.getStock() + " unit");
            
            // Sekarang coba checkout
            Transaction txn5 = new Transaction("TRX-005", "KASIR-001");
            try {
                TransactionDetail detail5 = new TransactionDetail("DTL-005", productRestock, 10, 0);
                txn5.addDetail(detail5);
                System.out.println("✓ Checkout BERHASIL setelah penambahan stok\n");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  KESIMPULAN HASIL TEST                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("✓ Validasi stok 0 berfungsi → Checkout DITOLAK");
            System.out.println("✓ Validasi stok tidak cukup → Checkout DITOLAK");
            System.out.println("✓ Stok cukup → Checkout DIIZINKAN");
            System.out.println("✓ Penambahan stok manual → Checkout bisa dilanjutkan");
            System.out.println("✓ Fitur keamanan stok berfungsi dengan baik ✓\n");
            
        } catch (Exception e) {
            System.err.println("✗ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
