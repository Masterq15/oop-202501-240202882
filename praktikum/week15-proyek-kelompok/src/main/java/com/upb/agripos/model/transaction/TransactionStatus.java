package com.upb.agripos.model.transaction;

/**
 * Enum untuk status transaksi
 * Person A - TRANSACTION LAYER
 */
public enum TransactionStatus {
    PENDING("Pending"),
    PROCESSING("Sedang diproses"),
    COMPLETED("Selesai"),
    FAILED("Gagal"),
    CANCELLED("Dibatalkan");
    
    private final String description;
    
    TransactionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
