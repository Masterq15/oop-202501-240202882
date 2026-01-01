# Laporan Praktikum Minggu 6 (sesuaikan minggu ke berapa?)
Topik: [WEEK 6 Desain Arsitektur Sistem dengan UML dan Prinsip SOLID]

## Identitas
- Nama  : [Risky Dimas Nugroho]
- NIM   : [240202882]
- Kelas : [3 IKRB]

---

## Tujuan

1. Mahasiswa mampu mengidentifikasi kebutuhan sistem ke dalam diagram UML.
2. Mahasiswa mampu menggambar UML Class Diagram dengan relasi antar class yang tepat.
3. Mahasiswa mampu menjelaskan prinsip desain OOP (SOLID).
4. Mahasiswa mampu menerapkan minimal dua prinsip SOLID dalam kode program.

---

## Deskripsi Sistem Agri-POS
Agri-POS adalah sistem Point of Sale untuk penjualan produk pertanian (benih, pupuk, alat). Terdapat dua peran utama:
Admin → Mengelola produk dan laporan.
Kasir → Melakukan transaksi checkout dan pembayaran.

Kebutuhan Fungsional (FR) yang dicakup:
Manajemen Produk (CRUD)
Transaksi Penjualan (Checkout)
Pembayaran Tunai & E-Wallet
Pencetakan Struk
Login & Hak Akses


---

##  Desain Arsitektur UML

# **1. Use Case Diagram**
Menunjukkan fungsi sistem dari perspektif aktor yang terlibat.

- Terdapat dua aktor: **Admin** dan **Kasir**
- Admin berperan dalam pengelolaan data dan akses sistem
- Kasir bertanggung jawab terhadap proses transaksi dan pembayaran

## **Relasi Utama**
- **Generalization** pada metode pembayaran:
  - **Bayar Tunai** dan **Bayar E-Wallet/Kartu** merupakan variasi dari proses **Pembayaran**
- **Include**:
  - Setelah **Checkout**, sistem akan melakukan **Cetak Struk**

## **Aktor & Use Case**

| Aktor  | Use Case |
|--------|----------------------------------------------|
| Admin  | Login, Kelola Produk, Lihat Laporan          |
| Kasir  | Buat Transaksi, Checkout, Pembayaran, Cetak Struk |

![alt text](<screenshots/Use Case.drawio (1).png>)


---

# **2. Activity Diagram – Proses Checkout**
Menggambarkan alur kerja proses transaksi hingga pembayaran.

**Swimlanes:**  
➡️ *Kasir* – input item, pilih metode, finalisasi  
➡️ *Sistem Agri-POS* – validasi stok, hitung total, cek diskon, update stok  
➡️ *Payment Gateway* – verifikasi transaksi e-wallet/kartu

## **Decision Node dalam Proses**
1. **Validasi Stok**
   - Stok tidak cukup → input ulang atau batalkan
   - Stok cukup → hitung total belanja

2. **Penerapan Diskon**
   - Jika ada kode promo → validasi dan perbarui total
   - Jika tidak ada → lanjut

3. **Pemilihan Pembayaran**
   - **Tunai** → input nominal → hitung kembalian
   - **E-Wallet/Kartu** → verifikasi Payment Gateway

4. **Validasi Dana**
   - Jika dana tidak cukup → transaksi gagal
   - Jika valid → lanjut cetak struk

[text](<src/main/java/com/upb/agripos/Activity Diagram.drawio.xml>)

---

# **3. Sequence Diagram – Proses Pembayaran**
Menggambarkan interaksi antar objek selama proses pembayaran berlangsung.

## **Alur Eksekusi**
1. Kasir memanggil `processPayment(transaction, paymentType, amount)` melalui **PaymentService**
2. `PaymentService` meminta objek pembayaran dari **PaymentMethodFactory**
3. Factory menghasilkan objek sesuai tipe:
   - `CashPayment`
   - `EWalletPayment`
4. `PaymentMethod.pay()` dijalankan sesuai implementasi
5. Blok alternatif (*alt*):
   - **Tunai** → hitung kembalian → transaksi sukses
   - **E-Wallet** → Payment Gateway cek saldo
      - Saldo cukup → sukses
      - Saldo kurang → gagal
6. `ReceiptService` mencetak dan menampilkan struk

