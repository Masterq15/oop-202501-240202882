package com.upb.agripos.service;

/**
 * DiscountStrategy Interface - OFR-2 Diskon/Promo/Voucher
 * 
 * Strategy Pattern: Flexible cara hitung diskon
 * 
 * Implementers:
 * - PercentageDiscount (diskon %)
 * - FixedDiscount (diskon fixed Rp)
 * - VoucherDiscount (diskon dari voucher code)
 * 
 * Created by: [Person B - Backend]
 * Last modified: 
 */
public interface DiscountStrategy {

    /**
     * Hitung jumlah diskon dari total
     * 
     * @param total total belanja sebelum diskon
     * @return jumlah diskon dalam Rp
     */
    double calculateDiscount(double total);

    /**
     * Get deskripsi diskon (untuk ditampilkan di receipt)
     */
    String getDescription();
}
