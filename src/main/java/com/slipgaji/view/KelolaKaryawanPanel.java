package com.slipgaji.view;

import com.slipgaji.controller.KaryawanController;
import com.slipgaji.model.Employee;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KelolaKaryawanPanel extends JPanel {

    private final KaryawanController controller = new KaryawanController();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;

    public KelolaKaryawanPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Kelola Karyawan");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnTambah = UIHelper.createStyledButton("+ Tambah Karyawan", Constants.ACCENT);
        btnTambah.addActionListener(e -> tambahKaryawan());
        JButton btnRefresh = UIHelper.createStyledButton("Refresh", Constants.REFRESH_BTN);
        btnRefresh.addActionListener(e -> refresh());
        actions.add(btnRefresh);
        actions.add(btnTambah);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"No", "ID", "Nama", "Jabatan", "Status", "Barcode", "Tipe", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> editSelected());
        JMenuItem hapusItem = new JMenuItem("Hapus");
        hapusItem.addActionListener(e -> hapusSelected());
        popup.add(editItem);
        popup.add(hapusItem);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            private void showPopup(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);

        statusLabel = new JLabel("Memuat data...");
        statusLabel.setForeground(Constants.TEXT_SECONDARY);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(statusLabel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        List<Employee> list = controller.getAllKaryawan();
        tableModel.setRowCount(0);
        int no = 1;
        for (Employee emp : list) {
            tableModel.addRow(new Object[]{
                    no++, emp.getId(), emp.getName(), emp.getPosition(),
                    emp.getStatus(), emp.getBarcode(),
                    emp.getEmploymentType(), "Klik Kanan"
            });
        }
        statusLabel.setText("Total: " + list.size() + " karyawan");
    }

    private void tambahKaryawan() {
        Window win = SwingUtilities.getWindowAncestor(this);
        TambahKaryawanDialog dialog = new TambahKaryawanDialog(win);
        dialog.setVisible(true);
        if (dialog.isSaved()) refresh();
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Pilih karyawan terlebih dahulu."); return; }
        int id = (int) tableModel.getValueAt(row, 1);
        Employee emp = controller.getById(id);
        if (emp == null) { UIHelper.showError(this, "Data karyawan tidak ditemukan."); return; }
        Window win = SwingUtilities.getWindowAncestor(this);
        TambahKaryawanDialog dialog = new TambahKaryawanDialog(win, emp);
        dialog.setVisible(true);
        if (dialog.isSaved()) refresh();
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Pilih karyawan terlebih dahulu."); return; }
        int id = (int) tableModel.getValueAt(row, 1);
        String nama = (String) tableModel.getValueAt(row, 2);
        if (UIHelper.showConfirm(this, "Hapus karyawan " + nama + "?")) {
            controller.deleteKaryawan(id);
            refresh();
        }
    }
}
