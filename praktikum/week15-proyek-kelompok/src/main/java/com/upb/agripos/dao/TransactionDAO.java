package com.upb.agripos.dao;

import java.util.List;

import com.upb.agripos.model.Transaction;

/**
 * TransactionDAO Interface
 * Mendefinisikan kontrak untuk akses data transaksi
 */
public interface TransactionDAO {
    /**
     * Simpan transaksi baru ke database
     * @param transaction Objek transaction yang akan disimpan
     * @return Transaction dengan ID yang sudah ter-generate
     */
    Transaction save(Transaction transaction) throws Exception;

    /**
     * Cari transaksi berdasarkan ID
     * @param id ID transaksi
     * @return Transaction atau null jika tidak ditemukan
     */
    Transaction findById(int id) throws Exception;

    /**
     * Cari transaksi berdasarkan kode transaksi
     * @param code Kode transaksi (TRX-xxx)
     * @return Transaction atau null jika tidak ditemukan
     */
    Transaction findByCode(String code) throws Exception;

    /**
     * Ambil semua transaksi
     * @return List semua transaksi
     */
    List<Transaction> findAll() throws Exception;

    /**
     * Cari transaksi dalam range tanggal
     * @param startDate Tanggal mulai (format: yyyy-MM-dd)
     * @param endDate Tanggal akhir (format: yyyy-MM-dd)
     * @return List transaksi dalam range tersebut
     */
    List<Transaction> findByDateRange(String startDate, String endDate) throws Exception;

    /**
     * Simpan item detail dalam transaksi
     * @param transactionId ID transaksi
     * @param productId ID produk
     * @param quantity Jumlah item
     * @param unitPrice Harga per unit
     * @param subtotal Total harga item
     * @return true jika berhasil
     */
    boolean saveTransactionItem(int transactionId, int productId, int quantity, double unitPrice, double subtotal) throws Exception;
}
