# 🤝 PANDUAN KOLABORASI WEEK 15

## Setup Awal

### 1. Clone Repository
```bash
git clone [repo-url] week15
cd week15
```

### 2. Setup Database (Person A)
```bash
# Install PostgreSQL
# Create database
psql -U postgres -c "CREATE DATABASE agripos;"

# Run schema
psql -U postgres -d agripos -f sql/schema.sql

# Run seed data
psql -U postgres -d agripos -f sql/seed.sql
```

### 3. Setup Git Local
```bash
# Configure git identity
git config user.name "[Nama Anda]"
git config user.email "[Email Anda]"

# Create & switch ke develop branch
git checkout -b develop origin/develop
```

---

## Workflow Harian

### 1. Mulai Kerja
```bash
# Update latest code dari develop
git checkout develop
git pull origin develop

# Buat feature branch dari develop
git checkout -b feature/[nama-fitur]
# Contoh: feature/payment-method, feature/login, feature/discount
```

### 2. Saat Coding
```bash
# Commit berkala (jangan menunggu besar-besaran)
git add [file-yang-diubah]
git commit -m "feat: deskripsi singkat"

# Push ke repository
git push origin feature/[nama-fitur]
```

### 3. Pull Request
```bash
# Saat feature selesai, buat PR di GitHub/GitLab
# - Title: [Task] Deskripsi
# - Description: Apa yang diubah, testing yang sudah dilakukan
# - Link ke issue/task (jika ada)
```

### 4. Code Review
```
Tunggu minimal 1 team member review:
- Apakah kode sesuai architecture?
- Apakah ada error/bug?
- Apakah test case sudah dibuat?

Setelah approval: MERGE ke develop
```

### 5. Selesai Hari
```bash
# Commit terakhir
git add .
git commit -m "[Task] [Status hari ini]"
git push origin feature/[nama-fitur]

# Update team di group chat/Discord
```

---

## Pembagian Task & Branch

### Person A: Database Layer
- Branch: `feature/database` + `feature/product-dao` + `feature/user-dao`
- Task:
  - ✅ Setup PostgreSQL
  - ✅ DDL schema.sql
  - ✅ Seed data
  - ✅ ProductDAOImpl (JDBC)
  - ✅ UserDAOImpl (JDBC)
  - ✅ DatabaseConnection (Singleton)
  - ✅ OFR-4: Inventory enhancement
- Testing: Unit test DAO methods
- Commit: `feat(dao): implement ProductDAO with JDBC`

### Person B: Service Layer & Business Logic
- Branch: `feature/product-service` + `feature/discount-strategy`
- Task:
  - ✅ ProductServiceImpl
  - ✅ DiscountStrategy (interface + implementations)
  - ✅ PercentageDiscount
  - ✅ FixedDiscount
  - ✅ Validasi business logic
- Testing: Unit test service methods
- Commit: `feat(service): implement ProductService & DiscountStrategy`

### Person C: Payment & Transaction
- Branch: `feature/payment-method` + `feature/transaction-service` + `feature/audit-log`
- Task:
  - ✅ PaymentMethod (interface)
  - ✅ CashPayment (implementation)
  - ✅ EWalletPayment (implementation)
  - ✅ TransactionServiceImpl
  - ✅ AuditLogServiceImpl (OFR-6)
  - ✅ Exception handling
- Testing: Unit test payment & transaction
- Commit: `feat(payment): add PaymentMethod interface & implementations`

### Person D: Frontend & Authentication
- Branch: `feature/login-view` + `feature/auth-service` + `feature/ui-improvement`
- Task:
  - ✅ AuthServiceImpl
  - ✅ LoginView (JavaFX)
  - ✅ AdminDashboard
  - ✅ Role-based access control
  - ✅ PosView improvement
  - ✅ PaymentMethodSelector UI
  - ✅ ReportView
- Testing: Manual UI testing
- Commit: `feat(auth): implement login & role management`

