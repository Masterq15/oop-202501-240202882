# Laporan Praktikum Minggu 15
## Proyek Kelompok - Implementasi Sistem Agri-POS Terintegrasi dengan Testing & Dokumentasi

---

## 1. Identitas

| Aspek | Keterangan |
|-------|-----------|
| **Nama** | Risky Dimas Nugroho |
| **NIM** | 240202882 |
| **Kelas** | 3IKRB |
| **Mata Kuliah** | Object Oriented Programming |
| **Minggu** | 15 (Proyek Kelompok) |
| **Topik** | Desain Sistem + Implementasi Terintegrasi + Testing + Dokumentasi |

---

## 2. Ringkasan Sistem

### 2.1 Tema & Tujuan
**Agri-POS** adalah sistem Point of Sale untuk penjualan produk pertanian. Proyek Minggu 15 adalah kelanjutan dan pengembangan dari Minggu 14 (integrasi individu) yang bertujuan menghasilkan sistem yang:

1. ✓ Terintegrasi end-to-end: GUI (JavaFX) → Controller → Service → DAO → PostgreSQL
2. ✓ Memiliki desain sistem lengkap (UML + arsitektur)
3. ✓ Menerapkan SOLID principles dan design patterns
4. ✓ Menyediakan dokumentasi lengkap (SRS, arsitektur, database, test plan)
5. ✓ Memiliki unit test minimal untuk validasi logika bisnis

### 2.2 Fitur Utama (FR)
1. **FR-1 Manajemen Produk**: CRUD produk dengan atribut (kode, nama, kategori, harga, stok)
2. **FR-2 Transaksi Penjualan**: Tambah produk ke keranjang, ubah qty, hitung total
3. **FR-3 Metode Pembayaran**: Tunai dan E-Wallet dengan pattern Strategy (extensible)
4. **FR-4 Struk dan Laporan**: Display struk transaksi, laporan penjualan sederhana
5. **FR-5 Login & Hak Akses**: Dua role (Admin & Kasir) dengan hak akses berbeda

---

## 3. Kebutuhan Fungsional (Requirements)

### 3.1 Functional Requirements (FR)

| No | Requirement | Deskripsi | Implementasi |
|----|-------------|-----------|--------------|
| FR-1 | Manajemen Produk | CRUD (Create, Read, Update, Delete) produk | `ProductController`, `ProductService`, `ProductDAO` |
| FR-2 | Transaksi Penjualan | Kelola keranjang belanja & checkout | `CartController`, `CartService`, `CartItem` |
| FR-3 | Metode Pembayaran | Tunai & E-Wallet (Strategy Pattern) | `PaymentMethod`, `CashPayment`, `EWalletPayment`, `PaymentFactory` |
| FR-4 | Struk & Laporan | Generate dan tampilkan struk transaksi | `ReceiptService`, `Transaction` |
| FR-5 | Login & Hak Akses | Autentikasi user, role-based access | `AuthController`, `AuthService`, `User`, `UserDAO` |

### 3.2 Non-Functional Requirements

| No | NFR | Deskripsi |
|----|-----|-----------|
| NFR-1 | Arsitektur | Layering MVC + Service + DAO (DIP compliant) |
| NFR-2 | Database | PostgreSQL dengan JDBC dan PreparedStatement |
| NFR-3 | Exception Handling | Custom exception: `ValidationException`, `OutOfStockException`, `AuthenticationException` |
| NFR-4 | Testing | Unit test JUnit 5 dengan coverage test logic non-UI |
| NFR-5 | Design Patterns | Factory (Payment), Strategy (Payment), Singleton (DB Connection) |

---

## 4. Desain Sistem

### 4.1 Arsitektur Layering

```
┌─────────────────────────────────────────────────────────┐
│                  VIEW LAYER (JavaFX)                    │
│              AppJavaFX, UI Components                   │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              CONTROLLER LAYER                            │
│  AuthController, ProductController, CartController      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              SERVICE LAYER                               │
│  AuthService, ProductService, CartService,              │
│  PaymentService (PaymentMethod, CashPayment, ...)       │
│  ReceiptService                                          │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              DAO LAYER (JDBC)                            │
│  ProductDAO/Impl, UserDAO/Impl                          │
│  DatabaseConfig (Singleton)                             │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│          DATABASE LAYER (PostgreSQL)                     │
│  Tables: products, users, transactions, transaction_items│
└─────────────────────────────────────────────────────────┘
```

