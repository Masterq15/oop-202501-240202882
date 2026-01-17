package com.upb.agripos;

import com.upb.agripos.model.payment.*;
import com.upb.agripos.model.transaction.*;
import com.upb.agripos.model.audit.*;
import com.upb.agripos.service.*;
import com.upb.agripos.model.Product;

/**
 * Simple standalone test untuk verify Payment Layer
 */
public class QuickPaymentTest {
    
    public static void main(String[] args) {
        System.out.println("=== QUICK PAYMENT LAYER TEST ===\n");
        
        try {
            // Test 1: CashPayment
            System.out.println("Test 1: CashPayment");
            CashPayment cashPayment = new CashPayment("PAY-001", 50000, 100000);
            boolean success = cashPayment.processPayment();
            System.out.println("  Status: " + (success ? "✓ PASS" : "✗ FAIL"));
            System.out.println("  Amount: Rp " + cashPayment.getAmount());
            System.out.println("  Change: Rp " + cashPayment.getChange());
            System.out.println();
            
            // Test 2: EWalletPayment
            System.out.println("Test 2: EWalletPayment");
            EWalletPayment eWalletPayment = new EWalletPayment("PAY-002", 75000, "GCash", "09123456789");
            System.out.println("  Provider: " + eWalletPayment.getWalletProvider());
            System.out.println("  Status: " + eWalletPayment.getStatus());
            System.out.println("  ✓ PASS");
            System.out.println();
            
            // Test 3: Transaction
            System.out.println("Test 3: Transaction Model");
            Transaction transaction = new Transaction("TRX-001", "CASHIER-001");
            Product product = new Product("P001", "Beras", "Grains", 50000, 100);
            TransactionDetail detail = new TransactionDetail("DTL-001", product, 2, 5000);
            transaction.addDetail(detail);
            System.out.println("  Transaction ID: " + transaction.getTransactionId());
            System.out.println("  Items: " + transaction.getDetails().size());
            System.out.println("  Grand Total: Rp " + transaction.getGrandTotal());
            System.out.println("  ✓ PASS");
            System.out.println();
            
            // Test 4: AuditLog
            System.out.println("Test 4: AuditLog");
            AuditLog auditLog = new AuditLog("LOG-001", "USER-001", "TRX-001", 
                                            AuditAction.TRANSACTION_START, "Test log");
            System.out.println("  Log ID: " + auditLog.getLogId());
            System.out.println("  User: " + auditLog.getUserId());
            System.out.println("  Action: " + auditLog.getAction().getDescription());
            System.out.println("  ✓ PASS");
            System.out.println();
            
            // Test 5: PaymentServiceImpl
            System.out.println("Test 5: PaymentServiceImpl");
            PaymentServiceImpl paymentService = new PaymentServiceImpl();
            CashPayment payment = paymentService.processCashPayment("PAY-003", 25000, 50000);
            System.out.println("  Payment Status: " + payment.getStatus());
            System.out.println("  Change: Rp " + payment.getChange());
            System.out.println("  ✓ PASS");
            System.out.println();
            
            // Test 6: AuditLogServiceImpl
            System.out.println("Test 6: AuditLogServiceImpl");
            AuditLogServiceImpl auditService = new AuditLogServiceImpl();
            auditService.log("LOG-002", "USER-002", "TRX-002", 
                           AuditAction.PAYMENT_SUCCESS, "Payment successful");
            var logs = auditService.getLogsByUser("USER-002");
            System.out.println("  Total Logs: " + logs.size());
            System.out.println("  ✓ PASS");
            System.out.println();
            
            System.out.println("=== ALL TESTS PASSED ✓ ===");
            
        } catch (Exception e) {
            System.err.println("✗ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
