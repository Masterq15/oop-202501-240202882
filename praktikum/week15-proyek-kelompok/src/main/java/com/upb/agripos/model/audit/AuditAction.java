package com.upb.agripos.model.audit;

/**
 * Enum untuk tipe aktivitas yang dicatat di audit log
 * Person A - AUDIT LAYER
 */
public enum AuditAction {
    TRANSACTION_START("Transaksi dimulai"),
    ITEM_ADDED("Item ditambahkan"),
    ITEM_REMOVED("Item dihapus"),
    DISCOUNT_APPLIED("Diskon diterapkan"),
    PAYMENT_INITIATED("Pembayaran dimulai"),
    PAYMENT_SUCCESS("Pembayaran berhasil"),
    PAYMENT_FAILED("Pembayaran gagal"),
    STOCK_UPDATED("Stok diupdate"),
    TRANSACTION_COMPLETED("Transaksi selesai"),
    TRANSACTION_CANCELLED("Transaksi dibatalkan"),
    ERROR_OCCURRED("Error terjadi");
    
    private final String description;
    
    AuditAction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
