package com.upb.agripos.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Model
 * Merepresentasikan transaksi penjualan dalam sistem Agri-POS
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String code;
    private LocalDateTime transactionDate;
    private double subtotal;
    private double discount;
    private double total;
    private double cashPaid;
    private String paymentMethod;
    private String paymentStatus;
    private String cashierName;
    private List<CartItem> items;

    // Constructor untuk transaksi baru
    public Transaction() {
        this.id = 0;
        this.transactionDate = LocalDateTime.now();
        this.subtotal = 0;
        this.discount = 0;
        this.total = 0;
        this.items = new ArrayList<>();
    }

    // Constructor lengkap dari database
    public Transaction(int id, String code, LocalDateTime transactionDate, 
                      double subtotal, double discount, double total, 
                      String paymentMethod, String paymentStatus, String cashierName) {
        this.id = id;
        this.code = code;
        this.transactionDate = transactionDate;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.cashierName = cashierName;
        this.items = new ArrayList<>();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public double getCashPaid() { return cashPaid; }
    public void setCashPaid(double cashPaid) { this.cashPaid = cashPaid; }

    public double getChange() { 
        return Math.max(0, cashPaid - total); 
    }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "code='" + code + '\'' +
                ", date=" + transactionDate +
                ", total=" + total +
                ", method='" + paymentMethod + '\'' +
                '}';
    }
}
