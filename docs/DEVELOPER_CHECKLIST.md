# 📋 DEVELOPER CHECKLIST - IMPLEMENTASI USER MANAGEMENT

Gunakan checklist ini untuk memastikan implementasi sistem manajemen user berjalan sempurna.

---

## ✅ PHASE 1: Database Setup

- [ ] Baca dokumentasi di `docs/USER_MANAGEMENT_SYSTEM.md`
- [ ] Backup database existing
- [ ] Jalankan migration SQL: `sql/03_user_management_migration.sql`
  ```bash
  psql -U postgres -d agripos -f sql/03_user_management_migration.sql
  ```
- [ ] Verify tabel baru:
  - `users` (dengan kolom baru)
  - `user_audit_log` (new)
  - `password_reset_token` (new)
- [ ] Verify views:
  - `v_user_management`
  - `v_audit_log_detail`
  - `v_user_login_stats`
- [ ] Verify function:
  - `get_next_username()`
- [ ] Test generate username:
  ```sql
  SELECT get_next_username('CASHIER');
  ```

---

## ✅ PHASE 2: Java Setup

### 2.1 Entity Classes

- [ ] Update `User.java` entity
  ```java
  // Tambah kolom baru:
  private LocalDateTime passwordChangedAt;
  private Boolean mustChangePassword;
  private LocalDateTime lastLogin;
  private Integer loginAttempts;
  private LocalDateTime lockedUntil;
  private Integer createdBy;
  private Integer updatedBy;
  ```

- [ ] Create `UserAuditLog.java` entity
  ```java
  @Entity
  @Table(name = "user_audit_log")
  public class UserAuditLog {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer auditId;
      
      private Integer adminId;
      private String action;
      private Integer targetUserId;
      private String targetUsername;
      private String oldValues;
      private String newValues;
      private String description;
      private String ipAddress;
      private LocalDateTime createdAt;
  }
  ```

- [ ] Create `PasswordResetToken.java` entity

### 2.2 Repository Interfaces

- [ ] Update `UserRepository.java`
  ```java
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.userId DESC LIMIT 1")
  User findLatestByRole(String role);
  @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.userId = :userId")
  boolean hasActiveTransactions(Integer userId);
  ```

- [ ] Create `UserAuditLogRepository.java`
  ```java
  List<UserAuditLog> findByAdminId(Integer adminId);
  List<UserAuditLog> findByAction(String action);
  ```

### 2.3 DTO Classes

- [ ] Create `UserDTO.java`
  ```java
  public class UserDTO {
      private Integer userId;
      private String username;
      private String fullName;
      private String role;
      private String email;
      private String phone;
      private boolean active;
      private LocalDateTime lastLogin;
      private String temporaryPassword; // untuk create response
      private boolean generateUsername;
      private boolean generatePassword;
  }
  ```

### 2.4 Copy Files

- [ ] Copy `UserManagementService.java` ke `src/main/java/com/upb/agripos/service/`
- [ ] Copy `UserManagementController.java` ke `src/main/java/com/upb/agripos/controller/`
- [ ] Copy `UsernameValidator.java` ke `src/main/java/com/upb/agripos/util/`
- [ ] Copy `PasswordValidator.java` ke `src/main/java/com/upb/agripos/util/`
- [ ] Copy `PasswordUtil.java` ke `src/main/java/com/upb/agripos/util/`

### 2.5 Security Configuration

