# DATABASE MASTER - Person A Documentation

## Overview
Tugas Person A untuk proyek kelompok AgriPOS adalah menangani semua aspek database termasuk:
1. Schema design
2. Data access layer (DAO)
3. Connection pooling
4. Audit logging

## 📁 Files yang telah dibuat

### 1. **sql/schema.sql**
Database schema dengan 9 tables:
- `users` - Menyimpan data pengguna sistem (ADMIN, MANAGER, CASHIER)
- `products` - Menyimpan data produk (terintegrasi dengan PersonB)
- `discounts` - Menyimpan data diskon (terintegrasi dengan PersonB Discount Strategy)
- `transactions` - Menyimpan data transaksi penjualan
- `transaction_items` - Detail item dalam transaksi
- `audit_logs` - Audit trail untuk semua perubahan data
- `stock_movements` - History perubahan stok produk
- Views untuk kemudahan query

### 2. **sql/seed.sql**
Sample data untuk testing:
- 4 users dengan role berbeda (ADMIN, MANAGER, CASHIER 1, CASHIER 2)
- 10 sample products (beras, minyak, gula, telur, dll)
- 5 sample discounts
- 5 sample transactions
- 14 transaction items
- Stock movement history

### 3. **src/main/java/com/upb/agripos/util/DatabaseConnection.java**
Connection Pool Management menggunakan HikariCP:
- Singleton pattern untuk centralized connection management
- HikariCP untuk efficient connection pooling
- Configurable pool size, timeouts, dan lifecycle
- Pool statistics monitoring
- Test connection method

**Konfigurasi:**
```
Database: agripos
User: root
Password: (kosong)
Max Pool Size: 10
Min Idle: 2
Connection Timeout: 30 seconds
Idle Timeout: 10 minutes
Max Lifetime: 30 minutes
```

### 4. **src/main/java/com/upb/agripos/dao/impl/ProductDAOImpl.java**
JDBC operations untuk Product:

**Methods:**
- `insert(Product)` - Tambah produk baru
- `update(Product)` - Update produk
- `delete(productId)` - Hapus produk
- `findById(productId)` - Cari by ID
- `findByCode(code)` - Cari by kode produk
- `findAll()` - Ambil semua produk aktif
- `findByCategory(category)` - Cari by kategori
- `updateStock(productId, newStock)` - Update stok
- `increaseStock(productId, quantity)` - Tambah stok
- `decreaseStock(productId, quantity)` - Kurangi stok
- `isStockAvailable(productId, quantity)` - Cek stok
- `findLowStockProducts()` - Cari produk stok rendah
- `calculateTotalPrice(products)` - Hitung total harga

**Features:**
- PreparedStatement untuk SQL injection prevention
- Try-with-resources untuk automatic resource closing
- Error handling dengan informative messages
- Support untuk multiple product models

### 5. **src/main/java/com/upb/agripos/dao/impl/UserDAOImpl.java**
JDBC operations untuk User Management:

**Methods:**
- `insert(User)` - Tambah user baru
- `update(User)` - Update user
- `delete(userId)` - Soft delete user (set is_active=false)
- `findById(userId)` - Cari by ID
- `findByUsername(username)` - Cari by username
- `authenticate(username, password)` - Login authentication
- `findAll()` - Ambil semua user aktif
- `findByRole(role)` - Cari by role (ADMIN, MANAGER, CASHIER)
- `isUsernameExists(username)` - Validasi username duplikat
- `getUserCountByRole()` - Statistik user per role
- `getUserStatistics()` - Detail statistik user
- `updatePassword(userId, newPassword)` - Update password

**Features:**
- Automatic timestamp tracking (created_at, last_login)
- Password hashing support (prepared untuk BCrypt)
- Role-based access control
- User statistics & analytics
- Soft delete untuk audit trail

## 🔧 Setup Instructions

### Prerequisites
- MySQL Server running
- Java 17+
- Maven 3.6+
- HikariCP (akan di-download via Maven)
- MySQL JDBC Driver (akan di-download via Maven)

### Step 1: Create Database
```sql
CREATE DATABASE agripos;
USE agripos;
```

### Step 2: Run Schema
```bash
mysql -u root -p agripos < sql/schema.sql
mysql -u root -p agripos < sql/seed.sql
```

