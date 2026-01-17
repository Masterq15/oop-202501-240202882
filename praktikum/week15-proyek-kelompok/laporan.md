# Laporan Proyek Kelompok - Week 15
## Agri-POS: Point of Sale System (Extended)

---

## 📋 IDENTITAS KELOMPOK

| Item | Keterangan |
|------|-----------|
| **Nama Kelompok** | [Nama kelompok / Tim] |
| **Jumlah Anggota** | [Jumlah orang] |

### Anggota Tim & Peran

| No | Nama | NIM | Peran | Kontribusi |
|----|------|-----|-------|------------|
| 1 | [Nama] | [NIM] | Lead + Database (Person A) | Setup DB, DAO, OFR-4 |
| 2 | [Nama] | [NIM] | Backend (Person B) | ProductService, DiscountStrategy |
| 3 | [Nama] | [NIM] | Backend (Person C) | CheckoutService, PaymentMethod, OFR-6 |
| 4 | [Nama] | [NIM] | Frontend (Person D) | UI, LoginView, FR-5 |
| 5 | [Nama] | [NIM] | QA + Docs (Person E) | Testing, Documentation |

---

## 📌 RINGKASAN SISTEM

### Latar Belakang
Agri-POS adalah sistem Point of Sale untuk pertanian yang membantu Kasir & Admin mengelola penjualan produk pertanian dengan fitur checkout, pembayaran, dan laporan.

### Tujuan Sistem
1. Memudahkan transaksi penjualan produk pertanian
2. Mengelola inventori produk (stok, kategori, harga)
3. Mendukung berbagai metode pembayaran
4. Menyediakan laporan penjualan & audit trail
5. Implementasi OOP + SOLID principles

### Scope & Fitur Utama
- **FR-1**: CRUD Produk (kategori, harga, stok)
- **FR-2**: Transaksi Penjualan (keranjang, checkout, total)
- **FR-3**: Metode Pembayaran (Tunai, E-Wallet)
- **FR-4**: Struk & Laporan Penjualan
- **FR-5**: Login & Role (Kasir, Admin)
- **OFR-2**: Diskon/Promo (Strategy Pattern)
- **OFR-4**: Inventori Lanjutan (Stok Minimum, Status)
- **OFR-6**: Audit Log

---

## 🏗️ ARSITEKTUR SISTEM

### Layering Architecture
```
┌─────────────────────────────┐
│   JavaFX GUI (View)         │  
├─────────────────────────────┤
│   Controller                │  
├─────────────────────────────┤
│   Service (Business Logic)  │  
├─────────────────────────────┤
│   DAO (Data Access)         │  
├─────────────────────────────┤
│   PostgreSQL Database       │  
└─────────────────────────────┘
```

### Design Patterns
- **Singleton**: Database Connection
- **Strategy**: PaymentMethod (FR-3), DiscountStrategy (OFR-2)
- **MVC**: View-Controller-Service pattern
- **DAO**: Database abstraction layer

---

## 📊 UML & DESIGN ARTIFACTS

**Minimal requirements:**
- ✅ Use Case Diagram (FR & OFR mapping)
- ✅ Class Diagram (models, services, dao)
- ✅ 2x Sequence Diagram (Checkout, Diskon)
- ✅ Database ERD + DDL

**Akan disediakan lengkap di `docs/` subfolder**

---

## 🗄️ DATABASE SCHEMA

### Tables
```sql
CREATE TABLE users (user_id, username, password, full_name, role);
CREATE TABLE products (product_code, name, category, price, stock, reorder_level, status);
CREATE TABLE transactions (transaction_id, user_id, transaction_date, subtotal, discount_amount, total_amount, payment_method, status);
CREATE TABLE transaction_items (item_id, transaction_id, product_code, quantity, subtotal);
CREATE TABLE audit_logs (log_id, action, entity_type, entity_id, user_id, log_timestamp, details);
```

**DDL lengkap di `sql/schema.sql`**

---

## ✅ TEST PLAN

### Manual Test Cases (Minimal 8)
1. TC-FR-1-01: Tambah Produk (Admin)
2. TC-FR-2-01: Tambah ke Keranjang
3. TC-FR-2-02: Hitung Total Keranjang
4. TC-FR-3-01: Checkout Tunai
5. TC-FR-3-02: Checkout E-Wallet
6. TC-FR-5-01: Login Kasir
7. TC-FR-5-02: Login Admin
8. TC-OFR-2-01: Diskon Applied

### Unit Testing
- Minimal 1 JUnit test berjalan
- Coverage fokus pada Service layer

---

## 🤝 KONTRIBUSI TIM

| Person | Tugas | Status |
|--------|-------|--------|
| Person A | DAO + Database | [ ] |
| Person B | ProductService + DiscountStrategy | [ ] |
| Person C | TransactionService + PaymentMethod | [ ] |
| Person D | UI + LoginView (FR-5) | [ ] |
| Person E | Testing + Documentation | [ ] |

---

## 📁 STRUKTUR PROJECT

```
week15-proyek-kelompok/
├─ src/main/java/com/upb/agripos/
│  ├─ model/
│  │  ├─ Product.java
│  │  ├─ User.java
│  │  ├─ CartItem.java
│  │  └─ Transaction.java
│  ├─ dao/
│  │  ├─ ProductDAO.java
│  │  ├─ UserDAO.java
│  │  └─ Impl classes...
│  ├─ service/
│  │  ├─ ProductService.java
│  │  ├─ PaymentMethod.java
│  │  ├─ DiscountStrategy.java
│  │  ├─ AuthService.java
│  │  ├─ TransactionService.java
│  │  └─ Impl classes...
│  ├─ controller/
│  ├─ view/
│  ├─ exception/
│  └─ AppJavaFX.java
├─ src/test/java/com/upb/agripos/
│  └─ [test classes]
├─ sql/
│  ├─ schema.sql
│  └─ seed.sql
├─ docs/
│  ├─ 01_srs.md
│  ├─ 02_arsitektur.md
│  ├─ 03_database.md
│  ├─ 04_test_plan.md
│  └─ 08_contribution.md
├─ screenshots/
└─ laporan.md
```

---

## 🎯 DELIVERABLES

### Wajib:
- ✅ Source code (src/main/java)
- ✅ UML diagrams (desain.md)
- ✅ Database schema + seed
- ✅ Test plan + test cases
- ✅ Unit test minimal 1
- ✅ Laporan lengkap
- ✅ Screenshots bukti

### Opsional:
- Dokumentasi teknis detail
- Integration test
- Performance test
- Deployment guide

---

## 📞 GIT COLLABORATION

**Branch strategy:**
```
main ← develop ← feature/[name]
```

**Commit message:**
```
feat(fr-1): tambah product crud
fix(checkout): handle null discount
test(service): add unit test for payment
docs(readme): update setup instructions
```

---

## 📝 NOTES

1. **Template siap pakai** di src/main/java - tinggal di-extend
2. **Silakan copy** dari week14 untuk yang sudah ada (CartItem, Cart, etc)
3. **Fokus** pada FR-3 (PaymentMethod) dan FR-5 (Auth) yang belum ada
4. **Kolaborasi** via Git - jangan main-main dengan file yang sama
5. **Daily standup** untuk koordinasi progress

---


