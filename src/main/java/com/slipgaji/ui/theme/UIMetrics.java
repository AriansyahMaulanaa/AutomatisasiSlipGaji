package com.slipgaji.ui.theme;

/**
 * Centralized spacing, radius, and sizing tokens.
 *
 * <p>Semua dimensi UI harus mengacu ke token di sini — dilarang hardcode
 * angka spacing/radius di file lain.
 */
public final class UIMetrics {

    private UIMetrics() {}

    // ==========================================================
    // Spacing — 8px grid
    // ==========================================================
    public static final int SPACE_2  = 2;
    public static final int SPACE_4  = 4;
    public static final int SPACE_8  = 8;
    public static final int SPACE_12 = 12;
    public static final int SPACE_16 = 16;
    public static final int SPACE_20 = 20;
    public static final int SPACE_24 = 24;
    public static final int SPACE_32 = 32;
    public static final int SPACE_40 = 40;

    // ==========================================================
    // Corner radius
    // ==========================================================
    /** Tombol & input. */
    public static final int RADIUS_BUTTON = 8;
    /** Card & panel. */
    public static final int RADIUS_CARD   = 12;
    /** Modal / dialog. */
    public static final int RADIUS_MODAL  = 14;
    /** Full pill radius — dipakai lewat height komponen. */
    public static final int RADIUS_PILL   = 999;

    // ==========================================================
    // Sizes
    // ==========================================================
    /** Tinggi tombol standar (Primary/Secondary/Danger). */
    public static final int BUTTON_HEIGHT     = 36;
    public static final int BUTTON_HEIGHT_LG  = 40;
    public static final int BUTTON_PAD_X      = 16;
    public static final int BUTTON_PAD_Y      = 8;

    /** Tinggi input & dropdown. */
    public static final int INPUT_HEIGHT      = 38;
    public static final int INPUT_PAD_X       = 12;
    public static final int INPUT_PAD_Y       = 8;

    /** Border width dasar. */
    public static final float BORDER_WIDTH        = 1.0f;
    public static final float BORDER_WIDTH_FOCUS  = 1.5f;

    /** Sidebar. */
    public static final int SIDEBAR_WIDTH             = 240;
    public static final int SIDEBAR_ITEM_HEIGHT       = 42;
    public static final int SIDEBAR_ACTIVE_INDICATOR  = 3;

    /** Card padding default. */
    public static final int CARD_PAD_X = 24;
    public static final int CARD_PAD_Y = 20;

    /** Row height tabel. */
    public static final int TABLE_ROW_HEIGHT        = 42;
    public static final int TABLE_HEADER_HEIGHT     = 40;
}
