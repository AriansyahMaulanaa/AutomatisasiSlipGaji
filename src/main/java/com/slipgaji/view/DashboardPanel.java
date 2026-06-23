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
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(createHeader(), BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(createStatsRow());
        center.add(Box.createVerticalStrut(16));
        center.add(createQuickActions());
        center.add(Box.createVerticalStrut(16));
        center.add(createBottomRow());
        add(center, BorderLayout.CENTER);
        refresh();
    }

    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 4, 16, 4));

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
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        empCountLabel = new JLabel();
        payslipCountLabel = new JLabel();
        sentCountLabel = new JLabel();
        failedCountLabel = new JLabel();
        row.add(createStatCard("Total Karyawan", empCountLabel, Constants.PRIMARY));
        row.add(createStatCard("Slip Gaji", payslipCountLabel, Constants.ACCENT));
        row.add(createStatCard("Email Terkirim", sentCountLabel, new Color(16, 185, 129)));
        row.add(createStatCard("Email Gagal", failedCountLabel, Constants.ACCENT_DANGER));
        return row;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), r = 12;
                for (int i = 2; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 * (3 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2 + 1, r, r));
                }
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                g2.setColor(accent);
                g2.fillRect(0, 0, w, 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel tl = new JLabel(title);
        tl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tl.setForeground(Constants.TEXT_SECONDARY);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        valueLabel.setForeground(Constants.TEXT_PRIMARY);
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(valueLabel, BorderLayout.NORTH);
        inner.add(Box.createVerticalStrut(2));
        inner.add(tl, BorderLayout.SOUTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActions() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), r = 12;
                for (int i = 2; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 * (3 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2 + 1, r, r));
                }
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Aksi Cepat");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        title.setForeground(Constants.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnRow.setOpaque(false);
        btnRow.add(makeBtn("Import Data", Constants.PRIMARY, e -> mainView.navigateToImport()));
        btnRow.add(makeBtn("Slip Gaji", new Color(79, 70, 229), e -> mainView.navigateToPayslips()));
        btnRow.add(makeBtn("Histori", Constants.ACCENT_WARN, e -> refresh()));
        card.add(btnRow, BorderLayout.CENTER);
        return card;
    }

    private JButton makeBtn(String text, Color bg, java.awt.event.ActionListener a) {
        JButton btn = new JButton(text) {
            private float hover = 0f;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = 0.1f; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = 0f; repaint(); }
            });}
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                if (hover > 0) { g2.setColor(new Color(255,255,255,(int)(hover*255))); g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10)); }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addActionListener(a);
        return btn;
    }

    private JPanel createBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
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
                int w = getWidth(), h = getHeight(), r = 12;
                for (int i = 2; i > 0; i--) {
                    g2.setColor(new Color(0,0,0,6*(3-i)));
                    g2.fill(new RoundRectangle2D.Double(i,i+1,w-i*2,h-i*2+1,r,r));
                }
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0,0,w,h,r,r));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel t = new JLabel(title);
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        t.setForeground(Constants.TEXT_PRIMARY);
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(t, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPanduanCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), r = 12;
                for (int i = 2; i > 0; i--) {
                    g2.setColor(new Color(0,0,0,6*(3-i)));
                    g2.fill(new RoundRectangle2D.Double(i,i+1,w-i*2,h-i*2+1,r,r));
                }
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0,0,w,h,r,r));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel t = new JLabel("Panduan");
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        t.setForeground(Constants.TEXT_PRIMARY);
        card.add(t, BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        for (String s : new String[]{"1. Import file Excel (.xlsx) di menu Import Data","2. Generate PDF di menu Slip Gaji","3. Kirim email ke karyawan via batch","4. Cek status kirim di Histori","5. Konfigurasi SMTP & parameter di Pengaturan"}) {
            JLabel l = new JLabel(s);
            l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            l.setForeground(Constants.TEXT_SECONDARY);
            l.setBorder(new EmptyBorder(3, 0, 3, 0));
            list.add(l);
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
            JLabel l = new JLabel(ps.getFormattedPeriod() + "  -  " + ps.getSlipCount() + " slip  |  " + df.format(ps.getTotalSalary()));
            l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            l.setForeground(Constants.TEXT_SECONDARY);
            l.setBorder(new EmptyBorder(2, 0, 2, 0));
            periodPanel.add(l);
        }
        if (periods.isEmpty()) {
            JLabel e = new JLabel("Belum ada data penggajian.");
            e.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            e.setForeground(Constants.TEXT_SECONDARY);
            periodPanel.add(e);
        }
        periodPanel.revalidate();
        periodPanel.repaint();
    }
}
