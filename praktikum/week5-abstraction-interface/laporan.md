# Laporan Praktikum Minggu 5
Topik: Abstraction (Abstract Class & Interface)

## Identitas
- Nama  : Risky Dimas Nugroho
- NIM   : 240202882
- Kelas : 3IKRB

---

## Tujuan
- Mahasiswa mampu **menjelaskan perbedaan abstract class dan interface**.
- Mahasiswa mampu **mendesain abstract class dengan method abstrak** sesuai kebutuhan kasus.
- Mahasiswa mampu **membuat interface dan mengimplementasikannya pada class**.
- Mahasiswa mampu **menerapkan multiple inheritance melalui interface** pada rancangan kelas.
- Mahasiswa mampu **mendokumentasikan kode** (komentar kelas/method, README singkat pada folder minggu).

---

## Dasar Teori
**Abstraksi** adalah proses menyederhanakan kompleksitas dengan menampilkan elemen penting dan menyembunyikan detail implementasi.
- **Abstract class**: tidak dapat diinstansiasi, dapat memiliki method abstrak (tanpa badan) dan non-abstrak. Dapat menyimpan state (field).
- **Interface**: kumpulan kontrak (method tanpa implementasi konkret). Sejak Java 8 mendukung default method. Mendukung **multiple inheritance** (class dapat mengimplementasikan banyak interface).
- Gunakan **abstract class** bila ada _shared state_ dan perilaku dasar; gunakan **interface** untuk mendefinisikan kemampuan/kontrak lintas hierarki.

Dalam konteks Agri-POS, **Pembayaran** dapat dimodelkan sebagai abstract class dengan method abstrak `prosesPembayaran()` dan `biaya()`. Implementasi konkritnya: `Cash`, `EWallet`, dan `Transfer Bank`. Kemudian, interface seperti `Validatable` (mis. verifikasi OTP) dan `Receiptable` (mencetak bukti) dapat diimplementasikan oleh jenis pembayaran yang relevan.

---

## Langkah Praktikum
1. **Abstract Class – Pembayaran**
   - Buat `Pembayaran` (abstract) dengan field `invoiceNo`, `total` dan method:
     - `double biaya()` (abstrak) → biaya tambahan (fee).
     - `boolean prosesPembayaran()` (abstrak) → mengembalikan status berhasil/gagal.
     - `double totalBayar()` (konkrit) → `return total + biaya();`.

2. **Subclass Konkret**
   - `Cash` → biaya = 0, proses = selalu berhasil jika `tunai >= totalBayar()`.
   - `EWallet` → biaya = 1.5% dari `total`; proses = membutuhkan validasi.
   - `TransferBank` → biaya tetap Rp3.500, proses dianggap selalu berhasil.

3. **Interface**
   - `Validatable` → `boolean validasi();` (contoh: OTP).
   - `Receiptable` → `String cetakStruk();`

4. **Multiple Inheritance via Interface**
   - `EWallet` mengimplementasikan **dua interface**: `Validatable`, `Receiptable`.
   - `Cash` dan `TransferBank` mengimplementasikan `Receiptable`.

5. **Main Class**
    - Buat `MainAbstraction.java` untuk mendemonstrasikan pemakaian `Pembayaran` (polimorfik).
    - Tampilkan hasil proses dan struk. Di akhir, panggil `CreditBy.print("[NIM]", "[Nama]")`.

6. **Commit dan Push**
   - Commit dengan pesan: `week5-abstraction-interface`.

---

## Kode Program

```java
  Pembayaran cash = new Cash("INV-001", 100000, 120000);
  Pembayaran ew = new EWallet("INV-002", 150000, "user@ewallet", "123456");
  Pembayaran tb = new TransferBank("INV-003", 200000, "Bank WTC", "123-456-7890");

   System.out.println(((Receiptable) cash).cetakStruk());
   System.out.println(((Receiptable) ew).cetakStruk());
   System.out.println(((Receiptable) tb).cetakStruk());

   CreditBy.print("240202882", "Risky Dimas Nugroho");
```
---

