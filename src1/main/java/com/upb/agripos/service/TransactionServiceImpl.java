package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.transaction.Transaction;
import com.upb.agripos.model.transaction.TransactionDetail;
import com.upb.agripos.model.payment.Payment;
import com.upb.agripos.model.audit.AuditAction;
import com.upb.agripos.discount.DiscountStrategyPersonB;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementasi TransactionServicePersonA
 * Menangani proses transaksi dari penambahan item hingga checkout
 * Terintegrasi dengan payment dan audit logging
 * Person A - TRANSACTION SERVICE IMPLEMENTATION
 */
public class TransactionServiceImpl implements TransactionServicePersonA {
    
    private List<Transaction> transactionHistory;
    private AuditLogServicePersonA auditLogService;
    
    public TransactionServiceImpl(AuditLogServicePersonA auditLogService) {
        this.transactionHistory = new ArrayList<>();
        this.auditLogService = auditLogService;
    }
    
    /**
     * Membuat transaksi baru
     */
    @Override
    public Transaction createTransaction(String transactionId, String cashierId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID tidak boleh kosong");
        }
        if (cashierId == null || cashierId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cashier ID tidak boleh kosong");
        }
        
        Transaction transaction = new Transaction(transactionId, cashierId);
        
        // Log aktivitas
        auditLogService.log(
            generateLogId(),
            cashierId,
            transactionId,
            AuditAction.TRANSACTION_START,
            "Transaksi dimulai oleh kasir"
        );
        
