package main.java.com.upb.agripos.model;

public class Pupuk extends Produk {
    private String jenis;

    public Pupuk(String kode, String nama, double harga, int stok, String jenis) {
        super(kode, nama, harga, stok);
        this.jenis = jenis;
    }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis;}

    public String deskripsi() {
        return getNama() + " - Jenis: " + jenis + "\nDeskripsi Produk:\n" + "Kode: " + getKode() + " | " + "Harga: " + getHarga() + " | " + "Stok: " + getStok();
    }
}