### 4.2 SOLID Principles Implementation

| Prinsip | Implementasi | Contoh |
|---------|--------------|--------|
| **SRP** | Satu kelas, satu tanggung jawab | `CartService` hanya handle cart logic; `ProductService` hanya handle product logic |
| **OCP** | Terbuka untuk ekstensi, tertutup modifikasi | `PaymentMethod` interface → `CashPayment`, `EWalletPayment` dapat ditambah tanpa ubah kode inti |
| **LSP** | Subclass dapat menggantikan parent | Semua payment method implement `PaymentMethod` dengan cara yang konsisten |
| **ISP** | Interface sesuai kebutuhan | `ProductDAO`, `UserDAO`, `PaymentMethod` fokus pada tanggung jawab spesifik |
| **DIP** | High-level depends on abstraction | `ProductController` → `ProductService` → `ProductDAO` (interface), bukan implementasi langsung |

### 4.3 Design Patterns

| Pattern | Penggunaan | Implementasi |
|---------|-----------|--------------|
| **Factory** | Membuat object PaymentMethod sesuai tipe | `PaymentFactory.createPaymentMethod(type)` |
| **Strategy** | Berbagai metode pembayaran dapat di-swap | `PaymentMethod` interface dengan implementasi `CashPayment`, `EWalletPayment` |
| **Singleton** | Database connection management | `DatabaseConfig.getInstance()` |
| **MVC** | Pemisahan View, Controller, Model | `AppJavaFX` (View), Controllers, Model classes |

---

## 5. Model Data & UML

### 5.1 Class Diagram (Struktur Statis)

```
MODELS:
┌─────────────────┐
│   Product       │
├─────────────────┤
│ id: int         │
│ code: String    │
│ name: String    │
│ category: String│
│ price: double   │
│ stock: int      │
└─────────────────┘

┌─────────────────┐
│   CartItem      │
├─────────────────┤
│ product: Product│
│ quantity: int   │
└─────────────────┘

┌─────────────────┐
│  Transaction    │
├─────────────────┤
│ id: int         │
│ code: String    │
│ items: List     │
│ total: double   │
│ method: String  │
│ cashier: String │
└─────────────────┘

┌─────────────────┐
│     User        │
├─────────────────┤
│ id: int         │
│ username: String│
│ password: String│
│ fullName: String│
│ role: String    │
│ active: boolean │
└─────────────────┘

SERVICES:
┌──────────────────────────┐
│   ProductService         │
├──────────────────────────┤
│ + getAllProducts()       │
│ + addProduct()           │
│ + updateProduct()        │
│ + deleteProduct()        │
│ + validateStock()        │
└──────────────────────────┘

┌──────────────────────────┐
│    CartService           │
├──────────────────────────┤
│ + addItem()              │
│ + removeItem()           │
│ + updateQuantity()       │
│ + getTotal()             │
│ + clear()                │
└──────────────────────────┘

┌──────────────────────────┐
│  PaymentMethod(interface)│
├──────────────────────────┤
│ + processPayment()       │
│ + getMethodName()        │
└──────────────────────────┘
  △          △
  │          │
  └─────┬────┘
        │
  ┌─────┴──────────────┐
  │                    │
CashPayment      EWalletPayment

┌──────────────────────────┐
│   AuthService            │
├──────────────────────────┤
│ + login()                │
│ + logout()               │
│ + isAdmin()              │
│ + isKasir()              │
└──────────────────────────┘
```

### 5.2 Use Case Diagram

