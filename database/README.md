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

Atau jika ingin login dulu:

```bash
mysql -u root -p
```

Kemudian jalankan:

```sql
SOURCE /path/to/project/database/schema.sql;
```

### 2. Konfigurasi Koneksi Database (WAJIB)

Aplikasi secara default akan terhubung ke:

| Konfigurasi | Nilai Default |
|-------------|---------------|
| Host        | `localhost`   |
| Port        | `3306`        |
| Database    | `slipgaji_db` |
| User        | `root`        |
| Password    | `[REDACTED]`   |

**⚠️ PENTING:** Password database (`DB_PASS`) adalah **password MariaDB/MySQL laptop Anda**, bukan password login aplikasi. Nilai `[REDACTED]` hanya berlaku di laptop pengembang. Setiap laptop bisa berbeda.

Jika password MariaDB Anda berbeda, edit file:

```
src/main/java/com/slipgaji/util/Constants.java
```

Cari baris berikut dan sesuaikan `DB_PASS` dengan password MariaDB Anda:

```java
public static final String DB_HOST = "localhost";
public static final int DB_PORT = 3306;
public static final String DB_NAME = "slipgaji_db";
public static final String DB_USER = "root";
public static final String DB_PASS = "[REDACTED]";   // ← ganti ini!
```

### 3. Jalankan Aplikasi

Setelah database terimport, jalankan aplikasi:

```bash
mvn compile exec:java -Dexec.mainClass="com.slipgaji.App"
```

Atau buka project di **VS Code** → klik tombol Run (App) di bagian atas.

### 4. Login Aplikasi

**Ini adalah password login aplikasi — SAMA di semua laptop** (sudah di-set di `schema.sql`).

| Role    | Username   | Password      | Akses                     |
|---------|------------|---------------|---------------------------|
| SPV     | `spv`      | `spv123`      | Dashboard, Import, Slip Gaji, Histori |
| Manager | `manager`  | `manager123`  | Semua menu + Pengaturan   |

> **Jangan bingung:** password login aplikasi (`spv123` / `manager123`) **berbeda** dengan password database MariaDB yang dikonfigurasi di `Constants.java`.

## Struktur Tabel

- **users** — Data login pengguna (SPV / Manager)
- **employees** — Data karyawan hasil import
- **payslips** — Data slip gaji per periode
- **send_history** — Riwayat pengiriman email
- **settings** — Konfigurasi aplikasi (SMTP, parameter gaji, dll)
