package com.upb.agripos.discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk FixedDiscount PersonB
 * Menguji perhitungan diskon nominal/fixed (Rp)
 * Person B - FIXED DISCOUNT
 */
@DisplayName("FixedDiscount PersonB Tests")
class FixedDiscountPersonBTest {
    private FixedDiscountPersonB discount;

    @BeforeEach
    void setUp() {
        discount = new FixedDiscountPersonB(10000);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create FixedDiscount with valid amount")
    void testConstructorValidAmount() {
        FixedDiscountPersonB d1 = new FixedDiscountPersonB(0);
        assertEquals(0, d1.getAmount());
        
        FixedDiscountPersonB d2 = new FixedDiscountPersonB(50000);
        assertEquals(50000, d2.getAmount());
        
        FixedDiscountPersonB d3 = new FixedDiscountPersonB(100000.50);
        assertEquals(100000.50, d3.getAmount());
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void testConstructorNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FixedDiscountPersonB(-10000);
        });
    }

    // ==================== CALCULATE DISCOUNT TESTS ====================

    @Test
    @DisplayName("Should calculate discount as nominal amount")
    void testCalculateDiscountNominal() {
        // Diskon nominal 10000
        double discount1 = discount.calculateDiscount(100000);
        assertEquals(10000, discount1);
        
        double discount2 = discount.calculateDiscount(50000);
        assertEquals(10000, discount2);
    }

    @Test
    @DisplayName("Should not exceed price when calculating discount")
    void testCalculateDiscountNotExceedPrice() {
        // Diskon 50000, tapi harga hanya 20000
        FixedDiscountPersonB d = new FixedDiscountPersonB(50000);
        double result = d.calculateDiscount(20000);
        assertEquals(20000, result); // Diskon maksimal sesuai harga
    }

    @Test
    @DisplayName("Should calculate zero discount when amount is 0")
    void testCalculateDiscountZeroAmount() {
        FixedDiscountPersonB d = new FixedDiscountPersonB(0);
        assertEquals(0, d.calculateDiscount(100000));
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void testCalculateDiscountNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.calculateDiscount(-100000);
        });
    }

    @Test
    @DisplayName("Should calculate discount correctly with decimal amount")
    void testCalculateDiscountDecimalAmount() {
        FixedDiscountPersonB d = new FixedDiscountPersonB(5500.50);
        double result = d.calculateDiscount(100000);
        assertEquals(5500.50, result);
    }

    // ==================== APPLY DISCOUNT TESTS ====================

    @Test
    @DisplayName("Should apply discount and return correct final price")
    void testApplyDiscountCorrect() {
        // 100000 - 10000 = 90000
        double finalPrice = discount.applyDiscount(100000);
        assertEquals(90000, finalPrice);
    }

    @Test
    @DisplayName("Should ensure final price never negative")
    void testApplyDiscountNotNegative() {
        // Diskon 50000, harga 20000 -> final price = 0
        FixedDiscountPersonB d = new FixedDiscountPersonB(50000);
        double finalPrice = d.applyDiscount(20000);
        assertEquals(0, finalPrice);
    }

    @Test
    @DisplayName("Should not apply discount when amount is 0")
    void testApplyDiscountZeroAmount() {
        FixedDiscountPersonB d = new FixedDiscountPersonB(0);
        assertEquals(100000, d.applyDiscount(100000));
    }

    @Test
    @DisplayName("Should throw exception when price is negative in apply discount")
    void testApplyDiscountNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.applyDiscount(-100000);
        });
    }

    @Test
    @DisplayName("Should apply discount correctly with decimal values")
    void testApplyDiscountDecimal() {
        FixedDiscountPersonB d = new FixedDiscountPersonB(5500.50);
        double finalPrice = d.applyDiscount(25000.75);
        assertEquals(19500.25, finalPrice, 0.01);
    }

    // ==================== SETTER TESTS ====================

    @Test
    @DisplayName("Should set amount successfully")
    void testSetAmountValid() {
        discount.setAmount(25000);
        assertEquals(25000, discount.getAmount());
    }

    @Test
    @DisplayName("Should throw exception when setting negative amount")
    void testSetAmountNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.setAmount(-10000);
        });
    }

    @Test
    @DisplayName("Should allow setting zero amount")
    void testSetAmountZero() {
        discount.setAmount(0);
        assertEquals(0, discount.getAmount());
    }

    // ==================== DESCRIPTION TESTS ====================

    @Test
    @DisplayName("Should return correct description")
    void testGetDescription() {
        String description = discount.getDescription();
        assertEquals("Diskon Rp 10000.00", description);
    }

    @Test
    @DisplayName("Should return correct description for different amounts")
    void testGetDescriptionVariousAmounts() {
        FixedDiscountPersonB d1 = new FixedDiscountPersonB(5000);
        assertEquals("Diskon Rp 5000.00", d1.getDescription());
        
        FixedDiscountPersonB d2 = new FixedDiscountPersonB(100000.50);
        assertEquals("Diskon Rp 100000.50", d2.getDescription());
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should calculate discount and apply consistently")
    void testDiscountConsistency() {
        double price = 100000;
        double discountAmount = discount.calculateDiscount(price);
        double finalPrice1 = discount.applyDiscount(price);
        double finalPrice2 = price - discountAmount;
        
        assertEquals(finalPrice1, finalPrice2);
    }

    @Test
    @DisplayName("Should handle edge case where discount equals price")
    void testDiscountEqualPrice() {
        FixedDiscountPersonB d = new FixedDiscountPersonB(100000);
        double finalPrice = d.applyDiscount(100000);
        assertEquals(0, finalPrice);
    }

    @Test
    @DisplayName("Should handle multiple sequential discounts")
    void testMultipleDiscounts() {
        FixedDiscountPersonB d1 = new FixedDiscountPersonB(10000);
        FixedDiscountPersonB d2 = new FixedDiscountPersonB(5000);
        
        double price = 100000;
        double afterDiscount1 = d1.applyDiscount(price); // 90000
        double afterDiscount2 = d2.applyDiscount(afterDiscount1); // 85000
        
        assertEquals(85000, afterDiscount2);
    }
}
