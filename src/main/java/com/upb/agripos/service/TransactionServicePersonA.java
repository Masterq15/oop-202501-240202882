package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.transaction.Transaction;
import com.upb.agripos.model.transaction.TransactionDetail;
import com.upb.agripos.model.payment.Payment;
import com.upb.agripos.discount.DiscountStrategyPersonB;
import java.util.List;

/**
 * Service interface untuk menghandle proses transaksi dan checkout
 * Mengelola transaksi dari penambahan item hingga pembayaran
 * Person A - TRANSACTION SERVICE LAYER
 */
public interface TransactionServicePersonA {
    
    /**
     * Membuat transaksi baru
     * @param transactionId ID transaksi unik
     * @param cashierId ID kasir yang melakukan transaksi
     * @return Transaction object baru
     */
    Transaction createTransaction(String transactionId, String cashierId);
    
    /**
     * Menambah item ke dalam transaksi
     * @param transaction transaksi yang akan ditambah item
     * @param product produk yang ditambahkan
     * @param quantity jumlah produk
     * @param discountStrategy strategi diskon yang diterapkan (bisa null)
     * @throws IllegalArgumentException jika stok tidak cukup
     */
    void addItemToTransaction(Transaction transaction, Product product,
                             int quantity, DiscountStrategyPersonB discountStrategy);
    
    /**
     * Menghapus item dari transaksi
     * @param transaction transaksi yang akan dihapus item
     * @param detailId ID detail item yang dihapus
     */
    void removeItemFromTransaction(Transaction transaction, String detailId);
    
    /**
     * Proses checkout transaksi
     * Validasi semua item, update stok, proses pembayaran
     * @param transaction transaksi yang akan di-checkout
     * @param payment pembayaran yang akan diproses
     * @return true jika checkout berhasil
     */
    boolean checkout(Transaction transaction, Payment payment);
    
    /**
     * Validasi checkout - cek apakah semua produk stoknya cukup sebelum pembayaran
     * @param transaction transaksi yang akan di-validate
     * @return null jika semua stok cukup, atau error message jika ada masalah
     */
    String validateCheckout(Transaction transaction);
    
    /**
     * Update stok produk setelah transaksi berhasil
     * @param product produk yang stoknya diupdate
     * @param quantity jumlah pengurangan stok
     * @throws IllegalArgumentException jika stok tidak cukup
     */
    void updateStock(Product product, int quantity);
    
    /**
     * Hitung total transaksi
     * @param transaction transaksi yang akan dihitung totalnya
     * @return total harga yang harus dibayar
     */
    double calculateTransactionTotal(Transaction transaction);
    
    /**
     * Dapatkan histori transaksi
     * @param limit jumlah transaksi yang diambil
     * @return List transaksi terbaru
     */
    List<Transaction> getTransactionHistory(int limit);
}
