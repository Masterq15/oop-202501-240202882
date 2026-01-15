package com.upb.agripos.discount;

/**
 * Implementasi DiscountStrategy untuk diskon nominal/fixed
 * Menerapkan diskon dengan nominal tetap (Rp)
 * Person B - FIXED DISCOUNT
 */
public class FixedDiscountPersonB implements DiscountStrategyPersonB {
    private double amount;

    /**
     * Constructor
     * @param amount nominal diskon dalam Rupiah
     * @throws IllegalArgumentException jika amount negatif
     */
    public FixedDiscountPersonB(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Nominal diskon tidak boleh negatif");
        }
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Nominal diskon tidak boleh negatif");
        }
        this.amount = amount;
    }

    @Override
    public double calculateDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        // Diskon tidak boleh lebih besar dari harga
        return Math.min(amount, price);
    }

    @Override
    public double applyDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        double finalPrice = price - calculateDiscount(price);
        // Pastikan harga tidak negatif
        return Math.max(0, finalPrice);
    }

    @Override
    public String getDescription() {
        return "Diskon Rp " + String.format("%.2f", amount);
    }

    @Override
    public String toString() {
        return "FixedDiscountPersonB{" +
                "amount=" + amount +
                '}';
    }
}
