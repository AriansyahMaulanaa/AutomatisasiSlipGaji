package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable button with enterprise minimalist styling.
 *
 * <p>4 varian sesuai spec redesign:
 * <ul>
 *   <li>{@link Variant#PRIMARY} — solid soft-blue, untuk 1 aksi utama per halaman.</li>
 *   <li>{@link Variant#SECONDARY} — outline neutral, untuk aksi sekunder.</li>
 *   <li>{@link Variant#DANGER} — outline danger muted (bukan solid merah).</li>
 *   <li>{@link Variant#GHOST} — text-only, untuk aksi ringan (mis. Logout).</li>
 * </ul>
 *
 * <p>Semua rendering pakai Graphics2D manual — tidak ada dependency icon.
 */
public class AppButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, DANGER, GHOST }

    private final Variant variant;
    private boolean hovered = false;

    public AppButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setup();
    }

    /** Convenience factory — Primary button. */
    public static AppButton primary(String text) {
        return new AppButton(text, Variant.PRIMARY);
    }

    /** Convenience factory — Secondary outline button. */
    public static AppButton secondary(String text) {
        return new AppButton(text, Variant.SECONDARY);
    }

    /** Convenience factory — Danger outline button. */
    public static AppButton danger(String text) {
        return new AppButton(text, Variant.DANGER);
    }

    /** Convenience factory — Ghost text button. */
    public static AppButton ghost(String text) {
        return new AppButton(text, Variant.GHOST);
    }

    private void setup() {
        setFont(UIFonts.BUTTON);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(UIMetrics.BUTTON_PAD_Y, UIMetrics.BUTTON_PAD_X,
                                   UIMetrics.BUTTON_PAD_Y, UIMetrics.BUTTON_PAD_X));

        // Foreground per variant
        switch (variant) {
            case PRIMARY   -> setForeground(Color.WHITE);
            case SECONDARY -> setForeground(UIColors.NEUTRAL_800);
            case DANGER    -> setForeground(UIColors.DANGER_FG);
            case GHOST     -> setForeground(UIColors.PRIMARY_500);
        }

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = UIMetrics.RADIUS_BUTTON * 2;
        boolean pressed = getModel().isPressed();
        boolean enabled = isEnabled();

        switch (variant) {
            case PRIMARY -> paintPrimary(g2, w, h, arc, pressed, enabled);
            case SECONDARY -> paintSecondary(g2, w, h, arc, pressed, enabled);
            case DANGER -> paintDanger(g2, w, h, arc, pressed, enabled);
            case GHOST -> paintGhost(g2, w, h, arc, pressed, enabled);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private void paintPrimary(Graphics2D g2, int w, int h, int arc, boolean pressed, boolean enabled) {
        Color bg;
        if (!enabled) {
            bg = UIColors.NEUTRAL_200;
        } else if (pressed) {
            bg = UIColors.PRIMARY_700;
        } else if (hovered) {
            bg = UIColors.PRIMARY_600;
        } else {
            bg = UIColors.PRIMARY_500;
        }
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
    }

    private void paintSecondary(Graphics2D g2, int w, int h, int arc, boolean pressed, boolean enabled) {
        Color bg;
        if (!enabled) {
            bg = UIColors.NEUTRAL_50;
        } else if (pressed) {
            bg = UIColors.NEUTRAL_100;
        } else if (hovered) {
            bg = UIColors.NEUTRAL_50;
        } else {
            bg = UIColors.NEUTRAL_0;
        }
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

        g2.setColor(UIColors.NEUTRAL_200);
        g2.setStroke(new BasicStroke(UIMetrics.BORDER_WIDTH));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, arc, arc));
    }

    private void paintDanger(Graphics2D g2, int w, int h, int arc, boolean pressed, boolean enabled) {
        Color bg;
        if (!enabled) {
            bg = UIColors.NEUTRAL_50;
        } else if (pressed) {
            bg = new Color(0xF3, 0xD4, 0xD4); // pekat sedikit
        } else if (hovered) {
            bg = UIColors.DANGER_BG;
        } else {
            bg = UIColors.NEUTRAL_0;
        }
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

        g2.setColor(UIColors.DANGER_FG);
        g2.setStroke(new BasicStroke(UIMetrics.BORDER_WIDTH));
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, arc, arc));
    }

    private void paintGhost(Graphics2D g2, int w, int h, int arc, boolean pressed, boolean enabled) {
        if (!enabled) return;
        if (pressed) {
            g2.setColor(UIColors.PRIMARY_100);
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
        } else if (hovered) {
            g2.setColor(UIColors.PRIMARY_50);
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
        }
    }
}
