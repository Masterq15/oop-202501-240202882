package com.upb.agripos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * PurchaseHistory - Riwayat Pembelian/Transaksi
 * Menyimpan data pembelian untuk laporan admin
 */
public class PurchaseHistory {
    private static final PurchaseHistory instance = new PurchaseHistory();
    private static final List<PurchaseRecord> records = new ArrayList<>();
    
    private PurchaseHistory() {
    }
    
    public static PurchaseHistory getInstance() {
        return instance;
    }
    
    /**
     * Tambah record pembelian baru
     */
    public static void addRecord(PurchaseRecord record) {
        records.add(record);
    }
    
    /**
     * Dapatkan semua record pembelian
     */
    public static List<PurchaseRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
    
    /**
     * Dapatkan record berdasarkan tanggal
     */
    public static List<PurchaseRecord> getRecordsByDate(String date) {
        List<PurchaseRecord> result = new ArrayList<>();
        for (PurchaseRecord record : records) {
            if (record.getDate().contains(date)) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * Clear semua records (untuk reset)
     */
    public static void clearRecords() {
        records.clear();
    }
    
    /**
     * Inner class untuk satu record pembelian
     */
    public static class PurchaseRecord {
        private String transactionId;
        private String date;
        private String time;
        private String productCode;
        private String productName;
        private int quantity;
        private long unitPrice;
        private long subtotal;
        private String cashier;
        private String paymentMethod;
        
        public PurchaseRecord(String transactionId, String productCode, String productName, 
                            int quantity, long unitPrice, long subtotal, String cashier, String paymentMethod) {
            this.transactionId = transactionId;
            this.productCode = productCode;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
            this.cashier = cashier;
            this.paymentMethod = paymentMethod;
            
            // Set tanggal dan waktu otomatis
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            this.date = now.format(dateFormatter);
            this.time = now.format(timeFormatter);
        }
        
        // Getters
        public String getTransactionId() { return transactionId; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getProductCode() { return productCode; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public long getUnitPrice() { return unitPrice; }
        public long getSubtotal() { return subtotal; }
        public String getCashier() { return cashier; }
        public String getPaymentMethod() { return paymentMethod; }
        
        @Override
        public String toString() {
            return String.format("%s | %s | %s | %s x Rp %,d = Rp %,d | %s | %s", 
                date + " " + time, transactionId, productCode + " - " + productName, 
                quantity, unitPrice, subtotal, cashier, paymentMethod)
                    .replace(",", ".");
        }
    }
}
