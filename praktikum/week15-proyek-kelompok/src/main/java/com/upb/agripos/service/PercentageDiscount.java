package com.upb.agripos.service;

/**
 * PercentageDiscount - Implementation dari DiscountStrategy
 * 
 * Diskon berdasarkan persentase (OFR-2)
 * 
 * Created by: [Person B - Backend]
 * Last modified: 
 */
public class PercentageDiscount implements DiscountStrategy {
    
    @Override
    public double calculateDiscount(double amount) {
        // TODO: Implement
        return 0.0;
    }
    
    @Override
    public String getDescription() {
        // TODO: Implement
        return "Percentage Discount";
    }
}
