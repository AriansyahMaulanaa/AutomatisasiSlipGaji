package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.controller.HistoryController;
import com.slipgaji.model.PeriodSummary;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.EmptyState;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * DashboardPanel — halaman utama.
 *
 * <p>Struktur:
 * <ul>
 *   <li>Header: greeting + tanggal.</li>
 *   <li>4 stat card (angka besar, icon soft-blue line-style).</li>
 *   <li>Aksi cepat (1 primary + 2 secondary).</li>
 *   <li>Ringkasan Periode + Panduan (grid 2 kolom).</li>
 * </ul>
 */
public class DashboardPanel extends JPanel {
    private JLabel empCountLabel, payslipCountLabel, sentCountLabel, failedCountLabel;
    private JPanel periodBody;
    private final MainView mainView;

    private static final DecimalFormat CURRENCY;
    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale.Builder().setLanguage("id").setRegion("ID").build());
        sym.setGroupingSeparator('.');
        CURRENCY = new DecimalFormat("Rp #,##0", sym);
    }

    public DashboardPanel(MainView mainView) {
        this.mainView = mainView;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIColors.NEUTRAL_50);
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_24));

        add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, UIMetrics.SPACE_16, 0);
        center.add(createStatsRow(), gbc);

        gbc.gridy = 1;
        center.add(createQuickActions(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(createBottomRow(), gbc);

        add(center, BorderLayout.CENTER);
        refresh();
    }

    // ============================================================
    // Header
    // ============================================================
    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, UIMetrics.SPACE_20, 0));

        int hr = java.time.LocalTime.now().getHour();
        String time = hr < 10 ? "Pagi" : hr < 15 ? "Siang" : hr < 18 ? "Sore" : "Malam";
        String user = AuthController.getCurrentUser() != null ? AuthController.getCurrentUser().getUsername() : "";

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        JLabel greet = new JLabel("Selamat " + time + ", " + user);
        greet.setFont(UIFonts.BODY);
        greet.setForeground(UIColors.NEUTRAL_600);
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(greet);
        h.add(left, BorderLayout.WEST);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy",
                new Locale("id", "ID")));
        JLabel dateLbl = new JLabel(today);
        dateLbl.setFont(UIFonts.BODY);
        dateLbl.setForeground(UIColors.NEUTRAL_600);
        h.add(dateLbl, BorderLayout.EAST);
        return h;
    }

    // ============================================================
    // Stat cards
    // ============================================================
    private JPanel createStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, UIMetrics.SPACE_16, 0));
        row.setOpaque(false);
        empCountLabel = new JLabel("0");
        payslipCountLabel = new JLabel("0");
        sentCountLabel = new JLabel("0");
        failedCountLabel = new JLabel("0");
        row.add(createStatCard("Total Karyawan", empCountLabel, StatIcon.USERS));
        row.add(createStatCard("Slip Gaji", payslipCountLabel, StatIcon.DOCUMENT));
        row.add(createStatCard("Email Terkirim", sentCountLabel, StatIcon.SEND));
        row.add(createStatCard("Email Gagal", failedCountLabel, StatIcon.ALERT));
        return row;
    }

    private enum StatIcon { USERS, DOCUMENT, SEND, ALERT }

    private JPanel createStatCard(String title, JLabel valueLabel, StatIcon icon) {
        AppCard card = new AppCard();

        JPanel body = new JPanel(new BorderLayout(UIMetrics.SPACE_16, 0));
        body.setOpaque(false);

        // Icon container (soft primary bg with line-style icon)
        JPanel iconWrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                // bg soft
                Color bg = (icon == StatIcon.ALERT) ? UIColors.DANGER_BG
                          : (icon == StatIcon.SEND)  ? UIColors.SUCCESS_BG
                          : UIColors.PRIMARY_50;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(x, y, size, size, 12, 12));

                // line-style icon
                Color fg = (icon == StatIcon.ALERT) ? UIColors.DANGER_FG
                          : (icon == StatIcon.SEND)  ? UIColors.SUCCESS_FG
                          : UIColors.PRIMARY_500;
                g2.setColor(fg);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + size / 2;
                int cy = y + size / 2;
                drawIcon(g2, icon, cx, cy);

                g2.dispose();
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(44, 44));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        valueLabel.setFont(UIFonts.STAT_VALUE);
        valueLabel.setForeground(UIColors.NEUTRAL_800);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tl = new JLabel(title);
        tl.setFont(UIFonts.LABEL);
        tl.setForeground(UIColors.NEUTRAL_600);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(tl);

        body.add(iconWrap, BorderLayout.WEST);
        body.add(textPanel, BorderLayout.CENTER);
        card.addBody(body);
        return card;
    }

    private void drawIcon(Graphics2D g2, StatIcon icon, int cx, int cy) {
        switch (icon) {
            case USERS -> {
                // dua kepala
                int hr = 4;
                g2.drawOval(cx - 8, cy - 8, hr * 2, hr * 2);
                g2.drawArc(cx - 11, cy - 1, 12, 12, 0, 180);
                g2.drawOval(cx + 2, cy - 6, hr * 2, hr * 2);
                g2.drawArc(cx - 1, cy + 1, 12, 12, 0, 180);
            }
            case DOCUMENT -> {
                int w = 12, h = 15;
                int x = cx - w / 2, y = cy - h / 2;
                int fold = 4;
                g2.draw(new RoundRectangle2D.Double(x, y, w, h, 2, 2));
                g2.drawLine(x + w - fold, y, x + w - fold, y + fold);
                g2.drawLine(x + w - fold, y + fold, x + w, y + fold);
                g2.drawLine(x + 3, y + h - 8, x + w - 3, y + h - 8);
                g2.drawLine(x + 3, y + h - 5, x + w - 4, y + h - 5);
            }
            case SEND -> {
                // paper plane
                int[] xs = { cx - 8, cx + 8, cx - 4 };
                int[] ys = { cy - 6, cy - 2, cy + 6 };
                g2.drawPolygon(xs, ys, 3);
                g2.drawLine(cx - 4, cy + 6, cx + 8, cy - 2);
            }
            case ALERT -> {
                // segitiga peringatan
                int[] xs = { cx, cx - 9, cx + 9 };
                int[] ys = { cy - 8, cy + 7, cy + 7 };
                g2.drawPolygon(xs, ys, 3);
                g2.drawLine(cx, cy - 2, cx, cy + 2);
                g2.fillOval(cx - 1, cy + 4, 2, 2);
            }
        }
    }

    // ============================================================
    // Quick actions
    // ============================================================
    private JPanel createQuickActions() {
        AppCard card = new AppCard("Aksi Cepat");
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, UIMetrics.SPACE_8, 0));
        row.setOpaque(false);

        JButton primary = AppButton.primary("Buka Slip Gaji");
        primary.addActionListener(e -> mainView.navigateToPayslips());
        JButton scan = AppButton.secondary("Presensi Scan");
        scan.addActionListener(e -> mainView.navigateToPresensi());
        JButton refresh = AppButton.secondary("Refresh Data");
        refresh.addActionListener(e -> refresh());

        row.add(primary);
        row.add(scan);
        row.add(refresh);
        card.addBody(row);
        return card;
    }

    // ============================================================
    // Bottom row (Ringkasan Periode + Panduan)
    // ============================================================
    private JPanel createBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, UIMetrics.SPACE_16, 0));
        row.setOpaque(false);

        AppCard periodCard = new AppCard("Ringkasan Periode");
        periodBody = new JPanel();
        periodBody.setLayout(new BoxLayout(periodBody, BoxLayout.Y_AXIS));
        periodBody.setOpaque(false);
        periodCard.addBody(periodBody);
        row.add(periodCard);

        row.add(createPanduanCard());
        return row;
    }

    private JPanel createPanduanCard() {
        AppCard card = new AppCard("Panduan");
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
            list.add(createStepRow(i + 1, steps[i], i < steps.length - 1));
        }
        card.addBody(list);
        return card;
    }

    private JPanel createStepRow(int num, String text, boolean withConnector) {
        JPanel row = new JPanel(new BorderLayout(UIMetrics.SPACE_12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(UIMetrics.SPACE_4, 0, UIMetrics.SPACE_4, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // circle with connector line below
        JPanel circle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 22;
                int cx = getWidth() / 2 - size / 2;
                int cy = 2;
                g2.setColor(UIColors.PRIMARY_100);
                g2.fillOval(cx, cy, size, size);

                g2.setColor(UIColors.PRIMARY_700);
                g2.setFont(UIFonts.CAPTION_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(num);
                int tx = cx + (size - fm.stringWidth(s)) / 2;
                int ty = cy + (size + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(s, tx, ty);

                if (withConnector) {
                    g2.setColor(UIColors.NEUTRAL_200);
                    g2.setStroke(new BasicStroke(1f));
                    int lineX = getWidth() / 2;
                    g2.drawLine(lineX, cy + size + 2, lineX, getHeight());
                }
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(24, 32));

        JLabel lbl = new JLabel(text);
        lbl.setFont(UIFonts.BODY);
        lbl.setForeground(UIColors.NEUTRAL_800);
        lbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        row.add(circle, BorderLayout.WEST);
        row.add(lbl, BorderLayout.CENTER);
        return row;
    }

    // ============================================================
    // Refresh
    // ============================================================
    public void refresh() {
        DatabaseService db = DatabaseService.getInstance();
        HistoryController hc = new HistoryController();
        empCountLabel.setText(String.valueOf(db.getEmployeeCount()));
        payslipCountLabel.setText(String.valueOf(db.getPayslipCount()));
        sentCountLabel.setText(String.valueOf(hc.getSentCount()));
        failedCountLabel.setText(String.valueOf(hc.getFailedCount()));

        periodBody.removeAll();
        List<PeriodSummary> periods = db.getPayslipPeriodSummaries();

        if (periods.isEmpty()) {
            JButton cta = AppButton.primary("Generate Slip Gaji →");
            cta.addActionListener(e -> mainView.navigateToPayslips());
            EmptyState empty = new EmptyState(EmptyState.Icon.DOCUMENT,
                    "Belum ada data penggajian",
                    "Slip gaji akan muncul di sini setelah proses generate",
                    cta);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            periodBody.add(empty);
        } else {
            int max = Math.min(periods.size(), 5);
            for (int i = 0; i < max; i++) {
                periodBody.add(createPeriodRow(periods.get(i)));
                if (i < max - 1) periodBody.add(createPeriodDivider());
            }
        }

        periodBody.revalidate();
        periodBody.repaint();
    }

    private JPanel createPeriodRow(PeriodSummary ps) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(UIMetrics.SPACE_8, 0, UIMetrics.SPACE_8, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel period = new JLabel(ps.getFormattedPeriod());
        period.setFont(UIFonts.BODY_BOLD);
        period.setForeground(UIColors.NEUTRAL_800);

        JLabel detail = new JLabel(ps.getSlipCount() + " slip  •  " + CURRENCY.format(ps.getTotalSalary()));
        detail.setFont(UIFonts.CAPTION);
        detail.setForeground(UIColors.NEUTRAL_600);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(period);
        left.add(Box.createVerticalStrut(2));
        left.add(detail);
        row.add(left, BorderLayout.WEST);
        return row;
    }

    private JPanel createPeriodDivider() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UIColors.NEUTRAL_200);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        d.setOpaque(false);
        d.setPreferredSize(new Dimension(0, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return d;
    }
}
