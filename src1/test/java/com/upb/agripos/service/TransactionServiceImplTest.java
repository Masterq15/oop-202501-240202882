package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.transaction.Transaction;
import com.upb.agripos.model.transaction.TransactionStatus;
import com.upb.agripos.model.payment.CashPayment;
import com.upb.agripos.model.audit.AuditAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk TransactionServiceImpl
 * Test proses transaksi dari penambahan item hingga checkout
 * Person A - TRANSACTION SERVICE TEST
 */
@DisplayName("Transaction Service Tests")
class TransactionServiceImplTest {
    
    private TransactionServiceImpl transactionService;
    private AuditLogServiceImpl auditLogService;
    private Product product1;
    private Product product2;
    
    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogServiceImpl();
        transactionService = new TransactionServiceImpl(auditLogService);
        
        // Setup test products
        product1 = new Product("P001", "Beras 5kg", 50000, 100);
        product2 = new Product("P002", "Gula 1kg", 15000, 50);
    }
    
    // ========== CREATE TRANSACTION TESTS ==========
    
    @Test
    @DisplayName("Should create new transaction with valid data")
    void testCreateTransactionValid() {
        // Arrange
        String transactionId = "TRX-001";
        String cashierId = "C001";
        
        // Act
        Transaction transaction = transactionService.createTransaction(transactionId, cashierId);
        
        // Assert
        assertNotNull(transaction);
        assertEquals(transactionId, transaction.getTransactionId());
        assertEquals(cashierId, transaction.getCashierId());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertTrue(transaction.getDetails().isEmpty());
    }
    
    @Test
    @DisplayName("Should fail creating transaction with null transaction ID")
    void testCreateTransactionNullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(null, "C001");
        });
    }
    
    @Test
    @DisplayName("Should fail creating transaction with empty transaction ID")
    void testCreateTransactionEmptyId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction("", "C001");
        });
    }
    
    @Test
    @DisplayName("Should fail creating transaction with null cashier ID")
    void testCreateTransactionNullCashierId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction("TRX-001", null);
        });
    }
    
    // ========== ADD ITEM TESTS ==========
    
    @Test
    @DisplayName("Should add item to transaction successfully")
    void testAddItemToTransactionValid() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-001", "C001");
        
        // Act
        transactionService.addItemToTransaction(transaction, product1, 2, null);
        
        // Assert
        assertEquals(1, transaction.getDetails().size());
        assertEquals(100000, transaction.getGrandTotal());
    }
    
    @Test
    @DisplayName("Should add multiple items to transaction")
    void testAddMultipleItems() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-002", "C001");
        
        // Act
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        transactionService.addItemToTransaction(transaction, product2, 2, null);
        
        // Assert
        assertEquals(2, transaction.getDetails().size());
        assertEquals(80000, transaction.getGrandTotal());
    }
    
    @Test
    @DisplayName("Should fail adding item when stock is insufficient")
    void testAddItemInsufficientStock() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-003", "C001");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addItemToTransaction(transaction, product1, 200, null);
        });
    }
    
    @Test
    @DisplayName("Should fail adding item with zero quantity")
    void testAddItemZeroQuantity() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-004", "C001");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addItemToTransaction(transaction, product1, 0, null);
        });
    }
    
    @Test
    @DisplayName("Should fail adding item with null product")
    void testAddItemNullProduct() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-005", "C001");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addItemToTransaction(transaction, null, 1, null);
        });
    }
    
    @Test
    @DisplayName("Should fail adding item with null transaction")
    void testAddItemNullTransaction() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addItemToTransaction(null, product1, 1, null);
        });
    }
    
    // ========== REMOVE ITEM TESTS ==========
    
    @Test
    @DisplayName("Should remove item from transaction")
    void testRemoveItemValid() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-006", "C001");
        transactionService.addItemToTransaction(transaction, product1, 2, null);
        String detailId = transaction.getDetails().get(0).getDetailId();
        
        // Act
        transactionService.removeItemFromTransaction(transaction, detailId);
        
        // Assert
        assertEquals(0, transaction.getDetails().size());
        assertEquals(0, transaction.getGrandTotal());
    }
    
    @Test
    @DisplayName("Should fail removing non-existent item")
    void testRemoveItemNonExistent() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-007", "C001");
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.removeItemFromTransaction(transaction, "INVALID-ID");
        });
    }
    
    // ========== CHECKOUT TESTS ==========
    
    @Test
    @DisplayName("Should successfully checkout transaction with cash payment")
    void testCheckoutSuccess() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-008", "C001");
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        
        CashPayment payment = new CashPayment("PAY-001", 50000, 100000);
        
        // Act
        boolean result = transactionService.checkout(transaction, payment);
        
        // Assert
        assertTrue(result);
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }
    
    @Test
    @DisplayName("Should fail checkout with mismatched payment amount")
    void testCheckoutMismatchedAmount() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-009", "C001");
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        
        // Payment amount doesn't match transaction total
        CashPayment payment = new CashPayment("PAY-002", 30000, 100000);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.checkout(transaction, payment);
        });
    }
    
    @Test
    @DisplayName("Should fail checkout with null transaction")
    void testCheckoutNullTransaction() {
        // Arrange
        CashPayment payment = new CashPayment("PAY-003", 50000, 100000);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.checkout(null, payment);
        });
    }
    
    @Test
    @DisplayName("Should fail checkout with null payment")
    void testCheckoutNullPayment() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-010", "C001");
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.checkout(transaction, null);
        });
    }
    
    @Test
    @DisplayName("Should fail checkout with empty transaction")
    void testCheckoutEmptyTransaction() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-011", "C001");
        CashPayment payment = new CashPayment("PAY-004", 0, 100000);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.checkout(transaction, payment);
        });
    }
    
    // ========== CALCULATE TOTAL TESTS ==========
    
    @Test
    @DisplayName("Should calculate transaction total correctly")
    void testCalculateTransactionTotal() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-012", "C001");
        transactionService.addItemToTransaction(transaction, product1, 2, null);
        transactionService.addItemToTransaction(transaction, product2, 1, null);
        
        // Act
        double total = transactionService.calculateTransactionTotal(transaction);
        
        // Assert
        assertEquals(115000, total);
    }
    
    // ========== HISTORY TESTS ==========
    
    @Test
    @DisplayName("Should retrieve transaction history with limit")
    void testGetTransactionHistory() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            Transaction transaction = transactionService.createTransaction("TRX-" + i, "C001");
            transactionService.addItemToTransaction(transaction, product1, 1, null);
            CashPayment payment = new CashPayment("PAY-" + i, 50000, 100000);
            transactionService.checkout(transaction, payment);
        }
        
        // Act
        var history = transactionService.getTransactionHistory(3);
        
        // Assert
        assertEquals(3, history.size());
    }
    
    // ========== AUDIT LOG TESTS ==========
    
    @Test
    @DisplayName("Should log transaction start event")
    void testAuditLogTransactionStart() {
        // Act
        transactionService.createTransaction("TRX-013", "C001");
        
        // Assert
        var logs = auditLogService.getLogsByAction(AuditAction.TRANSACTION_START);
        assertTrue(logs.size() > 0);
    }
    
    @Test
    @DisplayName("Should log item added event")
    void testAuditLogItemAdded() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-014", "C001");
        
        // Act
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        
        // Assert
        var logs = auditLogService.getLogsByAction(AuditAction.ITEM_ADDED);
        assertTrue(logs.size() > 0);
    }
    
    @Test
    @DisplayName("Should log transaction completion event")
    void testAuditLogTransactionCompleted() {
        // Arrange
        Transaction transaction = transactionService.createTransaction("TRX-015", "C001");
        transactionService.addItemToTransaction(transaction, product1, 1, null);
        CashPayment payment = new CashPayment("PAY-005", 50000, 100000);
        
        // Act
        transactionService.checkout(transaction, payment);
        
        // Assert
        var logs = auditLogService.getLogsByAction(AuditAction.TRANSACTION_COMPLETED);
        assertTrue(logs.size() > 0);
    }
}
