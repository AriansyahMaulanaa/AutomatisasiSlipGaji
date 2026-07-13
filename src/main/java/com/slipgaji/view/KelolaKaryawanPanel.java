package com.slipgaji.view;

import com.slipgaji.controller.KaryawanController;
import com.slipgaji.model.Employee;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.StatusBadge;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * KelolaKaryawanPanel — tabel karyawan + tambah/edit/hapus.
 *
 * <p>Redesign:
 * <ul>
 *   <li>Tombol "+ Tambah Karyawan" (hijau) → Primary blue.</li>
 *   <li>Tombol "Refresh" → Secondary outline.</li>
 *   <li>Kolom "Tipe" jadi badge Neutral, "Status" jadi badge Success.</li>
 *   <li>Tabel style muted, header bg NEUTRAL_100 letter-spacing.</li>
 * </ul>
 */
public class KelolaKaryawanPanel extends JPanel {

    private final KaryawanController controller = new KaryawanController();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;

    public KelolaKaryawanPanel() {
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

        JLabel title = new JLabel("Kelola Karyawan");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        actions.setOpaque(false);
        JButton btnRefresh = AppButton.secondary("Refresh");
        btnRefresh.addActionListener(e -> refresh());
        JButton btnTambah = AppButton.primary("+ Tambah Karyawan");
        btnTambah.addActionListener(e -> tambahKaryawan());
        actions.add(btnRefresh);
        actions.add(btnTambah);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private AppCard createTableCard() {
        AppCard card = new AppCard();

        JPanel wrap = new JPanel(new BorderLayout(0, UIMetrics.SPACE_8));
        wrap.setOpaque(false);

        statusLabel = new JLabel("Memuat data...");
        statusLabel.setFont(UIFonts.LABEL);
        statusLabel.setForeground(UIColors.NEUTRAL_600);
        wrap.add(statusLabel, BorderLayout.NORTH);

        String[] cols = {"No", "ID", "Nama", "Jabatan", "Status", "Barcode", "Tipe", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        KaryawanTableStyler.style(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new BadgeRenderer(true));
        table.getColumnModel().getColumn(6).setCellRenderer(new BadgeRenderer(false));
        table.getColumnModel().getColumn(7).setCellRenderer(new LinkRenderer("Klik Kanan"));

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
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIColors.NEUTRAL_0);
        wrap.add(scrollPane, BorderLayout.CENTER);

        card.addBody(wrap);
        return card;
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

    // ============================================================
    // Renderers
    // ============================================================
    /** Renderer badge — untuk kolom Status (Aktif=Success) atau Tipe (Neutral). */
    private static class BadgeRenderer implements TableCellRenderer {
        private final boolean isStatus;
        BadgeRenderer(boolean isStatus) { this.isStatus = isStatus; }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);
            if (v == null) return wrap;
            String text = v.toString();
            StatusBadge.Tone tone;
            if (isStatus) {
                tone = "Aktif".equalsIgnoreCase(text) ? StatusBadge.Tone.SUCCESS : StatusBadge.Tone.NEUTRAL;
            } else {
                tone = StatusBadge.Tone.NEUTRAL;
            }
            wrap.add(new StatusBadge(text, tone));
            return wrap;
        }
    }

    /** Renderer untuk kolom Aksi — text link-style. */
    private static class LinkRenderer extends DefaultTableCellRenderer {
        private final String text;
        LinkRenderer(String text) { this.text = text; }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, text, sel, foc, row, col);
            c.setHorizontalAlignment(SwingConstants.CENTER);
            c.setFont(UIFonts.LABEL_BOLD);
            c.setForeground(UIColors.PRIMARY_600);
            c.setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);
            c.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return c;
        }
    }

    // ============================================================
    // Shared table styler
    // ============================================================
    static class KaryawanTableStyler {
        static void style(JTable table) {
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

            // Default renderer — text left-aligned padding, alt row same white
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBorder(new EmptyBorder(0, 12, 0, 12));
                    if (!isSelected) {
                        c.setBackground(UIColors.NEUTRAL_0);
                        c.setForeground(UIColors.NEUTRAL_800);
                    } else {
                        c.setBackground(UIColors.PRIMARY_50);
                        c.setForeground(UIColors.NEUTRAL_800);
                    }
                    return c;
                }
            });
        }
    }
}
