package com.upb.agripos.model.transaction;

import com.upb.agripos.model.payment.Payment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mewakili satu transaksi/penjualan
 * Mengandung detail items, pembayaran, dan informasi transaksi
 * Person A - TRANSACTION LAYER
 */
public class Transaction {
    private String transactionId;
    private String cashierId;
    private LocalDateTime transactionTime;
    private List<TransactionDetail> details;
    private double subtotal;
    private double totalDiscount;
    private double grandTotal;
    private Payment payment;
    private TransactionStatus status;
    
    public Transaction(String transactionId, String cashierId) {
        this.transactionId = transactionId;
        this.cashierId = cashierId;
        this.transactionTime = LocalDateTime.now();
        this.details = new ArrayList<>();
        this.subtotal = 0;
        this.totalDiscount = 0;
        this.grandTotal = 0;
        this.status = TransactionStatus.PENDING;
    }
    
    /**
     * Tambah item ke transaksi
     */
    public void addDetail(TransactionDetail detail) {
        details.add(detail);
        calculateTotals();
    }
    
    /**
     * Hitung ulang subtotal, diskon, dan grand total
     */
    private void calculateTotals() {
        this.subtotal = 0;
        this.totalDiscount = 0;
        
        for (TransactionDetail detail : details) {
            subtotal += detail.getSubtotal();
            totalDiscount += detail.getDiscount();
        }
        
        this.grandTotal = subtotal - totalDiscount;
    }
    
    /**
     * Set pembayaran untuk transaksi
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    /**
     * Proses pembayaran
     */
    public boolean processPayment() {
        if (payment == null) {
            this.status = TransactionStatus.FAILED;
            return false;
        }
        
        if (payment.processPayment()) {
            this.status = TransactionStatus.COMPLETED;
            return true;
        } else {
            this.status = TransactionStatus.FAILED;
            return false;
        }
    }
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public String getCashierId() { return cashierId; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
    public List<TransactionDetail> getDetails() { return details; }
    public double getSubtotal() { return subtotal; }
    public double getTotalDiscount() { return totalDiscount; }
    public double getGrandTotal() { return grandTotal; }
    public Payment getPayment() { return payment; }
    public TransactionStatus getStatus() { return status; }
    
    @Override
    public String toString() {
        return String.format(
            "Transaction{id='%s', cashier='%s', items=%d, " +
            "subtotal=%.2f, discount=%.2f, total=%.2f, status=%s}",
            transactionId, cashierId, details.size(),
            subtotal, totalDiscount, grandTotal, status.getDescription()
        );
    }
}
