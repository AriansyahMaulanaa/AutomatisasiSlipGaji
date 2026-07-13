package com.slipgaji.view;

import com.slipgaji.dao.PresensiDAO;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.StatusBadge;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * HistoryPresensiPanel — daftar presensi per tanggal.
 *
 * <p>Redesign: badge "Sedang Shift" → Warning muted, "Belum pulang" → Neutral tag,
 * "Selesai" → Success. Tombol Cari = Primary, Hari Ini = Secondary.
 */
public class HistoryPresensiPanel extends JPanel {

    private final PresensiDAO presensiDAO = new PresensiDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private JSpinner dateSpinner;

    private enum StatusPresensi { SEDANG_SHIFT, SELESAI }

    public HistoryPresensiPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, UIMetrics.SPACE_16));
        setBackground(UIColors.NEUTRAL_50);
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_24));

        add(createHeader(), BorderLayout.NORTH);
        add(createTableCard(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Riwayat Presensi");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        header.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        controls.setOpaque(false);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(de);
        dateSpinner.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        dateSpinner.setPreferredSize(new Dimension(150, UIMetrics.INPUT_HEIGHT));
        dateSpinner.setFont(UIFonts.BODY);

        JButton btnHariIni = AppButton.secondary("Hari Ini");
        btnHariIni.addActionListener(e -> {
            dateSpinner.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            refresh();
        });

        JButton btnCari = AppButton.primary("Cari");
        btnCari.addActionListener(e -> refresh());

        controls.add(dateSpinner);
        controls.add(btnHariIni);
        controls.add(btnCari);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private AppCard createTableCard() {
        AppCard card = new AppCard();

        JPanel wrap = new JPanel(new BorderLayout(0, UIMetrics.SPACE_8));
        wrap.setOpaque(false);

        statusLabel = new JLabel("Pilih tanggal dan klik Cari");
        statusLabel.setFont(UIFonts.LABEL);
        statusLabel.setForeground(UIColors.NEUTRAL_600);
        wrap.add(statusLabel, BorderLayout.NORTH);

        String[] cols = {"No", "ID Karyawan", "Nama", "Jabatan", "Jam Masuk", "Jam Pulang", "Total Jam", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new JamPulangRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIColors.NEUTRAL_0);
        wrap.add(scrollPane, BorderLayout.CENTER);

        card.addBody(wrap);
        return card;
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
                    ? jamMasuk.format(DateTimeFormatter.ofPattern("HH:mm")) : "—";
            String jamPulangStr = jamPulang != null
                    ? jamPulang.format(DateTimeFormatter.ofPattern("HH:mm")) : "Belum pulang";
            String totalJam = "—";
            if (jamMasuk != null && jamPulang != null) {
                long diff = Duration.between(jamMasuk, jamPulang).toMinutes();
                totalJam = (diff / 60) + "j " + (diff % 60) + "m";
            }

            StatusPresensi status = (jamMasuk != null && jamPulang == null)
                    ? StatusPresensi.SEDANG_SHIFT : StatusPresensi.SELESAI;
            if (jamMasuk == null) continue;

            tableModel.addRow(new Object[]{
                    no++, r[1], r[2], r[3], jamMasukStr, jamPulangStr, totalJam, status
            });
        }
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));
        statusLabel.setText("Data presensi " + dateStr + " (" + tableModel.getRowCount() + " data)");

        if (tableModel.getRowCount() == 0) {
            tableModel.addRow(new Object[]{null, null, null, null, null, null, null, "Belum ada data presensi untuk tanggal ini"});
        }
    }

    // ============================================================
    // Renderers
    // ============================================================
    private static class StatusRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);

            if (v == null) return wrap;
            if (v instanceof String && ((String) v).startsWith("Belum")) {
                JLabel empty = new JLabel((String) v, SwingConstants.CENTER);
                empty.setFont(UIFonts.BODY);
                empty.setForeground(UIColors.NEUTRAL_400);
                empty.setBackground(UIColors.NEUTRAL_0);
                empty.setOpaque(true);
                return empty;
            }
            StatusPresensi s = (StatusPresensi) v;
            if (s == StatusPresensi.SEDANG_SHIFT) {
                wrap.add(new StatusBadge("Sedang Shift", StatusBadge.Tone.WARNING));
            } else {
                wrap.add(new StatusBadge("Selesai", StatusBadge.Tone.SUCCESS));
            }
            return wrap;
        }
    }

    private static class JamPulangRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (v != null && "Belum pulang".equals(v.toString())) {
                setForeground(UIColors.NEUTRAL_400);
                setFont(UIFonts.CAPTION);
            } else {
                setForeground(sel ? UIColors.NEUTRAL_800 : UIColors.NEUTRAL_800);
                setFont(UIFonts.BODY);
            }
            setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    // ============================================================
    // Table styling
    // ============================================================
    static void styleTable(JTable table) {
        table.setFont(UIFonts.BODY);
        table.setForeground(UIColors.NEUTRAL_800);
        table.setBackground(UIColors.NEUTRAL_0);
        table.setGridColor(UIColors.NEUTRAL_200);
        table.setSelectionBackground(UIColors.PRIMARY_50);
        table.setSelectionForeground(UIColors.NEUTRAL_800);
        table.setRowHeight(UIMetrics.TABLE_ROW_HEIGHT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(UIFonts.LABEL_BOLD);
        header.setForeground(UIColors.NEUTRAL_600);
        header.setBackground(UIColors.NEUTRAL_100);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIColors.NEUTRAL_200));
        header.setPreferredSize(new Dimension(header.getWidth(), UIMetrics.TABLE_HEADER_HEIGHT));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    c.setBackground(UIColors.NEUTRAL_0);
                    c.setForeground(UIColors.NEUTRAL_800);
                } else {
                    c.setBackground(UIColors.PRIMARY_50);
                }
                return c;
            }
        });
    }
}
