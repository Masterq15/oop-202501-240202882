package main.java.com.upb.agripos.model;

public class AlatPertanian extends Produk {
    private String material;

    public AlatPertanian(String kode, String nama, double harga, int stok, String material) {
        super(kode, nama, harga, stok);
        this.material = material;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material;}

    public String deskripsi() {
        return getNama() + " - Material: " + material + "\nDeskripsi Produk:\n" + "Kode: " + getKode() + " | " + "Harga: " + getHarga() + " | " + "Stok: " + getStok();
    }
}