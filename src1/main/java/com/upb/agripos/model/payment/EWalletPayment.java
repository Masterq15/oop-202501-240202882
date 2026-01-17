package com.upb.agripos.model.payment;

/**
 * Implementasi Payment untuk pembayaran E-Wallet
 * Menangani pembayaran melalui berbagai metode digital
 * Person A - PAYMENT LAYER
 */
public class EWalletPayment extends Payment {
    private String walletProvider; // "GCash", "PayMaya", "OVO", dll
    private String walletAccount;
    private String transactionReference;
    private boolean isProcessing;
    
    public EWalletPayment(String paymentId, double amount, 
                         String walletProvider, String walletAccount) {
        super(paymentId, amount);
        this.walletProvider = walletProvider;
        this.walletAccount = walletAccount;
        this.isProcessing = false;
    }
    
    /**
     * Proses pembayaran e-wallet
     * Simulasi koneksi ke payment gateway
     * Dalam implementasi nyata, akan terhubung ke API provider
     */
    @Override
    public boolean processPayment() {
        if (!validate()) {
            setStatus(PaymentStatus.FAILED);
            return false;
        }
        
        try {
            // Simulasi proses payment gateway
            isProcessing = true;
            
            // Validasi dengan payment provider (simulasi)
            if (connectToPaymentGateway()) {
                // Generate transaction reference
                this.transactionReference = generateTransactionReference();
                
                setStatus(PaymentStatus.SUCCESS);
                isProcessing = false;
                return true;
            } else {
                setStatus(PaymentStatus.FAILED);
                isProcessing = false;
                return false;
            }
        } catch (Exception e) {
            setStatus(PaymentStatus.FAILED);
            isProcessing = false;
            return false;
        }
    }
    
    /**
     * Validasi pembayaran e-wallet
     * - Provider harus valid
     * - Account harus valid
     * - Jumlah harus positif
     */
    @Override
    public boolean validate() {
        return walletProvider != null && !walletProvider.isEmpty() &&
               walletAccount != null && !walletAccount.isEmpty() &&
               getAmount() > 0;
    }
    
    /**
     * Simulasi koneksi ke payment gateway
     */
    private boolean connectToPaymentGateway() {
        // Dalam implementasi nyata, ini akan melakukan API call
        // Untuk testing, return true dengan probability tertentu
        return Math.random() > 0.1; // 90% success rate
    }
    
    /**
     * Generate reference number untuk transaksi
     */
    private String generateTransactionReference() {
        return "REF-" + System.currentTimeMillis();
    }
    
    /**
     * Mendapatkan detail pembayaran e-wallet
     */
    @Override
    public String getPaymentDetails() {
        return String.format(
            "PEMBAYARAN E-WALLET\n" +
            "ID: %s\n" +
            "Provider: %s\n" +
            "Account: %s\n" +
            "Total: Rp %.2f\n" +
            "Referensi: %s\n" +
            "Status: %s",
            getPaymentId(),
            walletProvider,
            maskAccount(walletAccount),
            getAmount(),
            transactionReference != null ? transactionReference : "Pending",
            getStatus().getDescription()
        );
    }
    
    /**
     * Mask nomor akun untuk keamanan (tampilkan hanya 4 digit terakhir)
     */
    private String maskAccount(String account) {
        if (account.length() <= 4) {
            return "*" + account;
        }
        return "*".repeat(account.length() - 4) + account.substring(account.length() - 4);
    }
    
    // Getters
    public String getWalletProvider() { return walletProvider; }
    public String getWalletAccount() { return walletAccount; }
    public String getTransactionReference() { return transactionReference; }
    public boolean isProcessing() { return isProcessing; }
    
    @Override
    public String toString() {
        return "EWalletPayment{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", amount=" + getAmount() +
                ", walletProvider='" + walletProvider + '\'' +
                ", walletAccount='" + maskAccount(walletAccount) + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
