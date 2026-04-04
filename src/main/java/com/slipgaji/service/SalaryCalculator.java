package com.slipgaji.service;

import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;

public class SalaryCalculator {

    private double overtimeRatePerHour;
    private int dailyRateDivisor;
    private double transportAllowance;
    private double mealAllowance;

    public SalaryCalculator(double overtimeRatePerHour, int dailyRateDivisor,
                            double transportAllowance, double mealAllowance) {
        this.overtimeRatePerHour = overtimeRatePerHour;
        this.dailyRateDivisor = dailyRateDivisor;
        this.transportAllowance = transportAllowance;
        this.mealAllowance = mealAllowance;
    }

    /**
     * Calculate salary breakdown:
     * - Daily rate = Base Salary / Work Days (22)
     * - Deductions = Daily Rate * Days Absent
     * - Overtime Pay = Overtime Hours * Overtime Rate Per Hour
     * - Allowances = Transport + Meal
     * - Net Salary = Base Salary - Deductions + Overtime Pay + Allowances
     */
    public Payslip calculate(Employee employee, String period) {
        double baseSalary = employee.getBaseSalary();
        double dailyRate = baseSalary / dailyRateDivisor;

        double deductions = dailyRate * employee.getDaysAbsent();
        double overtimePay = employee.getOvertimeHours() * overtimeRatePerHour;
        double allowances = transportAllowance + mealAllowance;
        double netSalary = baseSalary - deductions + overtimePay + allowances;

        Payslip payslip = new Payslip();
        payslip.setPeriod(period);
        payslip.setDaysPresent(employee.getDaysPresent());
        payslip.setDaysAbsent(employee.getDaysAbsent());
        payslip.setOvertimeHours(employee.getOvertimeHours());
        payslip.setBaseSalary(baseSalary);
        payslip.setOvertimePay(overtimePay);
        payslip.setDeductions(deductions);
        payslip.setAllowances(allowances);
        payslip.setNetSalary(netSalary);

        return payslip;
    }

    // Getters for display
    public double getTransportAllowance() { return transportAllowance; }
    public double getMealAllowance() { return mealAllowance; }
    public double getOvertimeRatePerHour() { return overtimeRatePerHour; }
    public int getDailyRateDivisor() { return dailyRateDivisor; }
}
