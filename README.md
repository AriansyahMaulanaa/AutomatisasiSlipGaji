# Sistem Otomatisasi Slip Gaji Karyawan (SlipGajiPro)

SlipGajiPro merupakan aplikasi desktop berbasis Java yang digunakan untuk membuat dan mengirim slip gaji karyawan secara otomatis melalui email. Sistem ini dibuat untuk membantu proses penggajian agar lebih cepat, rapi, dan mengurangi kesalahan perhitungan manual.

Aplikasi ini dibangun sebagai bagian dari tugas mata kuliah Rekayasa Perangkat Lunak (RPL) di Universitas Pamulang dan juga diimplementasikan dalam kegiatan Kerja Praktek.

---

## Fitur Utama

Aplikasi ini memiliki beberapa fitur utama, yaitu:

* Sistem login dengan akses manajerial (General Manager / Supervisor)
* Import data presensi karyawan secara massal dari file Excel
* Perhitungan gaji, lembur, dan potongan secara otomatis
* Pembuatan slip gaji massal dalam format PDF dengan desain minimalis dan watermark
* Pengiriman slip gaji otomatis ke email masing-masing karyawan menggunakan SMTP
* Preview slip gaji sebelum dikirim
* Riwayat pengiriman slip gaji (Success / Error Log)
* Penyimpanan arsip penggajian ke database lokal

---

## Alur Kerja Sistem

1. Manager melakukan login ke dalam sistem aplikasi.
2. Manager mengimport file Excel presensi karyawan.
3. Sistem menghitung gaji kotor dan gaji bersih secara otomatis.
4. Sistem membuat slip gaji dalam format PDF.
5. Manager menekan tombol kirim, lalu slip gaji dikirim ke email masing-masing karyawan.
6. Data penggajian dan riwayat pengiriman disimpan sebagai arsip.

---

## Teknologi yang Digunakan

Sebagian besar proyek ini dikembangkan menggunakan teknologi berikut:

* Java 17 (Desktop Application)
* Java Swing & FlatLaf (User Interface)
* SQLite (Database lokal)
* Apache POI (Membaca file Excel)
* OpenPDF (Generate PDF slip gaji)
* Jakarta Mail / JavaMail API (Pengiriman Email SMTP)

---

## Tujuan Project

Tujuan dari pembuatan aplikasi ini adalah:

* Mengotomatisasi proses pembuatan slip gaji bulanan
* Meminimalisir kesalahan perhitungan gaji secara manual
* Mempercepat distribusi slip gaji ke karyawan melalui email
* Sebagai media pembelajaran dalam mata kuliah Rekayasa Perangkat Lunak
* Sebagai implementasi project IT pada kegiatan Kerja Praktek

---

## Catatan

Project desktop ini masih dalam tahap pengembangan dan akan terus dikembangkan, diperbaiki, serta ditambahkan fitur baru jika memungkinkan.

---

Terima kasih.
