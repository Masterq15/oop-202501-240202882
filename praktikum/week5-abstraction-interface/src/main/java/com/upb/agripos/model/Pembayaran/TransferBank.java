package main.java.com.upb.agripos.model.Pembayaran;

import main.java.com.upb.agripos.model.Kontrak.Receiptable;

public class TransferBank extends Pembayaran implements Receiptable {
    private String bankName;
    private String accountNo;

    public TransferBank(String invoiceNo, double total, String bankName, String accountNo) {
        super(invoiceNo, total);
        this.bankName = bankName;
        this.accountNo = accountNo;
    }

    @Override
    public double biaya() {
        return 3500.0; // biaya tetap untuk transfer bank
    }

    @Override
    public boolean prosesPembayaran() {
        // sederhana: anggap selalu berhasil
        return true;
    }

    @Override
    public String cetakStruk() {
        return String.format("INVOICE %s | TOTAL+FEE: %.2f | TRANSFER BANK: %s (%s) | STATUS: %s",
                invoiceNo, totalBayar(), bankName, accountNo, prosesPembayaran() ? "BERHASIL" : "GAGAL");
    }
}