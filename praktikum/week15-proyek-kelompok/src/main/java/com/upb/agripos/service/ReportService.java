package com.upb.agripos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.upb.agripos.dao.TransactionDAO;
import com.upb.agripos.dao.TransactionDAOImpl;
import com.upb.agripos.model.Transaction;

/**
 * ReportService
 * Service untuk generate laporan penjualan
 * 
 * FR-4: Struk dan Laporan
 */
public class ReportService {
    private TransactionDAO transactionDAO;

    public ReportService() {
        this.transactionDAO = new TransactionDAOImpl();
    }

    // Untuk testing
    public ReportService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     * Generate laporan penjualan harian
     */
    public String generateDailyReport(String date) throws Exception {
        String startDate = date; // format: yyyy-MM-dd
        String endDate = date;
        
        List<Transaction> transactions = transactionDAO.findByDateRange(startDate, endDate);
        return formatReport(transactions, "Laporan Penjualan Harian - " + date);
    }

    /**
     * Generate laporan penjualan bulan ini
     */
    public String generateMonthlyReport() throws Exception {
        LocalDate today = LocalDate.now();
        String startDate = today.withDayOfMonth(1).toString();
        String endDate = today.toString();
        
        List<Transaction> transactions = transactionDAO.findByDateRange(startDate, endDate);
        return formatReport(transactions, "Laporan Penjualan Bulanan - " + today.getMonth() + " " + today.getYear());
    }

    /**
     * Generate laporan penjualan semua transaksi
     */
    public String generateAllTransactionsReport() throws Exception {
        List<Transaction> transactions = transactionDAO.findAll();
        return formatReport(transactions, "Laporan Semua Transaksi Penjualan");
    }

    /**
     * Generate laporan berdasarkan kasir
     */
    public String generateReportByKasir(String kasirName) throws Exception {
        List<Transaction> allTransactions = transactionDAO.findAll();
        
        // Filter berdasarkan nama kasir
        List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(t -> t.getCashierName() != null && t.getCashierName().contains(kasirName))
                .toList();
        
        return formatReport(filteredTransactions, "Laporan Penjualan - Kasir: " + kasirName);
    }

    /**
     * Generate laporan berdasarkan metode pembayaran
     */
    public String generateReportByPaymentMethod(String paymentMethod) throws Exception {
        List<Transaction> allTransactions = transactionDAO.findAll();
        
        // Filter berdasarkan metode pembayaran
        List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(t -> t.getPaymentMethod().equals(paymentMethod))
                .toList();
        
        return formatReport(filteredTransactions, "Laporan Penjualan - Metode: " + paymentMethod);
    }

    /**
     * Ambil transaksi berdasarkan date range
     */
    public List<Transaction> getTransactionsByDateRange(String startDate, String endDate) throws Exception {
        return transactionDAO.findByDateRange(startDate, endDate);
    }

    /**
     * Ambil semua transaksi
     */
    public List<Transaction> getAllTransactions() throws Exception {
        return transactionDAO.findAll();
    }

    /**
     * Hitung summary data untuk dashboard
     */
    public Map<String, Object> calculateSummary(List<Transaction> transactions) {
        Map<String, Object> summary = new HashMap<>();
        
        double totalRevenue = 0;
        double tunaiTotal = 0;
        double ewalletTotal = 0;
        
        for (Transaction t : transactions) {
            totalRevenue += t.getTotal();
            String method = t.getPaymentMethod();
            
            if (method != null) {
                String methodUpper = method.toUpperCase().trim();
                // Handle various formats: "Tunai", "TUNAI", "Tuna" etc
                if (methodUpper.startsWith("TUNAI") || methodUpper.contains("TUNAI")) {
                    tunaiTotal += t.getTotal();
                } else if (methodUpper.startsWith("EWALLET") || methodUpper.startsWith("E-WALLET") || 
                           methodUpper.contains("EWALLET") || methodUpper.contains("E-WALLET")) {
                    ewalletTotal += t.getTotal();
                }
            }
        }
        
        summary.put("totalTransactions", transactions.size());
        summary.put("totalRevenue", totalRevenue);
        summary.put("tunaiTotal", tunaiTotal);
        summary.put("ewalletTotal", ewalletTotal);
        
        return summary;
    }

