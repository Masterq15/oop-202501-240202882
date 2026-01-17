package com.upb.agripos.discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk PercentageDiscount PersonB
 * Menguji perhitungan diskon persentase
 * Person B - PERCENTAGE DISCOUNT
 */
@DisplayName("PercentageDiscount PersonB Tests")
class PercentageDiscountPersonBTest {
    private PercentageDiscountPersonB discount;

    @BeforeEach
    void setUp() {
        discount = new PercentageDiscountPersonB(10);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create PercentageDiscount with valid percentage")
    void testConstructorValidPercentage() {
        PercentageDiscountPersonB d1 = new PercentageDiscountPersonB(0);
        assertEquals(0, d1.getPercentage());
        
        PercentageDiscountPersonB d2 = new PercentageDiscountPersonB(50);
        assertEquals(50, d2.getPercentage());
        
        PercentageDiscountPersonB d3 = new PercentageDiscountPersonB(100);
        assertEquals(100, d3.getPercentage());
    }

    @Test
    @DisplayName("Should throw exception when percentage is negative")
    void testConstructorNegativePercentage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PercentageDiscountPersonB(-10);
        });
    }

    @Test
    @DisplayName("Should throw exception when percentage exceeds 100")
    void testConstructorExceedsHundredPercentage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PercentageDiscountPersonB(101);
        });
    }

    // ==================== CALCULATE DISCOUNT TESTS ====================

    @Test
    @DisplayName("Should calculate discount correctly")
    void testCalculateDiscountCorrect() {
        // 10% dari 100000 = 10000
        double discount1 = discount.calculateDiscount(100000);
        assertEquals(10000, discount1);
        
        // 10% dari 50000 = 5000
        double discount2 = discount.calculateDiscount(50000);
        assertEquals(5000, discount2);
    }

    @Test
    @DisplayName("Should calculate discount 0 when percentage is 0")
    void testCalculateDiscountZeroPercentage() {
        PercentageDiscountPersonB d = new PercentageDiscountPersonB(0);
        assertEquals(0, d.calculateDiscount(100000));
    }

    @Test
    @DisplayName("Should calculate discount equal to price when percentage is 100")
    void testCalculateDiscountFullPercentage() {
        PercentageDiscountPersonB d = new PercentageDiscountPersonB(100);
        assertEquals(100000, d.calculateDiscount(100000));
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void testCalculateDiscountNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.calculateDiscount(-100000);
        });
    }

    @Test
    @DisplayName("Should calculate discount correctly with decimal price")
    void testCalculateDiscountDecimalPrice() {
        // 10% dari 25000.5 = 2500.05
        double result = discount.calculateDiscount(25000.5);
        assertEquals(2500.05, result, 0.01);
    }

    // ==================== APPLY DISCOUNT TESTS ====================

    @Test
    @DisplayName("Should apply discount and return correct final price")
    void testApplyDiscountCorrect() {
        // 100000 - 10% = 90000
        double finalPrice = discount.applyDiscount(100000);
        assertEquals(90000, finalPrice);
    }

    @Test
    @DisplayName("Should apply full discount when percentage is 100")
    void testApplyDiscountFullPercentage() {
        PercentageDiscountPersonB d = new PercentageDiscountPersonB(100);
        assertEquals(0, d.applyDiscount(100000));
    }

    @Test
    @DisplayName("Should not apply discount when percentage is 0")
    void testApplyDiscountZeroPercentage() {
        PercentageDiscountPersonB d = new PercentageDiscountPersonB(0);
        assertEquals(100000, d.applyDiscount(100000));
    }

    @Test
    @DisplayName("Should throw exception when price is negative in apply discount")
    void testApplyDiscountNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.applyDiscount(-100000);
        });
    }

    // ==================== SETTER TESTS ====================

    @Test
    @DisplayName("Should set percentage successfully")
    void testSetPercentageValid() {
        discount.setPercentage(25);
        assertEquals(25, discount.getPercentage());
    }

    @Test
    @DisplayName("Should throw exception when setting negative percentage")
    void testSetPercentageNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.setPercentage(-10);
        });
    }

    @Test
    @DisplayName("Should throw exception when setting percentage exceeds 100")
    void testSetPercentageExceedsHundred() {
        assertThrows(IllegalArgumentException.class, () -> {
            discount.setPercentage(150);
        });
    }

    // ==================== DESCRIPTION TESTS ====================

    @Test
    @DisplayName("Should return correct description")
    void testGetDescription() {
        String description = discount.getDescription();
        assertEquals("Diskon 10.0%", description);
    }

    @Test
    @DisplayName("Should return correct description for different percentages")
    void testGetDescriptionVariousPercentages() {
        PercentageDiscountPersonB d1 = new PercentageDiscountPersonB(5);
        assertEquals("Diskon 5.0%", d1.getDescription());
        
        PercentageDiscountPersonB d2 = new PercentageDiscountPersonB(50);
        assertEquals("Diskon 50.0%", d2.getDescription());
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
}
