# SlipGaji Pro — Sistem Otomatisasi Slip Gaji Karyawan

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36.svg)](https://maven.apache.org/)
[![MariaDB](https://img.shields.io/badge/MariaDB-10.x-003545.svg)](https://mariadb.org/)
[![License](https://img.shields.io/badge/License-Academic-informational.svg)](#lisensi)

**SlipGaji Pro** adalah aplikasi desktop Java Swing untuk otomatisasi payroll: kelola karyawan, presensi via barcode scan, generate slip gaji PDF, dan pengiriman email SMTP secara massal.

Dibangun sebagai project mata kuliah **Rekayasa Perangkat Lunak (RPL)** di Universitas Pamulang, sekaligus diimplementasikan di kegiatan **Kerja Praktek**.

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Screenshot](#screenshot)
- [Teknologi](#teknologi)
- [Prasyarat](#prasyarat)
- [Instalasi](#instalasi)
  - [1. Clone Repository](#1-clone-repository)
  - [2. Setup Database](#2-setup-database)
  - [3. Konfigurasi Koneksi](#3-konfigurasi-koneksi)
  - [4. Build & Jalankan](#4-build--jalankan)
  - [5. Login Aplikasi](#5-login-aplikasi)
- [Konfigurasi SMTP](#konfigurasi-smtp)
- [Alur Kerja Sistem](#alur-kerja-sistem)
- [Struktur Project](#struktur-project)
- [Troubleshooting](#troubleshooting)
- [Lisensi](#lisensi)

---

## Fitur Utama

- Autentikasi berbasis role: **Supervisor (SPV)** & **Manager**
- Kelola data karyawan (CRUD) lengkap dengan foto, barcode, dan jenis kepegawaian (TETAP / PKWT / KANTOR)
- **Presensi Scan Barcode** via webcam — deteksi otomatis untuk absen masuk & pulang
- Riwayat presensi harian per karyawan
- Perhitungan gaji otomatis (gaji pokok, lembur, tunjangan, potongan absensi, insentif shift malam)
- Generate slip gaji **PDF** dengan desain minimalis dan watermark
- Pengiriman slip gaji ke email karyawan via **SMTP** (Gmail App Password, dll)
- Log riwayat pengiriman lengkap dengan status sukses / gagal
- Konfigurasi parameter gaji per jabatan (Crewstore / Store Leader / Manager)
- UI **enterprise minimalist** soft-blue dengan sidebar dark navy

---

## Screenshot

> Screenshot bisa ditambahkan di sini (Login / Dashboard / Presensi Scan / Slip Gaji).

---

## Teknologi

| Layer            | Teknologi                                    |
|------------------|----------------------------------------------|
| Bahasa           | **Java 17**                                  |
| UI Framework     | Java Swing + **FlatLaf**                     |
| Build Tool       | **Maven** 3.8+                               |
| Database         | **MariaDB** 10.x / **MySQL** 8.x             |
| Connection Pool  | HikariCP                                     |
| PDF              | OpenPDF                                      |
| Email            | Jakarta Mail (SMTP)                          |
| Excel Import     | Apache POI                                   |
| Barcode Scan     | ZXing + webcam-capture (sarxos)              |
| Password         | jBCrypt                                      |

---

## Prasyarat

Sebelum instalasi, pastikan sudah terpasang:

- **JDK 17** atau lebih baru
  ```bash
  java -version
  ```
- **Maven 3.8+**
  ```bash
  mvn -v
  ```
- **MariaDB 10.x** (atau MySQL 8.x)
  ```bash
  mysql --version
  ```
- **Webcam** (opsional — hanya jika ingin memakai fitur Presensi Scan)

---

## Instalasi

### 1. Clone Repository

```bash
git clone https://github.com/AriansyahMaulanaa/AutomatisasiSlipGaji.git
cd AutomatisasiSlipGaji
```

### 2. Setup Database

Login ke MariaDB / MySQL, lalu import schema:

```bash
mysql -u root -p < database/schema.sql
```

Atau via MySQL prompt:

```sql
mysql -u root -p
SOURCE /absolute/path/AutomatisasiSlipGaji/database/schema.sql;
```

Yang akan terbuat:

- Database **`slipgaji_db`**
- 7 tabel: `users`, `employees`, `presensi`, `payslips`, `send_history`, `settings`, `schema_version`
- 2 user default (password plain, akan otomatis di-hash BCrypt saat aplikasi jalan pertama):
  - `spv / spv123` (role SPV)
  - `manager / manager123` (role Manager)
- Seed setting default (SMTP kosong, parameter gaji per jabatan)

> **Catatan:** Schema di `database/schema.sql` sudah lengkap termasuk semua kolom hasil migrasi (`birth_date`, `photo`, `barcode`, `status`, `employment_type`, `night_shift_incentive`, dll). Aplikasi juga menjalankan migrasi otomatis via `schema_version` — jadi database lama yang sudah pernah pakai versi sebelumnya akan otomatis di-upgrade.

### 3. Konfigurasi Koneksi

Aplikasi membaca konfigurasi database dari file **`config.properties`** di root project.

Copy dari template lalu edit:

```bash
cp config.properties.example config.properties
```

Isi sesuai environment lokal Anda:

```properties
# Database
db_host=localhost
db_port=3306
db_name=slipgaji_db
db_user=root
db_password=YOUR_MARIADB_PASSWORD
```

> **Password database ≠ password login aplikasi.**
> - `db_password` di `config.properties` = password root MariaDB di komputer Anda
> - Password login aplikasi (`spv123` / `manager123`) sudah ada di database, sama di semua komputer

Kalau `config.properties` tidak ada / property tidak diisi, aplikasi akan pakai default di `com.slipgaji.util.Constants`.

### 4. Build & Jalankan

**Cara 1 — Via Maven (development):**

```bash
mvn clean compile exec:java -Dexec.mainClass="com.slipgaji.App"
```

**Cara 2 — Build fat JAR (produksi):**

```bash
mvn clean package -DskipTests
java -jar target/SlipGajiPro-1.1.0.jar
```

**Cara 3 — Script cepat (Linux/Mac):**

```bash
./run.sh
```

**Cara 4 — Via VS Code:**

Buka folder di VS Code → tekan `F5` atau klik **Run** di panel debugger (config sudah ada di `.vscode/launch.json`).

### 5. Login Aplikasi

Default credential (hanya untuk instalasi baru):

| Role    | Username   | Password       | Akses                                                    |
|---------|------------|----------------|----------------------------------------------------------|
| SPV     | `spv`      | `spv123`       | Dashboard, Presensi Scan, Kelola Karyawan, Slip Gaji, Histori |
| Manager | `manager`  | `manager123`   | Semua menu SPV + **Pengaturan** (SMTP, parameter gaji)   |

> **Ganti password default** setelah instalasi produksi untuk keamanan.

---

## Konfigurasi SMTP

Untuk pengiriman email slip gaji:

1. Login sebagai **Manager**
2. Buka menu **Pengaturan**
3. Isi bagian **Konfigurasi SMTP**:
   - Host: `smtp.gmail.com`
   - Port: `587`
   - Email: alamat pengirim Gmail
   - Password: **Gmail App Password** (bukan password login Gmail biasa — [cara buat App Password](https://support.google.com/accounts/answer/185833))
4. Klik **Test Koneksi** untuk verifikasi
5. Klik **Simpan SMTP**

Selain Gmail, provider SMTP lain juga bisa (Outlook, Yahoo, Zoho, custom mail server).

---

## Alur Kerja Sistem

```
┌─────────┐      ┌────────────┐      ┌──────────────┐      ┌─────────┐
│  Login  │ ───→ │  Presensi  │ ───→ │ Generate     │ ───→ │  Kirim  │
│(SPV/Mgr)│      │Scan Barcode│      │ Slip Gaji PDF│      │  Email  │
└─────────┘      └────────────┘      └──────────────┘      └─────────┘
                       ▲                      │                  │
                       │                      ▼                  ▼
                 (harian)                (bulanan)          (Histori)
```

1. **SPV** login dan melakukan scan barcode karyawan setiap hari
2. Sistem otomatis mencatat jam masuk dan jam pulang
3. Akhir bulan, **Manager** membuka menu Slip Gaji dan klik **Generate dari Presensi**
4. Sistem otomatis menghitung: gaji pokok, lembur, tunjangan, potongan, insentif shift malam
5. Manager klik **Kirim Mode Batch** → semua slip terkirim ke email masing-masing karyawan
6. Semua pengiriman tercatat di menu **Histori** (sukses / gagal + error detail)

---

## Struktur Project

```
AutomatisasiSlipGaji/
├── database/
│   ├── schema.sql                # Canonical DB schema
│   └── README.md                 # Detail setup DB
├── src/main/
│   ├── java/com/slipgaji/
│   │   ├── App.java              # Main entry
│   │   ├── controller/           # UI ↔ service bridge
│   │   ├── dao/                  # Data access objects
│   │   ├── model/                # Entity/DTO
│   │   ├── service/              # Business logic (DB, PDF, email, salary calc)
│   │   ├── ui/
│   │   │   ├── theme/            # Design token (UIColors, UIFonts, UIMetrics, Theme)
│   │   │   └── components/       # Reusable UI (AppButton, StatusBadge, AppCard, dll)
│   │   ├── view/                 # Halaman & dialog (Login, Dashboard, Presensi, dst)
│   │   └── util/                 # Constants, helper, validation
│   └── resources/db/init.sql     # Schema yang dijalankan aplikasi saat startup
├── output/
│   ├── pdf/                      # Output PDF per periode
│   └── foto/                     # Foto karyawan
├── config.properties.example     # Template konfigurasi
├── pom.xml                       # Maven build config
├── run.sh                        # Script cepat jalankan aplikasi
└── README.md                     # File ini
```

---

## Troubleshooting

### Error: "Database initialization failed"

- Cek MariaDB / MySQL sudah running (`sudo systemctl status mariadb` di Linux)
- Cek `config.properties` — pastikan host, port, user, password benar
- Test manual: `mysql -u root -p slipgaji_db`

### Error: "Access denied for user 'root'"

- Password MariaDB Anda beda dengan yang ada di `config.properties`. Sesuaikan.

### Kamera tidak terdeteksi di menu Presensi Scan

- Pastikan laptop punya webcam / webcam eksternal terhubung
- Di Linux: butuh permission `/dev/video0`. Cek dengan `ls -la /dev/video*`
- Fitur ini opsional — semua menu lain tetap bisa dipakai tanpa webcam

### Email gagal terkirim: "Authentication failed"

- Kalau pakai Gmail: **wajib pakai App Password**, bukan password login Gmail. [Cara buat](https://support.google.com/accounts/answer/185833).
- Cek 2FA sudah aktif di akun Gmail (App Password hanya bisa dibuat kalau 2FA on).

### PDF tidak ter-generate

- Cek folder `output/pdf/` writable
- Cek log konsol — biasanya ada error stack trace kalau ada masalah OpenPDF

---

## Tujuan Project

- Otomatisasi pembuatan slip gaji bulanan
- Meminimalisir kesalahan perhitungan manual
- Mempercepat distribusi slip gaji via email
- Media pembelajaran mata kuliah Rekayasa Perangkat Lunak (RPL)
- Implementasi project IT di kegiatan Kerja Praktek

---

## Lisensi

Project ini dibuat untuk tujuan akademik. Bebas dipakai, dimodifikasi, dan dijadikan referensi belajar. Jika dipakai di lingkungan produksi, silakan menyesuaikan sesuai kebutuhan organisasi.

---

## Kontribusi

Pull request, issue, dan saran sangat terbuka. Silakan buka [issue](https://github.com/AriansyahMaulanaa/AutomatisasiSlipGaji/issues) jika menemukan bug atau punya usulan fitur.

---

Terima kasih! 🎉
