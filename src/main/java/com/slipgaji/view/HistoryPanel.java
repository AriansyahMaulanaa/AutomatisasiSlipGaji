package com.slipgaji.view;

import com.slipgaji.controller.HistoryController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.model.Payslip;
import com.slipgaji.model.SendHistory;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.EmptyState;
import com.slipgaji.ui.components.StatusBadge;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * HistoryPanel — log pengiriman email slip gaji.
 *
 * <p>Redesign: filter periode + tombol Refresh Secondary, badge status pakai
 * {@link StatusBadge}, empty state jelas ketika tidak ada log.
 */
public class HistoryPanel extends JPanel {
    private final HistoryController historyController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    private List<SendHistory> currentHistory;
    private CardLayout centerLayout;
    private JPanel centerContainer;
    private AppCard tableCard;
    private JPanel emptyCard;

    public HistoryPanel() {
        this.historyController = new HistoryController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, UIMetrics.SPACE_16));
        setBackground(UIColors.NEUTRAL_50);
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel pageTitle = new JLabel("Histori Pengiriman");
        pageTitle.setFont(UIFonts.H1);
        pageTitle.setForeground(UIColors.NEUTRAL_800);
        header.add(pageTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Filter card
        AppCard filterCard = new AppCard();
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, UIMetrics.SPACE_12, 0));
        filterRow.setOpaque(false);

        JLabel periodLabel = new JLabel("Filter Periode:");
        periodLabel.setFont(UIFonts.LABEL);
        periodLabel.setForeground(UIColors.NEUTRAL_600);

        periodCombo = new JComboBox<>();
        periodCombo.setFont(UIFonts.BODY);
        periodCombo.setBackground(UIColors.NEUTRAL_0);
        periodCombo.setPreferredSize(new Dimension(180, UIMetrics.INPUT_HEIGHT));
        periodCombo.addActionListener(e -> loadHistory());

        JButton refreshBtn = AppButton.secondary("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        filterRow.add(periodLabel);
        filterRow.add(periodCombo);
        filterRow.add(refreshBtn);
        filterCard.addBody(filterRow);

        // Center: table card OR empty state (via CardLayout)
        tableCard = createTableCard();
        emptyCard = createEmptyCard();

        centerLayout = new CardLayout();
        centerContainer = new JPanel(centerLayout);
        centerContainer.setOpaque(false);
        centerContainer.add(tableCard, "table");
        centerContainer.add(emptyCard, "empty");

        JPanel body = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        body.setOpaque(false);
        body.add(filterCard, BorderLayout.NORTH);
        body.add(centerContainer, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private AppCard createTableCard() {
        AppCard card = new AppCard("Log Pengiriman Email");

        String[] columns = {"No", "Nama", "Email", "Periode", "Status", "Waktu", "Oleh", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        HistoryPresensiPanel.styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new LinkRenderer("Lihat Detail"));
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setMaxWidth(130);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 7) showDetail(row);
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UIColors.NEUTRAL_0);
        sp.setBorder(BorderFactory.createEmptyBorder());

        card.addBody(sp);
        return card;
    }

    private JPanel createEmptyCard() {
        AppCard card = new AppCard();
        card.addBody(new EmptyState(EmptyState.Icon.INBOX,
                "Belum ada log pengiriman",
                "Log akan muncul di sini setelah Anda mengirim slip gaji via email"));
        return card;
    }

    private void showDetail(int row) {
        if (currentHistory == null || row >= currentHistory.size()) return;
        SendHistory history = currentHistory.get(row);
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
                h.getStatus(), h.getSentAt() != null ? h.getSentAt() : "—",
                h.getSentBy() != null ? h.getSentBy() : "—",
                "Detail"
            });
        }

        centerLayout.show(centerContainer, currentHistory.isEmpty() ? "empty" : "table");
    }

    // ============================================================
    // Renderers
    // ============================================================
    private static class StatusCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);
            if (v == null) return wrap;
            if ("SUCCESS".equals(v)) {
                wrap.add(new StatusBadge("Terkirim", StatusBadge.Tone.SUCCESS));
            } else {
                wrap.add(new StatusBadge("Gagal", StatusBadge.Tone.DANGER));
            }
            return wrap;
        }
    }

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
}
