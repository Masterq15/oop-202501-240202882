package com.upb.agripos.service;

/**
 * PaymentResult - Hasil dari proses pembayaran
 * 
 * Mengembalikan informasi status pembayaran, pesan, dan kembalian uang
 * Digunakan oleh PaymentMethod implementations (CashPayment, EWalletPayment)
 */
public class PaymentResult {
    private final boolean success;
    private final String message;
    private final double change;

    /**
     * Constructor untuk PaymentResult
     * @param success Status pembayaran berhasil atau gagal
     * @param message Pesan detail pembayaran
     * @param change Jumlah uang kembalian (untuk pembayaran tunai)
     */
    public PaymentResult(boolean success, String message, double change) {
        this.success = success;
        this.message = message;
        this.change = change;
    }

    /**
     * Cek apakah pembayaran berhasil
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Dapatkan pesan pembayaran
     */
    public String getMessage() {
        return message;
    }

    /**
     * Dapatkan uang kembalian
     */
    public double getChange() {
        return change;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", change=" + change +
                '}';
    }
}
