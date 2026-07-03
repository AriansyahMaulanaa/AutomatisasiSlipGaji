package com.slipgaji.controller;

import com.slipgaji.dao.KaryawanDAO;
import com.slipgaji.dao.PresensiDAO;
import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.SalaryCalculator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PresensiToPayslipController {

    private final KaryawanDAO karyawanDAO = new KaryawanDAO();
    private final PresensiDAO presensiDAO = new PresensiDAO();
    private final DatabaseService db = DatabaseService.getInstance();

    public static class GenerateResult {
        private final int successCount;
        private final int skipCount;
        private final List<String> messages;

        public GenerateResult(int successCount, int skipCount, List<String> messages) {
            this.successCount = successCount;
            this.skipCount = skipCount;
            this.messages = messages;
        }

        public int getSuccessCount() { return successCount; }
        public int getSkipCount() { return skipCount; }
        public List<String> getMessages() { return messages; }
    }

    public GenerateResult generatePayslipsFromPresensi(String period) {
        int successCount = 0;
        int skipCount = 0;
        List<String> messages = new ArrayList<>();

        String[] parts = period.split("-");
        if (parts.length != 2) {
            messages.add("Format periode tidak valid: " + period);
            return new GenerateResult(0, 0, messages);
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Employee> employees = karyawanDAO.getAll();

        for (Employee emp : employees) {
            try {
                int hariHadir = presensiDAO.getCountByDateRange(startDate, endDate, emp.getId());

                if (hariHadir == 0) {
                    skipCount++;
                    messages.add("Skip " + emp.getName() + ": tidak ada presensi di periode " + period);
                    continue;
                }

                String position = emp.getPosition() != null ? emp.getPosition() : "Crewstore";
                int totalHariKerja = (int) db.getSettingDouble(getSettingKey(position, "daily_rate_divisor"), 22);
                int hariAbsen = Math.max(0, totalHariKerja - hariHadir);

                double overtimeRate = db.getSettingDouble(getSettingKey(position, "overtime_rate_per_hour"), 25000);
                int divisor = totalHariKerja;
                double transport = db.getSettingDouble(getSettingKey(position, "transport_allowance"), 500000);
                double meal = db.getSettingDouble(getSettingKey(position, "meal_allowance"), 300000);
                double nightShiftRate = db.getSettingDouble(getSettingKey(position, "night_shift_rate"), 50000);

                SalaryCalculator calculator = new SalaryCalculator(overtimeRate, divisor, transport, meal, nightShiftRate);

                emp.setDaysPresent(hariHadir);
                emp.setDaysAbsent(hariAbsen);
                emp.setOvertimeHours(0);
                emp.setNightShift(false);

                Payslip payslip = calculator.calculate(emp, period);
                payslip.setEmployeeId(emp.getId());
                payslip.setDaysPresent(hariHadir);
                payslip.setDaysAbsent(hariAbsen);

                int payslipId = db.savePayslip(payslip);
                if (payslipId > 0) {
                    successCount++;
                    messages.add("OK " + emp.getName() + ": " + hariHadir + " hari hadir, gaji Rp " + String.format("%,.0f", payslip.getNetSalary()));
                } else {
                    skipCount++;
                    messages.add("Gagal " + emp.getName() + ": error menyimpan payslip");
                }
            } catch (Exception e) {
                skipCount++;
                messages.add("Error " + emp.getName() + ": " + e.getMessage());
            }
        }

        return new GenerateResult(successCount, skipCount, messages);
    }

    private String getSettingKey(String position, String param) {
        String pos = position.toUpperCase();
        return switch (pos) {
            case "STORE LEADER" -> "store_leader_" + param;
            case "MANAGER" -> "manager_" + param;
            default -> {
                String empType = position.equalsIgnoreCase("PKWT") ? "pkwt" :
                                 position.equalsIgnoreCase("KANTOR") ? "kantor" : "crewstore";
                yield empType + "_" + param;
            }
        };
    }
}
