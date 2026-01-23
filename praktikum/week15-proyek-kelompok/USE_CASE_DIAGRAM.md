# Use Case Diagram - Agri-POS System

## 📊 Use Case Overview

```
                          ┌─────────────────────────────────────┐
                          │      AGRI-POS SYSTEM                │
                          │  (Point of Sale Pertanian)          │
                          └─────────────────────────────────────┘
                                      │
                ┌───────────────────────┴───────────────────────┐
                │                                               │
           ┌────────┐                                       ┌────────┐
           │ KASIR  │                                       │ ADMIN  │
           └────────┘                                       └────────┘
                │                                               │
    ┌───────────┼────────────────────┬──────────────────────────┼───────────────────┐
    │           │                    │                          │                   │
    │           │                    │                          │                   │
    ▼           ▼                    ▼                          ▼                   ▼
┌─────────┐ ┌──────────┐      ┌─────────────┐         ┌────────────┐        ┌──────────┐
│  Login  │ │View Prod │      │Checkout &   │         │   Manage   │        │  View    │
│  Users │ │Available │      │   Payment   │         │   Product  │        │ Reports  │
└─────────┘ └──────────┘      └─────────────┘         └────────────┘        └──────────┘
    │           │                    │                          │                   │
    │           ▼                    ▼                          ▼                   ▼
    │      ┌──────────────┐     ┌─────────────────┐     ┌───────────────┐  ┌──────────────┐
    │      │Select Product│     │Apply Discount   │     │   CRUD Prod   │  │Sales Report  │
    │      └──────────────┘     └─────────────────┘     └───────────────┘  └──────────────┘
    │           │                    │                          │                   │
    │           ▼                    ▼                          ▼                   ▼
    │      ┌──────────────┐     ┌─────────────────┐     ┌───────────────┐  ┌──────────────┐
    │      │  Add to Cart │     │ Process Payment │     │ Monitor Stock │  │Audit Report  │
    │      └──────────────┘     └─────────────────┘     └───────────────┘  └──────────────┘
    │           │                    │
    │           ▼                    ▼
    │      ┌──────────────┐     ┌─────────────────┐
    │      │Review Cart   │     │Generate Receipt │
    │      └──────────────┘     └─────────────────┘
    │                                 │
    └─────────────────────────────────┘
```

---

## 📋 Detailed Use Case Description

### 1. **UC-001: Login Pengguna**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-001 |
| **Use Case Name** | Login User |
| **Actor** | Kasir, Admin |
| **Precondition** | Sistem sudah running, user memiliki kredensial valid |
| **Description** | User login ke sistem dengan username dan password |
| **Flow** | 1. User membuka aplikasi<br>2. Masukkan username dan password<br>3. Sistem validasi kredensial<br>4. Jika valid → login sukses, redirect ke dashboard<br>5. Jika invalid → tampilkan error message |
| **Postcondition** | User berhasil login dan dapat mengakses fitur sesuai role-nya |
| **Exception** | Invalid credentials, Database connection error |

---

### 2. **UC-002: View Produk Tersedia**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-002 |
| **Use Case Name** | View Available Products |
| **Actor** | Kasir, Admin |
| **Precondition** | User sudah login, database memiliki data produk |
| **Description** | User dapat melihat daftar produk yang tersedia dengan filter kategori |
| **Flow** | 1. User membuka halaman produk<br>2. Sistem load daftar produk dari database<br>3. User dapat filter berdasarkan kategori<br>4. Tampilkan detail: nama, harga, stok, status |
| **Postcondition** | User dapat melihat informasi lengkap semua produk |
| **Exception** | Database query error, No data available |

---

### 3. **UC-003: Select Produk & Add to Cart**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-003 |
| **Use Case Name** | Select Product and Add to Shopping Cart |
| **Actor** | Kasir |
| **Precondition** | UC-002 completed, produk ada di sistem |
| **Description** | Kasir memilih produk dan menambahkan ke shopping cart dengan kuantitas |
| **Flow** | 1. Kasir klik produk dari list<br>2. Input quantity (default=1)<br>3. Sistem validasi stok tersedia<br>4. Jika stok cukup → add to cart<br>5. Tampilkan notification "Added to cart"<br>6. User dapat lanjut pilih produk lain |
| **Postcondition** | Produk tersimpan di shopping cart dengan quantity yang benar |
| **Exception** | Insufficient stock, Invalid quantity input |

