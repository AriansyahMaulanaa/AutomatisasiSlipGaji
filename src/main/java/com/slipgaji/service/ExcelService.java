package com.slipgaji.service;

import com.slipgaji.model.Employee;
import com.slipgaji.util.ValidationUtil;
import com.slipgaji.util.ValidationUtil.ValidationError;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {

    public static class ImportResult {
        private final List<Employee> employees;
        private final List<ValidationError> errors;
        private final List<ValidationError> warnings;

        public ImportResult(List<Employee> employees, List<ValidationError> errors, List<ValidationError> warnings) {
            this.employees = employees;
            this.errors = errors;
            this.warnings = warnings;
        }

        public List<Employee> getEmployees() { return employees; }
        public List<ValidationError> getErrors() { return errors; }
        public List<ValidationError> getWarnings() { return warnings; }
        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
    }

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
     * K: Shift Malam (Y/Ya/1 = true, optional column)
     * L: Tipe Karyawan (TETAP/PKWT/KANTOR, optional column)
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public ImportResult readExcelFile(File file) throws IOException {
        List<Employee> employees = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();
        List<ValidationError> warnings = new ArrayList<>();

        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".xlsx")) {
            throw new IOException("File bukan format Excel (.xlsx). File yang dipilih: " + file.getName());
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int startRow = 1;
            int lastRow = sheet.getLastRowNum();

            if (lastRow < 1) {
                warnings.add(new ValidationError(0, "Sheet", "Tabel Excel kosong. Tidak ada data yang ditemukan."));
                return new ImportResult(employees, errors, warnings);
            }

            for (int i = startRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell idCell = row.getCell(1);
                if (idCell == null || getCellStringValue(idCell).trim().isEmpty()) continue;

                int excelRow = i + 1;
                boolean rowHasError = false;

                Employee emp = new Employee();

                // B: Employee ID
                String empId = getCellStringValue(row.getCell(1));
                emp.setEmployeeId(empId);
                if (empId.isEmpty()) {
                    errors.add(new ValidationError(excelRow, "ID Karyawan", "ID Karyawan tidak boleh kosong"));
                    rowHasError = true;
                }

                // C: Name
                String name = getCellStringValue(row.getCell(2));
                emp.setName(name);
                if (name.isEmpty()) {
                    errors.add(new ValidationError(excelRow, "Nama", "Nama tidak boleh kosong"));
                    rowHasError = true;
                } else if (name.matches("\\d+")) {
                    errors.add(new ValidationError(excelRow, "Nama", "Nama tidak boleh hanya berisi angka"));
                    rowHasError = true;
                }

                // D: Email
                String email = getCellStringValue(row.getCell(3));
                emp.setEmail(email);
                if (email.isEmpty()) {
                    errors.add(new ValidationError(excelRow, "Email", "Email tidak boleh kosong"));
                    rowHasError = true;
                } else if (!ValidationUtil.isValidEmail(email)) {
                    errors.add(new ValidationError(excelRow, "Email", "Format email tidak valid: " + email));
                    rowHasError = true;
                }

                // E: Position
                String position = getCellStringValue(row.getCell(4));
                if (position.isEmpty()) {
                    warnings.add(new ValidationError(excelRow, "Posisi", "Posisi kosong"));
                } else if (position.matches("\\d+")) {
                    errors.add(new ValidationError(excelRow, "Posisi", "Posisi tidak boleh hanya berisi angka, ditemukan: " + position));
                    rowHasError = true;
                } else if (!position.equalsIgnoreCase("Crewstore")
                        && !position.equalsIgnoreCase("Store Leader")
                        && !position.equalsIgnoreCase("Manager")) {
                    errors.add(new ValidationError(excelRow, "Posisi", "Posisi harus diisi Crewstore, Store Leader, atau Manager. Ditemukan: " + position));
                    rowHasError = true;
                }
                emp.setPosition(position);

                // F: Department
                String department = getCellStringValue(row.getCell(5));
                if (!department.isEmpty() && department.matches("\\d+")) {
                    errors.add(new ValidationError(excelRow, "Departemen", "Departemen tidak boleh hanya berisi angka, ditemukan: " + department));
                    rowHasError = true;
                }
                emp.setDepartment(department);

                // G: Base Salary
                Cell salaryCell = row.getCell(6);
                String salaryRaw = getCellStringValue(salaryCell);
                double baseSalary = 0;
                if (salaryRaw.isEmpty()) {
                    warnings.add(new ValidationError(excelRow, "Gaji Pokok", "Gaji Pokok kosong, diisi 0"));
                } else if (!isCellNumeric(salaryCell)) {
                    errors.add(new ValidationError(excelRow, "Gaji Pokok", "Gaji Pokok harus berisi angka, ditemukan: " + salaryRaw));
                    rowHasError = true;
                } else {
                    baseSalary = getCellNumericValue(salaryCell);
                    if (baseSalary <= 0) {
                        warnings.add(new ValidationError(excelRow, "Gaji Pokok", "Gaji Pokok bernilai 0 atau kosong"));
                    } else if (baseSalary > 1_000_000_000) {
                        warnings.add(new ValidationError(excelRow, "Gaji Pokok", "Gaji Pokok terlalu besar: " + baseSalary));
                    }
                }
                emp.setBaseSalary(baseSalary);

                // H: Days Present
                Cell presentCell = row.getCell(7);
                String presentRaw = getCellStringValue(presentCell);
                int daysPresent = 0;
                if (presentRaw.isEmpty()) {
                    warnings.add(new ValidationError(excelRow, "Hari Hadir", "Hari Hadir kosong, diisi 0"));
                } else if (!isCellNumeric(presentCell)) {
                    errors.add(new ValidationError(excelRow, "Hari Hadir", "Hari Hadir harus berisi angka, ditemukan: " + presentRaw));
                    rowHasError = true;
                } else {
                    daysPresent = (int) getCellNumericValue(presentCell);
                    if (daysPresent < 0 || daysPresent > 31) {
                        errors.add(new ValidationError(excelRow, "Hari Hadir", "Hari Hadir tidak masuk akal (0-31), ditemukan: " + daysPresent));
                        rowHasError = true;
                    } else if (daysPresent == 0) {
                        warnings.add(new ValidationError(excelRow, "Hari Hadir", "Hari Hadir bernilai 0"));
                    }
                }
                emp.setDaysPresent(daysPresent);

                // I: Days Absent
                Cell absentCell = row.getCell(8);
                String absentRaw = getCellStringValue(absentCell);
                int daysAbsent = 0;
                if (absentRaw.isEmpty()) {
                    warnings.add(new ValidationError(excelRow, "Hari Absen", "Hari Absen kosong, diisi 0"));
                } else if (!isCellNumeric(absentCell)) {
                    errors.add(new ValidationError(excelRow, "Hari Absen", "Hari Absen harus berisi angka, ditemukan: " + absentRaw));
                    rowHasError = true;
                } else {
                    daysAbsent = (int) getCellNumericValue(absentCell);
                    if (daysAbsent < 0 || daysAbsent > 31) {
                        errors.add(new ValidationError(excelRow, "Hari Absen", "Hari Absen tidak masuk akal (0-31), ditemukan: " + daysAbsent));
                        rowHasError = true;
                    } else if (daysAbsent == 0) {
                        warnings.add(new ValidationError(excelRow, "Hari Absen", "Hari Absen bernilai 0"));
                    }
                }
                emp.setDaysAbsent(daysAbsent);

                // Validasi total hari hadir + absen tidak melebihi 31
                if (!rowHasError && (daysPresent + daysAbsent) > 31) {
                    errors.add(new ValidationError(excelRow, "Kehadiran",
                        "Total hari hadir (" + daysPresent + ") + absen (" + daysAbsent + ") = "
                        + (daysPresent + daysAbsent) + " tidak boleh melebihi 31"));
                    rowHasError = true;
                }

                // J: Overtime Hours
                Cell overtimeCell = row.getCell(9);
                String overtimeRaw = getCellStringValue(overtimeCell);
                double overtimeHours = 0;
                if (overtimeRaw.isEmpty()) {
                    warnings.add(new ValidationError(excelRow, "Jam Lembur", "Jam Lembur kosong, diisi 0"));
                } else if (!isCellNumeric(overtimeCell)) {
                    errors.add(new ValidationError(excelRow, "Jam Lembur", "Jam Lembur harus berisi angka, ditemukan: " + overtimeRaw));
                    rowHasError = true;
                } else {
                    overtimeHours = getCellNumericValue(overtimeCell);
                    if (overtimeHours < 0 || overtimeHours > 240) {
                        warnings.add(new ValidationError(excelRow, "Jam Lembur", "Jam Lembur tidak wajar: " + overtimeHours));
                    } else if (overtimeHours == 0) {
                        warnings.add(new ValidationError(excelRow, "Jam Lembur", "Jam Lembur bernilai 0"));
                    }
                }
                emp.setOvertimeHours(overtimeHours);

                // K: Shift Malam (optional)
                Cell nightShiftCell = row.getCell(10);
                emp.setNightShift(isNightShiftValue(nightShiftCell));

                // L: Tipe Karyawan (optional)
                String typeRaw = getCellStringValue(row.getCell(11)).trim().toUpperCase();
                String employmentType = "TETAP";
                if (!typeRaw.isEmpty()) {
                    if (typeRaw.equals("PKWT") || typeRaw.equals("KANTOR") || typeRaw.equals("TETAP")) {
                        employmentType = typeRaw;
                    } else {
                        warnings.add(new ValidationError(excelRow, "Tipe Karyawan", "Tipe tidak dikenal: " + typeRaw + ", menggunakan default TETAP"));
                    }
                }
                emp.setEmploymentType(employmentType);

                if (!rowHasError) {
                    employees.add(emp);
                }
            }

            if (employees.isEmpty() && errors.isEmpty()) {
                warnings.add(new ValidationError(0, "Data", "Tidak ada data karyawan yang valid ditemukan dalam file."));
            }
        }

        return new ImportResult(employees, errors, warnings);
    }

    /**
     * Check if the cell value indicates night shift.
     * Accepts: Y, Ya, YES, 1, true (case-insensitive)
     */
    private boolean isNightShiftValue(Cell cell) {
        if (cell == null) return false;
        String val = getCellStringValue(cell).trim().toLowerCase();
        return val.equals("y") || val.equals("ya") || val.equals("yes")
                || val.equals("1") || val.equals("true");
    }

    /**
     * Check if a cell contains a numeric value (either NUMERIC type or parseable string)
     * Handles Indonesian format: "." = thousands separator, "," = decimal separator
     */
    private boolean isCellNumeric(Cell cell) {
        if (cell == null) return false;
        if (cell.getCellType() == CellType.NUMERIC) return true;
        if (cell.getCellType() == CellType.STRING) {
            String val = cell.getStringCellValue().trim();
            if (val.isEmpty()) return false;
            try {
                parseIndonesianNumber(val);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                cell.getNumericCellValue();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Parse Indonesian number format to double.
     * Examples:
     *   "1.500.000"   -> 1500000
     *   "1.500.000,50" -> 1500000.50
     *   "1500000.50"  -> 1500000.50 (standard format)
     *   "1500000"     -> 1500000
     */
    private double parseIndonesianNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        String clean = value.trim();
        // Check if it contains comma (Indonesian decimal separator)
        if (clean.contains(",")) {
            // Remove all dots (thousands separators)
            clean = clean.replace(".", "");
            // Replace comma with dot (decimal)
            clean = clean.replace(",", ".");
        } else if (clean.contains(".")) {
            // Could be either thousands separators or decimal point
            // Count dots: if multiple dots, they're thousands separators
            int dotCount = clean.length() - clean.replace(".", "").length();
            if (dotCount > 1) {
                // Multiple dots = Indonesian thousands separators
                clean = clean.replace(".", "");
            }
            // Single dot could be decimal, keep as-is
        }
        return Double.parseDouble(clean);
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
                    yield parseIndonesianNumber(cell.getStringCellValue());
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