### Person E: Testing & Documentation
- Branch: `feature/testing` + `feature/documentation`
- Task:
  - ✅ Write unit test (JUnit)
  - ✅ Manual test cases (8 minimum)
  - ✅ Test report & evidence (screenshots)
  - ✅ UML diagrams
  - ✅ API documentation
  - ✅ Setup guide
  - ✅ Troubleshooting guide
- Commit: `test(service): add unit test for checkout flow`

---

## Conflict Resolution

### Jika terjadi conflict:

```bash
# Tarik latest develop
git fetch origin develop

# Merge develop ke feature branch
git merge origin/develop

# Resolve conflicts di editor
# - Edit file conflict
# - Remove conflict markers (<<<<, ====, >>>>)
# - Keep logic yang benar

# Selesaikan merge
git add [file-conflict]
git commit -m "fix(merge): resolve conflict dengan develop"
git push origin feature/[nama-fitur]
```

### Best practices:
- **Frequent pulls** dari develop (1x sehari minimal)
- **Atomic commits** (satu fitur = satu commit)
- **Small PR** (jangan tunggu 100 file berubah)
- **Quick merge** (review & merge dalam 1-2 hari)

---

## Daily Standup Format

### 10.00 pagi (atau sesuai kesepakatan):
```
Person A: 
- Kemarin: Setup database & schema ✅
- Hari ini: Implement ProductDAO
- Blocker: Koneksi timeout (investigating)

Person B:
- Kemarin: Design ProductService interface ✅
- Hari ini: Implement DiscountStrategy pattern
- Blocker: Tunggu DAO selesai

[dst...]
```

---

## Merge ke Main (Final)

Setelah semua feature di-test & integrated di develop:

```bash
# Update develop dengan latest
git checkout develop
git pull origin develop

# Merge develop ke main (Production)
git checkout main
git pull origin main
git merge --no-ff develop -m "release: week15 final"

# Tag untuk version
git tag -a v1.0 -m "Week 15 Final Release"

# Push semua
git push origin main develop --tags
```

---

## Tools & Utilities

### Git Cheat Sheet
```bash
# Lihat status
git status

# Lihat branch
git branch -a

# Lihat commit log
git log --oneline

# Undo last commit (belum push)
git reset HEAD~1

# Stash (temporary save)
git stash
git stash pop

# Rebase (clean history)
git rebase origin/develop

# Force push (HATI-HATI!)
git push origin feature/[nama] --force
```

### IDE Integration
- **IntelliJ**: View → Version Control → Git Branches
- **VS Code**: Source Control sidebar (Ctrl+Shift+G)
- **Eclipse**: Team → Git

### Database Tools
- **DBeaver**: GUI untuk query PostgreSQL
- **pgAdmin**: Web interface PostgreSQL
- **psql**: Command line client

---

## Troubleshooting

### Error: "Permission denied"
```bash
# Setup SSH key
ssh-keygen -t rsa -b 4096

# Copy ke GitHub/GitLab settings
cat ~/.ssh/id_rsa.pub
```

### Error: "Network timeout"
```bash
# Increase timeout
git config --global http.postBuffer 524288000
```

### Error: "Merge conflict too complex"
```bash
# Abort merge & start over
git merge --abort

# Atau gunakan ours/theirs strategy
git merge -X ours develop
```

### Database connection error
```bash
# Check PostgreSQL running
pg_isready -h localhost -p 5432

# Check credentials di DatabaseConnection.java
# Verify database & user exist
psql -U postgres -l
```

---

## Success Criteria

Collaboration dianggap berhasil jika:

✅ Setiap anggota punya commit dengan deskripsi meaningful
✅ Minimal 2 PR per orang (code review ada)
✅ Tidak ada conflict yang tidak terselesaikan
✅ Merge ke develop lancar tanpa error
✅ Unit test berjalan > 80% success
✅ Documentation lengkap & updated
✅ Final merge ke main berhasil
✅ Tim bisa present hasil kerja bareng

---

## Reference

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Workflow](https://guides.github.com/introduction/flow/)
- [PostgreSQL JDBC](https://jdbc.postgresql.org/)
- [Java Database Connectivity](https://docs.oracle.com/javase/tutorial/jdbc/)

---

**Happy collaborating! Jangan malu tanya kalau bingung 🚀**
