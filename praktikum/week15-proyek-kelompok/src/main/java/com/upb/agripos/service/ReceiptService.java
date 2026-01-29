package com.upb.agripos.service;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReceiptService
 * Service untuk generate struk pembayaran
 * 
 * FR-4: Struk dan Laporan
 */
public class ReceiptService {

    /**
     * Generate struk dalam format text
     */
    public String generateReceipt(Transaction transaction, List<CartItem> items) {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        receipt.append("╔════════════════════════════════════╗\n");
        receipt.append("║        AGRI-POS SYSTEM             ║\n");
        receipt.append("║     Penjualan Produk Pertanian      ║\n");
        receipt.append("╚════════════════════════════════════╝\n");
        receipt.append("\n");

        receipt.append("NO. TRANSAKSI: ").append(transaction.getCode()).append("\n");
        receipt.append("KASIR: ").append(transaction.getCashierName()).append("\n");
        receipt.append("TANGGAL: ").append(transaction.getTransactionDate().format(formatter)).append("\n");
        receipt.append("METODE: ").append(transaction.getPaymentMethod()).append("\n");
        receipt.append("\n");

        receipt.append("────────────────────────────────────\n");
        receipt.append(String.format("%-20s %8s %10s\n", "PRODUK", "QTY", "SUBTOTAL"));
        receipt.append("────────────────────────────────────\n");

        for (CartItem item : items) {
            String productName = item.getProduct().getName();
            if (productName.length() > 20) {
                productName = productName.substring(0, 17) + "...";
            }
            receipt.append(String.format("%-20s %8d Rp %,.0f\n", 
                productName,
                item.getQuantity(),
                item.getSubtotal()
            ));
        }

        receipt.append("────────────────────────────────────\n");
        receipt.append(String.format("%-20s %16s Rp %,.0f\n", 
            "SUBTOTAL", "", transaction.getSubtotal()));

        if (transaction.getDiscount() > 0) {
            receipt.append(String.format("%-20s %16s Rp %,.0f\n", 
                "DISKON", "", transaction.getDiscount()));
        }

        receipt.append("╔════════════════════════════════════╗\n");
        receipt.append(String.format("║ TOTAL %27s Rp %,.0f ║\n", 
            "", transaction.getTotal()));
        receipt.append("╚════════════════════════════════════╝\n");

        receipt.append("\n");
        receipt.append("────────────────────────────────────\n");
        receipt.append("          TERIMA KASIH TELAH\n");
        receipt.append("       BERBELANJA DI AGRI-POS\n");
        receipt.append("────────────────────────────────────\n");

        return receipt.toString();
    }

    /**
     * Generate struk untuk dialog/tampilan
     */
    public String generateSimpleReceipt(Transaction transaction, List<CartItem> items) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== AGRI-POS STRUK PEMBELIAN ===\n\n");
        receipt.append("No. Transaksi: ").append(transaction.getCode()).append("\n");
        receipt.append("Kasir: ").append(transaction.getCashierName()).append("\n");
        receipt.append("Tanggal: ").append(transaction.getTransactionDate()).append("\n\n");

        receipt.append("ITEM DIBELI:\n");
        for (CartItem item : items) {
            receipt.append("- ").append(item.getProduct().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" = Rp ").append(String.format("%,.0f", item.getSubtotal())).append("\n");
        }

        receipt.append("\nSubtotal: Rp ").append(String.format("%,.0f", transaction.getSubtotal())).append("\n");
        if (transaction.getDiscount() > 0) {
            receipt.append("Diskon: Rp ").append(String.format("%,.0f", transaction.getDiscount())).append("\n");
        }
        receipt.append("TOTAL: Rp ").append(String.format("%,.0f", transaction.getTotal())).append("\n");
        receipt.append("\nMetode Pembayaran: ").append(transaction.getPaymentMethod()).append("\n");
        receipt.append("\nTerima Kasih!\n");

        return receipt.toString();
    }
}
