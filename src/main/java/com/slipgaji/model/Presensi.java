package com.slipgaji.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Presensi {
    private int id;
    private int employeeId;
    private LocalDate tanggal;
    private LocalTime jam;
    private String jenisPresensi; // Masuk / Pulang
    private String createdAt;

    // JOIN fields
    private String employeeName;
    private String employeeIdCode;
    private String employeePosition;

    public Presensi() {}

    public Presensi(int employeeId, LocalDate tanggal, LocalTime jam, String jenisPresensi) {
        this.employeeId = employeeId;
        this.tanggal = tanggal;
        this.jam = jam;
        this.jenisPresensi = jenisPresensi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public LocalTime getJam() { return jam; }
    public void setJam(LocalTime jam) { this.jam = jam; }
    public String getJenisPresensi() { return jenisPresensi; }
    public void setJenisPresensi(String jenisPresensi) { this.jenisPresensi = jenisPresensi; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeIdCode() { return employeeIdCode; }
    public void setEmployeeIdCode(String employeeIdCode) { this.employeeIdCode = employeeIdCode; }
    public String getEmployeePosition() { return employeePosition; }
    public void setEmployeePosition(String employeePosition) { this.employeePosition = employeePosition; }
}
