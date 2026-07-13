package com.slipgaji.ui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;

/**
 * Centralized typography tokens.
 *
 * <p>Font hierarki:
 * <ul>
 *   <li>Inter (jika tersedia di sistem)</li>
 *   <li>Segoe UI (Windows fallback)</li>
 *   <li>SF Pro Display / SF Pro Text (Mac fallback)</li>
 *   <li>Sans-serif system default</li>
 * </ul>
 */
public final class UIFonts {

    private UIFonts() {}

    /** Family name yang benar-benar tersedia di sistem, dipilih otomatis di runtime. */
    public static final String FAMILY;

    static {
        Set<String> available = new HashSet<>();
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            available.add(f);
        }
        String chosen;
        if (available.contains("Inter")) {
            chosen = "Inter";
        } else if (available.contains("SF Pro Text")) {
            chosen = "SF Pro Text";
        } else if (available.contains("SF Pro Display")) {
            chosen = "SF Pro Display";
        } else if (available.contains("Segoe UI")) {
            chosen = "Segoe UI";
        } else if (available.contains("Helvetica Neue")) {
            chosen = "Helvetica Neue";
        } else {
            chosen = Font.SANS_SERIF;
        }
        FAMILY = chosen;
    }

    // ==========================================================
    // Type scale
    // ==========================================================
    /** Judul halaman (mis. "Dashboard"). */
    public static final Font H1        = new Font(FAMILY, Font.BOLD, 22);
    /** Judul card (mis. "Informasi Perusahaan"). */
    public static final Font H2        = new Font(FAMILY, Font.BOLD, 15);
    /** Sub-heading kecil di dalam card. */
    public static final Font H3        = new Font(FAMILY, Font.BOLD, 13);

    /** Body text default. */
    public static final Font BODY      = new Font(FAMILY, Font.PLAIN, 13);
    /** Body medium (untuk emphasis ringan tanpa bold). */
    public static final Font BODY_MED  = new Font(FAMILY, Font.PLAIN, 13);
    /** Body bold. */
    public static final Font BODY_BOLD = new Font(FAMILY, Font.BOLD, 13);

    /** Label field / header tabel — 12px medium. */
    public static final Font LABEL     = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font LABEL_BOLD = new Font(FAMILY, Font.BOLD, 12);

    /** Caption / micro copy. */
    public static final Font CAPTION   = new Font(FAMILY, Font.PLAIN, 11);
    public static final Font CAPTION_BOLD = new Font(FAMILY, Font.BOLD, 11);

    /** Angka besar di stat card (28–32px). */
    public static final Font STAT_VALUE = new Font(FAMILY, Font.BOLD, 30);

    /** Font button. */
    public static final Font BUTTON     = new Font(FAMILY, Font.BOLD, 13);
    /** Font button kecil. */
    public static final Font BUTTON_SM  = new Font(FAMILY, Font.BOLD, 12);
}
