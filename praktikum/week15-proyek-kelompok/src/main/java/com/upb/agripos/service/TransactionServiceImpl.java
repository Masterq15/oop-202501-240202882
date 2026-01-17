package com.upb.agripos.service;

import com.upb.agripos.exception.ValidationException;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * TransactionServiceImpl - Implementation dari TransactionService
 * 
 * Business logic untuk transaksi penjualan (FR-2, FR-4)
 * 
 * Created by: [Person C - Payment]
 * Last modified: 
 */
public class TransactionServiceImpl implements TransactionService {
    
    @Override
    public String checkout(PaymentMethod paymentMethod, DiscountStrategy discount) throws ValidationException {
        // TODO: Implement
        return null;
    }
    
    @Override
    public List<Transaction> getDailyReport(String date) {
        // TODO: Implement
        return new ArrayList<>();
    }
    
    @Override
    public List<Transaction> getReportByDateRange(String startDate, String endDate) {
        // TODO: Implement
        return new ArrayList<>();
    }
    
    @Override
    public double getTotalSales(String startDate, String endDate) {
        // TODO: Implement
        return 0.0;
    }
    
    @Override
    public Transaction findById(String transactionId) {
        // TODO: Implement
        return null;
    }
}