        return transaction;
    }
    
    /**
     * Menambah item ke transaksi
     */
    @Override
    public void addItemToTransaction(Transaction transaction, Product product,
                                     int quantity, DiscountStrategyPersonB discountStrategy) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction tidak boleh null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product tidak boleh null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(
                String.format("Stok tidak cukup. Stok tersedia: %d, diminta: %d",
                    product.getStock(), quantity)
            );
        }
        
        // Hitung diskon
        double discount = 0;
        if (discountStrategy != null) {
            double subtotal = product.getPrice() * quantity;
            discount = discountStrategy.calculateDiscount(subtotal);
        }
        
        // Tambah detail ke transaksi
        TransactionDetail detail = new TransactionDetail(
            generateDetailId(),
            product,
            quantity,
            discount
        );
        
        transaction.addDetail(detail);
        
        // Log aktivitas
        auditLogService.log(
            generateLogId(),
            transaction.getCashierId(),
            transaction.getTransactionId(),
            AuditAction.ITEM_ADDED,
            String.format("Product: %s, Qty: %d, Harga: Rp %.2f",
                product.getCode(), quantity, product.getPrice())
        );
    }
    
    /**
     * Menghapus item dari transaksi
     */
    @Override
    public void removeItemFromTransaction(Transaction transaction, String detailId) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction tidak boleh null");
        }
        if (detailId == null || detailId.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail ID tidak boleh kosong");
        }
        
        List<TransactionDetail> details = transaction.getDetails();
        TransactionDetail toRemove = null;
        
        for (TransactionDetail detail : details) {
            if (detail.getDetailId().equals(detailId)) {
                toRemove = detail;
                break;
            }
        }
        
        if (toRemove != null) {
            details.remove(toRemove);
            
            // Log aktivitas
            auditLogService.log(
                generateLogId(),
                transaction.getCashierId(),
                transaction.getTransactionId(),
                AuditAction.ITEM_REMOVED,
                String.format("Product: %s, Qty: %d dihapus dari transaksi",
                    toRemove.getProductCode(), toRemove.getQuantity())
            );
        } else {
            throw new IllegalArgumentException("Detail dengan ID " + detailId + " tidak ditemukan");
        }
    }
    
    /**
     * Proses checkout
     * Update stok dan proses pembayaran
     * VALIDASI: Cek stok sebelum checkout, jika stok habis maka checkout ditolak
     */
    @Override
    public boolean checkout(Transaction transaction, Payment payment) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction tidak boleh null");
        }
        if (payment == null) {
            throw new IllegalArgumentException("Payment tidak boleh null");
        }
        if (transaction.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Transaksi tidak memiliki item");
        }
        
        // ✓ VALIDASI STOK: Cek apakah semua produk masih tersedia sebelum checkout
        for (TransactionDetail detail : transaction.getDetails()) {
            Product product = detail.getProduct();
            
            // Jika stok sudah 0 (habis), tolak checkout
            if (product.getStock() == 0) {
                throw new IllegalArgumentException(
                    String.format("❌ CHECKOUT DITOLAK! Produk '%s' (Kode: %s) stoknya HABIS (0). " +
                        "Silakan lakukan penambahan stok terlebih dahulu!",
                        product.getName(), product.getCode())
                );
            }
            
            // Jika stok tidak cukup untuk quantity yang diminta, tolak checkout
            if (product.getStock() < detail.getQuantity()) {
                throw new IllegalArgumentException(
                    String.format("❌ CHECKOUT DITOLAK! Stok produk '%s' tidak cukup. " +
                        "Stok tersedia: %d, Diminta: %d",
                        product.getName(), product.getStock(), detail.getQuantity())
                );
            }
        }
        
        // Validasi total pembayaran sesuai dengan grand total
        if (Math.abs(payment.getAmount() - transaction.getGrandTotal()) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Jumlah pembayaran tidak sesuai. " +
                    "Harus: Rp %.2f, Dibayar: Rp %.2f",
                    transaction.getGrandTotal(), payment.getAmount())
            );
        }
        
        try {
            // Log payment initiation
            auditLogService.log(
                generateLogId(),
                transaction.getCashierId(),
                transaction.getTransactionId(),
                AuditAction.PAYMENT_INITIATED,
                String.format("Pembayaran: %s", payment.getClass().getSimpleName())
            );
            
            // Set payment ke transaction dan proses
            transaction.setPayment(payment);
            boolean paymentSuccess = transaction.processPayment();
            
            if (!paymentSuccess) {
                auditLogService.log(
                    generateLogId(),
                    transaction.getCashierId(),
                    transaction.getTransactionId(),
                    AuditAction.PAYMENT_FAILED,
                    "Pembayaran gagal diproses"
                );
                return false;
            }
            
            // Update stok untuk setiap item
            for (TransactionDetail detail : transaction.getDetails()) {
                // Dalam implementasi nyata, ini akan mengakses database
                // Untuk testing, kita simulasikan dengan update product stock
                // Product.setStock(Product.getStock() - detail.getQuantity());
                
                auditLogService.log(
                    generateLogId(),
                    transaction.getCashierId(),
                    transaction.getTransactionId(),
                    AuditAction.STOCK_UPDATED,
                    String.format("Product: %s, Stok berkurang: %d",
                        detail.getProductCode(), detail.getQuantity())
                );
            }
            
            // Log success
            auditLogService.log(
                generateLogId(),
                transaction.getCashierId(),
                transaction.getTransactionId(),
                AuditAction.PAYMENT_SUCCESS,
                String.format("Pembayaran berhasil. Total: Rp %.2f",
                    transaction.getGrandTotal())
            );
            
            // Simpan ke history
            transactionHistory.add(transaction);
            
            // Log transaction completion
            auditLogService.log(
                generateLogId(),
                transaction.getCashierId(),
                transaction.getTransactionId(),
                AuditAction.TRANSACTION_COMPLETED,
                "Transaksi selesai dengan sukses"
            );
            
            return true;
            
        } catch (Exception e) {
            auditLogService.log(
                generateLogId(),
                transaction.getCashierId(),
                transaction.getTransactionId(),
                AuditAction.ERROR_OCCURRED,
                "Error: " + e.getMessage()
            );
            throw new RuntimeException("Checkout gagal: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update stok produk (stub untuk integrasi dengan DAO)
     * Dalam implementasi nyata, ini akan mengakses database
     */
    @Override
    public void updateStock(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product tidak boleh null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(
                String.format("Stok tidak cukup. Stok tersedia: %d, diminta: %d",
                    product.getStock(), quantity)
            );
        }
        
        // Update stok
        product.setStock(product.getStock() - quantity);
    }
    
    /**
     * Hitung total transaksi
     */
    @Override
    public double calculateTransactionTotal(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction tidak boleh null");
        }
        return transaction.getGrandTotal();
    }
    
    /**
     * Dapatkan histori transaksi
     */
    @Override
    public List<Transaction> getTransactionHistory(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit harus lebih dari 0");
        }
        
        // Return n transaksi terakhir
        int fromIndex = Math.max(0, transactionHistory.size() - limit);
        return new ArrayList<>(transactionHistory.subList(fromIndex, transactionHistory.size()));
    }
    
    /**
     * ✓ VALIDASI CHECKOUT: Cek apakah semua produk dalam transaksi masih tersedia
     * Mengembalikan error message jika ada produk yang stoknya habis (0) atau tidak cukup
     * 
     * @param transaction Transaksi yang akan di-validate
     * @return null jika semua stok cukup, atau error message jika ada masalah
     */
    @Override
    public String validateCheckout(Transaction transaction) {
        if (transaction == null) {
            return "Error: Transaksi tidak valid";
        }
        
        if (transaction.getDetails().isEmpty()) {
            return "Error: Keranjang belanja kosong";
        }
        
        // Cek setiap item dalam transaksi
        for (TransactionDetail detail : transaction.getDetails()) {
            Product product = detail.getProduct();
            
            // Jika stok sudah 0 (habis), return error
            if (product.getStock() == 0) {
                return String.format("❌ CHECKOUT DITOLAK!\n\n" +
                    "Produk '%s' (Kode: %s) stoknya HABIS (0).\n\n" +
                    "SOLUSI: Lakukan penambahan stok terlebih dahulu di menu 'Kelola Stok Produk' " +
                    "sebelum melakukan transaksi ini.",
                    product.getName(), product.getCode());
            }
            
            // Jika stok tidak cukup, return error
            if (product.getStock() < detail.getQuantity()) {
                return String.format("❌ CHECKOUT DITOLAK!\n\n" +
                    "Stok produk '%s' (Kode: %s) tidak cukup.\n" +
                    "Stok tersedia: %d unit\n" +
                    "Diminta: %d unit\n\n" +
                    "SOLUSI: Kurangi jumlah pembelian atau tambahkan stok produk ini.",
                    product.getName(), product.getCode(), 
                    product.getStock(), detail.getQuantity());
            }
        }
        
        return null; // Semua stok cukup, checkout bisa dilanjutkan
    }
    
    /**
     * Generate ID yang unik untuk detail transaksi
     */
    private String generateDetailId() {
        return "DTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Generate ID yang unik untuk log
     */
    private String generateLogId() {
        return "LOG-" + System.currentTimeMillis() + "-" +
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
