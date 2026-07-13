package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Pill-shaped status badge with muted semantic colors.
 *
 * <p>Contoh:
 * <pre>
 *   StatusBadge b = StatusBadge.success("Aktif");
 *   panel.add(b);
 * </pre>
 *
 * <p>Padding 4px vertikal 10px horizontal, radius full-pill,
 * font 12px medium. TIDAK pernah pakai warna neon.
 */
public class StatusBadge extends JLabel {

    public enum Tone { SUCCESS, WARNING, DANGER, NEUTRAL, INFO }

    private final Tone tone;
    private final boolean withDot;

    public StatusBadge(String text, Tone tone) {
        this(text, tone, false);
    }

    public StatusBadge(String text, Tone tone, boolean withDot) {
        super(text);
        this.tone = tone;
        this.withDot = withDot;
        setFont(UIFonts.LABEL_BOLD);
        setForeground(fg());
        setOpaque(false);
        // Padding vertikal 4 (via border), horisontal 10, plus space untuk dot bila ada
        int leftPad = withDot ? 20 : 10;
        setBorder(new EmptyBorder(4, leftPad, 4, 10));
    }

    /** Factory shortcuts. */
    public static StatusBadge success(String text) { return new StatusBadge(text, Tone.SUCCESS); }
    public static StatusBadge warning(String text) { return new StatusBadge(text, Tone.WARNING); }
    public static StatusBadge danger(String text)  { return new StatusBadge(text, Tone.DANGER); }
    public static StatusBadge neutral(String text) { return new StatusBadge(text, Tone.NEUTRAL); }
    public static StatusBadge info(String text)    { return new StatusBadge(text, Tone.INFO); }
    public static StatusBadge infoWithDot(String text) { return new StatusBadge(text, Tone.INFO, true); }

    /** Ganti teks & tone sekaligus (untuk badge yang re-render seperti "Menunggu → Terkirim → Gagal"). */
    public void update(String text, Tone newTone) {
        setText(text);
        try {
            java.lang.reflect.Field f = StatusBadge.class.getDeclaredField("tone");
            f.setAccessible(true);
            f.set(this, newTone);
        } catch (Exception ignored) {}
        setForeground(fgFor(newTone));
        repaint();
    }

    private Color bg() { return bgFor(tone); }
    private Color fg() { return fgFor(tone); }

    private static Color bgFor(Tone t) {
        return switch (t) {
            case SUCCESS -> UIColors.SUCCESS_BG;
            case WARNING -> UIColors.WARNING_BG;
            case DANGER  -> UIColors.DANGER_BG;
            case NEUTRAL -> UIColors.NEUTRAL_TAG_BG;
            case INFO    -> UIColors.PRIMARY_100;
        };
    }

    private static Color fgFor(Tone t) {
        return switch (t) {
            case SUCCESS -> UIColors.SUCCESS_FG;
            case WARNING -> UIColors.WARNING_FG;
            case DANGER  -> UIColors.DANGER_FG;
            case NEUTRAL -> UIColors.NEUTRAL_TAG_FG;
            case INFO    -> UIColors.PRIMARY_700;
        };
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        // Tinggi minimum ~22px agar bentuk pill terlihat
        d.height = Math.max(d.height, 22);
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        // Full-pill radius = h (biar bulet penuh di kedua sisi)
        g2.setColor(bg());
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));

        if (withDot) {
            int dotSize = 6;
            int dotX = 8;
            int dotY = (h - dotSize) / 2;
            g2.setColor(fg());
            g2.fillOval(dotX, dotY, dotSize, dotSize);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    /** Static helper: gambar badge langsung di Graphics2D (untuk TableCellRenderer). */
    public static void paintBadge(Graphics2D g2, int x, int y, int w, int h,
                                   String text, Tone tone, boolean withDot) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgc = bgFor(tone);
        Color fgc = fgFor(tone);

        g2.setColor(bgc);
        g2.fill(new RoundRectangle2D.Double(x, y, w, h, h, h));

        int textX = x + (withDot ? 20 : 10);
        int dotSize = 6;
        if (withDot) {
            g2.setColor(fgc);
            g2.fillOval(x + 8, y + (h - dotSize) / 2, dotSize, dotSize);
        }
        g2.setFont(UIFonts.LABEL_BOLD);
        g2.setColor(fgc);
        FontMetrics fm = g2.getFontMetrics();
        int textY = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, textX, textY);
    }
}
