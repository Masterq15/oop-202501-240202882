package com.upb.agripos.service;

import com.upb.agripos.model.audit.AuditLog;
import com.upb.agripos.model.audit.AuditAction;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementasi AuditLogServicePersonA
 * Mencatat semua aktivitas penting dalam sistem untuk audit trail
 * Mendukung filtering berdasarkan transaction, user, atau action
 * Person A - AUDIT SERVICE IMPLEMENTATION
 */
public class AuditLogServiceImpl implements AuditLogServicePersonA {
    
    private List<AuditLog> logs;
    
    public AuditLogServiceImpl() {
        this.logs = new ArrayList<>();
    }
    
    /**
     * Menambah log entry baru
     */
    @Override
    public void log(String logId, String userId, String transactionId,
                   AuditAction action, String details) {
        logWithIp(logId, userId, transactionId, action, details, "127.0.0.1");
    }
    
    /**
     * Menambah log entry dengan IP address
     */
    @Override
    public void logWithIp(String logId, String userId, String transactionId,
                         AuditAction action, String details, String ipAddress) {
        if (logId == null || logId.trim().isEmpty()) {
            logId = generateLogId();
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID tidak boleh kosong");
        }
        if (action == null) {
            throw new IllegalArgumentException("Action tidak boleh null");
        }
        
        AuditLog auditLog = new AuditLog(logId, userId, transactionId, action, details);
        if (ipAddress != null && !ipAddress.isEmpty()) {
            auditLog.setIpAddress(ipAddress);
        }
        
        logs.add(auditLog);
    }
    
    /**
     * Mengambil log berdasarkan transaction ID
     */
    @Override
    public List<AuditLog> getLogsByTransaction(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : logs) {
            if (transactionId.equals(log.getTransactionId())) {
                result.add(log);
            }
        }
        return result;
    }
    
    /**
     * Mengambil log berdasarkan user ID
     */
    @Override
    public List<AuditLog> getLogsByUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : logs) {
            if (userId.equals(log.getUserId())) {
                result.add(log);
            }
        }
        return result;
    }
    
    /**
     * Mengambil log berdasarkan action
     */
    @Override
    public List<AuditLog> getLogsByAction(AuditAction action) {
        if (action == null) {
            return new ArrayList<>();
        }
        
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : logs) {
            if (action == log.getAction()) {
                result.add(log);
            }
        }
        return result;
    }
    
    /**
     * Mengambil semua log dengan limit
     */
    @Override
    public List<AuditLog> getAllLogs(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit harus lebih dari 0");
        }
        
        // Return n log terakhir
        int fromIndex = Math.max(0, logs.size() - limit);
        return new ArrayList<>(logs.subList(fromIndex, logs.size()));
    }
    
    /**
     * Export log ke file (stub implementation)
     * Dalam implementasi nyata, akan export ke CSV, PDF, atau format lain
     */
    @Override
    public boolean exportLogs(String format, String filename) {
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Format tidak boleh kosong");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename tidak boleh kosong");
        }
        
        // Validasi format
        String upperFormat = format.toUpperCase();
        if (!upperFormat.equals("CSV") && !upperFormat.equals("PDF") && 
            !upperFormat.equals("EXCEL")) {
            throw new IllegalArgumentException("Format tidak didukung: " + format);
        }
        
        // Simulasi export
        System.out.println(String.format(
            "Exporting %d logs to %s in %s format",
            logs.size(), filename, format
        ));
        
        return true;
    }
    
    /**
     * Generate Log ID yang unik
     */
    private String generateLogId() {
        return "LOG-" + System.currentTimeMillis() + "-" +
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Dapatkan total jumlah logs yang tersimpan
     */
    public int getTotalLogs() {
        return logs.size();
    }
    
    /**
     * Clear semua logs (untuk testing)
     */
    public void clearLogs() {
        logs.clear();
    }
}
