package com.upb.agripos.service;

/**
 * PaymentFactory
 * Factory untuk membuat objek PaymentMethod sesuai tipe
 * 
 * Implementasi Factory Pattern untuk DIP (Dependency Inversion Principle)
 */
public class PaymentFactory {
    
    public static PaymentMethod createPaymentMethod(String paymentType) {
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type tidak boleh null");
        }

        String type = paymentType.toLowerCase();
        if (type.equals("tunai") || type.equals("cash")) {
            return new CashPayment();
        } else if (type.equals("ewallet") || type.equals("e-wallet") || type.equals("kartu")) {
            return new EWalletPayment();
        } else {
            throw new IllegalArgumentException("Metode pembayaran tidak dikenali: " + paymentType);
        }
    }
}
