package com.upb.agripos.service;

/**
 * AuditLogService Interface - OFR-6 Audit Log & Approval
 * 
 * Implementer: AuditLogServiceImpl
 * 
 * Handles:
 * - Log setiap aksi penting (CREATE, UPDATE, DELETE, VOID, REFUND)
 * - Track user yang melakukan aksi + timestamp
 * - Provide audit trail untuk compliance
 * 
 * Created by: [Person C - Service Layer]
 * Last modified: 
 */
public interface AuditLogService {

    /**
     * Log aksi dengan detail
     * 
     * @param action aksi yang dilakukan (CREATE, UPDATE, DELETE, VOID, REFUND, LOGIN, LOGOUT)
     * @param entityType tipe entity (PRODUCT, TRANSACTION, USER)
     * @param entityId ID entity yang di-akses
     * @param userId user yang melakukan aksi
     * @param details detail tambahan (optional)
     */
    void log(String action, String entityType, String entityId, String userId, String details);

    /**
     * Get audit log untuk entity tertentu
     */
    String getAuditLog(String entityId);

    /**
     * Get all audit log (untuk admin review)
     */
    String getAllAuditLogs();
}
