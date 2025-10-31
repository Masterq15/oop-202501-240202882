# Laporan Praktikum Minggu 3
Topik: Inheritance (Kategori Produk)

## Identitas
- Nama  : Risky Dimas Nugroho
- NIM   : 240202882
- Kelas : 3IKRB

---

## Tujuan
- Mahasiswa mampu **menjelaskan konsep inheritance (pewarisan class)** dalam OOP.  
- Mahasiswa mampu **membuat superclass dan subclass** untuk produk pertanian.  
- Mahasiswa mampu **mendemonstrasikan hierarki class** melalui contoh kode.  
- Mahasiswa mampu **menggunakan `super` untuk memanggil konstruktor dan method parent class**.  
- Mahasiswa mampu **membuat laporan praktikum** yang menjelaskan perbedaan penggunaan inheritance dibanding class tunggal.  

---

## Dasar Teori
Inheritance adalah mekanisme dalam OOP yang memungkinkan suatu class mewarisi atribut dan method dari class lain.  
- **Superclass**: class induk yang mendefinisikan atribut umum.  
- **Subclass**: class turunan yang mewarisi atribut/method superclass, dan dapat menambahkan atribut/method baru.  
- `super` digunakan untuk memanggil konstruktor atau method superclass.  

---

## Langkah Praktikum
1. **Membuat Superclass Produk**  
   - Gunakan class `Produk` dari Bab 2 sebagai superclass.  

2. **Membuat Subclass**  
   - `Benih.java` → atribut tambahan: varietas.  
   - `Pupuk.java` → atribut tambahan: jenis pupuk (Urea, NPK, dll).  
   - `AlatPertanian.java` → atribut tambahan: material (baja, kayu, plastik).  

3. **Membuat Main Class**  
   - Instansiasi minimal satu objek dari tiap subclass.  
   - Tampilkan data produk dengan memanfaatkan inheritance.  

4. **Menambahkan CreditBy**  
   - Panggil class `CreditBy` untuk menampilkan identitas mahasiswa.  

5. **Commit dan Push**  
   - Commit dengan pesan: `week3-inheritance`.  

---

## Kode Program
```java
        Pupuk p = new Pupuk("PPK-101", "Pupuk Urea", 350000, 40, "Urea");
        AlatPertanian a = new AlatPertanian("ALT-501", "Cangkul Baja", 90000, 15, "Baja");

        System.out.println("------------------------------------");
        System.out.println(b.deskripsi());

        System.out.println("------------------------------------");
        System.out.println(p.deskripsi());
        
        System.out.println("------------------------------------");
        System.out.println(a.deskripsi());

        CreditBy.print("240202882", "Risky Dimas Nugroho");
```
---

## Hasil Eksekusi
![Screenshot hasil](oop-202501-240202882/praktikum/week3-inheritance/screenshots/week3-inheritance.png)
---

## Analisis
- Pewarisan Berhasil: Benih, Pupuk, dan AlatPertanian berhasil mendapatkan data dasar produk (kode, harga, stok) dari kelas Produk. Ini membuat kita tidak perlu menulis ulang kode data dasar di setiap kelas.
- Sifat Unik: Setiap kelas anak sukses menambahkan informasi unik mereka (misalnya, Pupuk punya Jenis).
- Efisiensi: Dibandingkan membuat tiga kelas yang berdiri sendiri, penggunaan Inheritance membuat kode lebih rapi, terpusat, dan mudah diubah. 
- Kode Jadi Lebih efisien dibanding Minggu lalu, Yang harus menulis detail produk yang sama (misalnya, harga dan stok) berulang kali di setiap jenis produk. Sekarang, berkat Inheritance, detail itu cukup ditulis sekali di kelas Produk lalu diwariskan ke semua anak. Ini membuat kode jauh lebih efisien.
---

## Kesimpulan
- Inheritance sangat berguna untuk membuat program jadi efisien karena mengurangi pengulangan kode.
- Teknik ini memungkinkan kita mengembangkan sistem (misalnya, menambah jenis produk baru) tanpa merusak atau mengubah kode inti. 
---

## Quiz
1. **Apa keuntungan menggunakan inheritance dibanding membuat class terpisah tanpa hubungan?**  
   **Jawaban:** Kode jadi lebih hemat dan mudah dikelola (reusable), karena semua sifat umum cukup ditulis sekali di kelas induk. 

2. **Bagaimana cara subclass memanggil konstruktor superclass?**  
   **Jawaban:** Untuk memastikan semua data dan pengaturan dari kelas induk dipanggil dan disiapkan terlebih dahulu sebelum kelas anak dibuat.  

3. **Berikan contoh kasus di POS pertanian selain Benih, Pupuk, dan Alat Pertanian yang bisa dijadikan subclass.**  

   **Jawaban:** Subclass ObatHama dengan sifat tambahan seperti jenisHama (misalnya: Penggerek Batang) dan dosisCampuran (misalnya: per liter air).
