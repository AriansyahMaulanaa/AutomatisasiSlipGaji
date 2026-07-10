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
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "slipgaji_db";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "";

    // Colors - Enterprise Slate/Navy Palette
    public static final Color PRIMARY_NAVY = new Color(15, 23, 42);        // #0F172A
    public static final Color TEXT_PRIMARY = new Color(30, 41, 59);         // #1E293B
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);   // #64748B
    public static final Color TEXT_MUTED = new Color(148, 163, 184);       // #94A3B8
    public static final Color ACCENT_ACTION = new Color(37, 99, 235);       // #2563EB
    public static final Color ACCENT_ACTION_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color SUCCESS = new Color(22, 163, 74);             // #16A34A
    public static final Color DANGER = new Color(220, 38, 38);              // #DC2626
    public static final Color OUTLINE_TEXT = new Color(51, 65, 85);         // #334155
    public static final Color BORDER_COLOR = new Color(226, 232, 240);     // #E2E8F0
    public static final Color BG_PAGE = new Color(248, 250, 252);          // #F8FAFC
    public static final Color BG_CARD = new Color(255, 255, 255);          // #FFFFFF
    public static final Color BG_SURFACE = new Color(241, 245, 249);       // #F1F5F9
    public static final Color SUCCESS_BG_TINT = new Color(240, 253, 244);  // #F0FDF4
    public static final Color DANGER_BG_TINT = new Color(254, 242, 242);   // #FEF2F2
    public static final Color LOGOUT_BG = new Color(254, 226, 226);        // #FEE2E2
    public static final Color LOGOUT_ICON = new Color(220, 38, 38);        // #DC2626

    // Blue-softer accents
    public static final Color TEXT_LABEL = new Color(71, 85, 105);          // #475569
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);        // #3B82F6
    public static final Color BADGE_BG = new Color(239, 246, 255);          // #EFF6FF
    public static final Color BADGE_BORDER = new Color(147, 197, 253);      // #93C5FD
    public static final Color BADGE_TEXT = new Color(29, 78, 216);          // #1D4ED8
    public static final Color BG_BLUE_SOFT = new Color(239, 246, 255);      // #EFF6FF
    public static final Color DOT_ACTIVE = new Color(59, 130, 246);         // #3B82F6

    // Spacing scale (px)
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;

    // Status badge colors
    public static final Color WARN_BG = new Color(254, 243, 199);           // #FEF3C7
    public static final Color WARN_TEXT = new Color(217, 119, 6);            // #D97706

    // Legacy aliases for compatibility with other panels
    public static final Color ACCENT = new Color(16, 185, 129);
    public static final Color ACCENT_WARN = new Color(245, 158, 11);
    public static final Color ACCENT_DANGER = new Color(239, 68, 68);
    public static final Color PRIMARY = PRIMARY_NAVY;
    public static final Color PRIMARY_DARK = new Color(22, 78, 133);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color BG_DARK = BG_PAGE;
    public static final Color SIDEBAR_BG = BG_CARD;
    public static final Color SIDEBAR_ACTIVE_BG = new Color(239, 246, 255);
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);

    // Login Gradient (legacy — unused in dashboard)
    public static final Color LOGIN_GRADIENT_START = new Color(99, 102, 241);
    public static final Color LOGIN_GRADIENT_END = new Color(168, 85, 247);

    // Fonts
    public static final String FONT_FAMILY = "SansSerif";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    // Salary defaults — kept unchanged
    public static final int DEFAULT_WORK_DAYS = 22;
    public static final double DEFAULT_OVERTIME_RATE = 25000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE = 500000;
    public static final double DEFAULT_MEAL_ALLOWANCE = 300000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE = 50000;
    public static final int DEFAULT_WORK_DAYS_PKWT = 22;
    public static final double DEFAULT_OVERTIME_RATE_PKWT = 20000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE_PKWT = 300000;
    public static final double DEFAULT_MEAL_ALLOWANCE_PKWT = 200000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE_PKWT = 40000;
    public static final int DEFAULT_WORK_DAYS_KANTOR = 22;
    public static final double DEFAULT_OVERTIME_RATE_KANTOR = 30000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE_KANTOR = 400000;
    public static final double DEFAULT_MEAL_ALLOWANCE_KANTOR = 250000;
    public static final double DEFAULT_NIGHT_SHIFT_RATE_KANTOR = 45000;

    // Photo directory
    public static final String PHOTO_DIR = APP_DIR + File.separator + "output" + File.separator + "foto";

    // Barcode scanner
    public static final Color SCAN_GUIDE_COLOR = new Color(59, 130, 246, 80);
    public static final Color SCAN_GUIDE_BORDER = new Color(59, 130, 246);
    public static final Color SCAN_SUCCESS_COLOR = new Color(16, 185, 129, 60);
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

    // Legacy colors kept for other panels
    public static final Color REFRESH_BTN = new Color(107, 114, 128);
    public static final Color BG_SURFACE_OLD = new Color(249, 250, 251);
    public static final Color TABLE_ROW_ALT = new Color(249, 250, 251);
    public static final Color SUCCESS_BG = new Color(220, 252, 231);
    public static final Color FAILED_BG = new Color(254, 226, 226);

    public static void ensureDirectories() {
        new File(PDF_DIR).mkdirs();
        new File(PHOTO_DIR).mkdirs();
    }
}