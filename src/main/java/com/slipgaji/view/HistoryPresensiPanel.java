package com.slipgaji.view;

import com.slipgaji.controller.PresensiController;
import com.slipgaji.dao.PresensiDAO;
import com.slipgaji.model.Presensi;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HistoryPresensiPanel extends JPanel {

    private final PresensiDAO presensiDAO = new PresensiDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private javax.swing.JSpinner dateSpinner;
    private JToggleButton viewMode;

    public HistoryPresensiPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

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

        JButton btnCari = UIHelper.createStyledButton("Cari", Constants.PRIMARY);
        btnCari.addActionListener(e -> refresh());

        JButton btnHariIni = UIHelper.createStyledButton("Hari Ini", Constants.REFRESH_BTN);
        btnHariIni.addActionListener(e -> {
            dateSpinner.setValue(java.util.Date.from(java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            refresh();
        });

        controls.add(dateSpinner);
        controls.add(btnHariIni);
        controls.add(btnCari);
        header.add(controls, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"No", "Waktu", "ID Karyawan", "Nama", "Jabatan", "Jenis", "Tanggal"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);

        statusLabel = new JLabel("Pilih tanggal dan klik Cari");
        statusLabel.setForeground(Constants.TEXT_SECONDARY);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(statusLabel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        java.util.Date selected = (java.util.Date) dateSpinner.getValue();
        LocalDate date = selected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        List<Presensi> list = presensiDAO.getHistoryByDate(date);
        tableModel.setRowCount(0);
        int no = 1;
        for (Presensi p : list) {
            tableModel.addRow(new Object[]{
                    no++,
                    p.getJam() != null ? p.getJam().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "-",
                    p.getEmployeeIdCode(),
                    p.getEmployeeName(),
                    p.getEmployeePosition(),
                    p.getJenisPresensi(),
                    p.getTanggal() != null ? p.getTanggal().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-"
            });
        }
        statusLabel.setText("Data presensi " +
                date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")))
                + " (" + list.size() + " data)");
    }
}
