package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.controller.HistoryController;
import com.slipgaji.model.PeriodSummary;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardPanel extends JPanel {
    private JLabel empCountLabel, payslipCountLabel, sentCountLabel, failedCountLabel;
    private JPanel periodPanel;
    private final MainView mainView;

    public DashboardPanel(MainView mainView) {
        this.mainView = mainView;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(Constants.SPACING_LG, Constants.SPACING_LG + 4,
                                   Constants.SPACING_LG, Constants.SPACING_LG + 4));

        add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;

        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, Constants.SPACING_SM, 0);
        center.add(createStatsRow(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, Constants.SPACING_MD, 0);
        center.add(createQuickActions(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(createBottomRow(), gbc);

        add(center, BorderLayout.CENTER);
        refresh();
    }

    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 4, Constants.SPACING_MD, 4));

        int hr = java.time.LocalTime.now().getHour();
        String time = hr < 10 ? "Pagi" : hr < 15 ? "Siang" : hr < 18 ? "Sore" : "Malam";
        String user = AuthController.getCurrentUser() != null ? AuthController.getCurrentUser().getUsername() : "";

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(Constants.TEXT_PRIMARY);
        JLabel greet = new JLabel("Selamat " + time + ", " + user);
        greet.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        greet.setForeground(Constants.TEXT_SECONDARY);
        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(greet);
        h.add(left, BorderLayout.WEST);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));
        JLabel dateLbl = new JLabel(today);
        dateLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        dateLbl.setForeground(Constants.TEXT_SECONDARY);
        h.add(dateLbl, BorderLayout.EAST);
        return h;
    }

    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, Constants.SPACING_MD, 0));
        row.setOpaque(false);
        empCountLabel = new JLabel();
        payslipCountLabel = new JLabel();
        sentCountLabel = new JLabel();
        failedCountLabel = new JLabel();
        row.add(createStatCard("Total Karyawan", empCountLabel, 0, false, false));
        row.add(createStatCard("Slip Gaji", payslipCountLabel, 1, false, false));
        row.add(createStatCard("Email Terkirim", sentCountLabel, 2, true, false));
        row.add(createStatCard("Email Gagal", failedCountLabel, 3, false, true));
        return row;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, int iconType, boolean isSuccess, boolean isError) {
        Color iconBg;
        Color iconColor;
        if (isSuccess) {
            iconBg = Constants.SUCCESS_BG_TINT;
            iconColor = Constants.SUCCESS;
        } else if (isError) {
            iconBg = Constants.DANGER_BG_TINT;
            iconColor = Constants.DANGER;
        } else {
            iconBg = Constants.BG_SURFACE;
            iconColor = Constants.OUTLINE_TEXT;
        }

        JPanel card = new JPanel(new BorderLayout(Constants.SPACING_MD, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h, 10, 10));
                // card bg
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                // border
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Constants.SPACING_SM + 12, Constants.SPACING_LG,
                                        Constants.SPACING_SM + 12, Constants.SPACING_LG));

        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setColor(iconBg);
                g2.fillOval(x, y, d, d);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(44, 44));
        JLabel iconLabel = new JLabel(getStatIcon(iconType));
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        iconPanel.add(iconLabel);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        valueLabel.setForeground(Constants.TEXT_PRIMARY);
        JLabel tl = new JLabel(title);
        tl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tl.setForeground(Constants.TEXT_SECONDARY);
        textPanel.add(valueLabel, BorderLayout.NORTH);
        textPanel.add(tl, BorderLayout.SOUTH);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private String getStatIcon(int iconType) {
        return switch (iconType) {
            case 0 -> "\uD83D\uDC65";
            case 1 -> "\uD83D\uDCC4";
            case 2 -> "\u2705";
            case 3 -> "\u274C";
            default -> "?";
        };
    }

    private JPanel createQuickActions() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, 10, 10));
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Constants.SPACING_SM + 12, Constants.SPACING_LG,
                                        Constants.SPACING_SM + 12, Constants.SPACING_LG));

        JLabel title = new JLabel("Aksi Cepat");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        title.setForeground(Constants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, Constants.SPACING_SM + 4, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        btnRow.add(makePrimaryBtn("Slip Gaji", e -> mainView.navigateToPayslips()));
        btnRow.add(makeOutlineBtn("Presensi Scan", e -> mainView.navigateToPresensi()));
        btnRow.add(makeOutlineBtn("Histori", e -> refresh()));

        card.add(btnRow, BorderLayout.CENTER);
        return card;
    }

    private JButton makePrimaryBtn(String text, java.awt.event.ActionListener a) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = 0.12f; repaint(); }
                    @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = 0f; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hover > 0 ? Constants.ACCENT_ACTION_HOVER : Constants.ACCENT_ACTION;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                if (hover > 0) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.addActionListener(a);
        return btn;
    }

    private JButton makeOutlineBtn(String text, java.awt.event.ActionListener a) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = 0.06f; repaint(); }
                    @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = 0f; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                if (hover > 0) {
                    g2.setColor(new Color(51, 65, 85, (int)(hover * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.setColor(Constants.OUTLINE_TEXT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btn.setForeground(Constants.OUTLINE_TEXT);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.addActionListener(a);
        return btn;
    }

    private JPanel createBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, Constants.SPACING_MD, 0));
        row.setOpaque(false);
        periodPanel = new JPanel();
        periodPanel.setLayout(new BoxLayout(periodPanel, BoxLayout.Y_AXIS));
        periodPanel.setOpaque(false);
        row.add(wrapCard(periodPanel, "Ringkasan Periode"));
        row.add(createPanduanCard());
        return row;
    }

    private JPanel wrapCard(JPanel content, String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, 10, 10));
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Constants.SPACING_SM + 12, Constants.SPACING_LG,
                                        Constants.SPACING_SM + 12, Constants.SPACING_LG));
        JLabel t = new JLabel(title);
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        t.setForeground(Constants.TEXT_PRIMARY);
        t.setBorder(new EmptyBorder(0, 0, Constants.SPACING_SM + 4, 0));
        card.add(t, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPanduanCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, 10, 10));
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Constants.SPACING_SM + 12, Constants.SPACING_LG,
                                        Constants.SPACING_SM + 12, Constants.SPACING_LG));
        JLabel t = new JLabel("Panduan");
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        t.setForeground(Constants.TEXT_PRIMARY);
        t.setBorder(new EmptyBorder(0, 0, Constants.SPACING_SM + 4, 0));
        card.add(t, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        String[] steps = {
            "SPV: Scan barcode kartu karyawan di menu Presensi",
            "SPV: Kelola data karyawan di menu Kelola Karyawan",
            "Manager: Generate slip gaji dari data presensi",
            "Manager: Kirim email slip gaji ke karyawan",
            "Konfigurasi SMTP & parameter di Pengaturan"
        };
        for (int i = 0; i < steps.length; i++) {
            final int idx = i;
            JPanel item = new JPanel(new BorderLayout(Constants.SPACING_SM, 0));
            item.setOpaque(false);
            item.setBorder(new EmptyBorder(Constants.SPACING_XS, 0, Constants.SPACING_XS, 0));

            JPanel circle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = Math.min(getWidth(), getHeight());
                    g2.setColor(Constants.ACCENT_ACTION);
                    g2.fillOval(0, 0, d, d);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    String num = String.valueOf(idx + 1);
                    int tx = (d - fm.stringWidth(num)) / 2;
                    int ty = (d + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(num, tx, ty);
                    g2.dispose();
                }
            };
            circle.setOpaque(false);
            circle.setPreferredSize(new Dimension(22, 22));

            JLabel text = new JLabel(steps[idx]);
            text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            text.setForeground(Constants.TEXT_PRIMARY);

            item.add(circle, BorderLayout.WEST);
            item.add(text, BorderLayout.CENTER);
            list.add(item);
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        DatabaseService db = DatabaseService.getInstance();
        HistoryController hc = new HistoryController();
        empCountLabel.setText(String.valueOf(db.getEmployeeCount()));
        payslipCountLabel.setText(String.valueOf(db.getPayslipCount()));
        sentCountLabel.setText(String.valueOf(hc.getSentCount()));
        failedCountLabel.setText(String.valueOf(hc.getFailedCount()));

        periodPanel.removeAll();
        List<PeriodSummary> periods = db.getPayslipPeriodSummaries();
        int max = Math.min(periods.size(), 5);
        java.text.DecimalFormat df = new java.text.DecimalFormat("Rp #,##0",
            new java.text.DecimalFormatSymbols(new Locale.Builder().setLanguage("id").setRegion("ID").build()));
        df.getDecimalFormatSymbols().setGroupingSeparator('.');
        for (int i = 0; i < max; i++) {
            PeriodSummary ps = periods.get(i);
            JLabel l = new JLabel(ps.getFormattedPeriod() + "  -  " + ps.getSlipCount() + " slip  |  "
                                + df.format(ps.getTotalSalary()));
            l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            l.setForeground(Constants.TEXT_SECONDARY);
            l.setBorder(new EmptyBorder(Constants.SPACING_XS, 0, Constants.SPACING_XS, 0));
            periodPanel.add(l);
        }
        if (periods.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout(0, 12));
            emptyPanel.setOpaque(false);

            JLabel illusLabel = new JLabel("\uD83D\uDCCB", SwingConstants.CENTER);
            illusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 56));
            illusLabel.setForeground(Constants.TEXT_MUTED);
            illusLabel.setPreferredSize(new Dimension(130, 100));

            JPanel textBlock = new JPanel();
            textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
            textBlock.setOpaque(false);

            JLabel msg = new JLabel("Belum ada data penggajian periode ini");
            msg.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            msg.setForeground(Constants.TEXT_SECONDARY);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel desc = new JLabel("Slip gaji akan muncul di sini setelah proses generate");
            desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            desc.setForeground(Constants.TEXT_MUTED);
            desc.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnWrap.setOpaque(false);
            JButton ctaBtn = makeOutlineBtn("Generate Slip Gaji \u2192", e -> mainView.navigateToPayslips());
            ctaBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            ctaBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
            btnWrap.add(ctaBtn);

            textBlock.add(msg);
            textBlock.add(Box.createVerticalStrut(4));
            textBlock.add(desc);
            textBlock.add(Box.createVerticalStrut(Constants.SPACING_SM + 4));
            textBlock.add(btnWrap);

            emptyPanel.add(illusLabel, BorderLayout.CENTER);
            emptyPanel.add(textBlock, BorderLayout.SOUTH);

            periodPanel.add(emptyPanel);
        }
        periodPanel.revalidate();
        periodPanel.repaint();
    }
}