```
┌──────────────────────────────────────┐
│          AGRI-POS System             │
├──────────────────────────────────────┤
│                                      │
│  ┌─────────────┐  ┌──────────────┐  │
│  │    Admin    │  │    Kasir     │  │
│  └─────┬───────┘  └──────┬───────┘  │
│        │                 │          │
│        │◄──── Login ────►│          │
│        │                 │          │
│        ├── Kelola Produk │          │
│        │  (CRUD)         │          │
│        │                 ├── Transaksi
│        │                 │  - Add Cart
│        │                 │  - Update Qty
│        │                 │  - Remove Item
│        │                 │  - Checkout
│        │                 │    (Tunai/E-Wallet)
│        │                 │
│        ├── Lihat Laporan │
│        │                 │
└──────────────────────────────────────┘
```

### 5.3 Sequence Diagram - Proses Checkout

```
Kasir        Controller      Service        DAO       Database
 │               │             │            │            │
 ├─ Click ─────►│             │            │            │
 │  Checkout    │             │            │            │
 │              ├─ Validate ─►│            │            │
 │              │   Cart      │            │            │
 │              │◄─ OK ────────│            │            │
 │              │             │            │            │
 │              ├─ Create ───►│            │            │
 │              │ Transaction │            │            │
 │              │             │            │            │
 │              │ Payment ───►│            │            │
 │              │ Method      │            │            │
 │              │◄─ Result ────│            │            │
 │              │             │            │            │
 │              │    if success:           │            │
 │              │    ├─ Save Transaction ─►│ INSERT ───►│
 │              │    │        Items         │  UPDATE   │
 │              │    │                      │ (stock)   │
 │              │◄───┴─ Success ────────────│◄───────────│
 │              │                          │            │
 │◄─ Show Struk │                          │            │
 │  (Alert)     │                          │            │
```

---

## 6. Database Design

### 6.1 Entity Relationship Diagram (ERD)

```
┌─────────────────────┐
│     PRODUCTS        │
├─────────────────────┤
│ id (PK)            │
│ code (UNIQUE)      │
│ name               │
│ category           │
│ price              │
│ stock              │
│ created_at         │
│ updated_at         │
└─────────────────────┘
         △
         │ (1:N)
         │
┌─────────────────────────────────┐
│   TRANSACTION_ITEMS             │
├─────────────────────────────────┤
│ id (PK)                         │
│ transaction_id (FK)             │
│ product_id (FK) ────────────────┘
│ quantity                        │
│ unit_price                      │
│ subtotal                        │
└─────────────────────────────────┘
         △
         │ (N:1)
         │
┌─────────────────────┐
│   TRANSACTIONS      │
├─────────────────────┤
│ id (PK)            │
│ code (UNIQUE)      │
│ transaction_date   │
│ subtotal           │
│ discount           │
│ total              │
│ payment_method     │
│ payment_status     │
│ cashier_name       │
│ created_at         │
└─────────────────────┘

┌─────────────────────┐
│      USERS          │
├─────────────────────┤
│ id (PK)            │
│ username (UNIQUE)  │
│ password           │
│ full_name          │
│ role               │
│ active             │
│ created_at         │
└─────────────────────┘
```

### 6.2 SQL DDL

```sql
-- Products Table
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'KASIR',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions Table
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    discount DECIMAL(12, 2) DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    cashier_name VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transaction Items Table
CREATE TABLE transaction_items (
    id SERIAL PRIMARY KEY,
    transaction_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### 6.3 Data Seeding

```sql
-- Insert sample products
INSERT INTO products (code, name, category, price, stock) VALUES
('P001', 'Pupuk Organik Premium', 'Pupuk', 30000, 50),
('P002', 'Benih Padi Berkualitas', 'Benih', 15000, 100),
('P003', 'Pestisida Organik 500ml', 'Pestisida', 45000, 30),
('P004', 'Pupuk NPK 25kg', 'Pupuk', 125000, 20),
('P005', 'Bibit Cabai Hibrida', 'Benih', 25000, 80);

