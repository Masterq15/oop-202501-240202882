package com.upb.agripos.service;

import com.upb.agripos.exception.ValidationException;

/**
 * EWalletPayment
 * Implementasi pembayaran e-wallet/kartu
 */
public class EWalletPayment implements PaymentMethod {

    @Override
    public PaymentResult processPayment(double amount, String input) throws ValidationException {
        // Simulasi verifikasi e-wallet
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Nomor akun e-wallet tidak boleh kosong");
        }

        // Validasi format: minimal 6 karakter, bisa berisi angka dan huruf
        if (input.length() < 6 || !input.matches("[a-zA-Z0-9]+")) {
            throw new ValidationException("Format nomor akun tidak valid (minimal 6 karakter, angka/huruf)");
        }

        // Mock: cek saldo (asumsi semua akun memiliki saldo cukup)
        boolean hasSufficientBalance = simulateBalanceCheck(amount);
        
        if (!hasSufficientBalance) {
            throw new ValidationException("Saldo e-wallet tidak cukup");
        }

        String message = "Pembayaran e-wallet berhasil. Akun: " + maskAccountNumber(input);
        return new PaymentResult(true, message, 0);
    }

    @Override
    public String getMethodName() {
        return "E-Wallet";
    }

    private boolean simulateBalanceCheck(double amount) {
        // Simulasi: anggap semua e-wallet memiliki saldo minimal 5 juta
        return amount <= 5000000;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber.length() > 4) {
            return "****" + accountNumber.substring(accountNumber.length() - 4);
        }
        return "****" + accountNumber;
    }
}
