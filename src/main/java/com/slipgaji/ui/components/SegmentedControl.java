package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * iOS/macOS-style segmented control.
 *
 * <ul>
 *   <li>Container bg {@link UIColors#NEUTRAL_100}, radius {@link UIMetrics#RADIUS_BUTTON}, padding 4px.</li>
 *   <li>Item terpilih: bg putih + shadow sangat tipis + text {@link UIColors#PRIMARY_600} bold.</li>
 *   <li>Item tidak terpilih: transparan, text {@link UIColors#NEUTRAL_600}.</li>
 * </ul>
 *
 * <p>Contoh:
 * <pre>
 *   SegmentedControl seg = new SegmentedControl();
 *   seg.addSegment("Crewstore", "crewstore");
 *   seg.addSegment("Store Leader", "store_leader");
 *   seg.addSegment("Manager", "manager");
 *   seg.setSelected("crewstore");
 *   seg.onChange(key -> loadForRole(key));
 * </pre>
 */
public class SegmentedControl extends JPanel {

    private final Map<String, SegmentButton> segments = new LinkedHashMap<>();
    private String selectedKey;
    private final List<Consumer<String>> listeners = new ArrayList<>();

    public SegmentedControl() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(4, 4, 4, 4));
    }

    /** Tambah segment. Panggil {@link #setSelected(String)} setelah semua ditambahkan. */
    public void addSegment(String label, String key) {
        SegmentButton btn = new SegmentButton(label, key);
        segments.put(key, btn);
        add(btn);
    }

    public void setSelected(String key) {
        if (!segments.containsKey(key)) return;
        boolean changed = !key.equals(this.selectedKey);
        this.selectedKey = key;
        segments.values().forEach(SegmentButton::repaint);
        // notify hanya kalau ada perubahan (mencegah re-fire saat init/refresh)
        if (changed) {
            for (Consumer<String> l : listeners) l.accept(key);
        }
    }

    public String getSelected() { return selectedKey; }

    public void onChange(Consumer<String> listener) {
        listeners.add(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = UIMetrics.RADIUS_BUTTON * 2;

        g2.setColor(UIColors.NEUTRAL_100);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

        g2.dispose();
    }

    /** Individual segment button (inner class). */
    private final class SegmentButton extends JButton {
        private final String key;
        private boolean hovered = false;

        SegmentButton(String label, String key) {
            super(label);
            this.key = key;
            setFont(UIFonts.LABEL_BOLD);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(UIMetrics.SPACE_8, UIMetrics.SPACE_16,
                                       UIMetrics.SPACE_8, UIMetrics.SPACE_16));
            addActionListener(e -> SegmentedControl.this.setSelected(this.key));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 30);
            return d;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean active = key.equals(selectedKey);
            int w = getWidth();
            int h = getHeight();
            int arc = UIMetrics.RADIUS_BUTTON * 2 - 2;

            if (active) {
                // Shadow tipis di bawah tab aktif
                g2.setColor(new Color(0, 0, 0, 14));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, arc, arc));
                g2.setColor(UIColors.NEUTRAL_0);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 1, arc, arc));
                setForeground(UIColors.PRIMARY_600);
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 140));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 1, arc, arc));
                setForeground(UIColors.NEUTRAL_800);
            } else {
                setForeground(UIColors.NEUTRAL_600);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
