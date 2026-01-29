package com.upb.agripos.service;

import com.upb.agripos.exception.ValidationException;

/**
 * CashPayment
 * Implementasi pembayaran tunai
 */
public class CashPayment implements PaymentMethod {

    @Override
    public PaymentResult processPayment(double amount, String input) throws ValidationException {
        try {
            double cashGiven = Double.parseDouble(input);
            
            if (cashGiven < amount) {
                throw new ValidationException("Uang tidak cukup. Butuh Rp " + String.format("%,.0f", amount) 
                    + " tetapi hanya Rp " + String.format("%,.0f", cashGiven));
            }

            double change = cashGiven - amount;
            String message = "Pembayaran tunai berhasil. Kembalian: Rp " + String.format("%,.0f", change);
            
            return new PaymentResult(true, message, change);

        } catch (NumberFormatException e) {
            throw new ValidationException("Format uang tidak valid", e);
        }
    }

    @Override
    public String getMethodName() {
        return "Tunai";
    }
}
