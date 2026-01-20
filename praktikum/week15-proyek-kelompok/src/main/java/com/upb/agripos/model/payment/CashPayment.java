package com.upb.agripos.model.payment;

/**
 * Implementasi Payment untuk pembayaran tunai (Cash)
 * Menangani pembayaran tunai dan perhitungan kembalian
 * Person A - PAYMENT LAYER
 */
public class CashPayment extends Payment {
    private double amountPaid;
    private double change;
    
    public CashPayment(String paymentId, double amount, double amountPaid) {
        super(paymentId, amount);
        this.amountPaid = amountPaid;
        this.change = 0;
    }
    
    /**
     * Proses pembayaran tunai
     * Validasi uang yang dibayar cukup untuk membayar total
     * Hitung kembalian
     */
    @Override
    public boolean processPayment() {
        if (!validate()) {
            setStatus(PaymentStatus.FAILED);
            return false;
        }
        
        // Hitung kembalian
        this.change = amountPaid - getAmount();
        
        // Pembayaran berhasil
        setStatus(PaymentStatus.SUCCESS);
        return true;
    }
    
    /**
     * Validasi pembayaran tunai
     * - Uang yang dibayar harus >= total pembayaran
     * - Jumlah harus positif
     */
    @Override
    public boolean validate() {
        return amountPaid >= getAmount() && amountPaid > 0 && getAmount() > 0;
    }
    
    /**
     * Mendapatkan detail pembayaran tunai
     */
    @Override
    public String getPaymentDetails() {
        return String.format(
            "PEMBAYARAN TUNAI\n" +
            "ID: %s\n" +
            "Total: Rp %.2f\n" +
            "Uang Diterima: Rp %.2f\n" +
            "Kembalian: Rp %.2f\n" +
            "Status: %s",
            getPaymentId(),
            getAmount(),
            amountPaid,
            change,
            getStatus().getDescription()
        );
    }
    
    // Getters
    public double getAmountPaid() { return amountPaid; }
    public double getChange() { return change; }
    
    @Override
    public String toString() {
        return "CashPayment{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", amount=" + getAmount() +
                ", amountPaid=" + amountPaid +
                ", change=" + change +
                ", status=" + getStatus() +
                '}';
    }
}
