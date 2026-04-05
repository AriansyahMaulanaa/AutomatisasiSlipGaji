package com.slipgaji.service;

import com.slipgaji.model.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {

    /**
     * Expected Excel columns:
     * A: No
     * B: Employee ID
     * C: Name
     * D: Email
     * E: Position
     * F: Department
     * G: Base Salary
     * H: Days Present
     * I: Days Absent
     * J: Overtime Hours
     */
    public List<Employee> readExcelFile(File file) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int startRow = 1; // Skip header row

            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Skip empty rows by checking column 1 (ID Karyawan)
                Cell idCell = row.getCell(1);
                if (idCell == null || getCellStringValue(idCell).trim().isEmpty()) continue;

                try {
                    Employee emp = new Employee();
                    emp.setEmployeeId(getCellStringValue(row.getCell(1))); // B
                    emp.setName(getCellStringValue(row.getCell(2)));       // C
                    emp.setEmail(getCellStringValue(row.getCell(3)));      // D
                    emp.setPosition(getCellStringValue(row.getCell(4)));   // E
                    emp.setDepartment(getCellStringValue(row.getCell(5))); // F
                    emp.setBaseSalary(getCellNumericValue(row.getCell(6)));// G
                    emp.setDaysPresent((int) getCellNumericValue(row.getCell(7)));  // H
                    emp.setDaysAbsent((int) getCellNumericValue(row.getCell(8)));   // I
                    emp.setOvertimeHours(getCellNumericValue(row.getCell(9)));      // J

                    // Validate required fields
                    if (emp.getEmployeeId().isEmpty() || emp.getName().isEmpty() || emp.getEmail().isEmpty()) {
                        System.err.println("Skipping row " + (i + 1) + ": Missing required fields");
                        continue;
                    }

                    employees.add(emp);
                } catch (Exception e) {
                    System.err.println("Error parsing row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return employees;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                // If it looks like an integer, format without decimal
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim().replace(",", "").replace(".", ""));
                } catch (NumberFormatException e) {
                    yield 0.0;
                }
            }
            case FORMULA -> {
                try {
                    yield cell.getNumericCellValue();
                } catch (Exception e) {
                    yield 0.0;
                }
            }
            default -> 0.0;
        };
    }
}
