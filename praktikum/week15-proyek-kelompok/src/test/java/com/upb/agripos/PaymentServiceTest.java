package com.upb.agripos;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.service.CashPayment;
import com.upb.agripos.service.EWalletPayment;
import com.upb.agripos.service.PaymentFactory;
import com.upb.agripos.service.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk Payment Service
 * Test FR-3: Metode Pembayaran
 */
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Test
    @DisplayName("TC-11: Pembayaran Tunai - Exact Amount")
    void testCashPaymentExactAmount() throws ValidationException {
        PaymentMethod payment = new CashPayment();
        var result = payment.processPayment(100000, "100000");
        
        assertTrue(result.isSuccess());
        assertEquals(0, result.getChange());
    }

    @Test
    @DisplayName("TC-12: Pembayaran Tunai - Return Change")
    void testCashPaymentWithChange() throws ValidationException {
        PaymentMethod payment = new CashPayment();
        var result = payment.processPayment(100000, "150000");
        
        assertTrue(result.isSuccess());
        assertEquals(50000, result.getChange());
    }

    @Test
    @DisplayName("TC-13: Pembayaran Tunai - Insufficient Money")
    void testCashPaymentInsufficientMoney() {
        PaymentMethod payment = new CashPayment();
        
        assertThrows(ValidationException.class, () -> {
            payment.processPayment(100000, "50000");
        });
    }

    @Test
    @DisplayName("TC-14: Pembayaran E-Wallet - Success")
    void testEWalletPaymentSuccess() throws ValidationException {
        PaymentMethod payment = new EWalletPayment();
        var result = payment.processPayment(100000, "081234567890");
        
        assertTrue(result.isSuccess());
        assertEquals(0, result.getChange());
    }

    @Test
    @DisplayName("TC-15: Pembayaran E-Wallet - Invalid Account")
    void testEWalletPaymentInvalidAccount() {
        PaymentMethod payment = new EWalletPayment();
        
        assertThrows(ValidationException.class, () -> {
            payment.processPayment(100000, "12345");
        });
    }

    @Test
    @DisplayName("TC-16: Payment Factory - Create Cash Payment")
    void testPaymentFactoryCash() {
        PaymentMethod payment = PaymentFactory.createPaymentMethod("tunai");
        assertEquals("Tunai", payment.getMethodName());
    }

    @Test
    @DisplayName("TC-17: Payment Factory - Create E-Wallet Payment")
    void testPaymentFactoryEWallet() {
        PaymentMethod payment = PaymentFactory.createPaymentMethod("ewallet");
        assertEquals("E-Wallet", payment.getMethodName());
    }

    @Test
    @DisplayName("TC-18: Payment Factory - Invalid Type")
    void testPaymentFactoryInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            PaymentFactory.createPaymentMethod("invalid");
        });
    }
}
