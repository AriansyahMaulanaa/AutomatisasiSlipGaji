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

    // Colors - Modern Dark Theme
    public static final Color PRIMARY = new Color(79, 70, 229);       // Indigo
    public static final Color PRIMARY_DARK = new Color(55, 48, 163);
    public static final Color PRIMARY_LIGHT = new Color(129, 120, 248);
    public static final Color ACCENT = new Color(16, 185, 129);       // Emerald
    public static final Color ACCENT_WARN = new Color(245, 158, 11);  // Amber
    public static final Color ACCENT_DANGER = new Color(239, 68, 68); // Red
    public static final Color BG_DARK = new Color(15, 23, 42);        // Slate 900
    public static final Color BG_CARD = new Color(30, 41, 59);        // Slate 800
    public static final Color BG_SURFACE = new Color(51, 65, 85);     // Slate 700
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252); // Slate 50
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184); // Slate 400
    public static final Color BORDER_COLOR = new Color(71, 85, 105);  // Slate 600
    public static final Color SIDEBAR_BG = new Color(20, 27, 45);
    public static final Color SUCCESS_BG = new Color(6, 78, 59);
    public static final Color FAILED_BG = new Color(127, 29, 29);

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
