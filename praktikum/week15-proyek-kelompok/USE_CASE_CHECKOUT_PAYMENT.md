# Use Case: Checkout & Payment Processing
## Agri-POS Week 15 - Core Transaction Feature

---

## 📌 Use Case Header

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-006 |
| **Use Case Name** | Checkout and Process Payment |
| **Category** | Core Business Process |
| **Priority** | CRITICAL (Main business function) |
| **Version** | 1.0 |
| **Created Date** | 23 January 2026 |
| **Last Updated** | 23 January 2026 |

---

## 👥 Actors & Stakeholders

### Primary Actor
- **Kasir (Cashier)** - Melakukan transaksi penjualan kepada pelanggan

### Secondary Actors
- **Payment Gateway** - Sistem validasi pembayaran e-wallet
- **Database System** - Menyimpan data transaksi dan update stok
- **Audit System** - Mencatat setiap aktivitas untuk compliance

### Stakeholder
- **Pemilik Toko** - Butuh laporan akurat dan audit trail lengkap
- **Customer** - Ingin transaksi cepat dan receipt yang jelas

---

## 📋 Preconditions

Sebelum use case ini dapat dijalankan, kondisi berikut harus terpenuhi:

1. ✅ Kasir sudah login ke sistem
2. ✅ Minimal 1 item ada di shopping cart
3. ✅ Semua item di cart memiliki stok yang cukup
4. ✅ Database connection aktif dan stabil
5. ✅ Payment gateway service tersedia (jika memilih e-wallet)
6. ✅ Printer tersedia (opsional, untuk print receipt)

---

## 🎯 Main Success Scenario (Happy Path)

### Step-by-Step Flow

```
Kasir                    System                  Database/Payment
  │                        │                          │
  ├─ View Cart ─────────→  │                          │
  │                        ├─ Load cart items ────→  │
  │                        │← Return items ────────┤
  │  ← Display cart ───────┤
  │                        │
  ├─ Review Total ────────→ │
  │                        ├─ Calculate subtotal  │
  │                        ├─ Apply discount      │
  │                        ├─ Calculate tax       │
  │  ← Final Total ────────┤
  │                        │
  ├─ Select Payment ──────→ │
  │  (Cash/EWallet)        ├─ Validate method    │
  │                        │
  ├─ Process Payment ─────→ │
  │                        ├─ Validate payment ──→  [Payment Gateway]
  │                        │← Response ──────────────┤
  │                        │
  │  ← Payment Success ────┤
  │                        ├─ Create Transaction  │
  │                        ├─ Update stok ───────→  │
  │                        ├─ Save audit log ───→  │
  │                        │
  ├─ Generate Receipt ────→ │
  │                        ├─ Create receipt ────→  │
  │  ← Receipt ────────────┤
  │
  └─ Transaction Complete (Return to POS)
```

### Detailed Steps

#### **Phase 1: Review Cart & Calculate Total (Steps 1-4)**

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 1 | Kasir | Membuka shopping cart | Sistem load semua items dari cart, display: product name, quantity, price per item, subtotal |
| 2 | System | Validate cart items | Cek apakah semua items masih tersedia dan stok cukup |
| 3 | Kasir | Review total | Kasir lihat subtotal, discount (jika ada), tax, dan total amount |
| 4 | System | Calculate totals | Subtotal = sum(qty × price), Tax = subtotal × tax_rate, Total = subtotal - discount + tax |

**Output:** Cart summary siap untuk checkout

---

#### **Phase 2: Select Payment Method (Steps 5-6)**

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 5 | Kasir | Pilih payment method | Kasir click tombol "Checkout" dan select payment type |
| 6 | System | Display payment options | Tampilkan 2 pilihan: **Cash Payment** atau **E-Wallet Payment** dengan detail |

**Output:** Payment method selection form

---

#### **Phase 3: Cash Payment Processing (Steps 7-9)** - *If Cash Selected*

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 7 | Kasir | Input nominal uang | Kasir input jumlah uang tunai yang diterima |
| 8 | System | Validate nominal | Cek apakah nominal >= total amount |
| 9 | System | Calculate change | Kembalian = nominal - total amount, display kembalian ke kasir |

**Output:** Payment validated, kembalian dihitung

---

#### **Phase 4: E-Wallet Payment Processing (Steps 7-9)** - *If E-Wallet Selected*

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 7 | Kasir | Input e-wallet reference | Kasir input nomor referensi e-wallet / scan QR code |
| 8 | System | Validate e-wallet | Kirim request ke payment gateway untuk validasi |
| 9 | Payment Gateway | Process payment | Validasi saldo e-wallet, deduct amount, return status |

**Output:** Payment validated oleh payment gateway

---

