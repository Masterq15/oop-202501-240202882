# Fitur E-Wallet Payment dengan QRIS Scan - AGRI-POS

## Overview
Implementasi fitur pembayaran E-Wallet yang berbeda dari Tunai (cash payment), dengan integrasi QRIS scan, notifikasi sukses, dan reference code.

## Fitur E-Wallet

### 1. **QRIS Scan Dialog**
Ketika customer memilih "E-Wallet" sebagai metode pembayaran:
- Dialog muncul dengan label "Pembayaran E-Wallet"
- Menampilkan nominal pembayaran
- Simulasi QRIS code untuk di-scan
- Button: "Pembayaran Berhasil" atau "Batal"

**Kode:**
```java
private String processEWalletPayment(double total)
```
- Membuat Alert dengan Title "Pembayaran E-Wallet"
- Menampilkan VBox berisi:
  - Label nominal (Rp format)
  - Simulasi QRIS code
  - Instruksi untuk customer

### 2. **Payment Success Notification**
Setelah memilih "Pembayaran Berhasil" pada QRIS dialog:
- Alert muncul dengan Title "E-Wallet Berhasil"
- Header: "Pembayaran E-Wallet Berhasil"
- Menampilkan: nominal dan reference number
- Reference number format: `EW` + `yyyyMMddHHmmss` (contoh: EW20260116193345)

### 3. **Receipt Reference Code**
Struk pembayaran untuk E-Wallet akan menampilkan:
- Metode Pembayaran: **E-Wallet**
- **Ref: [Reference Number]** (contoh: Ref: EW20260116193345)
- Jumlah Pembayaran (sama dengan total, tidak ada kembalian untuk E-Wallet)

**Perbedaan Tunai vs E-Wallet di Struk:**

**Tunai:**
```
Metode Pembayaran: Tunai
Jumlah Pembayaran: Rp 50,000
Kembalian: Rp 10,000
```

**E-Wallet:**
```
Metode Pembayaran: E-Wallet
Ref: EW20260116193345
Jumlah Pembayaran: Rp 50,000
```

## Workflow Pembayaran E-Wallet

```
1. Customer memilih produk dan checkout
2. Pilih "E-Wallet" dari dropdown metode pembayaran
3. Input nominal pembayaran (sama dengan total)
4. Klik "Bayar"
5. QRIS Scan Dialog muncul
   ↓
6a. Customer scan QRIS → "Pembayaran Berhasil"
    ↓
    Success Notification muncul
    ↓
    Struk ditampilkan dengan Ref Code
   
6b. Batal → Kembali ke form checkout
```

## Perbedaan dengan Tunai

| Aspek | Tunai | E-Wallet |
|-------|-------|----------|
| Input Nominal | Bisa lebih dari total (untuk kembalian) | Harus sama dengan total |
| QRIS Scan | ✗ | ✓ Wajib |
| Success Notification | ✗ | ✓ Ya |
| Reference Number | ✗ | ✓ Otomatis generate |
| Struk Kembalian | ✓ Ditampilkan | ✗ Tidak ada |
| Struk Reference | ✗ | ✓ Ditampilkan |

## Implementasi Teknis

### File Modified:
- `src/main/java/com/upb/agripos/view/PosView.java`

### Method:
1. **handleCheckout()** - Main checkout handler
   - Cek metode pembayaran
   - Jika E-Wallet: panggil `processEWalletPayment()`
   - Generate receipt dengan reference code untuk E-Wallet

2. **processEWalletPayment(double total)** - E-Wallet payment processor
   - Tampilkan QRIS dialog
   - Generate reference number jika sukses
   - Tampilkan success notification
   - Return reference number atau null jika batal

### Import Tambahan:
```java
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
```

## User Experience

### QRIS Dialog
```
┌─────────────────────────────────────┐
│ Pembayaran E-Wallet                 │
├─────────────────────────────────────┤
│                                     │
│ 📱 SCAN QRIS                        │
│ Nominal: Rp 50,000                  │
│ ─────────────────────────────────────│
│ Tunjukkan QRIS code ke customer     │
│ █████████████████████████████████   │
│ █  Simulate QRIS QR Code Scan  █   │
│ █████████████████████████████████   │
│                                     │
│ [✓ Pembayaran Berhasil] [✗ Batal] │
└─────────────────────────────────────┘
```

### Success Notification
```
┌─────────────────────────────────────┐
│ E-Wallet Berhasil                   │
├─────────────────────────────────────┤
│ Pembayaran E-Wallet Berhasil        │
│                                     │
│ ✓ Pembayaran sebesar Rp 50,000     │
│   telah diterima                    │
│                                     │
│ Ref: EW20260116193345              │
│                                     │
│              [   OK   ]              │
└─────────────────────────────────────┘
```

## Testing Checklist

- [x] Kompilasi berhasil (BUILD SUCCESS)
- [x] Aplikasi berjalan tanpa error
- [x] Login berhasil sebagai Kasir
- [x] Pilih metode "Tunai" → Struk menampilkan kembalian
- [ ] Pilih metode "E-Wallet" → QRIS dialog muncul
- [ ] Klik "Pembayaran Berhasil" → Success notification muncul
- [ ] Success notification menampilkan reference code
- [ ] Struk menampilkan "Ref: EW..." untuk E-Wallet
- [ ] Pilih "Batal" pada QRIS dialog → Kembali ke form checkout

## Future Enhancements

1. **Integrasi QR Code Library** - Gunakan library seperti `zxing` untuk generate real QRIS code
2. **Payment Gateway Integration** - Integrasi dengan provider E-Wallet (OVO, GoPay, Dana, etc)
3. **Transaction Logging** - Log setiap transaksi E-Wallet dengan reference code ke database
4. **Timeout Handler** - Auto-cancel jika customer tidak scan QRIS dalam waktu tertentu
5. **Multiple E-Wallet Support** - Pilihan provider E-Wallet sebelum scan QRIS
6. **Payment Status Verification** - Polling/webhook untuk verifikasi status pembayaran dari provider

## Notes

- Reference code format: `EW` + timestamp `yyyyMMddHHmmss`
- QRIS dialog menggunakan Alert dengan custom ButtonType
- Success notification muncul sebelum receipt ditampilkan
- User dapat membatalkan transaksi E-Wallet di QRIS dialog