    /**
     * Format laporan dalam format yang terstruktur
     */
    private String formatReport(List<Transaction> transactions, String title) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        report.append("╔═══════════════════════════════════════════════════════════════╗\n");
        report.append("║").append(centerString(title, 61)).append("║\n");
        report.append("╚═══════════════════════════════════════════════════════════════╝\n\n");

        if (transactions.isEmpty()) {
            report.append("Tidak ada transaksi untuk periode ini.\n");
            return report.toString();
        }

        // Header tabel
        report.append(String.format("%-6s | %-20s | %-15s | %-12s | %-8s\n", 
            "NO", "KASIR", "METODE", "TANGGAL", "TOTAL"));
        report.append("────────────────────────────────────────────────────────────────\n");

        // Detail transaksi
        double totalRevenue = 0;
        int count = 1;
        
        Map<String, Double> kasirTotal = new HashMap<>();
        Map<String, Integer> kasirCount = new HashMap<>();
        Map<String, Double> paymentMethodTotal = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String kasirName = transaction.getCashierName();
            String paymentMethod = transaction.getPaymentMethod();
            
            report.append(String.format("%-6d | %-20s | %-15s | %-12s | Rp %,.0f\n",
                count++,
                kasirName != null && kasirName.length() > 20 ? kasirName.substring(0, 17) + "..." : kasirName,
                paymentMethod,
                transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                transaction.getTotal()
            ));
            
            totalRevenue += transaction.getTotal();
            kasirTotal.put(kasirName, kasirTotal.getOrDefault(kasirName, 0.0) + transaction.getTotal());
            kasirCount.put(kasirName, kasirCount.getOrDefault(kasirName, 0) + 1);
            paymentMethodTotal.put(paymentMethod, paymentMethodTotal.getOrDefault(paymentMethod, 0.0) + transaction.getTotal());
        }

        report.append("────────────────────────────────────────────────────────────────\n");
        report.append(String.format("TOTAL TRANSAKSI: %d | TOTAL PENDAPATAN: Rp %,.0f\n", 
            transactions.size(), totalRevenue));

        // Ringkasan per kasir
        report.append("\n╔═══════════════════════════════════════════════════════════════╗\n");
        report.append("║ RINGKASAN PER KASIR\n");
        report.append("╚═══════════════════════════════════════════════════════════════╝\n");
        
        for (Map.Entry<String, Double> entry : kasirTotal.entrySet()) {
            String kasir = entry.getKey() != null ? entry.getKey() : "Unknown";
            double amount = entry.getValue();
            int trxCount = kasirCount.getOrDefault(kasir, 0);
            report.append(String.format("%-25s: %3d transaksi | Rp %,.0f\n", 
                kasir, trxCount, amount));
        }

        // Ringkasan per metode pembayaran
        report.append("\n╔═══════════════════════════════════════════════════════════════╗\n");
        report.append("║ RINGKASAN PER METODE PEMBAYARAN\n");
        report.append("╚═══════════════════════════════════════════════════════════════╝\n");
        
        for (Map.Entry<String, Double> entry : paymentMethodTotal.entrySet()) {
            report.append(String.format("%-25s: Rp %,.0f\n", 
                entry.getKey(), entry.getValue()));
        }

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("Generated: ").append(LocalDateTime.now().format(dateFormatter)).append("\n");

        return report.toString();
    }

    /**
     * Helper: center string dalam lebar tertentu
     */
    private String centerString(String str, int width) {
        if (str.length() >= width) {
            return str.substring(0, width);
        }
        int padding = (width - str.length()) / 2;
        int rightPadding = width - str.length() - padding;
        return " ".repeat(Math.max(0, padding)) + str + " ".repeat(Math.max(0, rightPadding));
    }
}
