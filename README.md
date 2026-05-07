# Sistem Otomatisasi Slip Gaji Karyawan (SlipGajiPro)

<<<<<<< HEAD
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
=======
Project ini merupakan aplikasi berbasis Java Desktop yang digunakan untuk membuat dan mengirim slip gaji karyawan secara otomatis melalui email. Sistem ini dibuat untuk membantu proses penggajian agar lebih cepat, rapi, dan mengurangi kesalahan perhitungan manual.

Aplikasi ini dibangun sebagai bagian dari tugas mata kuliah Rekayasa Perangkat Lunak (RPL) di Universitas Pamulang dan juga diimplementasikan dalam kegiatan Kerja Praktek.

## Fitur Utama

Aplikasi ini memiliki beberapa fitur komprehensif, yaitu:

* Akses khusus (Login) tingkat manajerial (General Manager).
* Import data presensi karyawan otomatis secara massal dari file Excel.
* Perhitungan gaji, lembur, serta pajak secara otomatis berdasarkan data kehadiran.
* Pembuatan slip gaji massal dalam format dokumen PDF elegan ber-watermark.
* Pengiriman slip gaji instan dan terenkripsi menggunakan SMTP ke email masing-masing karyawan.
* *Preview* slip gaji dan riwayat log (*History*) pengiriman (*success/error*).
* Manajemen dan integrasi *database* (*Arsip*) di sistem lokal perangkat.
>>>>>>> 0274c08

## Alur Kerja Sistem

1. Manager melakukan login ke dalam sistem aplikasi.
<<<<<<< HEAD
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
=======
2. Melakukan *import* berkas berekstensi Excel (`.xlsx`) yang memuat data presensi bulanan karyawan.
3. Sistem menghitung gaji kotor dan bersih tiap orang secara otomatis.
4. Sistem memproses slip digital dalam bentuk berkas PDF.
5. Manajer menekan tombol pengiriman, sehingga slip gaji diteruskan ke alamat email pribadi karyawan masing-masing.
6. Rekam arsip penggajian tersimpan dan dapat dilacak dengan indikator *batch* pengiriman.

## Dibangun Menggunakan

Sebagian besar proyek ini dikembangkan menggunakan *library* standar Java:
* **Java 17 (Desktop Application)**
* **Java Swing & FlatLaf** - Untuk desain antarmuka (*UI*) modern dan nyaman dipandang.
* **SQLite** - Database *offline* / lokal yang mandiri dan portabel.
* **Apache POI** - Untuk mendeteksi pembacaan kolom dari file Microsoft Excel.
* **OpenPDF** - Untuk memproduksi *layout* nota penggajian ke dalam PDF.
* **Jakarta Mail API (JavaMail)** - Mengantarkan surat (*email*) otomatis lewat SMTP.

## Tujuan Project

Tujuan dari perancangan dan pembuatan aplikasi ini adalah:

* Mengotomatisasi proses pembuatan slip gaji harian/bulanan di dalam perusahaan.
* Meminimalisasi *human-error* dari perhitungan gaji secara konvensional / manual.
* Mempermudah serta mempercepat laju distribusi slip gaji rahasia ke masing-masing karyawan.
* Menjadi media eksplorasi dan kewajiban karya pada mata kuliah Rekayasa Perangkat Lunak.
* Implementasi nyata project IT saat Kerja Praktek.

## Catatan

Project desktop ini masih dalam tahap *development* aktif dan akan terus dikembangkan, diperbaiki, serta ditambahkan fitur pendukung baru jika memungkinkan.

---

Terima Kasih!
>>>>>>> 0274c08
