package com.upb.agripos.service;

/**
 * CashPayment - Implementation dari PaymentMethod
 * 
 * Metode pembayaran TUNAI (FR-3)
 * 
 * Created by: [Person C - Payment]
 * Last modified: 
 */
public class CashPayment implements PaymentMethod {
    
    @Override
    public boolean processPayment(double amount) {
        // TODO: Implement
        return false;
    }
    
    @Override
    public String getMethodName() {
        return "TUNAI";
    }
    
    @Override
    public boolean validatePayment(double amount) {
        // TODO: Implement
        return false;
    }
    
    @Override
    public double getChange(double amount) {
        // TODO: Implement
        return 0.0;
    }
}
