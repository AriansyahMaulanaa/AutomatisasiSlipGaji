package com.slipgaji.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    public static boolean isValidPort(String port) {
        try {
            int p = Integer.parseInt(port.trim());
            return p > 0 && p <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim();
    }

    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            Double.parseDouble(value.trim().replace(",", "").replace(".", ""));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isReasonableDays(int days) {
        return days >= 0 && days <= 31;
    }

    public static boolean isReasonableSalary(double salary) {
        return salary > 0 && salary < 1_000_000_000;
    }

    public static boolean isReasonableOvertime(double hours) {
        return hours >= 0 && hours <= 240;
    }

    public static class ValidationError {
        private final int row;
        private final String column;
        private final String message;

        public ValidationError(int row, String column, String message) {
            this.row = row;
            this.column = column;
            this.message = message;
        }

        public int getRow() { return row; }
        public String getColumn() { return column; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "Baris " + row + " - " + column + ": " + message;
        }
    }
}