#### **Phase 5: Transaction Processing (Steps 10-13)**

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 10 | System | Create transaction record | Insert transaction ke database dengan status "PENDING" |
| 11 | System | Update stock | Untuk setiap item di cart, kurangi stok di database |
| 12 | System | Save payment details | Simpan payment method, amount, timestamp, dan payment status |
| 13 | System | Log audit trail | Create audit log entry: user=kasir, action=TRANSACTION_COMPLETED, timestamp=now |

**Output:** Transaction berhasil disimpan ke database

---

#### **Phase 6: Receipt Generation & Completion (Steps 14-16)**

| Step | Actor | Action | System Response |
|------|-------|--------|-----------------|
| 14 | System | Generate receipt | Buat receipt dokumen dengan format: Transaction ID, items detail, totals, payment method, timestamp |
| 15 | Kasir | Print/save receipt | Kasir dapat print receipt atau save sebagai PDF/email |
| 16 | System | Return to POS | Clear shopping cart, update dashboard (menampilkan transaction success), ready untuk transaksi berikutnya |

**Output:** Receipt generated dan tersimpan di database, transaksi complete

---

## 🔄 Alternative Scenarios

### Alternative Flow 1: Cancel Transaction (Steps 1-5)
**Condition:** Kasir cancel transaksi sebelum payment

| Step | Action | Result |
|------|--------|--------|
| 1 | Kasir click tombol "Cancel Checkout" | - |
| 2 | Sistem tampilkan confirmation dialog "Are you sure?" | - |
| 3 | Kasir konfirmasi cancel | Shopping cart tetap intact, return ke POS |
| 4 | System clear any temporary payment data | No transaction created |

**Outcome:** Transaksi dibatalkan, items tetap di cart untuk transaksi berikutnya

---

### Alternative Flow 2: Insufficient Stock (After Step 6)
**Condition:** Ketika sistem cek stok, ditemukan stok tidak cukup

| Step | Action | Result |
|------|--------|--------|
| 1 | System validate stock sebelum transaksi | Stok tidak cukup ditemukan |
| 2 | System tampilkan alert: "Product [X] insufficient stock. Available: [Y], Requested: [Z]" | - |
| 3 | Kasir dapat: (a) edit quantity, atau (b) remove item dari cart | Cart updated atau item dihapus |
| 4 | Return ke Phase 1 untuk recalculate total | - |

**Outcome:** Transaksi tidak lanjut sampai stok cukup

---

### Alternative Flow 3: Payment Validation Failed (Step 8/9)
**Condition:** Pembayaran validation gagal

| Step | Action | Result |
|------|--------|--------|
| 1 | System validasi payment (cash nominalnya kurang atau e-wallet saldo tidak cukup) | Validation error |
| 2 | System tampilkan error message: | - |
|    | - **Cash:** "Uang tidak cukup. Kurang Rp [X]" | - |
|    | - **E-Wallet:** "Saldo tidak cukup / Payment gateway error" | - |
| 3 | Kasir dapat: (a) input ulang uang tunai, atau (b) cancel transaksi | Retry atau cancel |

**Outcome:** Transaksi di-hold sampai payment valid atau cancelled

---

### Alternative Flow 4: Database Error (Step 10-11)
**Condition:** Terjadi error saat menyimpan transaction ke database

| Step | Action | Result |
|------|--------|--------|
| 1 | System attempt save transaction → Database error | Transaction creation failed |
| 2 | System rollback all changes (undo stok update jika ada) | No partial data |
| 3 | System tampilkan error: "Transaction failed. Please try again." | - |
| 4 | Kasir dapat retry atau report issue ke IT support | Shopping cart intact |

**Outcome:** Transaction tidak tersimpan, retry mechanism available

---

### Alternative Flow 5: Partial Payment (E-Wallet + Cash) - *Future Enhancement*
**Condition:** Customer ingin membayar dengan kombinasi e-wallet + cash (future)

| Step | Action | Result |
|------|--------|--------|
| 1 | Kasir select "Multiple Payment Methods" | - |
| 2 | System tampilkan option: split payment | - |
| 3 | Kasir input: e-wallet amount + cash amount | Total = e-wallet + cash = final total |
| 4 | Validasi both payment methods | Both must valid |
| 5 | Process both payments | Create 1 transaction with 2 payment details |

**Outcome:** Transaksi dengan multiple payment methods (future feature)

---

## ❌ Exception Handling

### Exception 1: Connection Lost During Transaction
**Trigger:** Database atau payment gateway koneksi putus di tengah transaksi

**Handling:**
```
1. System detect connection loss
2. Trigger automatic retry mechanism (max 3x dengan 2s interval)
3. Jika retry gagal:
   - Mark transaction as "PENDING_VERIFICATION"
   - Alert kasir: "Connection lost. Admin akan verify transaksi"
   - Don't update stok sampai verified
4. Kasir dapat lanjut transaksi lain atau call admin
5. Admin verify via dashboard dan confirm transaksi
```