## Hasil Eksekusi
![Screenshot hasil](oop-202501-240202882/praktikum/week5-abstraction-interface/screenshots)
---

## Analisis
Program pada minggu ini melanjutkan konsep dari minggu sebelumnya (Polymorphism) dengan fokus pada **Abstraction**, yaitu memisahkan antara definisi perilaku dan implementasinya.

- **Cara Kerja Program:**  
   Praktikum ini menunjukkan bahwa Abstraction membuat program lebih terstruktur dan fleksibel.
   Kelas Pembayaran menjadi dasar bagi metode pembayaran seperti Cash, EWallet, dan TransferBank yang memiliki logika berbeda.
   Abstract class menyatukan perilaku umum, sedangkan interface menambah fungsi khusus.
   Hasilnya, semua metode berjalan sesuai perannya, dan polymorphism terlihat saat tiap objek menunjukkan perilaku berbeda meski dipanggil dari tipe yang sama.

- **Perbedaan dengan Minggu Sebelumnya:**  
   Jika pada minggu sebelumnya fokusnya pada Polymorphism, maka minggu ini menekankan pada Abstraction melalui penggunaan abstract class dan interface.Polymorphism berfokus pada perbedaan perilaku antar objek dengan metode yang sama, sedangkan Abstraction menyoroti penyembunyian detail implementasi dan penyediaan kerangka dasar untuk kelas turunan.Dengan demikian, konsep minggu ini lebih menekankan pada perancangan struktur program, bukan hanya pada cara objek berperilaku.

- **Kendala dan Solusi:**  
   Manajemen folder paket (package) kadang keliru saat import.
   Kesalahan terjadi karena salah menuliskan struktur package com.upb.agripos.model.kontrak;. Solusinya adalah memastikan folder dan nama package identik dengan deklarasi di setiap file.

---

## Checklist Keberhasilan
- [x] Abstract class `Pembayaran` memiliki **method abstrak** dan **method konkrit** yang tepat.
- [x] Interface diimplementasikan **dengan benar** pada kelas yang relevan.
- [x] **Multiple inheritance via interface** berjalan (kelas mengimplementasikan ≥2 interface).
- [x] Program menampilkan **struk** dan status proses pembayaran.
- [x] Output menyertakan **credit by: [NIM] - [Nama]** melalui `CreditBy`.
- [x] Screenshot & laporan telah dilampirkan.

---

## Kesimpulan
Konsep abstraction dengan kombinasi abstract class dan interface membuat program lebih terstruktur, fleksibel, dan mudah dikembangkan.
Abstract class digunakan untuk menyimpan perilaku dasar, sedangkan interface untuk menambahkan kemampuan lintas kelas tanpa memecah hierarki.

---

## Quiz
1. Jelaskan perbedaan konsep dan penggunaan **abstract class** dan **interface**.  
   **Jawaban:** Abstract class digunakan sebagai dasar dengan atribut dan sebagian implementasi yang bisa diwariskan, sedangkan interface hanya berisi deklarasi metode tanpa isi sebagai kontrak perilaku.

2. Mengapa **multiple inheritance** lebih aman dilakukan dengan interface pada Java?  
   **Jawaban:** Karena interface tidak memiliki state atau implementasi, sehingga tidak terjadi konflik inheritance ganda antar kelas yang berbeda.

3. Pada contoh Agri-POS, bagian mana yang **paling tepat** menjadi abstract class dan mana yang menjadi interface? Jelaskan alasannya.  
   **Jawaban:** Class Pembayaran cocok menjadi abstract class karena memiliki atribut dan perilaku umum yang bisa inheritance,sedangkan Validatable dan Receiptable cocok menjadi interface karena hanya mendefinisikan fungsi tambahan seperti validasi dan pencetakan struk.