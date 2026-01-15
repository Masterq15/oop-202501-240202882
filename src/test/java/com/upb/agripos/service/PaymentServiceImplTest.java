package com.upb.agripos.service;

import com.upb.agripos.model.payment.CashPayment;
import com.upb.agripos.model.payment.EWalletPayment;
import com.upb.agripos.model.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk PaymentServiceImpl
 * Test berbagai skenario pembayaran tunai dan e-wallet
 * Person A - PAYMENT SERVICE TEST
 */
@DisplayName("Payment Service Tests")
class PaymentServiceImplTest {
    
    private PaymentServiceImpl paymentService;
    
    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl();
    }
    
    // ========== CASH PAYMENT TESTS ==========
    
    @Test
    @DisplayName("Should process cash payment with exact amount")
    void testProcessCashPaymentExactAmount() {
        // Arrange
        double amount = 50000;
        double amountPaid = 50000;
        
        // Act
        CashPayment payment = paymentService.processCashPayment("PAY-001", amount, amountPaid);
        
        // Assert
        assertNotNull(payment);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(0, payment.getChange());
        assertEquals(amount, payment.getAmount());
    }
    
    @Test
    @DisplayName("Should process cash payment with change")
    void testProcessCashPaymentWithChange() {
        // Arrange
        double amount = 50000;
        double amountPaid = 100000;
        
        // Act
        CashPayment payment = paymentService.processCashPayment("PAY-002", amount, amountPaid);
        
        // Assert
        assertNotNull(payment);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(50000, payment.getChange());
    }
    
    @Test
    @DisplayName("Should fail cash payment when amount is insufficient")
    void testProcessCashPaymentInsufficientAmount() {
        // Arrange
        double amount = 50000;
        double amountPaid = 30000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCashPayment("PAY-003", amount, amountPaid);
        });
    }
    
    @Test
    @DisplayName("Should fail cash payment with negative amount paid")
    void testProcessCashPaymentNegativeAmount() {
        // Arrange
        double amount = 50000;
        double amountPaid = -10000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCashPayment("PAY-004", amount, amountPaid);
        });
    }
    
    @Test
    @DisplayName("Should fail cash payment with zero total amount")
    void testProcessCashPaymentZeroAmount() {
        // Arrange
        double amount = 0;
        double amountPaid = 100000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCashPayment("PAY-005", amount, amountPaid);
        });
    }
    
    // ========== E-WALLET PAYMENT TESTS ==========
    
    @Test
    @DisplayName("Should process e-wallet payment with valid provider")
    void testProcessEWalletPaymentValid() {
        // Arrange
        double amount = 50000;
        String provider = "GCash";
        String account = "09123456789";
        
        // Act
        EWalletPayment payment = paymentService.processEWalletPayment(
            "PAY-006", amount, provider, account
        );
        
        // Assert
        assertNotNull(payment);
        assertEquals(provider, payment.getWalletProvider());
        // Payment might succeed or fail (simulated), but object should be created
        assertTrue(payment.getStatus() == PaymentStatus.SUCCESS || 
                  payment.getStatus() == PaymentStatus.FAILED);
    }
    
    @Test
    @DisplayName("Should support multiple wallet providers")
    void testProcessEWalletMultipleProviders() {
        // Arrange
        String[] providers = {"GCash", "PayMaya", "OVO", "DANA", "LinkAja"};
        double amount = 50000;
        
        // Act & Assert
        for (String provider : providers) {
            assertDoesNotThrow(() -> {
                paymentService.processEWalletPayment("PAY-" + provider, amount, 
                    provider, "09123456789");
            });
        }
    }
    
    @Test
    @DisplayName("Should fail e-wallet payment with unsupported provider")
    void testProcessEWalletUnsupportedProvider() {
        // Arrange
        double amount = 50000;
        String provider = "UnknownWallet";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processEWalletPayment("PAY-007", amount, provider, "09123456789");
        });
    }
    
    @Test
    @DisplayName("Should fail e-wallet payment with empty provider")
    void testProcessEWalletEmptyProvider() {
        // Arrange
        double amount = 50000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processEWalletPayment("PAY-008", amount, "", "09123456789");
        });
    }
    
    @Test
    @DisplayName("Should fail e-wallet payment with empty account")
    void testProcessEWalletEmptyAccount() {
        // Arrange
        double amount = 50000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processEWalletPayment("PAY-009", amount, "GCash", "");
        });
    }
    
    @Test
    @DisplayName("Should fail e-wallet payment with invalid amount")
    void testProcessEWalletInvalidAmount() {
        // Arrange
        double amount = -50000;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processEWalletPayment("PAY-010", amount, "GCash", "09123456789");
        });
    }
    
    // ========== PAYMENT UTILITY TESTS ==========
    
    @Test
    @DisplayName("Should calculate correct change")
    void testCalculateChange() {
        // Arrange
        double totalAmount = 50000;
        double amountPaid = 100000;
        
        // Act
        double change = paymentService.calculateChange(totalAmount, amountPaid);
        
        // Assert
        assertEquals(50000, change);
    }
    
    @Test
    @DisplayName("Should return zero change when exact amount paid")
    void testCalculateChangeExactAmount() {
        // Arrange
        double totalAmount = 50000;
        double amountPaid = 50000;
        
        // Act
        double change = paymentService.calculateChange(totalAmount, amountPaid);
        
        // Assert
        assertEquals(0, change);
    }
    
    @Test
    @DisplayName("Should return zero when amount paid is less than total")
    void testCalculateChangeInsufficientAmount() {
        // Arrange
        double totalAmount = 50000;
        double amountPaid = 30000;
        
        // Act
        double change = paymentService.calculateChange(totalAmount, amountPaid);
        
        // Assert
        assertEquals(0, change);
    }
    
    @Test
    @DisplayName("Should generate unique payment IDs")
    void testGenerateUniquePaymentIds() {
        // Arrange & Act
        String id1 = PaymentServiceImpl.generatePaymentId();
        String id2 = PaymentServiceImpl.generatePaymentId();
        
        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
        assertTrue(id1.startsWith("PAY-"));
        assertTrue(id2.startsWith("PAY-"));
    }
}
