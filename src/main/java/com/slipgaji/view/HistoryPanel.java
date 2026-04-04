package com.slipgaji.view;

import com.slipgaji.controller.HistoryController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.model.SendHistory;
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

    public HistoryPanel() {
        this.historyController = new HistoryController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel pageTitle = new JLabel("📋 Histori Pengiriman");
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
        periodCombo.setPreferredSize(new Dimension(150, 34));
        periodCombo.addActionListener(e -> loadHistory());
        JButton refreshBtn = UIHelper.createStyledButton("🔄 Refresh", Constants.BG_SURFACE);
        refreshBtn.addActionListener(e -> refresh());
        controlCard.add(periodLabel);
        controlCard.add(periodCombo);
        controlCard.add(refreshBtn);

        String[] columns = {"No", "Nama", "Email", "Periode", "Status", "Waktu", "Oleh", "Error"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    if ("SUCCESS".equals(v)) { setForeground(Constants.ACCENT); setText("✅ OK"); }
                    else { setForeground(Constants.ACCENT_DANGER); setText("❌ FAIL"); }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Constants.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));

        JPanel tableCard = UIHelper.createCard("📧 Log Pengiriman Email");
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Constants.BG_DARK);
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        top.add(controlCard, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Constants.BG_DARK);
        center.add(top, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
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
        List<SendHistory> list = historyController.getHistory(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (SendHistory h : list) {
            tableModel.addRow(new Object[]{
                no++, h.getEmployeeName(), h.getEmployeeEmail(), h.getPeriod(),
                h.getStatus(), h.getSentAt() != null ? h.getSentAt() : "-",
                h.getSentBy() != null ? h.getSentBy() : "-",
                h.getErrorMessage() != null ? h.getErrorMessage() : "-"
            });
        }
    }
}
