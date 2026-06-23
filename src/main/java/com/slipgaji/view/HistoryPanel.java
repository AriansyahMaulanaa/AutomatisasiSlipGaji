package com.slipgaji.view;

import com.slipgaji.controller.HistoryController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.model.Payslip;
import com.slipgaji.model.SendHistory;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final HistoryController historyController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    private List<SendHistory> currentHistory;

    public HistoryPanel() {
        this.historyController = new HistoryController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel pageTitle = new JLabel("Histori Pengiriman");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel controlCard = UIHelper.createCard("");
        controlCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        JLabel periodLabel = new JLabel("Filter Periode:");
        periodLabel.setFont(Constants.FONT_BODY);
        periodLabel.setForeground(Constants.TEXT_SECONDARY);
        periodCombo = new JComboBox<>();
        periodCombo.setFont(Constants.FONT_BODY);
        periodCombo.setPreferredSize(new Dimension(150, 36));
        periodCombo.addActionListener(e -> loadHistory());
        JButton refreshBtn = UIHelper.createStyledButton("Refresh", Constants.REFRESH_BTN);
        refreshBtn.addActionListener(e -> refresh());
        controlCard.add(periodLabel);
        controlCard.add(periodCombo);
        controlCard.add(refreshBtn);

        String[] columns = {"No", "Nama", "Email", "Periode", "Status", "Waktu", "Oleh", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        // Status column renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    if ("SUCCESS".equals(v)) {
                        setForeground(Constants.ACCENT);
                        setText("Terkirim");
                    } else {
                        setForeground(Constants.ACCENT_DANGER);
                        setText("Gagal");
                    }
                    setBackground(r % 2 == 0 ? Constants.BG_CARD : Constants.TABLE_ROW_ALT);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        // Aksi column renderer — "Detail" button look
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel label = new JLabel("Lihat Detail");
                label.setFont(Constants.FONT_SMALL);
                label.setForeground(Constants.PRIMARY);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.setOpaque(true);
                label.setBackground(sel ? new Color(219, 234, 254) :
                        (r % 2 == 0 ? Constants.BG_CARD : Constants.TABLE_ROW_ALT));
                return label;
            }
        });

        // Click handler for Detail column
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 7) { // Aksi column
                    showDetail(row);
                }
            }
        });

        // Set Aksi column width
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setMaxWidth(100);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Constants.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));

        JPanel tableCard = UIHelper.createCard("Log Pengiriman Email");
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        top.add(controlCard, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(top, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private void showDetail(int row) {
        if (currentHistory == null || row >= currentHistory.size()) return;
        SendHistory history = currentHistory.get(row);

        // Get associated payslip for PDF access
        Payslip payslip = DatabaseService.getInstance().getPayslipById(history.getPayslipId());

        HistoryDetailDialog dialog = new HistoryDetailDialog(
                SwingUtilities.getWindowAncestor(this), history, payslip);
        dialog.setVisible(true);
    }

    public void refresh() {
        PayslipController pc = new PayslipController();
        List<String> periods = pc.getPeriods();
        periodCombo.removeAllItems();
        periodCombo.addItem("-- Semua --");
        for (String p : periods) periodCombo.addItem(p);
        loadHistory();
    }

    private void loadHistory() {
        String sel = (String) periodCombo.getSelectedItem();
        String period = (sel != null && !sel.startsWith("--")) ? sel : null;
        currentHistory = historyController.getHistory(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (SendHistory h : currentHistory) {
            tableModel.addRow(new Object[]{
                no++, h.getEmployeeName(), h.getEmployeeEmail(), h.getPeriod(),
                h.getStatus(), h.getSentAt() != null ? h.getSentAt() : "-",
                h.getSentBy() != null ? h.getSentBy() : "-",
                "Detail"
            });
        }
    }
}
