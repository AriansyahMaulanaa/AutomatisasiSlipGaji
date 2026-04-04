package com.slipgaji.controller;

import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.ExcelService;
import com.slipgaji.service.SalaryCalculator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportController {
    private final ExcelService excelService;
    private final DatabaseService db;

    public ImportController() {
        this.excelService = new ExcelService();
        this.db = DatabaseService.getInstance();
    }

    public List<Employee> readExcel(File file) throws IOException {
        return excelService.readExcelFile(file);
    }

    public List<Payslip> processAndSave(List<Employee> employees, String period) {
        List<Payslip> payslips = new ArrayList<>();

        double overtimeRate = db.getSettingDouble("overtime_rate_per_hour", 25000);
        int divisor = (int) db.getSettingDouble("daily_rate_divisor", 22);
        double transport = db.getSettingDouble("transport_allowance", 500000);
        double meal = db.getSettingDouble("meal_allowance", 300000);

        SalaryCalculator calculator = new SalaryCalculator(overtimeRate, divisor, transport, meal);

        for (Employee emp : employees) {
            // Save/update employee
            int empDbId = db.saveOrUpdateEmployee(emp);
            emp.setId(empDbId);

            // Calculate salary
            Payslip payslip = calculator.calculate(emp, period);
            payslip.setEmployeeId(empDbId);

            // Save payslip
            int payslipId = db.savePayslip(payslip);
            payslip.setId(payslipId);

            // Set display fields
            payslip.setEmployeeName(emp.getName());
            payslip.setEmployeeEmail(emp.getEmail());
            payslip.setEmployeeIdCode(emp.getEmployeeId());
            payslip.setPosition(emp.getPosition());
            payslip.setDepartment(emp.getDepartment());

            payslips.add(payslip);
        }

        return payslips;
    }
}
