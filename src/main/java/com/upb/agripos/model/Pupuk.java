package main.java.com.upb.agripos.model;

public class Pupuk extends Product {

    private String jenis;

    public Pupuk(String kode, String nama, double harga, int stok, String jenis) {
        super(kode, nama, harga, stok);
        this.jenis = jenis;
    }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public  void deskripsi() {
        System.out.println("Nama Barang : " + getNama() + "\n       Jenis Pupuk : " + getJenis() + "\n      harga pupuk/20kg :" + getHarga() +"\n");
    }
}