package com.slipgaji.model;

public class Payslip {
    private int id;
    private int employeeId;
    private String period;
    private int daysPresent;
    private int daysAbsent;
    private double overtimeHours;
    private double baseSalary;
    private double overtimePay;
    private double deductions;
    private double allowances;
    private double netSalary;
    private String pdfPath;
    private String createdAt;

    // JOIN fields
    private String employeeName;
    private String employeeEmail;
    private String employeeIdCode;
    private String position;
    private String department;

    public Payslip() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public int getDaysPresent() { return daysPresent; }
    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }
    public int getDaysAbsent() { return daysAbsent; }
    public void setDaysAbsent(int daysAbsent) { this.daysAbsent = daysAbsent; }
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public double getAllowances() { return allowances; }
    public void setAllowances(double allowances) { this.allowances = allowances; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public String getEmployeeIdCode() { return employeeIdCode; }
    public void setEmployeeIdCode(String employeeIdCode) { this.employeeIdCode = employeeIdCode; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
