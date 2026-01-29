package com.upb.agripos.service;

import com.upb.agripos.exception.ValidationException;

/**
 * PaymentMethod Interface (Strategy Pattern)
 * Mendefinisikan kontrak untuk berbagai metode pembayaran
 * 
 * Implementasi OCP (Open/Closed Principle):
 * - Mudah menambah metode pembayaran baru tanpa mengubah kode inti
 * - Terbuka untuk ekstensi, tertutup untuk modifikasi
 */
public interface PaymentMethod {
    /**
     * Proses pembayaran
     * @param amount Jumlah yang harus dibayarkan
     * @param input  Input tambahan (misalnya uang tunai, nomor akun)
     * @return Kembalian atau pesan sukses
     */
    PaymentResult processPayment(double amount, String input) throws ValidationException;

    /**
     * Mendapatkan nama metode pembayaran
     */
    String getMethodName();
}
