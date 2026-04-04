package com.slipgaji.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class UIHelper {

    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("Rp #,##0", symbols);
    }

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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

    public static JButton createSidebarButton(String text, String icon) {
        JButton button = new JButton("  " + icon + "  " + text);
        button.setFont(Constants.FONT_BODY);
        button.setForeground(Constants.TEXT_SECONDARY);
        button.setBackground(Constants.SIDEBAR_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 16, 12, 16));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(Constants.BG_CARD);
                    button.setForeground(Constants.TEXT_PRIMARY);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(Constants.SIDEBAR_BG);
                    button.setForeground(Constants.TEXT_SECONDARY);
                }
            }
        });
        return button;
    }

    public static void setSidebarButtonActive(JButton button, boolean active) {
        if (active) {
            button.setBackground(Constants.PRIMARY);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Constants.SIDEBAR_BG);
            button.setForeground(Constants.TEXT_SECONDARY);
        }
    }

    public static JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Constants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(Constants.FONT_HEADING);
            titleLabel.setForeground(Constants.TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        return card;
    }

    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Constants.TEXT_SECONDARY);
                    g2.setFont(Constants.FONT_BODY);
                    g2.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(Constants.FONT_BODY);
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(Constants.BG_SURFACE);
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Constants.TEXT_SECONDARY);
                    g2.setFont(Constants.FONT_BODY);
                    g2.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(Constants.FONT_BODY);
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(Constants.BG_SURFACE);
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    public static void styleTable(JTable table) {
        table.setFont(Constants.FONT_BODY);
        table.setForeground(Constants.TEXT_PRIMARY);
        table.setBackground(Constants.BG_CARD);
        table.setGridColor(Constants.BORDER_COLOR);
        table.setSelectionBackground(Constants.PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(Constants.FONT_HEADING);
        header.setForeground(Constants.TEXT_PRIMARY);
        header.setBackground(Constants.BG_SURFACE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Constants.PRIMARY));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public static JLabel createStatCard(String label, String value, Color accentColor) {
        JLabel card = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setText("<html><div style='padding:8px'>"
                + "<span style='color:#94a3b8;font-size:10px'>" + label + "</span><br>"
                + "<span style='color:#f8fafc;font-size:20px'><b>" + value + "</b></span>"
                + "</div></html>");
        card.setFont(Constants.FONT_BODY);
        card.setForeground(Constants.TEXT_PRIMARY);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        card.setPreferredSize(new Dimension(200, 80));
        return card;
    }

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
}
