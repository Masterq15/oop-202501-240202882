package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.upb.agripos.model.Transaction;

/**
 * TransactionDAOImpl
 * Implementasi akses data untuk Transaction menggunakan JDBC dan PostgreSQL
 */
public class TransactionDAOImpl implements TransactionDAO {
    private DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    @Override
    public Transaction save(Transaction transaction) throws Exception {
        String sql = "INSERT INTO transactions (code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getCode());
            pstmt.setTimestamp(2, Timestamp.valueOf(transaction.getTransactionDate()));
            pstmt.setDouble(3, transaction.getSubtotal());
            pstmt.setDouble(4, transaction.getDiscount());
            pstmt.setDouble(5, transaction.getTotal());
            pstmt.setString(6, transaction.getPaymentMethod());
            pstmt.setString(7, transaction.getPaymentStatus());
            pstmt.setString(8, transaction.getCashierName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error saving transaction: " + e.getMessage(), e);
        }
        return transaction;
    }

    @Override
    public Transaction findById(int id) throws Exception {
        String sql = "SELECT id, code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name " +
                     "FROM transactions WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getTimestamp("transaction_date").toLocalDateTime(),
                            rs.getDouble("subtotal"),
                            rs.getDouble("discount"),
                            rs.getDouble("total"),
                            rs.getString("payment_method"),
                            rs.getString("payment_status"),
                            rs.getString("cashier_name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error finding transaction: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Transaction findByCode(String code) throws Exception {
        String sql = "SELECT id, code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name " +
                     "FROM transactions WHERE code = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getTimestamp("transaction_date").toLocalDateTime(),
                            rs.getDouble("subtotal"),
                            rs.getDouble("discount"),
                            rs.getDouble("total"),
                            rs.getString("payment_method"),
                            rs.getString("payment_status"),
                            rs.getString("cashier_name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error finding transaction: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name " +
                     "FROM transactions ORDER BY transaction_date DESC";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getTimestamp("transaction_date").toLocalDateTime(),
                        rs.getDouble("subtotal"),
                        rs.getDouble("discount"),
                        rs.getDouble("total"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getString("cashier_name")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(String startDate, String endDate) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, code, transaction_date, subtotal, discount, total, payment_method, payment_status, cashier_name " +
                     "FROM transactions WHERE DATE(transaction_date) BETWEEN ?::date AND ?::date ORDER BY transaction_date DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getTimestamp("transaction_date").toLocalDateTime(),
                            rs.getDouble("subtotal"),
                            rs.getDouble("discount"),
                            rs.getDouble("total"),
                            rs.getString("payment_method"),
                            rs.getString("payment_status"),
                            rs.getString("cashier_name")
                    );
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching transactions by date range: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public boolean saveTransactionItem(int transactionId, int productId, int quantity, double unitPrice, double subtotal) throws Exception {
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, unitPrice);
            pstmt.setDouble(5, subtotal);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error saving transaction item: " + e.getMessage(), e);
        }
    }
}