-- Insert demo users
INSERT INTO users (username, password, full_name, role, active) VALUES
('admin01', 'admin123', 'Admin System', 'ADMIN', true),
('kasir01', 'kasir123', 'Kasir 1', 'KASIR', true),
('kasir02', 'kasir456', 'Kasir 2', 'KASIR', true);
```

---

## 7. Test Plan & Test Cases

### 7.1 Test Strategy

| Aspek | Strategi |
|-------|----------|
| **Unit Test** | JUnit 5 untuk service layer (CartService, PaymentService, ProductService) |
| **Integration Test** | Manual testing untuk flow end-to-end (GUI → Service → DAO → DB) |
| **Scope** | Test logika bisnis non-UI, validasi, exception handling |

### 7.2 Manual Test Cases

#### TC-01: FR-1 - Tambah Produk (Admin)
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Admin login, di halaman Manajemen Produk |
| Langkah | 1. Isi Kode: P999, Nama: Produk Test, Kategori: Test, Harga: 50000, Stok: 10 |
| | 2. Klik tombol "Tambah Produk" |
| Expected | ✓ Produk ditambahkan ke tabel dengan data yang benar |
| | ✓ Pesan sukses muncul: "Produk berhasil ditambahkan" |

#### TC-02: FR-1 - Validasi Kode Produk Duplikasi
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Admin login, produk P001 sudah ada |
| Langkah | 1. Coba tambah produk dengan Kode: P001 |
| Expected | ✗ Error: "Kode produk sudah ada: P001" |

#### TC-03: FR-1 - Hapus Produk (Admin)
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Admin login, terdapat produk di tabel |
| Langkah | 1. Pilih produk dari tabel (misal: P001) |
| | 2. Klik tombol "Hapus Produk Terpilih" |
| Expected | ✓ Produk dihapus dari tabel |
| | ✓ Pesan sukses muncul |

#### TC-04: FR-2 - Tambah Produk ke Keranjang (Kasir)
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Kasir login, produk P001 tersedia |
| Langkah | 1. Pilih produk P001 dari tabel "Produk Tersedia" |
| | 2. Input kuantitas: 2 di field Qty |
| | 3. Klik tombol "Tambah ke Keranjang" |
| Expected | ✓ Item ditambahkan ke tabel Keranjang Belanja |
| | ✓ Qty ditampilkan: 2 |
| | ✓ Subtotal: Rp 60.000 |

#### TC-05: FR-2 - Update Kuantitas Item Keranjang
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Kasir login, ada item P001 di keranjang (qty=2) |
| Langkah | 1. Ubah qty di field (atau klik item dan update) |
| | 2. Qty menjadi 5 |
| Expected | ✓ Subtotal berubah menjadi Rp 150.000 |
| | ✓ Total otomatis update |

#### TC-06: FR-2 - Hapus Item Keranjang
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Kasir login, ada 2 item di keranjang |
| Langkah | 1. Pilih item P001 dari keranjang |
| | 2. Klik tombol "Hapus Item Terpilih" |
| Expected | ✓ Item dihapus dari keranjang |
| | ✓ Total otomatis recalculate |

#### TC-07: FR-3 - Checkout Pembayaran Tunai
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Kasir login, keranjang ada 2 item, Total: Rp 100.000 |
| Langkah | 1. Pilih Metode: "Tunai" |
| | 2. Input uang: 150000 |
| | 3. Klik tombol "CHECKOUT" |
| Expected | ✓ Pembayaran berhasil |
| | ✓ Struk ditampilkan dengan kembalian Rp 50.000 |
| | ✓ Keranjang kosong setelah checkout |

#### TC-08: FR-3 - Checkout Pembayaran E-Wallet
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Kasir login, keranjang ada item |
| Langkah | 1. Pilih Metode: "E-Wallet" |
| | 2. Input nomor akun: 081234567890 |
| | 3. Klik tombol "CHECKOUT" |
| Expected | ✓ Pembayaran berhasil |
| | ✓ Struk ditampilkan |

#### TC-09: FR-5 - Login Admin
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Di layar login |
| Langkah | 1. Username: admin01, Password: admin123 |
| | 2. Klik tombol LOGIN |
| Expected | ✓ Login berhasil |
| | ✓ Menampilkan Admin Dashboard (Manajemen Produk, Laporan) |

#### TC-10: FR-5 - Login Kasir
| Aspek | Deskripsi |
|-------|-----------|
| Precondition | Di layar login |
| Langkah | 1. Username: kasir01, Password: kasir123 |
| | 2. Klik tombol LOGIN |
| Expected | ✓ Login berhasil |
| | ✓ Menampilkan Kasir Dashboard (Produk, Keranjang, Checkout) |

### 7.3 Unit Test Results

Setelah menjalankan perintah:
```bash
mvn test
```

**Test Classes:**
- `CartServiceTest.java` - 10 test cases
- `PaymentServiceTest.java` - 8 test cases  
- `ProductServiceTest.java` - 6 test cases

**Total: 24 Unit Tests**

```
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 8. Traceability Matrix

