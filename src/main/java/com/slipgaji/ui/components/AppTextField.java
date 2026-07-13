package com.slipgaji.ui.components;

import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Styled text field & password field factory.
 *
 * <ul>
 *   <li>Border 1px {@link UIColors#NEUTRAL_200}, radius {@link UIMetrics#RADIUS_BUTTON}.</li>
 *   <li>Focus: border {@link UIColors#PRIMARY_500} + subtle ring.</li>
 *   <li>Height {@link UIMetrics#INPUT_HEIGHT}.</li>
 * </ul>
 */
public final class AppTextField {

    private AppTextField() {}

    /** Create text field ber-placeholder. */
    public static JTextField create(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UIMetrics.RADIUS_BUTTON * 2, UIMetrics.RADIUS_BUTTON * 2));
                g2.dispose();
                super.paintComponent(g);
                if (placeholder != null && getText().isEmpty() && !hasFocus()) {
                    Graphics2D gp = (Graphics2D) g.create();
                    gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gp.setColor(UIColors.NEUTRAL_400);
                    gp.setFont(UIFonts.BODY);
                    gp.drawString(placeholder, getInsets().left,
                            getHeight() / 2 + gp.getFontMetrics().getAscent() / 2 - 2);
                    gp.dispose();
                }
            }
        };
        styleField(f);
        return f;
    }

    /** Create password field ber-placeholder. */
    public static JPasswordField createPassword(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UIMetrics.RADIUS_BUTTON * 2, UIMetrics.RADIUS_BUTTON * 2));
                g2.dispose();
                super.paintComponent(g);
                if (placeholder != null && getPassword().length == 0 && !hasFocus()) {
                    Graphics2D gp = (Graphics2D) g.create();
                    gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gp.setColor(UIColors.NEUTRAL_400);
                    gp.setFont(UIFonts.BODY);
                    gp.drawString(placeholder, getInsets().left,
                            getHeight() / 2 + gp.getFontMetrics().getAscent() / 2 - 2);
                    gp.dispose();
                }
            }
        };
        styleField(f);
        return f;
    }

    private static void styleField(JTextField f) {
        f.setFont(UIFonts.BODY);
        f.setForeground(UIColors.NEUTRAL_800);
        f.setCaretColor(UIColors.PRIMARY_500);
        f.setBackground(UIColors.NEUTRAL_0);
        f.setOpaque(false);
        f.setPreferredSize(new Dimension(200, UIMetrics.INPUT_HEIGHT));
        applyBorder(f, false);

        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { applyBorder(f, true); }
            @Override public void focusLost(FocusEvent e)   { applyBorder(f, false); }
        });
    }

    private static void applyBorder(JTextField f, boolean focused) {
        Color borderColor = focused ? UIColors.PRIMARY_500 : UIColors.NEUTRAL_200;
        Border line = BorderFactory.createLineBorder(borderColor, focused ? 2 : 1, true);
        Border pad  = new EmptyBorder(UIMetrics.INPUT_PAD_Y,
                                       UIMetrics.INPUT_PAD_X - (focused ? 1 : 0),
                                       UIMetrics.INPUT_PAD_Y,
                                       UIMetrics.INPUT_PAD_X - (focused ? 1 : 0));
        f.setBorder(new CompoundBorder(line, pad));
        f.repaint();
    }
}
