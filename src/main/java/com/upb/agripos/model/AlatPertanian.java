package main.java.com.upb.agripos.model;

public class AlatPertanian extends Product{

    private String material;

    public AlatPertanian(String kode, String nama, double harga, int stok, String material) {
        super(kode, nama, harga, stok);
        this.material = material;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public  void deskripsi(){
        System.out.println("Nama Barang : "+ getNama() + "\n    Mterial yang dipakai : " + getMaterial() + "\n      harga barang :" +  getHarga() +"\n");
    }
}