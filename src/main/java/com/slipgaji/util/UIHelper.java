package com.slipgaji.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import java.awt.Dialog.ModalityType;

public class UIHelper {

    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale.Builder().setLanguage("id").setRegion("ID").build());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("Rp #,##0", symbols);
    }

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    // ======================== BUTTONS ========================

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private float hoverAlpha = 0f;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverAlpha = 0.15f;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverAlpha = 0f;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg;
                if (!isEnabled()) {
                    bg = Constants.BORDER_COLOR;
                } else if (getModel().isPressed()) {
                    bg = bgColor.darker();
                } else {
                    bg = bgColor;
                }
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        Constants.BUTTON_RADIUS * 2, Constants.BUTTON_RADIUS * 2));

                // Hover overlay
                if (hoverAlpha > 0 && isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                            Constants.BUTTON_RADIUS * 2, Constants.BUTTON_RADIUS * 2));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(Constants.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    // ======================== SIDEBAR ========================

    public static JButton createSidebarButton(String text, String icon) {
        JButton button = new JButton("  " + text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        Object active = getClientProperty("sidebar.active");
                        if (!Boolean.TRUE.equals(active)) {
                            hovered = true;
                            repaint();
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int margin = 8;
                boolean isActive = Boolean.TRUE.equals(getClientProperty("sidebar.active"));

                if (isActive) {
                    // Active state: light blue background with left accent
                    g2.setColor(Constants.SIDEBAR_ACTIVE_BG);
                    g2.fill(new RoundRectangle2D.Double(margin, 2, w - margin * 2, h - 4, 12, 12));
                    // Left accent bar
                    g2.setColor(Constants.PRIMARY);
                    g2.fill(new RoundRectangle2D.Double(margin, 6, 4, h - 12, 4, 4));
                } else if (hovered) {
                    g2.setColor(new Color(243, 244, 246));
                    g2.fill(new RoundRectangle2D.Double(margin, 2, w - margin * 2, h - 4, 12, 12));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(Constants.FONT_BODY);
        button.setForeground(Constants.TEXT_SECONDARY);
        button.setBackground(Constants.SIDEBAR_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 16, 12, 16));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        return button;
    }

    public static void setSidebarButtonActive(JButton button, boolean active) {
        // Call the internal setActive method via reflection-safe approach
        if (active) {
            button.setForeground(Constants.PRIMARY);
            button.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        } else {
            button.setForeground(Constants.TEXT_SECONDARY);
            button.setFont(Constants.FONT_BODY);
        }
        // Trigger the custom paint
        button.putClientProperty("sidebar.active", active);
        button.repaint();
    }

    // ======================== TOGGLE BUTTONS ========================

    public static JToggleButton createStyledToggleButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text) {
            private float hoverAlpha = 0f;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!isSelected()) {
                            hoverAlpha = 0.12f;
                            repaint();
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverAlpha = 0f;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = Constants.BUTTON_RADIUS * 2;

                if (isSelected()) {
                    g2.setColor(Constants.PRIMARY);
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
                } else {
                    g2.setColor(new Color(243, 244, 246));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
                    g2.setColor(Constants.BORDER_COLOR);
                    g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, arc, arc));
                }

                if (hoverAlpha > 0) {
                    g2.setColor(new Color(0, 0, 0, (int)(hoverAlpha * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        btn.setForeground(selected ? Color.WHITE : Constants.TEXT_SECONDARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setSelected(selected);

        btn.addItemListener(e -> {
            btn.setForeground(btn.isSelected() ? Color.WHITE : Constants.TEXT_SECONDARY);
            btn.repaint();
        });

        return btn;
    }

    // ======================== CARDS ========================

    public static JPanel createCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Shadow
                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 8 * (4 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2 + 1,
                            Constants.CARD_RADIUS * 2, Constants.CARD_RADIUS * 2));
                }

                // Card background
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1,
                        Constants.CARD_RADIUS * 2, Constants.CARD_RADIUS * 2));

                // Subtle border
                g2.setColor(Constants.BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1,
                        Constants.CARD_RADIUS * 2, Constants.CARD_RADIUS * 2));

                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(Constants.FONT_HEADING);
            titleLabel.setForeground(Constants.TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(0, 0, 16, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        return card;
    }

    // ======================== TEXT FIELDS ========================

    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(Constants.FONT_BODY);
                    g3.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g3.dispose();
                }
            }
        };
        field.setFont(Constants.FONT_BODY);
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(Constants.BG_SURFACE);
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(Constants.FONT_BODY);
                    g3.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g3.dispose();
                }
            }
        };
        field.setFont(Constants.FONT_BODY);
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(Constants.BG_SURFACE);
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return field;
    }

    // ======================== TABLES ========================

    public static void styleTable(JTable table) {
        table.setFont(Constants.FONT_BODY);
        table.setForeground(Constants.TEXT_PRIMARY);
        table.setBackground(Constants.BG_CARD);
        table.setGridColor(new Color(243, 244, 246));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Constants.TEXT_PRIMARY);
        table.setRowHeight(40);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Table Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        header.setForeground(Constants.TEXT_SECONDARY);
        header.setBackground(Constants.BG_SURFACE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Constants.BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));

        // Zebra / Alternating Row Colors with center alignment
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Constants.BG_CARD);
                    } else {
                        c.setBackground(Constants.TABLE_ROW_ALT);
                    }
                    c.setForeground(Constants.TEXT_PRIMARY);
                }
                return c;
            }
        });
    }

    // ======================== STAT CARDS ========================

    public static JLabel createStatCard(String label, String value, Color accentColor) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int r = Constants.CARD_RADIUS * 2;

                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 * (4 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2 + 1, r, r));
                }

                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));

                g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                g2.setColor(accentColor);
                g2.fillRect(0, 0, 6, h);
                g2.setClip(null);

                g2.setColor(Constants.BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, r, r));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setText("<html><div style='padding:4px 6px'>"
                + "<span style='color:#111827;font-size:22px;font-family:" + Constants.FONT_FAMILY + "'><b>" + value + "</b></span><br>"
                + "<span style='color:#6B7280;font-size:11px;font-family:" + Constants.FONT_FAMILY + "'>" + label + "</span>"
                + "</div></html>");
        card.setFont(Constants.FONT_BODY);
        card.setForeground(Constants.TEXT_PRIMARY);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 16, 10, 16));
        card.setPreferredSize(new Dimension(200, 80));
        return card;
    }

    // ======================== DIALOGS ========================

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ======================== VALIDATION DIALOG ========================

    public static void showValidationDialog(Component parent, String title,
            java.util.List<ValidationUtil.ValidationError> items, boolean isError) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(owner, title, ModalityType.APPLICATION_MODAL);
        dialog.setSize(560, 400);
        dialog.setLocationRelativeTo(owner);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_CARD);

        Color headerColor = isError ? Constants.ACCENT_DANGER : Constants.ACCENT_WARN;
        String icon = isError ? "[X]" : "[!]";
        String typeLabel = isError ? "Error Validasi" : "Peringatan Validasi";

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(headerColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.fillRect(0, getHeight() - 12, getWidth(), 12);
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        headerPanel.setLayout(new BorderLayout());

        JLabel headerIcon = new JLabel(" " + icon + "  " + items.size() + " " + typeLabel);
        headerIcon.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 15));
        headerIcon.setForeground(Color.WHITE);
        headerPanel.add(headerIcon, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Constants.BG_CARD);
        listPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        int maxShow = 50;
        int count = 0;
        for (ValidationUtil.ValidationError ve : items) {
            if (count >= maxShow) {
                JLabel more = new JLabel("... dan " + (items.size() - maxShow) + " lainnya");
                more.setFont(new Font(Constants.FONT_FAMILY, Font.ITALIC, 12));
                more.setForeground(Constants.TEXT_SECONDARY);
                more.setBorder(new EmptyBorder(4, 8, 4, 8));
                listPanel.add(more);
                break;
            }
            Color textColor = isError ? Constants.ACCENT_DANGER : Constants.ACCENT_WARN;
            String prefix = isError ? "-" : "-";
            JLabel item = new JLabel("<html><span style='color:" + colorToHex(textColor) + ";'>" + prefix + "</span> "
                    + escapeHtml(ve.toString()) + "</html>");
            item.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 12));
            item.setForeground(Constants.TEXT_PRIMARY);
            item.setBorder(new EmptyBorder(3, 4, 3, 4));
            listPanel.add(item);
            count++;
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footerPanel.setBackground(Constants.BG_CARD);
        JButton closeBtn = new JButton("Tutup");
        closeBtn.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(Constants.PRIMARY);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(new EmptyBorder(8, 24, 8, 24));
        closeBtn.addActionListener(e -> dialog.dispose());
        footerPanel.add(closeBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private static String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
