package com.upb.agripos.service;

/**
 * FixedDiscount - Implementation dari DiscountStrategy
 * 
 * Diskon fixed Rp (OFR-2)
 * 
 * Created by: [Person B - Backend]
 * Last modified: 
 */
public class FixedDiscount implements DiscountStrategy {
    
    @Override
    public double calculateDiscount(double amount) {
        // TODO: Implement
        return 0.0;
    }
    
    @Override
    public String getDescription() {
        // TODO: Implement
        return "Fixed Discount";
    }
}