- [ ] Configure `PasswordEncoder` di Security Config
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(10);
  }
  ```

- [ ] Update authentication untuk track login attempts
- [ ] Implement endpoint protection dengan `@PreAuthorize("hasRole('ADMIN')")`

---

## ✅ PHASE 3: Testing

### 3.1 Unit Tests

- [ ] Test `UsernameValidator.isValidFormat()`
  ```
  kasir001, admin001 → true
  kasir 001, Kasir001, kasir999a → false
  ```

- [ ] Test `UsernameValidator.generateNextUsername()`
  ```
  Expect: kasir001 (first)
  Expect: kasir002 (after kasir001 exists)
  ```

- [ ] Test `PasswordValidator.isValid()`
  ```
  Pass123 → true
  pass123, Pass, Pass123@@ → false
  ```

- [ ] Test `PasswordUtil.hashPassword()` & `verifyPassword()`

### 3.2 Integration Tests

- [ ] Test Create User (success)
  ```
  POST /api/admin/users
  Input: full_name=Test, role=CASHIER, generate_username=true, generate_password=true
  Expected: User created dengan username kasir001, password di-hash, audit log dibuat
  ```

- [ ] Test Create User (duplicate username)
  ```
  POST /api/admin/users
  Input: username=kasir001 (already exists)
  Expected: Error 400 - "Username sudah terdaftar"
  ```

- [ ] Test Create User (invalid format)
  ```
  POST /api/admin/users
  Input: username=invalid123, role=CASHIER
  Expected: Error 400 - Format tidak valid
  ```

- [ ] Test Update User (success)
  ```
  PUT /api/admin/users/3
  Input: full_name=Budi Baru, role=ADMIN
  Expected: User updated, audit log CREATE dengan old/new values
  ```

- [ ] Test Reset Password (success)
  ```
  POST /api/admin/users/3/reset-password
  Expected: Password di-reset, must_change_password=true, audit log dibuat
  ```

- [ ] Test Delete User (success)
  ```
  DELETE /api/admin/users/7 (no transactions)
  Expected: User deleted, audit log dibuat
  ```

- [ ] Test Delete User (with transactions)
  ```
  DELETE /api/admin/users/3 (kasir001 punya transactions)
  Expected: Error 409 - "Tidak bisa hapus user karena masih punya transaksi"
  ```

- [ ] Test Generate Username
  ```
  GET /api/admin/users/generate/username?role=CASHIER
  Expected: kasir007 (next available)
  ```

- [ ] Test Get All Users
  ```
  GET /api/admin/users?role=CASHIER&status=active
  Expected: List kasir yang aktif saja
  ```

### 3.3 API Testing (Postman/Thunder Client)

- [ ] Import collection dari dokumentasi
- [ ] Test semua endpoints
- [ ] Verify response codes:
  - 200 OK
  - 201 Created
  - 400 Bad Request
  - 401 Unauthorized
  - 409 Conflict
- [ ] Verify error messages jelas & helpful

---

## ✅ PHASE 4: Frontend Implementation

- [ ] Create admin dashboard page
- [ ] Create user management menu
- [ ] Implement user list table dengan:
  - [ ] Columns: No, Username, Nama, Role, Status, Terakhir Login, Aksi
  - [ ] Sorting by username, role, status
  - [ ] Filtering by role, status
  - [ ] Pagination
  - [ ] Status badge styling

- [ ] Implement modal "Tambah User"
  - [ ] Field: Nama Lengkap, Role, Username, Password, Status
  - [ ] Auto-generate buttons untuk username & password
  - [ ] Validation dengan pesan error
  - [ ] Show temporary password setelah created

- [ ] Implement modal "Edit User"
  - [ ] Pre-fill data dari server
  - [ ] Edit: Nama, Role, Status (username/password readonly)
  - [ ] Validation

- [ ] Implement "Reset Password"
  - [ ] Confirmation dialog
  - [ ] Display new password
  - [ ] Copy to clipboard button

- [ ] Implement "Hapus User"
  - [ ] Confirmation dialog
  - [ ] Show user info (username, nama, role)
  - [ ] Handle error jika user punya transaksi

- [ ] Add notification/toast untuk feedback:
  - [ ] Success message
  - [ ] Error message dengan detail

- [ ] Add loading spinner saat request ke server

---

## ✅ PHASE 5: Security & Validation

### Server-Side

- [ ] Validate semua input di service layer
- [ ] Check authorization: only ADMIN can manage users
- [ ] Check role exists (ADMIN, CASHIER)
- [ ] Hash password dengan BCrypt
- [ ] Implement login attempt tracking
- [ ] Implement account locking (5 attempts, 15 min lock)
- [ ] Log semua aksi di audit log

### Client-Side

- [ ] Validate form input sebelum submit
- [ ] Show password strength indicator
- [ ] Confirm destructive actions (delete, reset password)
- [ ] Handle network errors gracefully

### Database

- [ ] Verify UNIQUE constraint pada username
- [ ] Verify FK constraint pada user_id references
- [ ] Verify audit log immutability (no update/delete)

---

## ✅ PHASE 6: Documentation

- [ ] Verify semua kode terdokumentasi dengan JavaDoc
- [ ] Update API documentation
- [ ] Create user guide untuk admin (how to manage users)
- [ ] Document password policy untuk users
- [ ] Document troubleshooting guide

---

## ✅ PHASE 7: Deployment

- [ ] Review code dengan team lead
- [ ] Run full test suite (unit + integration)
- [ ] Test di staging environment
- [ ] Backup production database
- [ ] Deploy ke production
- [ ] Verify semua endpoint berfungsi
- [ ] Monitor logs untuk error
- [ ] Inform users tentang password policy

---

## 🐛 Common Issues & Solutions

### Issue 1: BCryptPasswordEncoder not found
```
Solution: Make sure spring-security-crypto dependency ada di pom.xml
```

### Issue 2: User audit log tidak tercatat
```
Solution: Verify UserAuditLogRepository autowired di service
         Verify @Transactional di method create/update/delete
```

### Issue 3: Generate username tidak increment
```
Solution: Verify query di UserRepository.findLatestByRole() correct
         Test manual: SELECT get_next_username('CASHIER');
```

### Issue 4: Password validation terlalu ketat
```
Solution: Review criteria di PasswordValidator
         Komunikasikan password policy ke users
```

### Issue 5: User tidak bisa delete karena transaksi
```
Solution: This is INTENTIONAL - prevent data loss
         Inform admin untuk archive/deactivate user dulu
```

---

## 📞 Support

Jika ada pertanyaan atau issue:

1. **Check dokumentasi**: `docs/USER_MANAGEMENT_SYSTEM.md`
2. **Check SQL migration**: `sql/03_user_management_migration.sql`
3. **Check contoh kode**: Service, Controller, Util files
4. **Check API documentation** di sistem

---

## ✨ Success Criteria

Implementasi dianggap SUCCESS jika:

- [x] Database migration berjalan tanpa error
- [x] Semua Java files compile tanpa error
- [x] Semua unit tests pass
- [x] Semua integration tests pass
- [x] API endpoints respond dengan status code yang benar
- [x] Frontend form bekerja dan validasi jalan
- [x] Audit log tercatat untuk semua aksi
- [x] Password di-hash, bukan plain text
- [x] Only ADMIN bisa akses endpoints
- [x] Username auto-generation berfungsi
- [x] Error handling & user feedback jelas
- [x] Documentation lengkap & up-to-date

---

**Last Updated**: 17 January 2026
**Version**: 1.0
