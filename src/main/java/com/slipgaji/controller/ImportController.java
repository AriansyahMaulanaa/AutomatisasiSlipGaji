package com.slipgaji.controller;

import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.ExcelService;
import com.slipgaji.service.ExcelService.ImportResult;
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

    public ImportResult readExcel(File file) throws IOException {
        return excelService.readExcelFile(file);
    }

    public List<Payslip> processAndSave(List<Employee> employees, String period) {
        List<Payslip> payslips = new ArrayList<>();

        for (Employee emp : employees) {
            String position = emp.getPosition() != null ? emp.getPosition() : "Crewstore";

            double overtimeRate;
            int divisor;
            double transport;
            double meal;
            double nightShiftRate;

            switch (position.toUpperCase()) {
                case "STORE LEADER":
                    overtimeRate = db.getSettingDouble("store_leader_overtime_rate_per_hour", 30000);
                    divisor = (int) db.getSettingDouble("store_leader_daily_rate_divisor", 22);
                    transport = db.getSettingDouble("store_leader_transport_allowance", 600000);
                    meal = db.getSettingDouble("store_leader_meal_allowance", 350000);
                    nightShiftRate = db.getSettingDouble("store_leader_night_shift_rate", 55000);
                    break;
                case "MANAGER":
                    overtimeRate = db.getSettingDouble("manager_overtime_rate_per_hour", 35000);
                    divisor = (int) db.getSettingDouble("manager_daily_rate_divisor", 22);
                    transport = db.getSettingDouble("manager_transport_allowance", 700000);
                    meal = db.getSettingDouble("manager_meal_allowance", 400000);
                    nightShiftRate = db.getSettingDouble("manager_night_shift_rate", 60000);
                    break;
                default: // Crewstore
                    overtimeRate = db.getSettingDouble("crewstore_overtime_rate_per_hour", 25000);
                    divisor = (int) db.getSettingDouble("crewstore_daily_rate_divisor", 22);
                    transport = db.getSettingDouble("crewstore_transport_allowance", 500000);
                    meal = db.getSettingDouble("crewstore_meal_allowance", 300000);
                    nightShiftRate = db.getSettingDouble("crewstore_night_shift_rate", 50000);
                    break;
            }

            SalaryCalculator calculator = new SalaryCalculator(overtimeRate, divisor, transport, meal, nightShiftRate);

            int empDbId = db.saveOrUpdateEmployee(emp);
            emp.setId(empDbId);

            Payslip payslip = calculator.calculate(emp, period);
            payslip.setEmployeeId(empDbId);

            int payslipId = db.savePayslip(payslip);
            payslip.setId(payslipId);

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
