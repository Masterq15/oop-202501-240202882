package main.java.com.upb.agripos;

import main.java.com.upb.agripos.model.Kontrak.*;
import main.java.com.upb.agripos.model.Pembayaran.*;
import main.java.com.upb.agripos.util.CreditBy;

public class MainAbstraction {
    public static void main(String[] args) {
        Pembayaran cash = new Cash("INV-001", 100000, 120000);
        Pembayaran ew = new EWallet("INV-002", 150000, "user@ewallet", "123456");
        Pembayaran tb = new TransferBank("INV-003", 200000, "Bank WTC", "123-456-7890");

        System.out.println(((Receiptable) cash).cetakStruk());
        System.out.println(((Receiptable) ew).cetakStruk());
        System.out.println(((Receiptable) tb).cetakStruk());

        CreditBy.print("240202882", "Risky Dimas Nugroho");
    }
}