package com.upb.agripos.service;

import com.upb.agripos.model.payment.Payment;
import com.upb.agripos.model.payment.CashPayment;
import com.upb.agripos.model.payment.EWalletPayment;
import java.util.UUID;

/**
 * Implementasi PaymentServicePersonA
 * Menangani berbagai metode pembayaran (tunai dan e-wallet)
 * Person A - PAYMENT SERVICE IMPLEMENTATION
 */
public class PaymentServiceImpl implements PaymentServicePersonA {
    
    /**
     * Proses pembayaran tunai
     * Validasi uang yang dibayar cukup dan hitung kembalian
     */
    @Override
    public CashPayment processCashPayment(String paymentId, double amount, double amountPaid) {
        // Validasi input
        if (amount <= 0) {
            throw new IllegalArgumentException("Total pembayaran harus lebih dari 0");
        }
        if (amountPaid < 0) {
            throw new IllegalArgumentException("Jumlah uang yang dibayar tidak boleh negatif");
        }
        
        // Buat payment object
        CashPayment payment = new CashPayment(paymentId, amount, amountPaid);
        
        // Validasi dan proses pembayaran
        if (!payment.processPayment()) {
            throw new IllegalArgumentException(
                String.format("Uang tidak cukup. Harus: Rp %.2f, Dibayar: Rp %.2f",
                    amount, amountPaid)
            );
        }
        
        return payment;
    }
    
    /**
     * Proses pembayaran e-wallet
     * Koneksi dengan payment gateway dan generate transaction reference
     */
    @Override
    public EWalletPayment processEWalletPayment(String paymentId, double amount,
                                                String walletProvider, String walletAccount) {
        // Validasi input
        if (amount <= 0) {
            throw new IllegalArgumentException("Total pembayaran harus lebih dari 0");
        }
        if (walletProvider == null || walletProvider.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider e-wallet tidak boleh kosong");
        }
        if (walletAccount == null || walletAccount.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor akun e-wallet tidak boleh kosong");
        }
        
        // Validasi provider yang didukung
        String[] supportedProviders = {"GCash", "PayMaya", "OVO", "DANA", "LinkAja"};
        boolean isSupported = false;
        for (String provider : supportedProviders) {
            if (provider.equalsIgnoreCase(walletProvider)) {
                isSupported = true;
                break;
            }
        }
        if (!isSupported) {
            throw new IllegalArgumentException(
                "Provider e-wallet tidak didukung: " + walletProvider
            );
        }
        
        // Buat payment object
        EWalletPayment payment = new EWalletPayment(paymentId, amount, 
                                                     walletProvider, walletAccount);
        
        // Proses pembayaran
        if (!payment.processPayment()) {
            throw new IllegalArgumentException(
                "Pembayaran e-wallet gagal. Silakan coba lagi atau gunakan metode pembayaran lain."
            );
        }
        
        return payment;
    }
    
    /**
     * Validasi payment
     */
    @Override
    public boolean validatePayment(Payment payment) {
        return payment != null && payment.validate();
    }
    
    /**
     * Hitung kembalian untuk pembayaran tunai
     */
    @Override
    public double calculateChange(double totalAmount, double amountPaid) {
        if (amountPaid < totalAmount) {
            return 0; // Tidak ada kembalian, uang tidak cukup
        }
        return amountPaid - totalAmount;
    }
    
    /**
     * Generate payment ID yang unik
     */
    public static String generatePaymentId() {
        return "PAY-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