### Step 3: Update Configuration (jika perlu)
Edit `src/main/java/com/upb/agripos/util/DatabaseConnection.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/agripos";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

### Step 4: Build Project
```bash
mvn clean install
```

### Step 5: Test Connection
```bash
cd src/main/java/com/upb/agripos/util/
java DatabaseConnection
```

## 📊 Database Schema Diagram

```
users (1) ──┬──> transactions (1) ──> transaction_items ──> products
            │
            ├──> audit_logs
            │
            └──> stock_movements ──> products

products (1) ──> discounts
             ──> (terintegrasi dengan PersonB)
```

## 🔐 Security Features

1. **SQL Injection Prevention**
   - Menggunakan PreparedStatement untuk semua queries
   - Parameter binding bukan string concatenation

2. **Connection Security**
   - Connection pooling mencegah connection leaks
   - Auto-closing resources dengan try-with-resources
   - Leak detection threshold di HikariCP

3. **Data Integrity**
   - Foreign key constraints
   - Transaction support
   - Audit logging untuk semua changes

4. **Password Security**
   - Support untuk password hashing (BCrypt ready)
   - Soft delete untuk historical data

## 🔗 Integration with Other Teams

### Integration dengan Person B (Service & Discount)
- Product table disesuaikan dengan ProductPersonB model
- Discount table untuk support DiscountStrategy pattern
- Transaction dan transaction_items untuk mendukung CartService

### Integration dengan Person C (UI/Controller)
- User authentication via authenticate() method
- Product search dan filtering untuk UI display
- Transaction logging untuk audit trail

## 📝 Usage Examples

### Example 1: Insert Product
```java
Product prod = new Product("PROD001", "Beras Premium", 75000.0, 50, "Beras");
ProductDAOImpl dao = new ProductDAOImpl();
if (dao.insert(prod)) {
    System.out.println("Product inserted successfully");
}
```

### Example 2: Authenticate User
```java
UserDAOImpl userDao = new UserDAOImpl();
UserDAOImpl.User user = userDao.authenticate("cashier1", "password");
if (user != null) {
    System.out.println("Login successful: " + user.getFullName());
}
```

### Example 3: Check Stock
```java
ProductDAOImpl prodDao = new ProductDAOImpl();
if (prodDao.isStockAvailable("PROD001", 5)) {
    prodDao.decreaseStock("PROD001", 5);
}
```

### Example 4: Get User Statistics
```java
UserDAOImpl userDao = new UserDAOImpl();
Map<String, Object> stats = userDao.getUserStatistics();
System.out.println("Total Users: " + stats.get("totalUsers"));
System.out.println("Active Users: " + stats.get("activeUsers"));
System.out.println("Cashiers: " + stats.get("cashierCount"));
```

## 🐛 Troubleshooting

### Connection Refused
- Pastikan MySQL server running
- Check DB_URL, DB_USER, DB_PASSWORD di DatabaseConnection.java
- Run: `mysql -u root -p` untuk verify credentials

### Table Not Found
- Run schema.sql terlebih dahulu
- Verify dengan: `SHOW TABLES;` di MySQL

### Class Not Found Exception
- Run `mvn clean install` untuk download dependencies
- Check pom.xml untuk MySQL dan HikariCP dependencies

## 📋 Dependencies

```xml
<!-- MySQL JDBC Driver 8.0.33 -->
<!-- HikariCP 5.0.1 -->
<!-- JUnit 5 (untuk testing) -->
```

## 📈 Performance Considerations

1. **Connection Pooling**
   - Max 10 connections di pool
   - Min 2 idle connections
   - Automatic connection reuse

2. **Query Optimization**
   - All queries menggunakan indexes
   - Views untuk complex queries
   - PreparedStatement untuk caching

3. **Monitoring**
   - Pool statistics via printPoolStats()
   - Audit logs untuk tracking
   - Stock movement history

## 🎯 Next Steps

1. ✅ Database schema created
2. ✅ Sample data loaded
3. ✅ Connection pooling configured
4. ✅ DAO classes implemented
5. ⏳ Integration testing dengan PersonB & PersonC
6. ⏳ Performance tuning
7. ⏳ Production deployment

---
**Created by: Person A - DATABASE MASTER**
**Integration: AgriPOS Group Project Week 14-15**
**Last Updated: January 15, 2026**
