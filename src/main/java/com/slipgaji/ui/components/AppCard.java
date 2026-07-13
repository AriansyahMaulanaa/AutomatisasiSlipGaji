package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Enterprise-flat card:
 * <ul>
 *   <li>Background {@link UIColors#NEUTRAL_0}</li>
 *   <li>Border 1px {@link UIColors#NEUTRAL_200}</li>
 *   <li>Radius {@link UIMetrics#RADIUS_CARD}</li>
 *   <li>Shadow sangat tipis atau tanpa shadow (default: no shadow)</li>
 * </ul>
 *
 * <p>Untuk shadow lebih visible, panggil {@link #withShadow(boolean)}.
 */
public class AppCard extends JPanel {

    private boolean drawShadow = false;
    private final JLabel titleLabel;

    /** Card kosong tanpa judul. */
    public AppCard() {
        this(null);
    }

    /** Card dengan judul H2 di atas. */
    public AppCard(String title) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(UIMetrics.CARD_PAD_Y, UIMetrics.CARD_PAD_X,
                                   UIMetrics.CARD_PAD_Y, UIMetrics.CARD_PAD_X));
        if (title != null && !title.isEmpty()) {
            titleLabel = new JLabel(title);
            titleLabel.setFont(UIFonts.H2);
            titleLabel.setForeground(UIColors.NEUTRAL_800);
            titleLabel.setBorder(new EmptyBorder(0, 0, UIMetrics.SPACE_16, 0));
            super.add(titleLabel, BorderLayout.NORTH);
        } else {
            titleLabel = null;
        }
    }

    /** Aktifkan shadow tipis (untuk dropdown/popup, jarang dipakai di enterprise flat). */
    public AppCard withShadow(boolean shadow) {
        this.drawShadow = shadow;
        repaint();
        return this;
    }

    /**
     * Tambahkan komponen ke body card (bukan ke NORTH title).
     * Kalau card tidak punya title, ini sama seperti add ke CENTER BorderLayout.
     */
    public void addBody(Component c) {
        add(c, BorderLayout.CENTER);
    }

    /** Ganti judul card runtime. */
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    /** Utility: bikin card berisi konten vertikal (BoxLayout Y_AXIS). */
    public static AppCard verticalStack(String title, Component... items) {
        AppCard card = new AppCard(title);
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        for (int i = 0; i < items.length; i++) {
            if (i > 0) body.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
            body.add(items[i]);
        }
        card.addBody(body);
        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = UIMetrics.RADIUS_CARD * 2;

        int inset = 0;
        if (drawShadow) {
            // shadow sangat halus: 2 layer alpha kecil, offsetY 2px
            for (int i = 2; i >= 1; i--) {
                Color c = new Color(0, 0, 0, 5 + i * 4);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2, arc, arc));
            }
            inset = 2;
        }

        g2.setColor(UIColors.NEUTRAL_0);
        g2.fill(new RoundRectangle2D.Double(inset, inset,
                w - inset * 2 - 1, h - inset * 2 - 1, arc, arc));

        g2.setColor(UIColors.NEUTRAL_200);
        g2.setStroke(new BasicStroke(UIMetrics.BORDER_WIDTH));
        g2.draw(new RoundRectangle2D.Double(inset + 0.5, inset + 0.5,
                w - inset * 2 - 1, h - inset * 2 - 1, arc, arc));

        g2.dispose();
    }

    /** Utility ensure card doesn't collapse when empty. */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width = Math.max(d.width, 100);
        d.height = Math.max(d.height, 60);
        return d;
    }
}
