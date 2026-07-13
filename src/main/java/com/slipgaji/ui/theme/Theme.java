package com.slipgaji.ui.theme;

import javax.swing.UIManager;

/**
 * Theme facade — one-stop entry point untuk design token.
 *
 * <p>Contoh pemakaian:
 * <pre>
 *   panel.setBackground(Theme.color(UIColors.NEUTRAL_50));
 *   label.setFont(UIFonts.H1);
 *   label.setForeground(UIColors.NEUTRAL_800);
 * </pre>
 *
 * <p>Panggil {@link #applyGlobals()} sekali di awal aplikasi untuk
 * mengunci default warna & font FlatLaf agar tidak konflik dengan tema.
 */
public final class Theme {

    private Theme() {}

    /**
     * Terapkan token global ke FlatLaf UIManager.
     * Idempoten — aman dipanggil beberapa kali.
     */
    public static void applyGlobals() {
        // Radius default
        UIManager.put("Button.arc", UIMetrics.RADIUS_BUTTON * 2);
        UIManager.put("Component.arc", UIMetrics.RADIUS_BUTTON * 2);
        UIManager.put("TextComponent.arc", UIMetrics.RADIUS_BUTTON * 2);

        // Scrollbar tipis
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);

        // Default font
        UIManager.put("defaultFont", UIFonts.BODY);

        // Tabel — grid tipis
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", UIColors.NEUTRAL_200);
        UIManager.put("TableHeader.background", UIColors.NEUTRAL_100);
        UIManager.put("TableHeader.foreground", UIColors.NEUTRAL_600);
        UIManager.put("TableHeader.font", UIFonts.LABEL_BOLD);
        UIManager.put("Table.selectionBackground", UIColors.PRIMARY_100);
        UIManager.put("Table.selectionForeground", UIColors.NEUTRAL_800);

        // Focus color
        UIManager.put("Component.focusColor", UIColors.PRIMARY_500);
        UIManager.put("Component.focusedBorderColor", UIColors.PRIMARY_500);
    }
}
