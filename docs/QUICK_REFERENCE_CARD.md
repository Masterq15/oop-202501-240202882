# 🎯 QUICK REFERENCE CARD - USER MANAGEMENT SYSTEM

Laminated reference untuk developers yang working on user management implementation.

---

## 📍 FILE STRUCTURE

```
Database Layer:
  sql/03_user_management_migration.sql ← Execute first!
  sql/schema_agripos.sql
  sql/seed_agripos.sql

Java Layer:
  service/UserManagementService.java (CRUD + audit)
  controller/UserManagementController.java (8 endpoints)
  repository/UserRepository.java (24 queries)
  repository/UserAuditLogRepository.java (20 queries)
  util/UsernameValidator.java (format, auto-gen)
  util/PasswordValidator.java (policy, strength)
  util/PasswordUtil.java (BCrypt)
  entity/User.java (need to update)
  entity/UserAuditLog.java (need to create)
  entity/PasswordResetToken.java (need to create)

Documentation:
  docs/USER_MANAGEMENT_SYSTEM.md ← Read this first!
  docs/IMPLEMENTASI_SUMMARY.md
  docs/DEVELOPER_CHECKLIST.md ← Follow this!
  docs/00_SISTEM_MANAJEMEN_USER_SUMMARY.md
```

---

## 🔑 KEY CLASSES & METHODS

### UserManagementService
```java
// CRUD Operations
createUser(UserDTO, autoGenUsername, autoGenPassword)
updateUser(Integer userId, UserDTO)
resetPassword(Integer userId, autoGenerate)
deleteUser(Integer userId)
getUserById(Integer userId)
getAllUsers(Page, filters)

// Audit Logging
logAudit(adminId, action, targetUserId, oldValues, newValues)

// Helpers
getCurrentAdminId()
getCurrentIpAddress()
toJson(Object)
```

### UserManagementController
```java
GET    /api/admin/users
GET    /api/admin/users/{userId}
POST   /api/admin/users
PUT    /api/admin/users/{userId}
POST   /api/admin/users/{userId}/reset-password
DELETE /api/admin/users/{userId}
GET    /api/admin/users/generate/username?role=CASHIER
GET    /api/admin/users/generate/password
```

### UsernameValidator
```java
isValidFormat(username, role) → boolean
generateNextUsername(role, repo) → String
isUnique(username, repo) → boolean
validate(username, role, repo) → throws Exception
// Pattern: kasir001-999, admin001-999
```

### PasswordValidator
```java
isValid(password) → boolean
generateRandomPassword() → String (8-char)
validateWithMessage(password) → String (or null)
calculateStrength(password) → int (0-100)
getStrengthLabel(score) → String
// Policy: 8+ chars, uppercase, lowercase, digit
```

### PasswordUtil
```java
hashPassword(rawPassword) → String (BCrypt)
verifyPassword(rawPassword, hash) → boolean
generateRandomPassword() → String
isHashed(string) → boolean (is BCrypt format?)
// Cost factor: 10 (~100ms per hash)
```

---

## 🗄️ DATABASE QUICK LOOK

### Users Table (Modified)
```sql
userId (PK)
username (UNIQUE)
password (hashed)
fullName
role [ADMIN, CASHIER]
email
phone
active
createdAt
createdBy (FK → users)  -- NEW
updatedBy (FK → users)  -- NEW
passwordChangedAt       -- NEW
mustChangePassword      -- NEW
lastLogin               -- NEW
loginAttempts           -- NEW
lockedUntil             -- NEW
```

### UserAuditLog Table (New)
```sql
auditId (PK)
adminId (FK)
action [CREATE_USER, UPDATE_USER, RESET_PASSWORD, DELETE_USER]
targetUserId (FK)
targetUsername
oldValues (JSON)
newValues (JSON)
description
ipAddress
createdAt
```

### Sample Data
```
ID=1  username=admin001   role=ADMIN    fullName=Administrator 1
ID=2  username=admin002   role=ADMIN    fullName=Administrator 2
ID=3  username=kasir001   role=CASHIER  fullName=Kasir Cabang 1
ID=4  username=kasir002   role=CASHIER  fullName=Kasir Cabang 2
...
ID=8  username=kasir006   role=CASHIER  fullName=Kasir Cabang 6
```

---

## 📝 USERNAME RULES

```
Format: [role][NNN]
Where:
  - role: kasir atau admin (lowercase)
  - NNN: 3 digits (001-999)

Examples:
  ✅ kasir001, kasir007, kasir999, admin001, admin002
  ❌ Kasir001, kasir1, kasir0001, kasir 001, kasir-001

Auto-generate:
  1. Find latest user by role
  2. Extract number
  3. Increment by 1
  4. Format with zero-padding
  5. Return: kasir007 (if latest was kasir006)
```

