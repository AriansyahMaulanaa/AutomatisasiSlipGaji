# Setup Database SlipGaji Pro

Database ini digunakan oleh aplikasi **SlipGaji Pro v1.1.0** — sistem otomatisasi slip gaji berbasis Java Swing dengan MariaDB.

## Persyaratan

- **MariaDB** 10.x atau **MySQL** 8.x
- **Java** 17 atau lebih baru (untuk menjalankan aplikasi)

## Nama Database

```
slipgaji_db
```

## Cara Install

### 1. Import Schema

Buka terminal / command prompt, lalu jalankan:

```bash
# MariaDB / MySQL
mysql -u root -p < database/schema.sql
```

Atau kalau ingin login dulu:

```bash
mysql -u root -p
```

lalu di dalam MySQL prompt:

```sql
SOURCE /absolute/path/AutomatisasiSlipGaji/database/schema.sql;
```

### 2. Konfigurasi Koneksi Database

Aplikasi membaca konfigurasi dari file **`config.properties`** di root project. File ini **di-.gitignore** (tidak ikut ke repo) supaya credential Anda tidak ke-commit.

Copy dari template:

```bash
cp config.properties.example config.properties
```

Isi dengan credential MariaDB lokal:

```properties
db_host=localhost
db_port=3306
db_name=slipgaji_db
db_user=root
db_password=YOUR_MARIADB_PASSWORD
```

Kalau `config.properties` tidak dibuat, aplikasi akan pakai default dari `com.slipgaji.util.Constants`:

| Konfigurasi | Nilai Default |
|-------------|---------------|
| Host        | `localhost`   |
| Port        | `3306`        |
| Database    | `slipgaji_db` |
| User        | `root`        |
| Password    | *(kosong)*    |

> **PENTING:** Password database ≠ password login aplikasi.
> - Password database (`db_password` di `config.properties`) = password MariaDB/MySQL laptop Anda.
> - Password login aplikasi (`spv123` / `manager123`) sama di semua laptop, sudah di-seed dari `schema.sql`.

### 3. Jalankan Aplikasi

Setelah database di-import, jalankan aplikasi dari root project:

```bash
mvn compile exec:java -Dexec.mainClass="com.slipgaji.App"
```

Atau build fat JAR dan jalankan langsung:

```bash
mvn clean package -DskipTests
java -jar target/SlipGajiPro-1.1.0.jar
```

Atau via VS Code: buka folder → tekan `F5` (config di `.vscode/launch.json`).

### 4. Login Aplikasi

Kredensial default (sama di semua laptop, sudah ada di `schema.sql`):

| Role    | Username   | Password      | Akses                                     |
|---------|------------|---------------|-------------------------------------------|
| SPV     | `spv`      | `spv123`      | Dashboard, Presensi Scan, Karyawan, Slip Gaji, Histori |
| Manager | `manager`  | `manager123`  | Semua menu SPV + **Pengaturan**           |

> Ganti password default setelah instalasi produksi.

## Struktur Tabel

| Tabel            | Isi                                                           |
|------------------|----------------------------------------------------------------|
| `users`          | Data login (SPV / Manager), password BCrypt                    |
| `employees`      | Data karyawan lengkap (nama, jabatan, gaji, foto, barcode, dst)|
| `presensi`       | Log presensi harian (masuk / pulang) hasil scan barcode        |
| `payslips`       | Data slip gaji per periode + PDF path                          |
| `send_history`   | Riwayat pengiriman email (sukses / gagal + error message)      |
| `settings`       | Konfigurasi (SMTP, parameter gaji per employment type)         |
| `schema_version` | Tracking migrasi database (dikelola otomatis oleh aplikasi)    |

Semua kolom yang dibutuhkan aplikasi (`employment_type`, `birth_date`, `photo`, `barcode`, `status`, `night_shift_incentive`, `is_night_shift`) sudah ada langsung di `CREATE TABLE`. Untuk database lama yang belum punya kolom-kolom ini, aplikasi otomatis menambah via runtime migration.

## Index

Index yang ditambahkan untuk optimasi query:

- `idx_users_role`
- `idx_employees_department`, `idx_employees_position`, `idx_employees_barcode`
- `idx_presensi_employee`, `idx_presensi_tanggal`, `idx_presensi_employee_tanggal`
- `idx_payslips_employee`, `idx_payslips_period`, `idx_payslips_employee_period`
- `idx_send_history_payslip`, `idx_send_history_period`, `idx_send_history_status`, `idx_send_history_period_status`
- Foreign key dengan `ON DELETE CASCADE` — data terkait (presensi, payslip, send_history) otomatis terhapus kalau parent-nya dihapus