---

### Exception 2: Invalid Payment Response
**Trigger:** Payment gateway return response yang tidak valid/corrupt

**Handling:**
```
1. System catch invalid response
2. Log error dengan full details ke database
3. Transaction marked as "VERIFICATION_REQUIRED"
4. Alert admin untuk manual verification
5. Kasir: "Payment status unclear. Please contact admin"
6. Admin verify dan confirm transaksi dari dashboard
```

---

### Exception 3: Duplicate Transaction Attempt
**Trigger:** Kasir click "Process Payment" 2x dalam 5 detik

**Handling:**
```
1. System detect duplicate transaction attempt
2. Ignore 2nd request, show message "Processing..."
3. Return result dari 1st transaction
4. Prevent duplicate charges
```

---

### Exception 4: Receipt Generation Failed
**Trigger:** Printer tidak available atau template error

**Handling:**
```
1. Transaction sudah berhasil disimpan
2. Only receipt generation yang error
3. System tampilkan: "Transaction successful. Receipt generation failed."
4. Kasir dapat:
   - Retry print receipt nanti
   - Send receipt via email
   - View receipt dari history
5. Transaction tetap valid dan recorded
```

---

### Exception 5: Stok Mismatch During Update
**Trigger:** Stok di database berbeda dengan expected

**Handling:**
```
1. System detect mismatch saat update stok
2. Rollback transaction
3. Alert kasir: "Stock mismatch. Please refresh product list"
4. Log incident untuk audit
5. Admin investigate discrepancy
6. Kasir retry transaksi atau report ke admin
```

---

## 📊 Data Flow Diagram

```
┌─────────────────┐
│  SHOPPING CART  │
│   [CartItem]    │
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│   CALCULATE TOTALS       │
│  - Subtotal              │
│  - Tax                   │
│  - Discount (if any)     │
│  - Final Total           │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────────┐
│  SELECT PAYMENT METHOD       │
│  ├─ Cash Payment             │
│  └─ E-Wallet Payment         │
└────────┬─────────────────────┘
         │
         ├─────────────────────────┬──────────────────────┐
         │                         │                      │
         ▼                         ▼                      ▼
    ┌────────┐           ┌──────────────┐      ┌─────────────────┐
    │  CASH  │           │   E-WALLET   │      │  OTHER METHOD   │
    │VALIDATE│           │   VALIDATE   │      │   (Future)      │
    └────┬───┘           └──────┬───────┘      └────────┬────────┘
         │                      │                       │
         └──────────┬───────────┴───────────────────────┘
                    │
                    ▼
         ┌────────────────────┐
         │ PAYMENT CONFIRMED  │
         └──────────┬─────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌─────────┐   ┌──────────┐   ┌─────────────┐
│ CREATE  │   │ UPDATE   │   │ SAVE AUDIT  │
│TRANSACTION  │   │ STOK     │   │ LOG         │
└─────────┘   └──────────┘   └─────────────┘
    │               │               │
    └───────────────┼───────────────┘
                    │
                    ▼
         ┌────────────────────┐
         │ GENERATE RECEIPT   │
         └──────────┬─────────┘
                    │
                    ▼
         ┌────────────────────┐
         │  TRANSACTION DONE  │
         │  Ready for Next TX │
         └────────────────────┘
```

---

## 🔒 Business Rules & Validation

### Payment Validation Rules

