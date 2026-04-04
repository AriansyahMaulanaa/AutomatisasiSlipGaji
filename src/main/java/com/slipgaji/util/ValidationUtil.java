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
}
