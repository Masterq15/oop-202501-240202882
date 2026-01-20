package com.upb.agripos.discount;

/**
 * Strategy Pattern untuk Discount
 * Interface yang mendefinisikan kontrak untuk berbagai tipe diskon
 * Person B - DISCOUNT STRATEGY
 */
public interface DiscountStrategyPersonB {
    /**
     * Menghitung besaran diskon
     * @param price harga awal
     * @return besaran diskon
     */
    double calculateDiscount(double price);

    /**
     * Mendapatkan harga setelah diskon
     * @param price harga awal
     * @return harga setelah diskon
     */
    double applyDiscount(double price);

    /**
     * Mendapatkan deskripsi diskon
     * @return deskripsi diskon
     */
    String getDescription();
}