```
╔════════════════════════════════════════════════════════════════╗
║              PAYMENT VALIDATION BUSINESS RULES                 ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║ 1. CASH PAYMENT                                                ║
║    Rule: nominal_received >= total_amount                      ║
║    Error: "Uang tidak cukup. Kurang: Rp {diff}"                ║
║    Change: kembalian = nominal_received - total_amount         ║
║                                                                ║
║ 2. E-WALLET PAYMENT                                            ║
║    Rule: e_wallet_balance >= total_amount                      ║
║    Rule: e_wallet.isActive == TRUE                             ║
║    Rule: e_wallet.isExpired == FALSE                           ║
║    Error: "Saldo e-wallet tidak cukup" / "E-wallet inactive"   ║
║    Verification: Payment gateway response status must be OK    ║
║                                                                ║
║ 3. GENERAL RULES                                               ║
║    Rule: payment_method != NULL                                ║
║    Rule: total_amount > 0                                      ║
║    Rule: cart_items.length > 0                                 ║
║    Rule: stock_available >= quantity_ordered (all items)       ║
║                                                                ║
║ 4. DUPLICATE PREVENTION                                        ║
║    Rule: 1 transaction per 5 seconds per kasir (prevent retry) ║
║    Rule: transaction_id MUST unique                            ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📋 Post-Condition (Expected Outcome)

Setelah use case berhasil completed:

✅ **Data Persisted:**
- Transaction record tersimpan di database dengan status COMPLETED
- Payment details tersimpan dengan timestamp
- Stok semua items ter-update di database
- Receipt tersimpan dan accessible

✅ **Audit Trail:**
- Audit log entry created dengan action=TRANSACTION_COMPLETED
- User=kasir, timestamp=now, transaction_id=logged
- Any error atau exception juga di-log untuk investigation

✅ **System State:**
- Shopping cart cleared untuk transaksi berikutnya
- UI updated untuk menampilkan transaction success
- Dashboard updated dengan new sales data
- POS ready untuk transaksi baru

✅ **Customer State:**
- Receipt diterima (print atau email)
- Customer dapat check saldo e-wallet (jika e-wallet payment)
- Customer meninggalkan toko dengan barang yang dibeli

---

## 🚨 Failure Scenarios (Post-Condition if Failure)

❌ **If Payment Failed:**
- No transaction created
- No stok updated
- Shopping cart intact untuk retry
- Error message displayed ke kasir
- Kasir dapat retry atau cancel

❌ **If Database Error:**
- Transaction rolled back
- Stok tidak ter-update
- Audit log created untuk investigation
- Alert ke admin untuk manual verification

---

## 📊 Performance Requirements

| Metric | Target | Tolerance |
|--------|--------|-----------|
| Total checkout time | < 3 seconds | ±500ms |
| Payment validation | < 2 seconds | ±300ms |
| Stok update | < 1 second | ±200ms |
| Receipt generation | < 1 second | ±200ms |
| Database transaction | ACID compliant | Critical |

---

## 🔐 Security Requirements

| Requirement | Implementation |
|-------------|-----------------|
| **Payment Data** | Encrypted in transit & at rest |
| **User Authentication** | Session token validation sebelum checkout |
| **Audit Trail** | Immutable log untuk compliance |
| **Error Messages** | Generic message (no sensitive data exposed) |
| **Rate Limiting** | Max 3 retry attempts per minute |
| **Transaction ID** | Unique UUID untuk prevent duplication |

---

## 📌 Related Use Cases

- **UC-001:** Login User (Prerequisite)
- **UC-002:** View Available Products (Related)
- **UC-003:** Select Product & Add to Cart (Prerequisite)
- **UC-004:** Review Cart & Modify (Prerequisite)
- **UC-005:** Apply Discount (Related)
- **UC-007:** Generate Receipt (Part of this UC)
- **UC-011:** View Audit Logs (Uses data from this UC)

---

## 📚 Supporting Documentation

- Database Schema: `schema_agripos.sql`
- API Specification: Payment Gateway Integration
- Class Diagram: `TransactionService`, `PaymentMethod`, `Transaction`, `AuditLog`
- Test Cases: `TransactionServiceImplTest.java`, `PaymentServiceImplTest.java`

---

## ✅ Acceptance Criteria

```
GIVEN: Kasir logged in dengan shopping cart yang valid
WHEN: Kasir melakukan checkout dan memilih payment method
THEN: 
  ✓ Sistem validate payment berdasarkan method yang dipilih
  ✓ Transaction record tersimpan ke database
  ✓ Stok ter-update untuk setiap item
  ✓ Audit log entry ter-create
  ✓ Receipt ter-generate dan ter-display
  ✓ Shopping cart di-clear
  ✓ System ready untuk transaksi berikutnya

AND:
  ✓ Jika payment gagal → clear cache, tampilkan error, kasir retry
  ✓ Jika database error → rollback transaction, log error, alert admin
  ✓ Jika connection lost → retry mechanism, mark as pending
  ✓ Jika stok mismatch → rollback, alert kasir, log incident
```

---

## 📝 Notes & Comments

### Development Notes:
- Implementasi menggunakan Strategy Pattern untuk payment methods
- TransactionService handle bisnis logic, DAO layer handle database
- Payment validation di-split: CashPayment vs EWalletPayment classes
- Stok update menggunakan transaction dengan ACID guarantee

### Testing Notes:
- Unit test: Test setiap validation rule
- Integration test: Test end-to-end checkout flow
- Stress test: Test concurrent transactions (multiple kasir)
- Security test: Test payment data encryption

### Future Enhancement:
- Support multiple payment methods (bank transfer, installment)
- Real-time payment gateway integration
- Refund/reversal capability untuk incomplete transactions
- Payment analytics & KPI dashboard

---

**Document Version:** 1.0  
**Last Modified:** 23 January 2026  
**Project:** Agri-POS Week 15 - OOP Final Project  
**Team:** Kelompok 1 - Sistem Point of Sale Terintegrasi  
