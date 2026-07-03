package com.slipgaji.util;

import java.awt.*;
import java.io.File;

public class Constants {
    // App Info
    public static final String APP_NAME = "SlipGaji Pro";
    public static final String APP_VERSION = "1.1.0";
    public static final String COMPANY_NAME = "CV. Mandiri Sukses Pratama";

    // Paths
    public static final String APP_DIR = System.getProperty("user.dir");
    public static final String PDF_DIR = APP_DIR + File.separator + "output" + File.separator + "pdf";

    // Database MariaDB — Fallback defaults (override via config.properties)
    // 🔐 WARNING: Do not store real credentials here!
    // Copy config.properties.example to config.properties and set your credentials there.
    // config.properties is in .gitignore and will not be committed.
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "slipgaji_db";
    public static final String DB_USER = "root";
    public static final String DB_PASS = ""; // Change via config.properties!

    // Colors - Modern Light Theme (Refined)
    public static final Color PRIMARY = new Color(59, 130, 246); // Soft Blue
    public static final Color PRIMARY_DARK = new Color(37, 99, 235);
    public static final Color PRIMARY_LIGHT = new Color(147, 197, 253);
    public static final Color ACCENT = new Color(16, 185, 129); // Emerald Green
    public static final Color ACCENT_WARN = new Color(245, 158, 11); // Amber Orange
    public static final Color ACCENT_DANGER = new Color(239, 68, 68); // Soft Red
    public static final Color REFRESH_BTN = new Color(107, 114, 128); // Gray for Refresh
    public static final Color BG_DARK = new Color(243, 244, 246); // Soft Gray Background (#F3F4F6)
    public static final Color BG_CARD = new Color(255, 255, 255); // White Card
    public static final Color BG_SURFACE = new Color(249, 250, 251); // Very Light Surface
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39); // Near-Black Text
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128); // Gray Text
    public static final Color BORDER_COLOR = new Color(229, 231, 235); // Light Border
    public static final Color SIDEBAR_BG = new Color(255, 255, 255); // White Sidebar
    public static final Color SIDEBAR_ACTIVE_BG = new Color(239, 246, 255); // Light Blue Active
    public static final Color SUCCESS_BG = new Color(220, 252, 231);
    public static final Color FAILED_BG = new Color(254, 226, 226);
    public static final Color TABLE_ROW_ALT = new Color(249, 250, 251); // Zebra Row
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 20); // Subtle Shadow

    // Login Gradient
    public static final Color LOGIN_GRADIENT_START = new Color(99, 102, 241); // Indigo
    public static final Color LOGIN_GRADIENT_END = new Color(168, 85, 247); // Purple

    // Fonts — SemiBold simulated via PLAIN+BOLD hybrid; Java doesn't have native
    // SemiBold
    public static final String FONT_FAMILY = "SansSerif";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    // Salary defaults - Karyawan Tetap
    public static final int DEFAULT_WORK_DAYS = 22;
    public static final double DEFAULT_OVERTIME_RATE = 25000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE = 500000;
    public static final double DEFAULT_MEAL_ALLOWANCE = 300000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE = 50000;

    // Salary defaults - PKWT
    public static final int DEFAULT_WORK_DAYS_PKWT = 22;
    public static final double DEFAULT_OVERTIME_RATE_PKWT = 20000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE_PKWT = 300000;
    public static final double DEFAULT_MEAL_ALLOWANCE_PKWT = 200000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE_PKWT = 40000;

    // Salary defaults - Staff Kantor
    public static final int DEFAULT_WORK_DAYS_KANTOR = 22;
    public static final double DEFAULT_OVERTIME_RATE_KANTOR = 30000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE_KANTOR = 400000;
    public static final double DEFAULT_MEAL_ALLOWANCE_KANTOR = 250000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE_KANTOR = 45000;

    // Photo directory
    public static final String PHOTO_DIR = APP_DIR + File.separator + "output" + File.separator + "foto";

    // Barcode scanner
    public static final Color SCAN_GUIDE_COLOR = new Color(59, 130, 246, 80); // Soft blue overlay
    public static final Color SCAN_GUIDE_BORDER = new Color(59, 130, 246);
    public static final Color SCAN_SUCCESS_COLOR = new Color(16, 185, 129, 60); // Green for detected
    public static final Color SCAN_SUCCESS_BORDER = new Color(16, 185, 129);
    public static final Color SCAN_ERROR_COLOR = new Color(239, 68, 68, 60);
    public static final Color SCAN_ERROR_BORDER = new Color(239, 68, 68);

    // Camera
    public static final int CAMERA_WIDTH = 640;
    public static final int CAMERA_HEIGHT = 480;

    // UI Constants
    public static final int CARD_RADIUS = 16;
    public static final int BUTTON_RADIUS = 12;
    public static final int SIDEBAR_WIDTH = 260;

    // Ensure directories exist
    public static void ensureDirectories() {
        new File(PDF_DIR).mkdirs();
        new File(PHOTO_DIR).mkdirs();
    }
}
