package com.slipgaji.service;

import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;

public class SalaryCalculator {

    private double overtimeRatePerHour;
    private int dailyRateDivisor;
    private double transportAllowance;
    private double mealAllowance;
    private double nightShiftRate;

    public SalaryCalculator(double overtimeRatePerHour, int dailyRateDivisor,
                            double transportAllowance, double mealAllowance,
                            double nightShiftRate) {
        this.overtimeRatePerHour = overtimeRatePerHour;
        this.dailyRateDivisor = dailyRateDivisor;
        this.transportAllowance = transportAllowance;
        this.mealAllowance = mealAllowance;
        this.nightShiftRate = nightShiftRate;
    }

    /**
     * Calculate salary breakdown:
     * - Daily rate = Base Salary / Work Days (22)
     * - Deductions = Daily Rate * Days Absent
     * - Overtime Pay = Overtime Hours * Overtime Rate Per Hour
     * - Night Shift Incentive = nightShiftRate (if night shift)
     * - Allowances = Transport + Meal
     * - Net Salary = Base Salary - Deductions + Overtime Pay + Allowances + Night Shift Incentive
     */
    public Payslip calculate(Employee employee, String period) {
        double baseSalary = employee.getBaseSalary();
        double dailyRate = baseSalary / dailyRateDivisor;

        double deductions = dailyRate * employee.getDaysAbsent();
        double overtimePay = employee.getOvertimeHours() * overtimeRatePerHour;
        double allowances = transportAllowance + mealAllowance;

        double nightShiftIncentive = 0;
        if (employee.isNightShift()) {
            nightShiftIncentive = nightShiftRate;
        }

        double netSalary = baseSalary - deductions + overtimePay + allowances + nightShiftIncentive;

        Payslip payslip = new Payslip();
        payslip.setPeriod(period);
        payslip.setDaysPresent(employee.getDaysPresent());
        payslip.setDaysAbsent(employee.getDaysAbsent());
        payslip.setOvertimeHours(employee.getOvertimeHours());
        payslip.setBaseSalary(baseSalary);
        payslip.setOvertimePay(overtimePay);
        payslip.setDeductions(deductions);
        payslip.setAllowances(allowances);
        payslip.setNightShiftIncentive(nightShiftIncentive);
        payslip.setNightShift(employee.isNightShift());
        payslip.setNetSalary(netSalary);

        return payslip;
    }

    // Getters for display
    public double getTransportAllowance() { return transportAllowance; }
    public double getMealAllowance() { return mealAllowance; }
    public double getOvertimeRatePerHour() { return overtimeRatePerHour; }
    public int getDailyRateDivisor() { return dailyRateDivisor; }
    public double getNightShiftRate() { return nightShiftRate; }
}