---

### 4. **UC-004: Review Cart & Modifikasi**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-004 |
| **Use Case Name** | Review Cart and Modify Items |
| **Actor** | Kasir |
| **Precondition** | UC-003 completed, ada minimal 1 item di cart |
| **Description** | Kasir dapat melihat ringkasan cart dan melakukan modifikasi |
| **Flow** | 1. Kasir buka shopping cart<br>2. Lihat semua items, subtotal, total<br>3. Opsi: increase/decrease quantity atau remove item<br>4. Sistem recalculate total otomatis<br>5. Kasir confirm cart atau lanjut shopping |
| **Postcondition** | Cart sudah divalidasi dan siap untuk checkout |
| **Exception** | Empty cart, Item no longer available |

---

### 5. **UC-005: Apply Discount**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-005 |
| **Use Case Name** | Apply Discount/Promotion |
| **Actor** | Kasir |
| **Precondition** | UC-004 completed, promo tersedia di sistem |
| **Description** | Kasir dapat apply diskon (fixed atau percentage) ke transaksi |
| **Flow** | 1. Kasir input promo code atau select dari list<br>2. Sistem validasi promo code (valid, tidak expired)<br>3. Jika valid → hitung discount amount<br>4. Tampilkan final price setelah diskon<br>5. Jika invalid → tampilkan error |
| **Postcondition** | Diskon berhasil diapply ke cart |
| **Exception** | Invalid promo code, Promo expired, Promo not applicable |

---

### 6. **UC-006: Checkout & Payment Processing**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-006 |
| **Use Case Name** | Checkout and Process Payment |
| **Actor** | Kasir, Payment System |
| **Precondition** | UC-005 completed, cart tidak kosong |
| **Description** | Kasir melakukan checkout dan memproses pembayaran |
| **Flow** | 1. Kasir review final amount<br>2. Pilih payment method (Cash / E-Wallet)<br>3. Jika Cash → input nominal uang<br>4. Jika E-Wallet → validasi saldo e-wallet<br>5. Sistem proses transaksi<br>6. Generate receipt<br>7. Update stok di database |
| **Postcondition** | Transaksi berhasil, stok updated, receipt generated |
| **Exception** | Insufficient balance, Payment failed, Connection error |

---

### 7. **UC-007: Generate & Print Receipt**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-007 |
| **Use Case Name** | Generate and Print Receipt |
| **Actor** | Kasir, Printer |
| **Precondition** | UC-006 completed, payment successful |
| **Description** | Sistem generate receipt dan kasir dapat print atau email |
| **Flow** | 1. Setelah payment sukses, sistem generate receipt<br>2. Receipt berisi: transaction ID, items, amount, payment method<br>3. Kasir dapat print ke printer atau share via email<br>4. Receipt disimpan di database untuk audit trail |
| **Postcondition** | Receipt tersimpan dan dapat diakses anytime |
| **Exception** | Printer not available, Email service error |

---

### 8. **UC-008: Manage Products (Admin)**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-008 |
| **Use Case Name** | Manage Products (CRUD) |
| **Actor** | Admin |
| **Precondition** | Admin sudah login |
| **Description** | Admin dapat Create, Read, Update, Delete produk |
| **Flow** | **Create:** Admin input nama, harga, kategori, stok → save<br>**Read:** Admin view semua produk dalam table<br>**Update:** Admin edit detail produk → save changes<br>**Delete:** Admin hapus produk dari sistem |
| **Postcondition** | Database produk ter-update dengan perubahan terbaru |
| **Exception** | Duplicate product code, Invalid input, Database error |

---

### 9. **UC-009: Monitor Stock & Alerts**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-009 |
| **Use Case Name** | Monitor Stock and Receive Alerts |
| **Actor** | Admin |
| **Precondition** | UC-008 completed, produk ada di sistem |
| **Description** | Admin dapat monitor stok real-time dan menerima alert |
| **Flow** | 1. Admin buka stock monitoring page<br>2. Sistem tampilkan status semua produk: NORMAL, LOW_STOCK, DISCONTINUED<br>3. Jika stok < threshold → tampilkan alert<br>4. Admin dapat set custom threshold per kategori |
| **Postcondition** | Admin aware akan status stok dan dapat reorder tepat waktu |
| **Exception** | Database connection error, Invalid threshold value |

---