## **Prinsip OOP**
- **OCP:** metode pembayaran baru dapat ditambahkan tanpa modifikasi kelas inti
- **LSP:** setiap metode pembayaran dapat menggantikan interface induknya
- **DIP:** modul tingkat tinggi bergantung pada interface, bukan kelas konkret

[text](src/main/java/com/upb/agripos/SequenceDiagram.drawio.xml)

---

# **4. Class Diagram**
Struktur statis sistem Agri-POS sesuai model UML.

## **Kelas Utama Sistem**

| Kelas                   | Tanggung Jawab |
|--------------------------|----------------|
| **Product**              | Menyimpan data barang (kode, harga, kategori, stok) |
| **Transaction**          | Mencatat item transaksi, total, dan waktu |
| **ProductService**       | Validasi & update stok produk, hitung subtotal |
| **PaymentService**       | Koordinator proses pembayaran |
| **PaymentMethod (Interface)** | Kontrak standar semua metode pembayaran |
| **CashPayment**          | Implementasi pembayaran tunai |
| **EWalletPayment**       | Implementasi pembayaran e-wallet/kartu |
| **PaymentMethodFactory** | Pembuat objek metode pembayaran |
| **ReceiptService**       | Cetak dan generate struk |

![alt text](<screenshots/Class Diagram.drawio.png>)

---
# **. Penerapan Prinsip SOLID**

| Prinsip | Penjelasan | Implementasi pada Agri-POS |
|---------|-------------|-----------------------------|
| **SRP** | 1 kelas = 1 tanggung jawab | `ProductService`, `ReceiptService`, `PaymentService` terpisah sesuai fungsi |
| **OCP** | Mudah ditambah tanpa ubah kode lama | Tambah `QRISPayment` cukup buat class baru |
| **LSP** | Subclass = bisa mengganti parent | `CashPayment` & `EWalletPayment` interchangeable sebagai `PaymentMethod` |
| **ISP** | Interface sesuai kebutuhan klien | `PaymentMethod` fokus transaksi, `ProductRepository` fokus CRUD |
| **DIP** | High-level tergantung abstraksi | `PaymentService` menggunakan `PaymentMethod` (interface), bukan implementasi |

---

# **. Traceability Matrix**
Kesesuaian kebutuhan dengan desain sistem.

| Functional Requirement | Use Case Terkait | Diagram | Realisasi |
|------------------------|------------------|----------|-----------|
| Manajemen Produk | Kelola Produk | - | ProductService, ProductRepository |
| Pembuatan Transaksi | Buat Transaksi | Activity (Validasi Stok) | CheckoutService, ProductService |
| Pembayaran Tunai | Bayar Tunai | Sequence Pembayaran | CashPayment, PaymentFactory |
| Pembayaran E-Wallet | Bayar E-Wallet/Kartu | Sequence Pembayaran | EWalletPayment, PaymentGateway |
| Cetak Struk | Cetak Struk | Activity Akhir | ReceiptService |
| Login Akses Sistem | Login | - | AuthService, User |

---

# **. Quiz & Argumentasi Desain**

### **1. Aggregation vs Composition**
| Jenis | Makna | Contoh di Agri-POS |
|-------|--------|--------------------|
| Aggregation | Hubungan lemah (objek tetap hidup) | Store ↔ Cashier |
| Composition | Hubungan kuat (bergantung penuh) | Transaction → OrderDetail |

### **2. Bagaimana OCP mempermudah pengembangan?**
- Tambah fitur → buat class baru
- Tidak mengubah kelas inti
- Risiko bug lebih kecil
- Pengembangan aman & scalable

Contoh: `QRISPayment` tinggal implementasi `PaymentMethod`.

### **3. Mengapa DIP meningkatkan Testability?**
- Bergantung pada interface, bukan objek nyata
- Dapat memakai **Mock Repository / Mock Payment**
- Tidak perlu koneksi database atau gateway asli

---

## Kesimpulan
Desain sistem pembayaran kasir sudah selaras dengan UML dan prinsip SOLID. Arsitektur ini:
- Mendukung kebutuhan fungsional utama
- Mudah dirawat dan dikembangkan
- Mengurangi ketergantungan antar kelas
- Fleksibel untuk penambahan fitur baru

Penggunaan factory, repository, dan interface membantu menjaga sistem tetap modular dan siap diperluas tanpa mengubah struktur inti.

---
