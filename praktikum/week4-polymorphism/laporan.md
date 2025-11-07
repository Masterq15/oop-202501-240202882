# Laporan Praktikum Minggu 4
Topik: Polymorphism (Info Produk)

## Identitas
- Nama  : Risky Dimas Nugroho
- NIM   : 240202882
- Kelas : 3IKRB

---

## Tujuan
- Mahasiswa mampu **menjelaskan konsep polymorphism** dalam OOP.  
- Mahasiswa mampu **membedakan method overloading dan overriding**.  
- Mahasiswa mampu **mengimplementasikan polymorphism (overriding, overloading, dynamic binding)** dalam program.  
- Mahasiswa mampu **menganalisis contoh kasus polymorphism** pada sistem nyata (Agri-POS).  

---

## Dasar Teori
Polymorphism berarti “banyak bentuk” dan memungkinkan objek yang berbeda merespons panggilan method yang sama dengan cara yang berbeda.  
1. **Overloading** → mendefinisikan method dengan nama sama tetapi parameter berbeda.  
2. **Overriding** → subclass mengganti implementasi method dari superclass.  
3. **Dynamic Binding** → pemanggilan method ditentukan saat runtime, bukan compile time.  

Dalam konteks Agri-POS, misalnya:  
- Method `getInfo()` pada `Produk` dioverride oleh `Benih`, `Pupuk`, `AlatPertanian` untuk menampilkan detail spesifik.  
- Method `tambahStok()` bisa dibuat overload dengan parameter berbeda (int, double).  

---

## Langkah Praktikum
1. **Overloading**  
   - Tambahkan method `tambahStok(int jumlah)` dan `tambahStok(double jumlah)` pada class `Produk`.  

2. **Overriding**  
   - Tambahkan method `getInfo()` pada superclass `Produk`.  
   - Override method `getInfo()` pada subclass `Benih`, `Pupuk`, dan `AlatPertanian`.  

3. **Dynamic Binding**  
   - Buat array `Produk[] daftarProduk` yang berisi objek `Benih`, `Pupuk`, dan `AlatPertanian`.  
   - Loop array tersebut dan panggil `getInfo()`. Perhatikan bagaimana Java memanggil method sesuai jenis objek aktual.  

4. **Main Class**  
   - Buat `MainPolymorphism.java` untuk mendemonstrasikan overloading, overriding, dan dynamic binding.  

5. **CreditBy**  
   - Tetap panggil `CreditBy.print("<NIM>", "<Nama>")`.  

6. **Commit dan Push**  
   - Commit dengan pesan: `week4-polymorphism`.  

---

## Kode Program

```java
   Produk[] daftarProduk = {
      new Benih("BNH-001", "Benih Padi IR64", 25000, 100, "IR64"),
      new Pupuk("PPK-101", "Pupuk Urea", 350000, 40, "Urea"),
      new AlatPertanian("ALT-501", "Cangkul Baja", 90000, 15, "Baja"),
      new ObatHama("OBT-220", "Wastak",25000,4,"basmi wereng")
   };

   for (Produk p : daftarProduk) {
      System.out.println(p.getInfo()); // Dynamic Binding
   }

   CreditBy.print("240202882", "Risky Dimas Nugroho");
```
---

## Hasil Eksekusi
![Screenshot hasil](oop-202501-240202882/praktikum/week4-polymorphism/screenshots)
---

## Analisis
- Program ini menjelaskan cara kerja polymorphism di Java. Dalam array bertipe Produk, terdapat berbagai objek seperti Benih, Pupuk, AlatPertanian, dan ObatHama. Saat getInfo() dipanggil, Java otomatis menjalankan method sesuai jenis objeknya. Hal ini disebut dynamic binding yang membuat program lebih fleksibel.
- Minggu lalu membahas inheritance di mana subclass hanya mewarisi atribut dan method dari superclass. Minggu ini membahas polymorphism, yaitu kemampuan objek turunan untuk memiliki perilaku berbeda meskipun dipanggil dengan referensi yang sama dari superclass.
- Kendala: -
---

## Kesimpulan
Dari praktikum ini dapat disimpulkan bahwa polymorphism adalah konsep penting dalam OOP yang membuat satu method bisa memiliki banyak bentuk. Dengan overloading dan overriding, method yang sama dapat bekerja berbeda tergantung objeknya. Berkat dynamic binding, Java memilih method yang sesuai saat program dijalankan, sehingga kode menjadi lebih fleksibel dan mudah dikembangkan.
---

## Quiz
1. Apa perbedaan overloading dan overriding?
   **Jawaban:** Overloading terjadi saat ada dua atau lebih method dengan nama sama, tapi berbeda pada jumlah atau tipe parameternya. Proses ini ditentukan saat program dikompilasi (compile-time).sedangkan Overriding terjadi saat subclass membuat ulang method dari superclass dengan isi berbeda. Proses ini ditentukan ketika program dijalankan (runtime).

2. Bagaimana Java menentukan method mana yang dipanggil dalam dynamic binding?
   **Jawaban:** Java melihat objek sebenarnya yang digunakan, bukan tipe variabelnya. Jadi kalau variabel bertipe superclass tapi berisi objek subclass, method dari subclass-lah yang dijalankan.

3. Berikan contoh kasus polymorphism dalam sistem POS selain produk pertanian.
   **Jawaban:** Dalam POS restoran, class MenuItem bisa punya turunan Makanan, Minuman, dan Dessert. Tiap kelas punya getInfo() sendiri, dan Java akan memanggil yang sesuai dengan jenis objeknya.
