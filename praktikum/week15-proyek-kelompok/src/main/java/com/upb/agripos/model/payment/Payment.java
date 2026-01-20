package com.upb.agripos.model.payment;

import java.time.LocalDateTime;

/**
 * Abstract base class untuk Payment
 * Mewakili metode pembayaran dalam sistem POS
 * Person A - PAYMENT LAYER
 */
public abstract class Payment {
    private String paymentId;
    private double amount;
    private LocalDateTime paymentTime;
    private PaymentStatus status;
    
    public Payment(String paymentId, double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentTime = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }
    
    /**
     * Proses pembayaran
     * @return true jika pembayaran berhasil
     */
    public abstract boolean processPayment();
    
    /**
     * Validasi pembayaran sebelum diproses
     * @return true jika valid
     */
    public abstract boolean validate();
    
    /**
     * Mendapatkan detail pembayaran
     * @return string deskripsi pembayaran
     */
    public abstract String getPaymentDetails();
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public PaymentStatus getStatus() { return status; }
    
    // Setters
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentTime=" + paymentTime +
                ", status=" + status +
                '}';
    }
}
