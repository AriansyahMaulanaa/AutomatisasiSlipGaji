package com.slipgaji.controller;

import com.slipgaji.dao.KaryawanDAO;
import com.slipgaji.dao.PresensiDAO;
import com.slipgaji.model.Employee;
import com.slipgaji.model.Presensi;

import java.time.LocalDate;
import java.time.LocalTime;

public class PresensiController {

    private final KaryawanDAO karyawanDAO = new KaryawanDAO();
    private final PresensiDAO presensiDAO = new PresensiDAO();

    public static class PresensiResult {
        private final boolean success;
        private final String message;
        private final Employee employee;
        private final Presensi presensi;

        public PresensiResult(boolean success, String message, Employee employee, Presensi presensi) {
            this.success = success;
            this.message = message;
            this.employee = employee;
            this.presensi = presensi;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Employee getEmployee() { return employee; }
        public Presensi getPresensi() { return presensi; }
    }

    public PresensiResult processBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return new PresensiResult(false, "Barcode kosong", null, null);
        }

        Employee employee = karyawanDAO.findByBarcode(barcode.trim());
        if (employee == null) {
            return new PresensiResult(false, "Karyawan tidak ditemukan", null, null);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String jenis;
        if (!presensiDAO.isAlreadyCheckedIn(employee.getId(), today)) {
            jenis = "Masuk";
        } else if (!presensiDAO.isAlreadyCheckedOut(employee.getId(), today)) {
            jenis = "Pulang";
        } else {
            return new PresensiResult(true, "Sudah presensi Masuk dan Pulang hari ini", employee, null);
        }

        Presensi presensi = new Presensi(employee.getId(), today, now, jenis);
        int id = presensiDAO.save(presensi);
        if (id > 0) {
            presensi.setId(id);
            return new PresensiResult(true, "Presensi Berhasil - " + jenis, employee, presensi);
        } else {
            return new PresensiResult(false, "Gagal menyimpan presensi", employee, null);
        }
    }

    public PresensiResult processBarcodeWithType(String barcode, String tipe) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return new PresensiResult(false, "Barcode kosong", null, null);
        }

        Employee employee = karyawanDAO.findByBarcode(barcode.trim());
        if (employee == null) {
            return new PresensiResult(false, "Karyawan tidak ditemukan", null, null);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int empId = employee.getId();

        if ("Masuk".equals(tipe)) {
            if (presensiDAO.isAlreadyCheckedIn(empId, today)) {
                return new PresensiResult(false, "Anda sudah absen masuk hari ini, silakan absen pulang", employee, null);
            }
        } else if ("Pulang".equals(tipe)) {
            if (!presensiDAO.isAlreadyCheckedIn(empId, today)) {
                return new PresensiResult(false, "Anda belum absen masuk hari ini", employee, null);
            }
            if (presensiDAO.isAlreadyCheckedOut(empId, today)) {
                return new PresensiResult(false, "Anda sudah absen pulang hari ini", employee, null);
            }
        } else {
            return new PresensiResult(false, "Tipe presensi tidak valid", employee, null);
        }

        Presensi presensi = new Presensi(empId, today, now, tipe);
        int id = presensiDAO.save(presensi);
        if (id > 0) {
            presensi.setId(id);
            String msg = "Absen " + tipe + " berhasil";
            return new PresensiResult(true, msg, employee, presensi);
        } else {
            return new PresensiResult(false, "Gagal menyimpan presensi", employee, null);
        }
    }

    public Presensi getTodayStatus(int employeeId) {
        java.util.List<Presensi> list = presensiDAO.getTodayPresensi(employeeId);
        if (list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }
}
