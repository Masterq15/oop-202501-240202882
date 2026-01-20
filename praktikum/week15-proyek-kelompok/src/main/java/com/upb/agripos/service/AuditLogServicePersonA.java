package com.upb.agripos.service;

import com.upb.agripos.model.audit.AuditLog;
import com.upb.agripos.model.audit.AuditAction;
import java.util.List;

/**
 * Service interface untuk menghandle audit logging
 * Mencatat semua aktivitas penting dalam sistem untuk keperluan audit trail
 * Person A - AUDIT SERVICE LAYER
 */
public interface AuditLogServicePersonA {
    
    /**
     * Menambah log entry baru
     * @param logId ID log yang unik
     * @param userId ID user yang melakukan aksi
     * @param transactionId ID transaksi (bisa null untuk non-transaction actions)
     * @param action tipe aksi yang dicatat
     * @param details detail informasi tambahan tentang aksi
     */
    void log(String logId, String userId, String transactionId,
            AuditAction action, String details);
    
    /**
     * Menambah log entry dengan custom IP address
     * @param logId ID log yang unik
     * @param userId ID user yang melakukan aksi
     * @param transactionId ID transaksi (bisa null)
     * @param action tipe aksi yang dicatat
     * @param details detail informasi tambahan tentang aksi
     * @param ipAddress IP address dari client
     */
    void logWithIp(String logId, String userId, String transactionId,
                  AuditAction action, String details, String ipAddress);
    
    /**
     * Mengambil log berdasarkan transaksi ID
     * @param transactionId ID transaksi
     * @return List log yang terkait dengan transaksi tersebut
     */
    List<AuditLog> getLogsByTransaction(String transactionId);
    
    /**
     * Mengambil log berdasarkan user ID
     * @param userId ID user
     * @return List log yang dilakukan oleh user tersebut
     */
    List<AuditLog> getLogsByUser(String userId);
    
    /**
     * Mengambil log berdasarkan tipe aksi
     * @param action tipe aksi
     * @return List log dengan aksi tersebut
     */
    List<AuditLog> getLogsByAction(AuditAction action);
    
    /**
     * Mengambil semua log (dengan limit untuk performa)
     * @param limit maksimal jumlah log yang diambil
     * @return List log terbaru
     */
    List<AuditLog> getAllLogs(int limit);
    
    /**
     * Export log ke format tertentu (CSV, PDF, dll)
     * @param format format export (CSV, PDF, EXCEL)
     * @param filename nama file output
     * @return true jika export berhasil
     */
    boolean exportLogs(String format, String filename);
}
