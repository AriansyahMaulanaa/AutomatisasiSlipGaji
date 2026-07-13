package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Empty state — icon custom line-style + heading + subtitle + optional CTA.
 *
 * <ul>
 *   <li>Icon line-style pakai Graphics2D (bukan icon berwarna solid).</li>
 *   <li>Text utama {@link UIColors#NEUTRAL_600}, sekunder {@link UIColors#NEUTRAL_400}.</li>
 * </ul>
 */
public class EmptyState extends JPanel {

    /** Ikon yang tersedia. */
    public enum Icon { DOCUMENT, INBOX, SEARCH, USERS, CLOCK }

    public EmptyState(Icon icon, String title, String subtitle) {
        this(icon, title, subtitle, null);
    }

    public EmptyState(Icon icon, String title, String subtitle, JButton ctaButton) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_16,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_16));

        IconPanel ip = new IconPanel(icon);
        ip.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(ip);
        add(Box.createVerticalStrut(UIMetrics.SPACE_16));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIFonts.BODY_BOLD);
        titleLbl.setForeground(UIColors.NEUTRAL_600);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLbl);

        if (subtitle != null && !subtitle.isEmpty()) {
            add(Box.createVerticalStrut(UIMetrics.SPACE_4));
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(UIFonts.CAPTION);
            subLbl.setForeground(UIColors.NEUTRAL_400);
            subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(subLbl);
        }

        if (ctaButton != null) {
            add(Box.createVerticalStrut(UIMetrics.SPACE_16));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(false);
            wrap.setAlignmentX(Component.CENTER_ALIGNMENT);
            wrap.add(ctaButton);
            add(wrap);
        }
    }

    /** Line-style icon rendered manually. */
    private static class IconPanel extends JPanel {
        private final Icon type;

        IconPanel(Icon type) {
            this.type = type;
            setOpaque(false);
            setPreferredSize(new Dimension(64, 64));
            setMaximumSize(new Dimension(64, 64));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            // background bulat lembut
            g2.setColor(UIColors.PRIMARY_50);
            int bgSize = Math.min(w, h);
            g2.fillOval(cx - bgSize / 2, cy - bgSize / 2, bgSize, bgSize);

            g2.setColor(UIColors.NEUTRAL_400);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case DOCUMENT -> drawDocument(g2, cx, cy);
                case INBOX -> drawInbox(g2, cx, cy);
                case SEARCH -> drawSearch(g2, cx, cy);
                case USERS -> drawUsers(g2, cx, cy);
                case CLOCK -> drawClock(g2, cx, cy);
            }

            g2.dispose();
        }

        private void drawDocument(Graphics2D g2, int cx, int cy) {
            int w = 22, h = 28;
            int x = cx - w / 2, y = cy - h / 2;
            // lipatan sudut kanan-atas
            int fold = 6;
            g2.draw(new RoundRectangle2D.Double(x, y, w, h, 3, 3));
            g2.drawLine(x + w - fold, y, x + w - fold, y + fold);
            g2.drawLine(x + w - fold, y + fold, x + w, y + fold);
            // garis konten
            for (int i = 0; i < 3; i++) {
                int ly = y + 12 + i * 5;
                int lw = (i == 2) ? w - 10 : w - 6;
                g2.drawLine(x + 4, ly, x + 4 + lw - 4, ly);
            }
        }

        private void drawInbox(Graphics2D g2, int cx, int cy) {
            int w = 26, h = 22;
            int x = cx - w / 2, y = cy - h / 2;
            g2.draw(new RoundRectangle2D.Double(x, y, w, h, 3, 3));
            g2.drawLine(x, cy + 1, x + 8, cy + 1);
            g2.drawLine(x + 8, cy + 1, x + 12, cy - 4);
            g2.drawLine(x + 12, cy - 4, x + 14, cy - 4);
            g2.drawLine(x + 14, cy - 4, x + 18, cy + 1);
            g2.drawLine(x + 18, cy + 1, x + w, cy + 1);
        }

        private void drawSearch(Graphics2D g2, int cx, int cy) {
            int r = 10;
            g2.drawOval(cx - r, cy - r - 2, r * 2, r * 2);
            g2.drawLine(cx + r - 2, cy + r - 4, cx + r + 6, cy + r + 4);
        }

        private void drawUsers(Graphics2D g2, int cx, int cy) {
            // dua orang siluet
            int hr = 5;
            g2.drawOval(cx - 10, cy - 10, hr * 2, hr * 2);
            g2.drawArc(cx - 14, cy, 14, 12, 0, 180);
            g2.drawOval(cx + 2, cy - 8, hr * 2, hr * 2);
            g2.drawArc(cx - 2, cy + 2, 14, 10, 0, 180);
        }

        private void drawClock(Graphics2D g2, int cx, int cy) {
            int r = 12;
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.drawLine(cx, cy, cx, cy - r + 4);
            g2.drawLine(cx, cy, cx + r - 5, cy);
        }
    }
}