| Artefak | Referensi | Implementasi (kelas/metode) | Bukti |
|---------|-----------|---------------------------|-------|
| FR | FR-1 Manajemen Produk | `ProductController`, `ProductService`, `ProductDAO` | CRUD di Admin Dashboard |
| FR | FR-2 Transaksi Penjualan | `CartController`, `CartService`, `CartItem` | Keranjang + Add/Remove/Update Qty |
| FR | FR-3 Metode Pembayaran | `PaymentMethod` (interface), `CashPayment`, `EWalletPayment`, `PaymentFactory` | Pilihan Tunai/E-Wallet di Checkout |
| FR | FR-4 Struk & Laporan | `ReceiptService`, `Transaction` | Display Struk setelah Checkout |
| FR | FR-5 Login & Hak Akses | `AuthController`, `AuthService`, `User`, `UserDAO` | Login screen, role-based dashboard |
| Design | Singleton DB | `DatabaseConfig.getInstance()` | Database connection pool |
| Design | Factory Pattern | `PaymentFactory.createPaymentMethod()` | Create payment objects |
| Design | Strategy Pattern | `PaymentMethod` interface + implementations | Polymorphic payment handling |
| Test | TC-01 (Cart Add) | `CartServiceTest.testAddItemToEmptyCart()` | ✓ PASS |
| Test | TC-02 (Qty Update) | `CartServiceTest.testAddDuplicateItemUpdatesQuantity()` | ✓ PASS |
| Test | TC-03 (Multiple Items) | `CartServiceTest.testAddMultipleItems()` | ✓ PASS |
| Test | TC-04 (Subtotal) | `CartServiceTest.testCalculateSubtotal()` | ✓ PASS |
| Test | TC-05 (Remove Item) | `CartServiceTest.testRemoveItem()` | ✓ PASS |
| Test | TC-06 (Update Qty) | `CartServiceTest.testUpdateItemQuantity()` | ✓ PASS |
| Test | TC-11 (Cash Exact) | `PaymentServiceTest.testCashPaymentExactAmount()` | ✓ PASS |
| Test | TC-12 (Cash Change) | `PaymentServiceTest.testCashPaymentWithChange()` | ✓ PASS |
| Test | TC-13 (Insufficient) | `PaymentServiceTest.testCashPaymentInsufficientMoney()` | ✓ PASS |

---

## 9. Pembagian Kerja & Kontribusi

**Model Individu (Kelompok = 1 orang):**
- Implementasi semua komponen sendiri dengan commit terstruktur

| Komponen | Commit | Deskripsi |
|----------|--------|-----------|
| Model Classes | `week15: init model classes` | Product, User, CartItem, Transaction |
| Exception | `week15: add custom exceptions` | ValidationException, OutOfStockException, AuthenticationException |
| DAO Layer | `week15: implement dao layer with jdbc` | ProductDAO/Impl, UserDAO/Impl, DatabaseConfig |
| Service Layer | `week15: implement business logic services` | ProductService, CartService, AuthService, PaymentService |
| Controller Layer | `week15: implement controllers` | AuthController, ProductController, CartController |
| View/JavaFX | `week15: build javafx gui` | AppJavaFX dengan login, admin dashboard, kasir dashboard |
| Testing | `week15: add unit tests` | CartServiceTest, PaymentServiceTest, ProductServiceTest |
| Documentation | `week15: complete documentation` | laporan.md, design docs |

---

## 10. Kendala & Solusi

### Kendala 1: Database Connection Management
**Masalah**: Saat multiple concurrent access, perlu connection pooling.
**Solusi**: Implementasi Singleton pattern di `DatabaseConfig` + use PreparedStatement untuk query safety.

