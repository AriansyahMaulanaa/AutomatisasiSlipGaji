package com.slipgaji.ui.theme;

import java.awt.Color;

/**
 * Centralized color tokens for SlipGaji Pro — enterprise minimalist palette.
 *
 * <p>Style guide:
 * <ul>
 *   <li>Soft blue for primary/aksen — NOT navy dark, NOT electric blue.</li>
 *   <li>All semantic colors (success/warning/danger) are muted, NEVER neon.</li>
 *   <li>Sidebar is dark navy (kept), content area is neutral off-white.</li>
 * </ul>
 *
 * <p>Do NOT hardcode hex colors elsewhere — always reference tokens from here.
 */
public final class UIColors {

    private UIColors() {}

    // ==========================================================
    // Primary — Soft Blue
    // ==========================================================
    /** Ringan sekali — background hover, background info panel. */
    public static final Color PRIMARY_50  = new Color(0xEF, 0xF4, 0xFA);
    /** Ringan — background badge netral biru, selected row. */
    public static final Color PRIMARY_100 = new Color(0xDC, 0xE7, 0xF5);
    /** Aksen utama — tombol primary, link, ikon aktif. */
    public static final Color PRIMARY_500 = new Color(0x4A, 0x7F, 0xC9);
    /** Hover state tombol primary. */
    public static final Color PRIMARY_600 = new Color(0x3D, 0x6B, 0xAE);
    /** Pressed state, sidebar active indicator. */
    public static final Color PRIMARY_700 = new Color(0x2F, 0x55, 0x90);

    // ==========================================================
    // Neutral — Base UI
    // ==========================================================
    /** Background card / content utama. */
    public static final Color NEUTRAL_0   = new Color(0xFF, 0xFF, 0xFF);
    /** Background halaman (content area). */
    public static final Color NEUTRAL_50  = new Color(0xF7, 0xF9, 0xFC);
    /** Background header tabel, background disabled. */
    public static final Color NEUTRAL_100 = new Color(0xEE, 0xF1, 0xF6);
    /** Border card, border input, divider tabel. */
    public static final Color NEUTRAL_200 = new Color(0xE2, 0xE6, 0xED);
    /** Placeholder text, icon inactive, label sekunder. */
    public static final Color NEUTRAL_400 = new Color(0x9A, 0xA5, 0xB1);
    /** Body text sekunder. */
    public static final Color NEUTRAL_600 = new Color(0x5B, 0x65, 0x72);
    /** Heading, text utama. */
    public static final Color NEUTRAL_800 = new Color(0x1F, 0x29, 0x37);

    // ==========================================================
    // Sidebar — dark navy (kept, tuned)
    // ==========================================================
    public static final Color SIDEBAR_BG              = new Color(0x18, 0x22, 0x34);
    public static final Color SIDEBAR_BG_ACTIVE       = new Color(255, 255, 255, 10); // rgba(255,255,255,4%) overlay
    public static final Color SIDEBAR_TEXT_INACTIVE   = new Color(0x85, 0x92, 0xA6);
    public static final Color SIDEBAR_TEXT_ACTIVE     = new Color(0xFF, 0xFF, 0xFF);
    public static final Color SIDEBAR_SECTION_LABEL   = new Color(0x85, 0x92, 0xA6);
    public static final Color SIDEBAR_DIVIDER         = new Color(255, 255, 255, 18);

    // ==========================================================
    // Semantic — Muted only (NEVER pure/neon)
    // ==========================================================
    public static final Color SUCCESS_BG      = new Color(0xE6, 0xF4, 0xEC);
    public static final Color SUCCESS_FG      = new Color(0x2E, 0x8B, 0x57);

    public static final Color WARNING_BG      = new Color(0xFC, 0xF3, 0xE3);
    public static final Color WARNING_FG      = new Color(0xB8, 0x79, 0x1F);

    public static final Color DANGER_BG       = new Color(0xFB, 0xEA, 0xEA);
    public static final Color DANGER_FG       = new Color(0xC0, 0x39, 0x2B);

    /** Neutral tag — untuk "TETAP", "Belum pulang", dsb. */
    public static final Color NEUTRAL_TAG_BG  = NEUTRAL_100;
    public static final Color NEUTRAL_TAG_FG  = NEUTRAL_600;

    // ==========================================================
    // Shadow (very subtle — enterprise flat)
    // ==========================================================
    public static final Color SHADOW_CARD     = new Color(0, 0, 0, 18);   // ~7% alpha
    public static final Color SHADOW_POPUP    = new Color(0, 0, 0, 26);   // ~10% alpha

    // ==========================================================
    // Focus ring for inputs
    // ==========================================================
    public static final Color FOCUS_RING      = new Color(74, 127, 201, 60);
}