---

## 🔐 PASSWORD RULES

```
Minimum: 8 characters
Must have: 
  - 1 Uppercase (A-Z)
  - 1 Lowercase (a-z)
  - 1 Digit (0-9)
Optional: Special chars allowed

Examples:
  ✅ Pass123, Admin@123, Kasir#456
  ❌ pass123 (no upper), Pass (no digit), Pas1 (too short)

Auto-generate:
  - 8 characters
  - Always includes: 1 Upper, 1 Lower, 1 Digit
  - Random alphanumeric
  - Example: Xb7kLm2Q, Qa9xBcPq

Strength Scoring:
  < 30  = Lemah (Weak)
  30-60 = Sedang (Medium)
  60-80 = Kuat (Strong)
  > 80  = Sangat Kuat (Very Strong)
```

---

## 🔄 API FLOW EXAMPLES

### Create User
```
REQUEST:
POST /api/admin/users
{
  "fullName": "Rudi Santoso",
  "role": "CASHIER",
  "generateUsername": true,
  "generatePassword": true
}

RESPONSE (201):
{
  "userId": 9,
  "username": "kasir007",
  "temporaryPassword": "Abc12345",
  "message": "User berhasil dibuat"
}

AUDIT LOG:
{
  "adminId": 1,
  "action": "CREATE_USER",
  "targetUserId": 9,
  "targetUsername": "kasir007",
  "newValues": {"fullName": "Rudi Santoso", ...},
  "ipAddress": "192.168.1.100"
}
```

### Reset Password
```
REQUEST:
POST /api/admin/users/7/reset-password
{"generatePassword": true}

RESPONSE (200):
{
  "userId": 7,
  "username": "kasir005",
  "temporaryPassword": "Xb7kLm2Q",
  "mustChangePassword": true
}

AUDIT LOG:
{
  "adminId": 1,
  "action": "RESET_PASSWORD",
  "targetUserId": 7,
  "description": "Password di-reset via admin"
}
```

### Delete User
```
REQUEST:
DELETE /api/admin/users/9

RESPONSE (204 No Content)

OR (409 Conflict if has transactions):
{
  "error": "Conflict",
  "message": "Tidak bisa hapus user karena masih punya transaksi"
}

AUDIT LOG:
{
  "adminId": 1,
  "action": "DELETE_USER",
  "targetUserId": 9,
  "targetUsername": "kasir007"
}
```

---

## ✅ VALIDATION CHECKLIST

### Before Create User
```
☐ Full Name: 3-100 chars, required
☐ Role: ADMIN atau CASHIER only
☐ Username: Format kasirNNN/adminNNN (if manual)
☐ Username: Must be unique
☐ Password: 8+ chars, upper, lower, digit (if manual)
☐ Email: Valid format & unique (if provided)
```

### Before Update User
```
☐ User exists (by ID)
☐ New Full Name: valid (if changed)
☐ New Role: valid (if changed)
☐ Status: valid boolean
☐ Username: CANNOT be changed (readonly)
☐ Password: CANNOT be changed here (use reset)
```

### Before Delete User
```
☐ User exists
☐ User NOT admin001 or admin002 (system accounts)
☐ User has NO active transactions
☐ Admin confirms delete
```

---

## 🧪 QUICK TEST CASES

### Test Username Generation
```sql
-- In PostgreSQL
SELECT get_next_username('CASHIER');
-- Expected: kasir007 (or next available)

SELECT get_next_username('ADMIN');
-- Expected: admin003 (or next available)
```

### Test Password Strength
```java
// In Java Test
PasswordValidator pv = new PasswordValidator();
assertTrue(pv.isValid("Pass123"));      // ✅
assertFalse(pv.isValid("pass123"));     // ❌ no upper
assertFalse(pv.isValid("Pass"));        // ❌ no digit
assertFalse(pv.isValid("Pas1"));        // ❌ too short

String pwd = pv.generateRandomPassword();
assertTrue(pv.isValid(pwd));            // ✅ always valid
```

### Test Duplicate Username
```java
// Should throw exception
assertThrows(ValidationException.class, () -> {
  userService.createUser(new UserDTO(...with username="kasir001"...));
});
```

### Test Delete with Transactions
```
Expected: 409 Conflict
Message: "Tidak bisa hapus user karena masih punya transaksi"
```

---

## 🚀 IMPLEMENTATION CHECKLIST

### Database
- [ ] Execute migration SQL
- [ ] Verify tables exist
- [ ] Verify views work
- [ ] Test get_next_username() function
- [ ] Verify sample data loaded (8 users)

