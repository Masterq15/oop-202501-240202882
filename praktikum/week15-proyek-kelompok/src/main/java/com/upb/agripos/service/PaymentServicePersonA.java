package com.upb.agripos.service;

import com.upb.agripos.model.payment.Payment;
import com.upb.agripos.model.payment.CashPayment;
import com.upb.agripos.model.payment.EWalletPayment;

/**
 * Service interface untuk menghandle operasi pembayaran
 * Mendefinisikan kontrak untuk berbagai tipe pembayaran
 * Person A - PAYMENT SERVICE LAYER
 */
public interface PaymentServicePersonA {
    
    /**
     * Proses pembayaran tunai
     * @param paymentId ID pembayaran unik
     * @param amount total pembayaran yang harus dibayar
     * @param amountPaid jumlah uang tunai yang dibayar
     * @return CashPayment object dengan status dan kembalian
     * @throws IllegalArgumentException jika validasi gagal
     */
    CashPayment processCashPayment(String paymentId, double amount, double amountPaid);
    
    /**
     * Proses pembayaran e-wallet
     * @param paymentId ID pembayaran unik
     * @param amount total pembayaran yang harus dibayar
     * @param walletProvider nama provider e-wallet (GCash, PayMaya, OVO, dll)
     * @param walletAccount nomor akun e-wallet
     * @return EWalletPayment object dengan transaction reference
     * @throws IllegalArgumentException jika validasi gagal
     */
    EWalletPayment processEWalletPayment(String paymentId, double amount,
                                         String walletProvider, String walletAccount);
    
    /**
     * Validasi pembayaran
     * @param payment Payment object yang akan divalidasi
     * @return true jika pembayaran valid
     */
    boolean validatePayment(Payment payment);
    
    /**
     * Hitung kembalian untuk pembayaran tunai
     * @param totalAmount total yang harus dibayar
     * @param amountPaid jumlah uang yang dibayar
     * @return jumlah kembalian
     */
    double calculateChange(double totalAmount, double amountPaid);
}
