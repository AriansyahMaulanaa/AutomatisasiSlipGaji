# Sistem Otomatisasi Slip Gaji Karyawan (SlipGajiPro)

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

## Alur Kerja Sistem

1. Manager melakukan login ke dalam sistem aplikasi.
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
