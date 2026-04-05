package com.slipgaji.util;

import java.awt.*;
import java.io.File;

public class Constants {
    // App Info
    public static final String APP_NAME = "SlipGaji Pro";
    public static final String APP_VERSION = "1.0.0";
    public static final String COMPANY_NAME = "PT. Maju Bersama Sejahtera";

    // Paths
    public static final String APP_DIR = System.getProperty("user.dir");
    public static final String DATA_DIR = APP_DIR + File.separator + "data";
    public static final String DB_PATH = DATA_DIR + File.separator + "slipgaji.db";
    public static final String PDF_DIR = APP_DIR + File.separator + "output" + File.separator + "pdf";

    // Colors - Modern Light Theme (macOS style)
    public static final Color PRIMARY = new Color(0, 122, 255);       // macOS System Blue
    public static final Color PRIMARY_DARK = new Color(0, 88, 208);
    public static final Color PRIMARY_LIGHT = new Color(106, 175, 255);
    public static final Color ACCENT = new Color(52, 199, 89);       // macOS Green
    public static final Color ACCENT_WARN = new Color(255, 149, 0);  // macOS Orange
    public static final Color ACCENT_DANGER = new Color(255, 59, 48); // macOS Red
    public static final Color BG_DARK = new Color(245, 245, 247);     // macOS Gray Background
    public static final Color BG_CARD = new Color(255, 255, 255);     // White Card
    public static final Color BG_SURFACE = new Color(235, 235, 240);  // Light Surface
    public static final Color TEXT_PRIMARY = new Color(0, 0, 0);      // Black Text
    public static final Color TEXT_SECONDARY = new Color(100, 100, 105); // Gray Text
    public static final Color BORDER_COLOR = new Color(220, 220, 225);   // Light Border
    public static final Color SIDEBAR_BG = new Color(235, 235, 240);  // Sidebar Light
    public static final Color SUCCESS_BG = new Color(228, 248, 232);
    public static final Color FAILED_BG = new Color(255, 235, 235);

    // Fonts
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    // Salary defaults
    public static final int DEFAULT_WORK_DAYS = 22;
    public static final double DEFAULT_OVERTIME_RATE = 25000;
    public static final double DEFAULT_TRANSPORT_ALLOWANCE = 500000;
    public static final double DEFAULT_MEAL_ALLOWANCE = 300000;

    // Ensure directories exist
    public static void ensureDirectories() {
        new File(DATA_DIR).mkdirs();
        new File(PDF_DIR).mkdirs();
    }
}