### Java Code
- [ ] Copy all 5 utility/service/controller files
- [ ] Create 3 entity classes
- [ ] Create 2 repository interfaces
- [ ] Configure PasswordEncoder bean
- [ ] Add @PreAuthorize to controller

### Testing
- [ ] Unit test each validator
- [ ] Integration test each endpoint
- [ ] API test with Postman
- [ ] Security test (role-based access)
- [ ] Edge case tests

### Frontend
- [ ] Create user list page
- [ ] Add create user form
- [ ] Add edit user form
- [ ] Add reset password dialog
- [ ] Add delete confirmation
- [ ] Style with Bootstrap
- [ ] Add AJAX calls

### Deployment
- [ ] Code review
- [ ] Full test suite pass
- [ ] Deploy to staging
- [ ] Test in staging
- [ ] Deploy to production

---

## 🔐 SECURITY CHECKLIST

```
Authentication:
  ☐ JWT tokens configured
  ☐ Login endpoint validates credentials
  ☐ Token refresh configured
  ☐ Token expiration set

Authorization:
  ☐ @PreAuthorize on all endpoints
  ☐ Only ADMIN can create/edit/delete users
  ☐ Role checks at service layer
  ☐ IP address tracked in audit

Password:
  ☐ BCrypt hashing (cost 10)
  ☐ No plaintext in logs
  ☐ No plaintext in response body (except temporary)
  ☐ Password reset forces change on next login

Database:
  ☐ UNIQUE constraint on username
  ☐ NOT NULL on required fields
  ☐ FK constraints with cascade
  ☐ Audit log immutable

Input Validation:
  ☐ Username format checked
  ☐ Password strength checked
  ☐ Email format validated
  ☐ Role whitelist enforced
  ☐ SQL injection prevention (parameterized queries)
```

---

## 🆘 COMMON ERRORS & FIXES

```
Error: "BCryptPasswordEncoder not found"
Fix: Add spring-security-crypto to pom.xml

Error: "Duplicate entry 'kasir001' for key 'username'"
Fix: Check username uniqueness before create

Error: "Could not initialize UserAuditLogRepository"
Fix: Verify UserAuditLogRepository interface exists

Error: "Cannot delete user with ID 3"
Fix: Check if user has transactions using query:
     SELECT COUNT(*) FROM transactions WHERE user_id = 3

Error: "mustChangePassword is always false"
Fix: Verify field is properly persisted in database
     Check: UPDATE after reset password sets it to true

Error: "Generate username returns null"
Fix: Check if query is finding latest user correctly
     Test: SELECT * FROM users WHERE role = 'CASHIER' ORDER BY user_id DESC
```

---

## 📞 SUPPORT CONTACTS

```
Database Questions:
  → Check: sql/03_user_management_migration.sql
  → Run: SELECT * FROM v_user_management;
  → Call DBA for schema validation

Backend Development:
  → Check: docs/USER_MANAGEMENT_SYSTEM.md (full spec)
  → Check: IMPLEMENTASI_SUMMARY.md (API examples)
  → Copy code from: service/, controller/, util/ files

Frontend Development:
  → Check: USER_MANAGEMENT_SYSTEM.md section "Frontend"
  → Use provided HTML template
  → Test endpoints first with Postman

Testing & QA:
  → Use: DEVELOPER_CHECKLIST.md
  → Use: Test scenarios section
  → Report issues with: endpoint, request, response, expected

Deployment:
  → Follow: Phase 7 in DEVELOPER_CHECKLIST.md
  → Backup database first!
  → Verify migration runs successfully
```

---

## 💡 PRO TIPS

1. **Test Database First**
   ```sql
   -- Always verify migration worked
   SELECT * FROM user_audit_log;
   SELECT get_next_username('CASHIER');
   SELECT * FROM v_user_management;
   ```

2. **Auto-Generate over Manual Entry**
   - Let system generate username (format guaranteed)
   - Let system generate password (secure random)
   - Admin only manually enters full name

3. **Always Audit**
   - Every create/update/delete creates audit log
   - Cannot be deleted (immutable)
   - Good for compliance & troubleshooting

4. **Test Before Deployment**
   - Unit test each method isolated
   - Integration test all endpoints
   - API test with Postman before frontend
   - Full test suite run before production

5. **Password Best Practices**
   - Never store plaintext
   - Always hash with BCrypt
   - Force change on first login (mustChangePassword=true)
   - Never echo password back to client (except temporary)

6. **Error Handling**
   - Return appropriate HTTP status (201, 400, 409, 500)
   - Error messages should be helpful but not leaky
   - Log errors server-side for debugging
   - Don't expose stack traces to client

---

**Version**: 1.0  
**Last Updated**: 17 January 2026  
**Print & Laminate for Easy Reference** 📋