### Kendala 2: UI Responsiveness pada Operasi Database
**Masalah**: UI freeze ketika query database berjalan lama.
**Solusi**: (Optional untuk week 15) Dapat diimplementasikan dengan Thread/Task di JavaFX untuk background query.

### Kendala 3: Payment Method Extensibility
**Masalah**: Menambah metode pembayaran baru harus modify kode yang sudah ada.
**Solusi**: Implementasi Strategy Pattern + Factory Pattern membuat mudah add metode baru (QRISPayment, BankTransfer, dll) tanpa ubah kode inti.

### Kendala 4: Stock Update Saat Multiple Transaksi Simultan
**Masalah**: Race condition jika 2 transaksi update stok bersamaan.
**Solusi**: (Future) Implementasi database transaction lock atau optimistic locking di DAO layer.

### Kendala 5: Password Plaintext (Security Issue)
**Masalah**: Password disimpan plaintext di database.
**Solusi**: (Future) Gunakan hashing (bcrypt/SHA-256) sebelum simpan ke DB.

---

## 11. Kesimpulan

Proyek Minggu 15 telah berhasil mengimplementasikan **Agri-POS** sebagai sistem yang:

✅ **Terintegrasi end-to-end**: GUI JavaFX → Controller → Service → DAO → PostgreSQL
✅ **Menerapkan SOLID**: SRP, OCP, LSP, ISP, DIP diimplementasikan dengan baik
✅ **Menggunakan Design Patterns**: Factory, Strategy, Singleton
✅ **Memiliki dokumentasi lengkap**: UML, database design, test plan, architecture
✅ **Teruji dengan unit tests**: 24 unit tests dengan coverage pada logic non-UI
✅ **Dapat dikembangkan lebih lanjut**: Arsitektur memungkinkan penambahan fitur tanpa major refactoring

Sistem ini siap untuk:
- Diuji oleh tim QA
- Dikembangkan dengan fitur tambahan
- Dideploy ke production (dengan security hardening)

---

## 12. Panduan Menjalankan Aplikasi

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL 12+
- JavaFX 21

### Database Setup
```bash
# Create database
createdb agripos_db

# Database akan auto-initialize saat aplikasi pertama kali run
```

### Compile & Run
```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run aplikasi
mvn clean javafx:run
```

### Login Credentials
```
Admin:
  Username: admin01
  Password: admin123

Kasir:
  Username: kasir01
  Password: kasir123
```

---

## 13. Lampiran & Referensi

### File Struktur
```
week15-proyek-kelompok/
├── src/
│   ├── main/java/com/upb/agripos/
│   │   ├── AppJavaFX.java
│   │   ├── model/
│   │   │   ├── Product.java
│   │   │   ├── User.java
│   │   │   ├── CartItem.java
│   │   │   └── Transaction.java
│   │   ├── exception/
│   │   │   ├── ValidationException.java
│   │   │   ├── OutOfStockException.java
│   │   │   └── AuthenticationException.java
│   │   ├── dao/
│   │   │   ├── DatabaseConfig.java
│   │   │   ├── ProductDAO.java & Impl
│   │   │   └── UserDAO.java & Impl
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── CartService.java
│   │   │   ├── AuthService.java
│   │   │   ├── ReceiptService.java
│   │   │   ├── PaymentMethod.java (interface)
│   │   │   ├── CashPayment.java
│   │   │   ├── EWalletPayment.java
│   │   │   └── PaymentFactory.java
│   │   └── controller/
│   │       ├── AuthController.java
│   │       ├── ProductController.java
│   │       └── CartController.java
│   └── test/java/com/upb/agripos/
│       ├── CartServiceTest.java
│       ├── PaymentServiceTest.java
│       └── ProductServiceTest.java
├── pom.xml
└── laporan.md
```

### Referensi
- Modul Bab 6 (UML + SOLID)
- Modul Bab 10 (Testing)
- Modul Bab 11 (DAO + Database)
- Modul Bab 12-14 (JavaFX GUI)

---

**Selesai** - Minggu 15 Proyek Kelompok  
**Tanggal**: 25 Januari 2026  
**Status**: ✅ SELESAI
