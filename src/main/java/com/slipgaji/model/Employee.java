package com.slipgaji.model;

public class Employee {
    private int id;
    private String employeeId;
    private String name;
    private String email;
    private String position;
    private String department;
    private double baseSalary;

    // Transient fields (from Excel import, not stored in employees table)
    private int daysPresent;
    private int daysAbsent;
    private double overtimeHours;

    public Employee() {}

    public Employee(String employeeId, String name, String email, String position,
                    String department, double baseSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.position = position;
        this.department = department;
        this.baseSalary = baseSalary;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public int getDaysPresent() { return daysPresent; }
    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }
    public int getDaysAbsent() { return daysAbsent; }
    public void setDaysAbsent(int daysAbsent) { this.daysAbsent = daysAbsent; }
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }

    @Override
    public String toString() {
        return name + " (" + employeeId + ")";
    }
}
