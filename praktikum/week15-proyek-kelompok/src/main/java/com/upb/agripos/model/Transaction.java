package com.upb.agripos.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Model untuk FR-2 & FR-4
 * 
 * Attributes:
 * - transactionId: unique identifier
 * - transactionDate: tanggal transaksi
 * - cashier: kasir yang handle
 * - items: daftar CartItem
 * - subtotal: total sebelum diskon
 * - discountAmount: jumlah diskon (OFR-2)
 * - totalAmount: total akhir
 * - paymentMethod: CASH / EWALLET (FR-3)
 * - status: SUCCESS / FAILED / CANCELLED
 * 
 * Created by: [Person B/C - Service Layer]
 * Last modified: 
 */
public class Transaction {
    private String transactionId;
    private LocalDateTime transactionDate;
    private User cashier;
    private List<CartItem> items;
    private double subtotal;
    private double discountAmount;  // OFR-2
    private double totalAmount;
    private String paymentMethod;   // CASH, EWALLET
    private String status;          // SUCCESS, FAILED, CANCELLED

    public Transaction(String transactionId, LocalDateTime transactionDate, User cashier, List<CartItem> items) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.cashier = cashier;
        this.items = items;
        this.discountAmount = 0;
        this.status = "PENDING";
    }

    // Getters & Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public User getCashier() { return cashier; }
    public void setCashier(User cashier) { this.cashier = cashier; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return transactionId + " | " + transactionDate + " | Rp " + totalAmount + " | " + status;
    }
}
