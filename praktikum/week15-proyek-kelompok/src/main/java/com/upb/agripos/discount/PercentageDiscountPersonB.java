package com.upb.agripos.discount;

/**
 * Implementasi DiscountStrategy untuk diskon persentase
 * Menerapkan diskon berdasarkan persentase dari harga awal
 * Person B - PERCENTAGE DISCOUNT
 */
public class PercentageDiscountPersonB implements DiscountStrategyPersonB {
    private double percentage;

    /**
     * Constructor
     * @param percentage persentase diskon (0-100)
     * @throws IllegalArgumentException jika persentase tidak valid
     */
    public PercentageDiscountPersonB(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Persentase diskon harus antara 0-100");
        }
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Persentase diskon harus antara 0-100");
        }
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        return price * (percentage / 100);
    }

    @Override
    public double applyDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        return price - calculateDiscount(price);
    }

    @Override
    public String getDescription() {
        return "Diskon " + percentage + "%";
    }

    @Override
    public String toString() {
        return "PercentageDiscountPersonB{" +
                "percentage=" + percentage +
                '}';
    }
}
