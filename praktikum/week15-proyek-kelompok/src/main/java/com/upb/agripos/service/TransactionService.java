package com.upb.agripos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.upb.agripos.dao.TransactionDAO;
import com.upb.agripos.dao.TransactionDAOImpl;
import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Transaction;

/**
 * TransactionService
 * Service layer untuk business logic transaksi
 * Menangani penyimpanan transaksi, item transaksi, dan update stok
 */
public class TransactionService {
    private TransactionDAO transactionDAO;
    private ProductService productService;

    public TransactionService() {
        this.transactionDAO = new TransactionDAOImpl();
        this.productService = new ProductService();
    }

    // Untuk testing: allow dependency injection
    public TransactionService(TransactionDAO transactionDAO, ProductService productService) {
        this.transactionDAO = transactionDAO;
        this.productService = productService;
    }

    /**
     * FR-3: Buat transaksi baru dengan menyimpan ke database dan update stok
     * @param paymentMethod Metode pembayaran (TUNAI, EWALLET, dll)
     * @param total Total pembayaran
     * @param cashierName Nama kasir
     * @param items List item yang dibeli
     * @param cashPaid Jumlah uang yang dibayarkan (untuk Tunai)
     * @return Transaction yang sudah disimpan
     */
    public Transaction createTransaction(String paymentMethod, double total, String cashierName, List<CartItem> items, double cashPaid) throws Exception {
        if (items == null || items.isEmpty()) {
            throw new ValidationException("Tidak ada item dalam transaksi");
        }

        Transaction transaction = new Transaction();
        transaction.setCode(generateTransactionCode());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPaymentMethod(paymentMethod);
        transaction.setPaymentStatus("COMPLETED");
        transaction.setCashierName(cashierName);
        transaction.setItems(items);
        transaction.setCashPaid(cashPaid);

        // Hitung subtotal
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        transaction.setSubtotal(subtotal);
        transaction.setDiscount(0);
        transaction.setTotal(total);

        // Simpan transaksi ke database
        transaction = transactionDAO.save(transaction);

        // Simpan item-item transaksi dan update stok
        for (CartItem item : items) {
            // Simpan detail item transaksi
            transactionDAO.saveTransactionItem(
                    transaction.getId(),
                    item.getProduct().getId(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal()
            );
            
            // Update stok produk (kurangi stok)
            productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
        }

        return transaction;
    }

    /**
     * Cari transaksi berdasarkan ID
     */
    public Transaction findById(int id) throws Exception {
        return transactionDAO.findById(id);
    }

    /**
     * Cari transaksi berdasarkan kode
     */
    public Transaction findByCode(String code) throws Exception {
        return transactionDAO.findByCode(code);
    }

    /**
     * Ambil semua transaksi
     */
    public List<Transaction> findAll() throws Exception {
        return transactionDAO.findAll();
    }

    /**
     * Cari transaksi dalam range tanggal
     */
    public List<Transaction> findByDateRange(String startDate, String endDate) throws Exception {
        return transactionDAO.findByDateRange(startDate, endDate);
    }

    /**
     * Generate nomor transaksi unik dengan format TRX-yyyyMMddHHmmss-XXXXXXXX
     */
    private String generateTransactionCode() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "TRX-" + now.format(formatter) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
