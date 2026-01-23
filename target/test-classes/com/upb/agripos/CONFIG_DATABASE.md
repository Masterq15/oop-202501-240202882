# Week 15 - Database Configuration & pgAdmin Integration

## 📋 Ringkasan

Week 15 sekarang dilengkapi dengan **config folder** dan **database configuration** yang terhubung langsung ke **pgAdmin dengan PostgreSQL**.

## 🗂️ Struktur Folder

```
week15-proyek-kelompok/
├─ src/main/java/com/upb/agripos/
│  ├─ config/
│  │  ├─ DatabaseConnection.java     ← PostgreSQL Connection Manager
│  │  └─ AppConfig.java              ← Global Application Configuration
│  ├─ controller/
│  ├─ view/
│  ├─ service/
│  ├─ model/
│  ├─ dao/
│  ├─ exception/
│  └─ AppJavaFX.java
├─ pom.xml
└─ ...
```

## 🗄️ Database Configuration

### DatabaseConnection.java
**Lokasi:** `src/main/java/com/upb/agripos/config/DatabaseConnection.java`

**Fitur:**
- ✅ PostgreSQL JDBC Driver integration
- ✅ Singleton pattern untuk single connection instance
- ✅ Automatic connection initialization
- ✅ Error handling dengan informasi detail
- ✅ Connection test method

**Database Details:**
```
Driver:     org.postgresql.Driver
Host:       localhost
Port:       5432
Database:   agripos
Username:   postgres
Password:   postgres
JDBC URL:   jdbc:postgresql://localhost:5432/agripos
```

### AppConfig.java
**Lokasi:** `src/main/java/com/upb/agripos/config/AppConfig.java`

**Fitur:**
- ✅ Centralized configuration management
- ✅ Database configuration
- ✅ Application settings
- ✅ Role definitions (ADMIN, KASIR)
- ✅ UI configuration
- ✅ Payment method definitions

**Usage:**
```java
// Access database config
String dbUrl = AppConfig.Database.URL;
String dbUser = AppConfig.Database.USER;
String connectionInfo = AppConfig.Database.getConnectionInfo();

// Access app config
String appTitle = AppConfig.App.TITLE;
int appWidth = AppConfig.App.DEFAULT_WIDTH;

// Get role constants
String adminRole = AppConfig.Role.ADMIN;
String kasirRole = AppConfig.Role.KASIR;

// Print all configuration
AppConfig.printConfiguration();
```

## 🔌 Koneksi ke pgAdmin

### Akses pgAdmin
- **URL:** `http://localhost:5050`
- **Default Username:** postgres
- **Default Password:** postgres

### Setup Database di pgAdmin

1. **Buka pgAdmin**
   - URL: http://localhost:5050
   - Login dengan credentials default

2. **Create Database**
   - Klik Databases → Create → Database
   - Nama: `agripos`
   - Owner: `postgres`

3. **Setup Schema & Data**
   - Buka Query Tool
   - Run: `sql/schema_agripos.sql` - membuat tables
   - Run: `sql/seed_agripos.sql` - insert test data

4. **Verify Connection**
   - Jalankan: `mvn exec:java -Dexec.mainClass="com.upb.agripos.config.DatabaseConnection"`
   - Output akan menunjukkan status koneksi

## 🚀 Cara Menggunakan

### 1. Koneksi Sederhana
```java
import com.upb.agripos.config.DatabaseConnection;

// Get connection
Connection conn = DatabaseConnection.getInstance().getConnection();

// Use connection untuk query
// ... database operations ...

// Close connection
DatabaseConnection.getInstance().closeConnection();
```

### 2. Cek Konfigurasi
```java
import com.upb.agripos.config.AppConfig;

// Print semua config
AppConfig.printConfiguration();

// Access specific config
String dbInfo = AppConfig.Database.getConnectionInfo();
System.out.println("Connected to: " + dbInfo);
```

### 3. Test Koneksi Database
```bash
cd praktikum/week15-proyek-kelompok
mvn exec:java -Dexec.mainClass="com.upb.agripos.config.DatabaseConnection"
```

## ✅ Checklist Setup

- [ ] PostgreSQL installed dan running (port 5432)
- [ ] pgAdmin installed dan running (port 5050)
- [ ] Database `agripos` sudah dibuat di PostgreSQL
- [ ] Schema tables sudah di-create (schema_agripos.sql)
- [ ] Test data sudah di-insert (seed_agripos.sql)
- [ ] Week 15 aplikasi compiled dengan baik
- [ ] Connection test berhasil

## 🧪 Testing

### Test Database Connection
```bash
mvn exec:java -Dexec.mainClass="com.upb.agripos.config.DatabaseConnection"
```

### Expected Output
```
✓ PostgreSQL Driver loaded
✓ PostgreSQL Connection established: jdbc:postgresql://localhost:5432/agripos
✓ Connected to pgAdmin database 'agripos'
✓ Database connection is active
CONNECTION SUCCESSFUL!
Ready to connect with pgAdmin
```

## 📚 File Reference

| File | Purpose |
|------|---------|
| `config/DatabaseConnection.java` | PostgreSQL connection management |
| `config/AppConfig.java` | Global application configuration |
| `../../../sql/schema_agripos.sql` | Database schema (tables definition) |
| `../../../sql/seed_agripos.sql` | Test data |

## 🔗 Integration dengan Week 15

Config yang sudah dibuat di folder **week15-proyek-kelompok/src/main/java/com/upb/agripos/config/** dapat diakses oleh:
- Semua controller
- Semua service class
- Main application (AppJavaFX)
- DAO layer

## 📝 Notes

- ✅ Week 15 sudah punya folder `config/` sendiri
- ✅ DatabaseConnection.java specifik untuk week 15
- ✅ AppConfig.java centralize semua configuration
- ✅ PostgreSQL adalah database yang digunakan
- ✅ pgAdmin digunakan untuk database management UI
- ✅ Connection pooling belum diimplementasikan (simple JDBC)

---

**Person A - DATABASE MASTER**  
**Week 15 - Proyek Kelompok**