### 10. **UC-010: View Sales Reports**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-010 |
| **Use Case Name** | View Sales Reports and Analytics |
| **Actor** | Admin |
| **Precondition** | Ada minimal 1 transaksi di sistem |
| **Description** | Admin dapat view laporan penjualan dengan berbagai filter |
| **Flow** | 1. Admin buka Reports menu<br>2. Filter berdasarkan: date range, payment method, kategori<br>3. Sistem tampilkan: total sales, top products, revenue by category<br>4. Admin dapat export report ke CSV/PDF |
| **Postcondition** | Admin memiliki insights untuk decision making |
| **Exception** | No data in date range, Export service error |

---

### 11. **UC-011: View Audit Logs**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-011 |
| **Use Case Name** | View Audit Logs and User Activities |
| **Actor** | Admin |
| **Precondition** | Ada user activities di sistem |
| **Description** | Admin dapat audit setiap user action untuk compliance |
| **Flow** | 1. Admin buka Audit Logs menu<br>2. Filter by: user, action type, date range<br>3. Sistem tampilkan: username, action, timestamp, details<br>4. Admin dapat export audit trail untuk backup |
| **Postcondition** | Semua user activities tercatat untuk audit trail |
| **Exception** | No logs in date range, Database error |

---

### 12. **UC-012: Logout**

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-012 |
| **Use Case Name** | Logout User |
| **Actor** | Kasir, Admin |
| **Precondition** | User sudah login |
| **Description** | User logout dari sistem dengan aman |
| **Flow** | 1. User klik logout<br>2. Sistem end session<br>3. Redirect ke login page<br>4. Log action di audit trail |
| **Postcondition** | User berhasil logout, session terminated |
| **Exception** | None |

---

## 🔄 Interaction Flows

### Flow 1: Complete Transaction (Kasir)
```
Login → View Products → Add to Cart → Review Cart → 
Apply Discount → Checkout → Process Payment → 
Generate Receipt → Logout
```

### Flow 2: Product Management (Admin)
```
Login → View Products → Create/Edit/Delete Product → 
Monitor Stock → View Reports → View Audit Logs → Logout
```

---

## 📊 System Data Flow

```
┌──────────┐
│ USER     │
└────┬─────┘
     │ Login credentials
     ▼
┌──────────────────┐
│ AUTH SERVICE     │──────────► [DB] User accounts
└────┬─────────────┘
     │ User info
     ▼
┌──────────────────────┐
│ DASHBOARD            │
├──────────────────────┤
│ Kasir: PosView       │
│ Admin: AdminDashboard│
└────┬─────────────────┘
     │
     ├──────────────────┬────────────────┬─────────────────┐
     │                  │                │                 │
     ▼                  ▼                ▼                 ▼
 PRODUCT          TRANSACTION         AUDIT              REPORTS
 SERVICE          SERVICE             SERVICE            SERVICE
     │                  │                │                 │
     └──────────────────┴────────────────┴─────────────────┘
                        │
                        ▼
                   DATABASE
            (PostgreSQL + DAO Layer)
```

---

## 🎯 Key Features Summary

| No | Feature | Actor | Status |
|---|---------|-------|--------|
| 1 | Multi-user Authentication | Both | ✅ Implemented |
| 2 | Product Catalog & Search | Both | ✅ Implemented |
| 3 | Shopping Cart Management | Kasir | ✅ Implemented |
| 4 | Flexible Discount System | Kasir | ✅ Implemented |
| 5 | Multi-Payment Methods | Kasir | ✅ Implemented |
| 6 | Real-time Stock Tracking | Both | ✅ Implemented |
| 7 | Audit Trail & Compliance | Admin | ✅ Implemented |
| 8 | Sales Reports & Analytics | Admin | ✅ Implemented |
| 9 | Product CRUD Management | Admin | ✅ Implemented |
| 10 | Receipt Generation | Kasir | ✅ Implemented |

---

## 🔐 Non-Functional Requirements

| Requirement | Description |
|------------|-------------|
| **Performance** | Transaksi harus diproses < 3 detik |
| **Availability** | Sistem available 24/7 dengan uptime 99.5% |
| **Security** | Password terenkripsi, role-based access control |
| **Scalability** | Support up to 100 concurrent users |
| **Auditability** | All transactions logged dengan timestamp |
| **Usability** | User-friendly GUI dengan JavaFX |

---

**Generated:** 23 January 2026  
**Project:** Agri-POS Week 15 - OOP Final Project
