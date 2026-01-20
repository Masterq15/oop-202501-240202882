package com.upb.agripos.model.audit;

import java.time.LocalDateTime;

/**
 * Mewakili satu log entry untuk audit trail
 * Mencatat semua aktivitas penting dalam sistem
 * Person A - AUDIT LAYER
 */
public class AuditLog {
    private String logId;
    private String userId;
    private String transactionId;
    private AuditAction action;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
    
    public AuditLog(String logId, String userId, String transactionId,
                   AuditAction action, String details) {
        this.logId = logId;
        this.userId = userId;
        this.transactionId = transactionId;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.ipAddress = "127.0.0.1"; // Default, bisa diupdate
    }
    
    // Getters
    public String getLogId() { return logId; }
    public String getUserId() { return userId; }
    public String getTransactionId() { return transactionId; }
    public AuditAction getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getIpAddress() { return ipAddress; }
    
    // Setters
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    @Override
    public String toString() {
        return String.format(
            "[%s] %s - User: %s | Transaction: %s | Action: %s | Details: %s",
            timestamp, logId, userId, transactionId,
            action.getDescription(), details
        );
    }
}
