package com.upb.agripos.model.payment;

/**
 * Enum untuk status pembayaran
 * Person A - PAYMENT LAYER
 */
public enum PaymentStatus {
    PENDING("Pending"),
    SUCCESS("Berhasil"),
    FAILED("Gagal"),
    CANCELLED("Dibatalkan");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
