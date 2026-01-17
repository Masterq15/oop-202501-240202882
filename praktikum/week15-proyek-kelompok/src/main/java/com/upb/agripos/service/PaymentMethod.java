package com.upb.agripos.service;

/**
 * PaymentMethod Interface - FR-3 Metode Pembayaran
 * 
 * Strategy Pattern: Flexible untuk berbagai metode pembayaran
 * 
 * Implementers:
 * - CashPayment (tunai)
 * - EWalletPayment (e-wallet: GCash, OVO, Dana, etc)
 * - [Future] CardPayment, BankTransfer, QRIS
 * 
 * OCP (Open/Closed Principle): Bisa add payment type baru tanpa modify existing code
 * 
 * Created by: [Person C - Service/Payment]
 * Last modified: 
 */
public interface PaymentMethod {

    /**
     * Proses pembayaran
     * 
     * @param amount jumlah yang dibayar
     * @return true jika pembayaran berhasil
     */
    boolean processPayment(double amount);

    /**
     * Get nama metode pembayaran
     */
    String getMethodName();

    /**
     * Validasi pembayaran sebelum process
     */
    boolean validatePayment(double amount);

    /**
     * Get jumlah kembalian (untuk tunai)
     * Return 0 untuk non-cash payment
     */
    double getChange(double amount);
}
