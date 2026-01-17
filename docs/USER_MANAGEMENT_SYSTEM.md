# Sistem Manajemen User - Aplikasi POS (AgriPOS)

## 📋 Daftar Isi
1. [Spesifikasi Sistem](#spesifikasi-sistem)
2. [Database Schema](#database-schema)
3. [Aturan & Validasi](#aturan--validasi)
4. [API Endpoints](#api-endpoints)
5. [Flow Proses](#flow-proses)
6. [Contoh Kode](#contoh-kode)
7. [Keamanan](#keamanan)

---

## 🎯 Spesifikasi Sistem

### 1. Role Pengguna
| Role | Deskripsi | Permissions |
|------|-----------|------------|
| **admin** | Administrator Sistem | Kelola user, lihat laporan, setting sistem |
| **kasir** | Kasir/Operator Toko | Proses transaksi penjualan |

### 2. Tabel Manajemen User (Admin Panel)

**Kolom yang ditampilkan:**
```
No | Username | Nama Lengkap | Role | Status | Terakhir Login | Aksi (Edit · Reset Password · Hapus)
```

**Fitur Tabel:**
- ✅ Sorting by username, role, status
- ✅ Pencarian/filter
- ✅ Pagination
- ✅ Status badge (aktif/nonaktif)
- ✅ Format waktu "Terakhir Login" yang readable

### 3. Aturan Username

#### Format Username
```
Kasir:  kasir001, kasir002, ..., kasir999
Admin:  admin001, admin002, ..., admin999
```

#### Validasi Username
- ✅ **Unique**: Tidak boleh duplikat di database
- ✅ **Lowercase**: Harus huruf kecil semua
- ✅ **No spaces**: Tanpa spasi
- ✅ **Alphanumeric only**: Hanya huruf & angka
- ✅ **Auto-numbering**: Nomor otomatis increment berdasarkan role
- ❌ **Tidak boleh** mengandung nama asli (tidak ada validasi hardcoded, tapi user diminta input berbeda)

#### Contoh Valid
- ✅ kasir001, kasir002, admin001, admin002
- ❌ kasir 001 (spasi)
- ❌ Kasir001 (uppercase)
- ❌ kasir@001 (special char)
- ❌ budi_santoso (tidak sesuai format)

---

## 🗄️ Database Schema

### Tabel users (update)
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CASHIER', -- ADMIN, CASHIER
    email VARCHAR(100),
    phone VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Password management
    password_changed_at TIMESTAMP,
    must_change_password BOOLEAN DEFAULT TRUE,
    
    -- Login tracking
    last_login TIMESTAMP NULL,
    login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_is_active ON users(is_active);
```

### Tabel user_audit_log (NEW)
```sql
CREATE TABLE user_audit_log (
    audit_id SERIAL PRIMARY KEY,
    admin_id INT NOT NULL,
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, RESET_PASSWORD, LOGIN
    target_user_id INT,
    target_username VARCHAR(50),
    old_values JSON,
    new_values JSON,
    description TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_admin_id ON user_audit_log(admin_id);
CREATE INDEX idx_action ON user_audit_log(action);
CREATE INDEX idx_created_at ON user_audit_log(created_at);
```

### Tabel password_reset_token (NEW)
```sql
CREATE TABLE password_reset_token (
    token_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_token ON password_reset_token(token);
CREATE INDEX idx_user_id ON password_reset_token(user_id);
```

---

## ✅ Aturan & Validasi

### Username Validation Rules
```java
public class UsernameValidator {
    
    /**
     * Validasi format username
     * - Harus lowercase
     * - Hanya alphanumeric
     * - Format: [role][NNN] contoh: kasir001, admin001
     */
    public static boolean isValidFormat(String username, String role) {
        String rolePrefix = role.toLowerCase(); // 'admin' atau 'kasir'
        String pattern = "^" + rolePrefix + "\\d{3}$"; // kasir/admin + 3 digit
        return username.matches(pattern);
    }
    
    /**
     * Validasi keunikan username
     */
    public static boolean isUnique(String username, UserRepository repo) {
        return !repo.existsByUsername(username);
    }
    
    /**
     * Generate next username otomatis
     * Cari user dengan role terbaru, ambil nomor, increment +1
     */
    public static String generateNextUsername(String role, UserRepository repo) {
        User lastUser = repo.findLatestByRole(role);
        int nextNumber = (lastUser == null) ? 1 : extractNumber(lastUser.getUsername()) + 1;
        return String.format("%s%03d", role.toLowerCase(), nextNumber);
    }
    
    private static int extractNumber(String username) {
        return Integer.parseInt(username.replaceAll("[^0-9]", ""));
    }
}
```

### Password Validation Rules
```java
public class PasswordValidator {
    
    /**
     * Validasi format password
     * - Minimal 8 karakter
     * - Minimal 1 huruf besar
     * - Minimal 1 huruf kecil
     * - Minimal 1 angka
     * - Boleh special chars (!@#$%^&*)
     */
    public static boolean isValid(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*]{8,}$";
        return password.matches(pattern);
    }
    
    /**
     * Generate random password (untuk auto-generate)
     * Format: 8 karakter, kombinasi huruf besar, kecil, dan angka
     */
    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
```

### Demo Mode Passwords
```
Mode Default (Secure): Password acak, user wajib ganti saat login pertama
Mode Demo/Testing:
  - admin001 → Akyiu8@23 (auto-generated, still need change on first login)
  - kasir001 → Bkyio9@24 (auto-generated, still need change on first login)
  
Atau bisa hardcode untuk testing:
  - admin001 → admin123Sec (tapi tetap wajib ganti saat login pertama)
  - kasir001 → kasir123Sec  (tapi tetap wajib ganti saat login pertama)
```

---

## 🔌 API Endpoints

### Authentication & User Management

#### 1. Login User
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "kasir001",
  "password": "initialPassword123"
}

Response 200:
{
  "token": "JWT_TOKEN",
  "user": {
    "user_id": 3,
    "username": "kasir001",
    "full_name": "Budi Santoso",
    "role": "CASHIER",
    "must_change_password": true
  }
}

Response 401: Unauthorized
Response 423: Account Locked (terlalu banyak login gagal)
```

#### 2. Change Password (First Login)
```
POST /api/auth/change-password
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
  "old_password": "initialPassword123",
  "new_password": "MySecurePass123"
}

Response 200:
{
  "message": "Password changed successfully",
  "must_change_password": false
}
```

---

### Admin Management Endpoints

#### 3. Get All Users (Admin Only)
```
GET /api/admin/users?page=1&limit=10&role=CASHIER&status=active
Authorization: Bearer ADMIN_JWT_TOKEN

Response 200:
{
  "data": [
    {
      "user_id": 3,
      "username": "kasir001",
      "full_name": "Budi Santoso",
      "role": "CASHIER",
      "status": "aktif",
      "last_login": "2025-01-17T10:30:00",
      "created_at": "2025-01-15T08:00:00"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 10,
    "total": 25
  }
}
```

#### 4. Create User (Admin Only)
```
POST /api/admin/users
Authorization: Bearer ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "full_name": "Ani Wijaya",
  "role": "CASHIER",
  "username": "kasir003", // optional, bisa auto-generate
  "password": "AutoGen123", // optional, bisa auto-generate
  "is_active": true,
  "generate_username": true, // jika true, auto-generate username
  "generate_password": true // jika true, auto-generate password
}

Response 201:
{
  "message": "User created successfully",
  "user": {
    "user_id": 5,
    "username": "kasir003",
    "full_name": "Ani Wijaya",
    "role": "CASHIER",
    "temporary_password": "XyZ9@bKl", // jika auto-generate
    "must_change_password": true
  }
}

Response 400: Validation Error
Response 409: Username Already Exists
```

#### 5. Update User (Admin Only)
```
PUT /api/admin/users/{user_id}
Authorization: Bearer ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "full_name": "Budi Santoso Wijaya",
  "role": "ADMIN", // bisa ubah role
  "is_active": false // bisa deaktifkan
}

Response 200:
{
  "message": "User updated successfully",
  "user": {
    "user_id": 3,
    "username": "kasir001",
    "full_name": "Budi Santoso Wijaya",
    "role": "ADMIN",
    "is_active": false
  }
}
```

#### 6. Reset Password (Admin Only)
```
POST /api/admin/users/{user_id}/reset-password
Authorization: Bearer ADMIN_JWT_TOKEN
Content-Type: application/json

{
  "auto_generate": true // atau false untuk specify password
}

Response 200:
{
  "message": "Password reset successfully",
  "new_password": "TempPass123", // jika auto-generate
  "must_change_password": true
}
```

#### 7. Delete User (Admin Only)
```
DELETE /api/admin/users/{user_id}
Authorization: Bearer ADMIN_JWT_TOKEN

Response 200:
{
  "message": "User deleted successfully"
}

Response 409: Cannot delete - user has transactions
```

#### 8. Get User Audit Log (Admin Only)
```
GET /api/admin/audit-logs?user_id=3&action=CREATE&limit=50
Authorization: Bearer ADMIN_JWT_TOKEN

Response 200:
{
  "data": [
    {
      "audit_id": 1,
      "admin_id": 1,
      "admin_username": "admin001",
      "action": "CREATE",
      "target_username": "kasir001",
      "description": "User kasir001 created",
      "created_at": "2025-01-15T08:00:00"
    }
  ]
}
```

---

## 🔄 Flow Proses

### Flow: Tambah User
```
Admin → Klik "Tambah User"
  ↓
Form Tampil:
- Nama Lengkap (input)
- Role (dropdown: ADMIN/CASHIER)
- Username (auto-generate / manual)
- Password (auto-generate / manual)
- Status (aktif/nonaktif)
  ↓
Input Data
  ↓
Validasi:
  - Nama tidak kosong?
  - Username format valid? (kasir/admin + 3 digit)
  - Username unik?
  - Password minimal 8 karakter?
  ↓
Jika error → Tampil error, form tetap
Jika valid → Lanjut
  ↓
Hash Password (bcrypt)
  ↓
Insert ke Database
Set must_change_password = TRUE
  ↓
Catat Audit Log:
- Admin: admin001
- Action: CREATE
- Target: kasir003
- Waktu: 2025-01-17 10:30
  ↓
Success → Tampil notification
Return ke tabel user
```

### Flow: Edit User
```
Admin → Klik Edit pada row user
  ↓
Form Tampil (pre-filled):
- Nama Lengkap
- Role
- Status
(Username & Password NOT editable)
  ↓
Edit Data
  ↓
Validasi: Nama tidak kosong?
  ↓
Update Database
  ↓
Catat Audit Log:
- Admin: admin001
- Action: UPDATE
- Target: kasir001
- Old: {full_name: "Budi", is_active: true}
- New: {full_name: "Budi Santoso", is_active: false}
  ↓
Success → Refresh tabel
```

### Flow: Reset Password
```
Admin → Klik Reset Password pada row user
  ↓
Konfirmasi: "Reset password kasir001?"
  ↓
Generate Password Baru
  ↓
Hash & Update Database
Set must_change_password = TRUE
  ↓
Catat Audit Log:
- Admin: admin001
- Action: RESET_PASSWORD
- Target: kasir001
- Waktu: 2025-01-17 10:31
  ↓
Display: "Password baru: TempPass123"
  ↓
Inform user (via email/SMS atau print)
```

### Flow: Hapus User
```
Admin → Klik Hapus pada row user
  ↓
Konfirmasi: "Yakin hapus kasir001?"
  ↓
Check: User punya transaksi aktif?
  - YES → Prevent delete, show error
  - NO → Lanjut
  ↓
Delete dari users table
  ↓
Catat Audit Log:
- Admin: admin001
- Action: DELETE
- Target: kasir001
  ↓
Success → Refresh tabel
```

---

## 💻 Contoh Kode

### Java - User Service

```java
package com.upb.agripos.service;

import com.upb.agripos.dto.UserDTO;
import com.upb.agripos.entity.User;
import com.upb.agripos.repository.UserRepository;
import com.upb.agripos.repository.UserAuditLogRepository;
import com.upb.agripos.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAuditLogRepository auditLogRepository;
    
    /**
     * Tambah user baru
     */
    @Transactional
    public UserDTO createUser(UserDTO dto, boolean autoGenUsername, boolean autoGenPassword) {
        
        // 1. Validasi nama
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap tidak boleh kosong");
        }
        
        // 2. Generate atau validasi username
        String username;
        if (autoGenUsername) {
            username = generateNextUsername(dto.getRole());
        } else {
            if (!UsernameValidator.isValidFormat(dto.getUsername(), dto.getRole())) {
                throw new IllegalArgumentException("Format username tidak valid. Gunakan format: " + 
                    dto.getRole().toLowerCase() + "NNN (contoh: kasir001)");
            }
            username = dto.getUsername();
        }
        
        // 3. Cek username duplikat
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username sudah terdaftar: " + username);
        }
        
        // 4. Generate atau validasi password
        String password;
        if (autoGenPassword) {
            password = PasswordUtil.generateRandomPassword();
        } else {
            if (!PasswordValidator.isValid(dto.getPassword())) {
                throw new IllegalArgumentException("Password tidak memenuhi kriteria keamanan");
            }
            password = dto.getPassword();
        }
        
        // 5. Create user entity
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password)); // bcrypt
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setIsActive(dto.isActive() != null ? dto.isActive() : true);
        user.setMustChangePassword(true); // Wajib ganti password saat login pertama
        user.setCreatedBy(getCurrentAdminId());
        
        User savedUser = userRepository.save(user);
        
        // 6. Log audit
        logAudit("CREATE", savedUser.getUserId(), username, null,
            "{\"full_name\": \"" + user.getFullName() + "\", \"role\": \"" + user.getRole() + "\"}",
            "User baru " + username + " berhasil dibuat");
        
        // 7. Return DTO
        UserDTO responseDTO = new UserDTO(savedUser);
        if (autoGenPassword) {
            responseDTO.setTemporaryPassword(password); // Hanya tampil password yang di-generate
        }
        return responseDTO;
    }
    
    /**
     * Update user
     */
    @Transactional
    public UserDTO updateUser(Integer userId, UserDTO dto) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }
        
        User user = userOpt.get();
        
        // Store old values for audit log
        String oldValues = String.format("{\"full_name\": \"%s\", \"role\": \"%s\", \"is_active\": %s}",
            user.getFullName(), user.getRole(), user.getIsActive());
        
        // Update fields yang boleh diubah
        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName());
        }
        
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        
        if (dto.isActive() != null) {
            user.setIsActive(dto.isActive());
        }
        
        user.setUpdatedBy(getCurrentAdminId());
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        // Log audit
        String newValues = String.format("{\"full_name\": \"%s\", \"role\": \"%s\", \"is_active\": %s}",
            updatedUser.getFullName(), updatedUser.getRole(), updatedUser.getIsActive());
        logAudit("UPDATE", userId, updatedUser.getUsername(), oldValues, newValues,
            "User " + updatedUser.getUsername() + " berhasil diupdate");
        
        return new UserDTO(updatedUser);
    }
    
    /**
     * Reset password user
     */
    @Transactional
    public String resetPassword(Integer userId, boolean autoGenerate) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }
        
        User user = userOpt.get();
        
        // Generate password baru
        String newPassword = autoGenerate ? 
            PasswordUtil.generateRandomPassword() : "DefaultPass123";
        
        // Hash dan update
        user.setPassword(PasswordUtil.hashPassword(newPassword));
        user.setMustChangePassword(true);
        user.setUpdatedBy(getCurrentAdminId());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Log audit
        logAudit("RESET_PASSWORD", userId, user.getUsername(), null, null,
            "Password user " + user.getUsername() + " berhasil direset");
        
        return newPassword; // Return untuk ditampilkan ke admin
    }
    
    /**
     * Hapus user
     */
    @Transactional
    public void deleteUser(Integer userId) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }
        
        User user = userOpt.get();
        String username = user.getUsername();
        
        // Cek apakah user punya transaksi
        if (userRepository.hasActiveTransactions(userId)) {
            throw new IllegalArgumentException("Tidak bisa hapus user yang punya transaksi aktif");
        }
        
        // Delete
        userRepository.delete(user);
        
        // Log audit
        logAudit("DELETE", userId, username, null, null,
            "User " + username + " berhasil dihapus");
    }
    
    /**
     * Generate username otomatis
     */
    private String generateNextUsername(String role) {
        return UsernameValidator.generateNextUsername(role, userRepository);
    }
    
    /**
     * Log audit
     */
    private void logAudit(String action, Integer targetUserId, String targetUsername,
                         String oldValues, String newValues, String description) {
        
        Integer adminId = getCurrentAdminId();
        
        UserAuditLog log = new UserAuditLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTargetUserId(targetUserId);
        log.setTargetUsername(targetUsername);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);
        log.setDescription(description);
        log.setIpAddress(getCurrentIpAddress());
        
        auditLogRepository.save(log);
    }
    
    /**
     * Get current admin ID dari authentication
     */
    private Integer getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extract user ID dari JWT token
        return Integer.parseInt(auth.getName()); // Atau ambil dari claim
    }
    
    /**
     * Get current IP address
     */
    private String getCurrentIpAddress() {
        // Ambil dari HttpServletRequest
        return "127.0.0.1"; // Placeholder
    }
}
```

### Java - Controller

```java
package com.upb.agripos.controller;

import com.upb.agripos.dto.UserDTO;
import com.upb.agripos.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userService;
    
    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        // TODO: Implement pagination, filter
        return ResponseEntity.ok("Users list");
    }
    
    /**
     * Create user
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO dto) {
        try {
            boolean autoGenUsername = dto.isGenerateUsername();
            boolean autoGenPassword = dto.isGeneratePassword();
            
            UserDTO created = userService.createUser(dto, autoGenUsername, autoGenPassword);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                    "message", "User created successfully",
                    "user", created
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Update user
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userId,
            @RequestBody UserDTO dto) {
        try {
            UserDTO updated = userService.updateUser(userId, dto);
            
            return ResponseEntity.ok(
                Map.of(
                    "message", "User updated successfully",
                    "user", updated
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Reset password
     */
    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "true") boolean autoGenerate) {
        try {
            String newPassword = userService.resetPassword(userId, autoGenerate);
            
            return ResponseEntity.ok(
                Map.of(
                    "message", "Password reset successfully",
                    "new_password", newPassword,
                    "must_change_password", true
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            userService.deleteUser(userId);
            
            return ResponseEntity.ok(
                Map.of("message", "User deleted successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
}
```

### HTML - Admin Panel (Thymeleaf Template)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Manajemen User - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container-fluid mt-5">
    <div class="row mb-4">
        <div class="col">
            <h2>Manajemen User</h2>
        </div>
        <div class="col-auto">
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalTambahUser">
                + Tambah User
            </button>
        </div>
    </div>
    
    <!-- Tabel User -->
    <div class="table-responsive">
        <table class="table table-hover">
            <thead class="table-dark">
                <tr>
                    <th>No</th>
                    <th>Username</th>
                    <th>Nama Lengkap</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Terakhir Login</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody id="userTable">
                <!-- Populated by JavaScript -->
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Tambah User -->
<div class="modal fade" id="modalTambahUser" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Tambah User Baru</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="formTambahUser">
                <div class="modal-body">
                    <div class="mb-3">
                        <label>Nama Lengkap *</label>
                        <input type="text" class="form-control" id="fullName" required>
                    </div>
                    
                    <div class="mb-3">
                        <label>Role *</label>
                        <select class="form-select" id="role" required>
                            <option value="">-- Pilih --</option>
                            <option value="ADMIN">Admin</option>
                            <option value="CASHIER">Kasir</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label>Username</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="username" placeholder="Auto-generate">
                            <button class="btn btn-outline-secondary" type="button" id="btnGenerateUsername">
                                Generate
                            </button>
                        </div>
                        <small class="form-text text-muted">Format: kasir001, admin001, dll</small>
                    </div>
                    
                    <div class="mb-3">
                        <label>Password</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="password" placeholder="Auto-generate">
                            <button class="btn btn-outline-secondary" type="button" id="btnGeneratePassword">
                                Generate
                            </button>
                        </div>
                        <small class="form-text text-muted">Min 8 karakter, kombinasi huruf & angka</small>
                    </div>
                    
                    <div class="mb-3">
                        <label>Status</label>
                        <select class="form-select" id="status">
                            <option value="true">Aktif</option>
                            <option value="false">Nonaktif</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Edit User -->
<div class="modal fade" id="modalEditUser" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Edit User</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="formEditUser">
                <input type="hidden" id="editUserId">
                <div class="modal-body">
                    <div class="mb-3">
                        <label>Username</label>
                        <input type="text" class="form-control" id="editUsername" readonly>
                    </div>
                    
                    <div class="mb-3">
                        <label>Nama Lengkap *</label>
                        <input type="text" class="form-control" id="editFullName" required>
                    </div>
                    
                    <div class="mb-3">
                        <label>Role *</label>
                        <select class="form-select" id="editRole" required>
                            <option value="ADMIN">Admin</option>
                            <option value="CASHIER">Kasir</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label>Status</label>
                        <select class="form-select" id="editStatus">
                            <option value="true">Aktif</option>
                            <option value="false">Nonaktif</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
$(document).ready(function() {
    loadUsers();
    
    // Load users
    function loadUsers() {
        $.get('/api/admin/users', function(response) {
            let table = '';
            response.data.forEach((user, index) => {
                table += `<tr>
                    <td>${index + 1}</td>
                    <td>${user.username}</td>
                    <td>${user.full_name}</td>
                    <td><span class="badge bg-info">${user.role}</span></td>
                    <td>
                        <span class="badge ${user.is_active ? 'bg-success' : 'bg-danger'}">
                            ${user.is_active ? 'Aktif' : 'Nonaktif'}
                        </span>
                    </td>
                    <td>${user.last_login || '-'}</td>
                    <td>
                        <button class="btn btn-sm btn-warning" onclick="editUser(${user.user_id})">Edit</button>
                        <button class="btn btn-sm btn-info" onclick="resetPassword(${user.user_id})">Reset Pass</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.user_id})">Hapus</button>
                    </td>
                </tr>`;
            });
            $('#userTable').html(table);
        });
    }
    
    // Form Tambah User
    $('#formTambahUser').submit(function(e) {
        e.preventDefault();
        
        let data = {
            full_name: $('#fullName').val(),
            role: $('#role').val(),
            username: $('#username').val() || undefined,
            password: $('#password').val() || undefined,
            is_active: $('#status').val() === 'true',
            generate_username: !$('#username').val(),
            generate_password: !$('#password').val()
        };
        
        $.ajax({
            url: '/api/admin/users',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                alert('User berhasil dibuat');
                if (response.user.temporary_password) {
                    alert('Password temporary: ' + response.user.temporary_password);
                }
                $('#modalTambahUser').modal('hide');
                $('#formTambahUser')[0].reset();
                loadUsers();
            },
            error: function(xhr) {
                alert('Error: ' + xhr.responseJSON.error);
            }
        });
    });
    
    // Generate Username
    $('#btnGenerateUsername').click(function() {
        let role = $('#role').val();
        if (!role) {
            alert('Pilih role terlebih dahulu');
            return;
        }
        // Call API untuk generate
        $.get('/api/admin/generate-username?role=' + role, function(response) {
            $('#username').val(response.username);
        });
    });
    
    // Generate Password
    $('#btnGeneratePassword').click(function() {
        $.get('/api/admin/generate-password', function(response) {
            $('#password').val(response.password);
        });
    });
    
    // Edit User
    window.editUser = function(userId) {
        $.get('/api/admin/users/' + userId, function(user) {
            $('#editUserId').val(user.user_id);
            $('#editUsername').val(user.username);
            $('#editFullName').val(user.full_name);
            $('#editRole').val(user.role);
            $('#editStatus').val(user.is_active);
            $('#modalEditUser').modal('show');
        });
    };
    
    $('#formEditUser').submit(function(e) {
        e.preventDefault();
        
        let userId = $('#editUserId').val();
        let data = {
            full_name: $('#editFullName').val(),
            role: $('#editRole').val(),
            is_active: $('#editStatus').val() === 'true'
        };
        
        $.ajax({
            url: '/api/admin/users/' + userId,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function() {
                alert('User berhasil diupdate');
                $('#modalEditUser').modal('hide');
                loadUsers();
            },
            error: function(xhr) {
                alert('Error: ' + xhr.responseJSON.error);
            }
        });
    });
    
    // Reset Password
    window.resetPassword = function(userId) {
        if (confirm('Reset password user ini?')) {
            $.post('/api/admin/users/' + userId + '/reset-password', function(response) {
                alert('Password baru: ' + response.new_password);
                loadUsers();
            });
        }
    };
    
    // Delete User
    window.deleteUser = function(userId) {
        if (confirm('Yakin hapus user ini?')) {
            $.ajax({
                url: '/api/admin/users/' + userId,
                method: 'DELETE',
                success: function() {
                    alert('User berhasil dihapus');
                    loadUsers();
                },
                error: function(xhr) {
                    alert('Error: ' + xhr.responseJSON.error);
                }
            });
        }
    };
});
</script>
</body>
</html>
```

---

## 🔐 Keamanan

### 1. Password Hashing
- **Algorithm**: bcrypt (Spring Security)
- **Cost Factor**: 10 (default, ~4 digit)
- **Implementasi**: `BCryptPasswordEncoder`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```

### 2. Authorization
- **Only ADMIN** bisa tambah/edit/delete user
- **Only ADMIN** bisa reset password
- **Only ADMIN** bisa lihat audit log
- **Only USER ITSELF** bisa ubah password sendiri

### 3. Login Security
- **Max attempts**: 5 kali salah
- **Lock duration**: 15 menit
- **Session timeout**: 30 menit
- **HTTPS only** di production

### 4. Password Policy
- **Minimum 8 karakter**
- **Must have uppercase** (A-Z)
- **Must have lowercase** (a-z)
- **Must have digit** (0-9)
- **Optional special char** (!@#$%^&*)
- **No username dalam password**

### 5. Audit Trail
- **All changes logged** dengan timestamp
- **IP address dicatat**
- **Admin yang melakukan dicatat**
- **Old & New values disimpan** (JSON)
- **Immutable logs** (tidak boleh diedit)

---

## 📝 Contoh Data

| user_id | username | full_name | role | is_active | must_change_password |
|---------|----------|-----------|------|-----------|----------------------|
| 1 | admin001 | Admin Utama | ADMIN | true | false |
| 2 | admin002 | Admin Sistem | ADMIN | true | false |
| 3 | kasir001 | Budi Santoso | CASHIER | true | true |
| 4 | kasir002 | Siti Nurhaliza | CASHIER | true | true |
| 5 | kasir003 | Raden Wijaya | CASHIER | false | true |

---

**Created**: 17 January 2026
**Version**: 1.0
**Status**: Ready for Implementation
