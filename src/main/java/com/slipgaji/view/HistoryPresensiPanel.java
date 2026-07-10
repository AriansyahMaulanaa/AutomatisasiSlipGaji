package com.slipgaji.view;

import com.slipgaji.dao.PresensiDAO;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HistoryPresensiPanel extends JPanel {

    private final PresensiDAO presensiDAO = new PresensiDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private javax.swing.JSpinner dateSpinner;

    private enum StatusPresensi { SEDANG_SHIFT, SELESAI }

    public HistoryPresensiPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(Constants.SPACING_MD, Constants.SPACING_LG - 4,
                                   Constants.SPACING_MD, Constants.SPACING_LG - 4));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Riwayat Presensi");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        dateSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(de);
        dateSpinner.setValue(java.util.Date.from(java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        dateSpinner.setPreferredSize(new Dimension(140, 36));
        dateSpinner.setFont(Constants.FONT_BODY);

        JButton btnHariIni = UIHelper.createOutlineButton("Hari Ini");
        btnHariIni.addActionListener(e -> {
            dateSpinner.setValue(java.util.Date.from(java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            refresh();
        });

        JButton btnCari = UIHelper.createStyledButton("Cari", Constants.ACCENT_ACTION);
        btnCari.addActionListener(e -> refresh());

        controls.add(dateSpinner);
        controls.add(btnHariIni);
        controls.add(btnCari);
        header.add(controls, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"No", "ID Karyawan", "Nama", "Jabatan", "Jam Masuk", "Jam Pulang", "Total Jam", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(44);

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);

        UIHelper.styleTable(table);

        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);

        statusLabel = new JLabel("Pilih tanggal dan klik Cari");
        statusLabel.setFont(Constants.FONT_BODY);
        statusLabel.setForeground(Constants.TEXT_LABEL);
        statusLabel.setBorder(new EmptyBorder(0, 4, Constants.SPACING_SM + 4, 4));

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, 12, 12));
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 12, 12));
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(Constants.SPACING_SM + 8, Constants.SPACING_LG,
                                          Constants.SPACING_SM + 8, Constants.SPACING_LG));

        wrapper.add(statusLabel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    public void refresh() {
        java.util.Date selected = (java.util.Date) dateSpinner.getValue();
        LocalDate date = selected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        List<Object[]> rows = presensiDAO.getAggregatedHistoryByDate(date);
        tableModel.setRowCount(0);
        int no = 1;
        for (Object[] r : rows) {
            LocalTime jamMasuk = (LocalTime) r[5];
            LocalTime jamPulang = (LocalTime) r[6];

            String jamMasukStr = jamMasuk != null
                    ? jamMasuk.format(DateTimeFormatter.ofPattern("HH:mm")) : "-";
            String jamPulangStr = jamPulang != null
                    ? jamPulang.format(DateTimeFormatter.ofPattern("HH:mm")) : "Belum pulang";
            String totalJam = "-";
            if (jamMasuk != null && jamPulang != null) {
                long diff = Duration.between(jamMasuk, jamPulang).toMinutes();
                totalJam = (diff / 60) + "j " + (diff % 60) + "m";
            }

            StatusPresensi status = (jamMasuk != null && jamPulang == null)
                    ? StatusPresensi.SEDANG_SHIFT : StatusPresensi.SELESAI;
            if (jamMasuk == null) continue;

            tableModel.addRow(new Object[]{
                    no++,
                    r[1],
                    r[2],
                    r[3],
                    jamMasukStr,
                    jamPulangStr,
                    totalJam,
                    status
            });
        }
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));
        statusLabel.setText("Data presensi " + dateStr + " (" + tableModel.getRowCount() + " data)");

        if (tableModel.getRowCount() == 0) {
            tableModel.addRow(new Object[]{null, null, null, null, null, null, null, "Belum ada data presensi untuk tanggal ini"});
        }
    }

    private static class StatusBadgeRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, h, h));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        private final JLabel dot = new JLabel();
        private final JLabel label = new JLabel();
        private Color bg, border, dotColor, textColor;

        StatusBadgeRenderer() {
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(2, 10, 2, 10));
            JPanel d = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(dotColor);
                    g2.fillOval(0, 0, 7, 7);
                    g2.dispose();
                }
            };
            d.setOpaque(false);
            d.setPreferredSize(new Dimension(7, 7));

            label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 11));
            label.setForeground(textColor);

            panel.add(d);
            panel.add(label);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            if (v == null) return new JLabel("");
            if (v instanceof String && ((String) v).startsWith("Belum")) {
                JLabel empty = new JLabel((String) v, SwingConstants.CENTER);
                empty.setFont(Constants.FONT_BODY);
                empty.setForeground(Constants.TEXT_MUTED);
                return empty;
            }
            StatusPresensi s = (StatusPresensi) v;
            if (s == StatusPresensi.SEDANG_SHIFT) {
                bg = Constants.WARN_BG;
                border = Constants.WARN_TEXT;
                dotColor = Constants.WARN_TEXT;
                textColor = Constants.WARN_TEXT;
                label.setText("Sedang Shift");
            } else {
                bg = Constants.SUCCESS_BG;
                border = Constants.SUCCESS;
                dotColor = Constants.SUCCESS;
                textColor = Constants.SUCCESS;
                label.setText("Selesai");
            }
            label.setForeground(textColor);
            panel.repaint();
            return panel;
        }
    }
}