package com.upb.agripos.service;

import com.upb.agripos.model.Transaction;
import com.upb.agripos.exception.ValidationException;
import java.util.List;

/**
 * TransactionService Interface - FR-2 & FR-4 Transaksi & Laporan
 * 
 * Implementer: TransactionServiceImpl
 * 
 * Handles:
 * - Create transaction from cart
 * - Process payment
 * - Generate receipt/struk
 * - Generate reports
 * 
 * Created by: [Person C - Service/Payment]
 * Last modified: 
 */
public interface TransactionService {

    /**
     * FR-2: Buat transaksi dari keranjang yang ada
     * Melakukan checkout + update stok
     */
    String checkout(PaymentMethod paymentMethod, DiscountStrategy discount) throws ValidationException;

    /**
     * FR-4: Get laporan penjualan harian
     */
    List<Transaction> getDailyReport(String date);

    /**
     * FR-4: Get laporan penjualan by range tanggal
     */
    List<Transaction> getReportByDateRange(String startDate, String endDate);

    /**
     * FR-4: Get total penjualan for period
     */
    double getTotalSales(String startDate, String endDate);

    /**
     * Get transaction by ID
     */
    Transaction findById(String transactionId);
}
