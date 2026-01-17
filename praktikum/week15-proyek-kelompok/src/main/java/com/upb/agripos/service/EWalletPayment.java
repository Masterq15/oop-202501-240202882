package com.upb.agripos.service;

/**
 * EWalletPayment - Implementation dari PaymentMethod
 * 
 * Metode pembayaran E-WALLET (FR-3)
 * 
 * Created by: [Person C - Payment]
 * Last modified: 
 */
public class EWalletPayment implements PaymentMethod {
    
    @Override
    public boolean processPayment(double amount) {
        // TODO: Implement
        return false;
    }
    
    @Override
    public String getMethodName() {
        return "E-WALLET";
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
