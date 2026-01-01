# Laporan Praktikum Week 7 - Collections dan Keranjang Belanja

## Identitas
**Nama**       : Risky Dimas Nugroho
**NIM**        : 240202882
**Kelas**      : 3IKRB
**Mata Kuliah**: Object Oriented Programming


---

## 1. Tujuan Praktikum
- Memahami konsep Collections Framework dalam Java (List, Map, Set)
- Mengimplementasikan keranjang belanja menggunakan ArrayList
- Mengimplementasikan keranjang belanja dengan quantity menggunakan HashMap
- Melakukan operasi dasar: tambah, hapus, dan hitung total

---

## 2. Dasar Teori

### Collections Framework
Java Collections Framework adalah arsitektur untuk menyimpan dan memanipulasi kelompok objek. Tiga struktur utama:

1. **List (ArrayList)**
   - Terurut berdasarkan urutan penambahan
   - Mengizinkan duplikat
   - Akses menggunakan index

2. **Map (HashMap)**
   - Menyimpan pasangan key-value
   - Key harus unik
   - Akses cepat berdasarkan key

3. **Set (HashSet)**
   - Tidak ada urutan
   - Tidak mengizinkan duplikat
   - Cocok untuk data unik

---

## 3. Implementasi

### 3.1 Class Product
```java
package com.upb.agripos;

public class Product {
    private final String code;
    private final String name;
    private final double price;

    public Product(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
```

### 3.2 ShoppingCart (ArrayList)
```java
package com.upb.agripos;

import java.util.ArrayList;

public class ShoppingCart {
    private final ArrayList<Product> items = new ArrayList<>();

    public void addProduct(Product p) { items.add(p); }
    public void removeProduct(Product p) { items.remove(p); }

    public double getTotal() {
        double sum = 0;
        for (Product p : items) {
            sum += p.getPrice();
        }
        return sum;
    }

    public void printCart() {
        System.out.println("Isi Keranjang:");
        for (Product p : items) {
            System.out.println("- " + p.getCode() + " " + p.getName() + " = " + p.getPrice());
        }
        System.out.println("Total: " + getTotal());
    }
}
```

### 3.3 ShoppingCartMap (HashMap) - OPSIONAL
```java
package com.upb.agripos;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCartMap {
    private final Map<Product, Integer> items = new HashMap<>();

    public void addProduct(Product p) { 
        items.put(p, items.getOrDefault(p, 0) + 1); 
    }

    public void removeProduct(Product p) {
        if (!items.containsKey(p)) return;
        int qty = items.get(p);
        if (qty > 1) items.put(p, qty - 1);
        else items.remove(p);
    }

    public double getTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void printCart() {
        System.out.println("Isi Keranjang (Map):");
        for (Map.Entry<Product, Integer> e : items.entrySet()) {
            System.out.println("- " + e.getKey().getCode() + " " + 
                               e.getKey().getName() + " x" + e.getValue());
        }
        System.out.println("Total: " + getTotal());
    }
}
```

### 3.4 MainCart
```java
package com.upb.agripos;

public class MainCart {
    public static void main(String[] args) {
        System.out.println("Hello, I am [Nama Anda]-[NIM Anda] (Week7)");

        // Buat produk
        Product p1 = new Product("P01", "Beras", 50000);
        Product p2 = new Product("P02", "Pupuk", 30000);

        // Test ArrayList
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(p1);
        cart.addProduct(p2);
        cart.printCart();

        System.out.println("\nSetelah hapus Beras:");
        cart.removeProduct(p1);
        cart.printCart();

        // Test HashMap (OPSIONAL)
        System.out.println("\n=== Test dengan Map ===");
        ShoppingCartMap cartMap = new ShoppingCartMap();
        cartMap.addProduct(p1);
        cartMap.addProduct(p1);  // Tambah beras lagi (quantity +1)
        cartMap.addProduct(p2);
        cartMap.printCart();
    }
}
```

---

## 4. Hasil Eksekusi

![Screenshot Hasil](<screenshots/Doc.week7 (1).png>)
![Screenshot Hasil](<screenshots/Doc.week7 (2).png>)

**Output yang diharapkan:**
```
Hello, I am Risky Dimas Nugroho - 240202882 (Week7)
Isi Keranjang:
- P01 Beras = 50000.0
- P02 Pupuk = 30000.0
Total: 80000.0

Setelah hapus Beras:
Isi Keranjang:
- P02 Pupuk = 30000.0
Total: 30000.0

=== Test dengan Map ===
Isi Keranjang (Map):
- P01 Beras x2
- P02 Pupuk x1
Total: 130000.0
```

---

## 5. Jawaban Quiz

### 1. Perbedaan List, Map, dan Set
- **List**: Terurut, boleh duplikat, akses dengan index
- **Map**: Pasangan key-value, key unik, akses dengan key
- **Set**: Tidak terurut, tidak boleh duplikat, untuk data unik

### 2. Mengapa ArrayList cocok untuk keranjang sederhana?
ArrayList cocok karena mudah digunakan, mempertahankan urutan penambahan, ukuran dinamis, dan cukup untuk menyimpan daftar produk sederhana tanpa perlu tracking quantity.

### 3. Bagaimana Set mencegah duplikasi?
Set menggunakan hashCode() untuk mengecek keunikan. Saat menambah elemen, jika hashCode sudah ada, elemen tidak ditambahkan.

### 4. Kapan menggunakan Map vs List?
- **Map**: Saat butuh key-value, perlu tracking quantity/frekuensi
- **List**: Saat hanya perlu daftar sederhana dan urutan penting

Contoh: Keranjang POS pakai Map (tracking quantity), riwayat transaksi pakai List (urutan penting).

---

## 6. Analisis

### Kelebihan ArrayList:
- Implementasi sederhana
- Operasi add/remove cepat
- Cocok untuk keranjang sederhana

### Kelebihan HashMap:
- Bisa tracking quantity
- Lebih realistis untuk sistem POS nyata
- Menghindari duplikasi produk yang sama

### Pilihan Terbaik untuk Agri-POS:
**HashMap** lebih cocok karena:
1. Pelanggan sering beli produk sama lebih dari 1
2. Tampilan lebih ringkas (Beras x3 vs 3 baris Beras)
3. Perhitungan total lebih akurat (harga × quantity)

---

## 7. Kesimpulan

1. Collections Framework menyediakan struktur data yang efisien untuk mengelola objek
2. ArrayList cocok untuk data sederhana yang perlu urutan
3. HashMap lebih powerful untuk sistem POS karena bisa tracking quantity
4. Pemilihan collection yang tepat meningkatkan efisiensi program

---

## 8. Referensi
- Modul Praktikum Week 7 - Collections dan Keranjang Belanja
- Oracle Java Documentation - Collections Framework
```

---

## 4️⃣ CARA MENJALANKAN & SCREENSHOT

### **Langkah-langkah**:

1. **Buat struktur folder**:
```
praktikum/week7-collections/
 ├─ src/main/java/com/upb/agripos/
 │   ├─ Product.java
 │   ├─ ShoppingCart.java
 │   ├─ ShoppingCartMap.java
 │   └─ MainCart.java
 ├─ screenshots/
 │   └─ hasil.png
 └─ laporan_week7.md