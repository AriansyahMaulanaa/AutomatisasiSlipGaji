package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.controller.EmailController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.controller.PresensiToPayslipController;
import com.slipgaji.controller.PresensiToPayslipController.GenerateResult;
import com.slipgaji.model.Payslip;
import com.slipgaji.model.PeriodSummary;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.EmptyState;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * PayslipPanel — daftar slip gaji per periode.
 *
 * <p>Redesign: hierarki tombol jelas — <b>Generate dari Presensi</b> = Primary
 * (1 aksi utama), Generate Semua & Kirim Mode Batch = Secondary, Hapus = Danger outline.
 * Period card selektor pakai border tebal PRIMARY_500 saat aktif.
 */
public class PayslipPanel extends JPanel {
    private final PayslipController payslipController;
    private final EmailController emailController;

    private String currentPeriod = null;
    private List<Payslip> currentPayslips;

    private JPanel cardsWrapper;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private Payslip popupSelectedPayslip;

    public PayslipPanel() {
        this.payslipController = new PayslipController();
        this.emailController = new EmailController();
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
        JLabel pageTitle = new JLabel("Daftar Slip Gaji");
        pageTitle.setFont(UIFonts.H1);
        pageTitle.setForeground(UIColors.NEUTRAL_800);
        header.add(pageTitle, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        headerActions.setOpaque(false);
        JButton btnRefresh = AppButton.secondary("Refresh");
        btnRefresh.addActionListener(e -> refresh());
        headerActions.add(btnRefresh);
        header.add(headerActions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: period cards + table
        JPanel center = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        center.setOpaque(false);

        cardsWrapper = new JPanel();
        cardsWrapper.setOpaque(false);
        center.add(cardsWrapper, BorderLayout.NORTH);

        center.add(createTableCard(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private AppCard createTableCard() {
        AppCard card = new AppCard();

        JPanel wrap = new JPanel(new BorderLayout(0, UIMetrics.SPACE_8));
        wrap.setOpaque(false);

        // Top row: status + action buttons
        JPanel tblTop = new JPanel(new BorderLayout());
        tblTop.setOpaque(false);

        statusLabel = new JLabel("Silahkan pilih periode");
        statusLabel.setFont(UIFonts.LABEL);
        statusLabel.setForeground(UIColors.NEUTRAL_600);
        tblTop.add(statusLabel, BorderLayout.WEST);

        JPanel tblOptions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        tblOptions.setOpaque(false);

        // Hierarki: Hapus (Danger), Kirim Batch (Secondary), Generate Semua (Secondary), Generate dari Presensi (Primary)
        JButton btnDelete = AppButton.danger("Hapus");
        btnDelete.addActionListener(e -> deleteSelected());
        tblOptions.add(btnDelete);

        JButton btnSend = AppButton.secondary("Kirim Mode Batch");
        btnSend.addActionListener(e -> sendAllEmails());
        tblOptions.add(btnSend);

        JButton btnGen = AppButton.secondary("Generate Semua");
        btnGen.addActionListener(e -> generateAllPdfs());
        tblOptions.add(btnGen);

        if (AuthController.isManager()) {
            JButton btnGenPresensi = AppButton.primary("Generate dari Presensi");
            btnGenPresensi.addActionListener(e -> generateFromPresensi());
            tblOptions.add(btnGenPresensi);
        }

        tblTop.add(tblOptions, BorderLayout.EAST);
        wrap.add(tblTop, BorderLayout.NORTH);

        // Table
        String[] cols = {"No", "ID", "Nama", "Jabatan", "Gaji", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        HistoryPresensiPanel.styleTable(table); // reuse table styler
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusColRenderer());

        // Right-click popup menu
        JPopupMenu popup = new JPopupMenu();
        addPopupItem(popup, "Lihat Slip (PDF)", () -> { popupSelectedPayslip = getSelectedPayslip(); previewSelected(); });
        addPopupItem(popup, "Generate Slip",    () -> { popupSelectedPayslip = getSelectedPayslip(); generateSelectedPdf(); });
        popup.addSeparator();
        addPopupItem(popup, "Kirim Email",      () -> { popupSelectedPayslip = getSelectedPayslip(); sendSelected(); });
        addPopupItem(popup, "Edit Data",        () -> { popupSelectedPayslip = getSelectedPayslip(); editSelected(); });
        popup.addSeparator();
        addPopupItem(popup, "Hapus Data",       () -> { popupSelectedPayslip = getSelectedPayslip(); deleteSelected(); });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) previewSelected(); }
            @Override public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) handlePopup(e); }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) handlePopup(e); }
            private void handlePopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) table.setRowSelectionInterval(row, row);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIColors.NEUTRAL_0);
        wrap.add(scrollPane, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        wrap.add(progressBar, BorderLayout.SOUTH);

        card.addBody(wrap);
        return card;
    }

    private void addPopupItem(JPopupMenu popup, String text, Runnable action) {
        JMenuItem it = new JMenuItem(text);
        it.addActionListener(e -> action.run());
        popup.add(it);
    }

    // ============================================================
    // Period card
    // ============================================================
    public void refresh() {
        List<PeriodSummary> summaries = DatabaseService.getInstance().getPayslipPeriodSummaries();
        cardsWrapper.removeAll();

        if (summaries.isEmpty()) {
            cardsWrapper.setLayout(new BorderLayout());
            cardsWrapper.setPreferredSize(new Dimension(0, 180));
            AppCard emptyCard = new AppCard();
            emptyCard.addBody(new EmptyState(EmptyState.Icon.DOCUMENT,
                    "Belum ada slip gaji untuk periode manapun",
                    "Generate dari data presensi untuk memulai"));
            cardsWrapper.add(emptyCard, BorderLayout.CENTER);
        } else {
            int cardCount = summaries.size();
            cardsWrapper.setLayout(new GridLayout(1, cardCount, UIMetrics.SPACE_12, 0));
            cardsWrapper.setPreferredSize(new Dimension(0, 110));
            for (PeriodSummary s : summaries) cardsWrapper.add(createPeriodCard(s));
        }

        cardsWrapper.revalidate();
        cardsWrapper.repaint();

        if (currentPeriod == null && !summaries.isEmpty()) {
            currentPeriod = summaries.get(0).getPeriod();
        }
        if (currentPeriod != null) {
            loadPayslips(currentPeriod);
        } else {
            tableModel.setRowCount(0);
            statusLabel.setText("Belum ada data slip untuk ditampilkan.");
        }
    }

    private JPanel createPeriodCard(PeriodSummary summary) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        currentPeriod = summary.getPeriod();
                        loadPayslips(summary.getPeriod());
                        cardsWrapper.repaint();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = UIMetrics.RADIUS_CARD * 2;
                boolean active = currentPeriod != null && currentPeriod.equals(summary.getPeriod());
                Color bg = active ? UIColors.PRIMARY_50 : (hovered ? UIColors.NEUTRAL_50 : UIColors.NEUTRAL_0);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));

                g2.setColor(active ? UIColors.PRIMARY_500 : UIColors.NEUTRAL_200);
                g2.setStroke(new BasicStroke(active ? 2f : 1f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, r, r));
                g2.dispose();
            }
        };
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(UIMetrics.SPACE_16, UIMetrics.SPACE_16,
                                        UIMetrics.SPACE_16, UIMetrics.SPACE_16));

        JLabel title = new JLabel(summary.getFormattedPeriod());
        title.setFont(UIFonts.H3);
        title.setForeground(UIColors.NEUTRAL_800);
        card.add(title, BorderLayout.NORTH);

        int pending = summary.getSlipCount() - summary.getEmailSentCount();
        StatusBadge.Tone tone;
        String statusText;
        if (pending == 0 && summary.getSlipCount() > 0) {
            tone = StatusBadge.Tone.SUCCESS; statusText = "Terkirim";
        } else if (pending > 0 && summary.getPdfGeneratedCount() >= summary.getSlipCount()) {
            tone = StatusBadge.Tone.WARNING; statusText = "Generated";
        } else {
            tone = StatusBadge.Tone.NEUTRAL; statusText = "Draft";
        }

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(UIMetrics.SPACE_8, 0, 0, 0));

        JLabel meta = new JLabel(summary.getSlipCount() + " slip  •  " + UIHelper.formatCurrency(summary.getTotalSalary()));
        meta.setFont(UIFonts.CAPTION);
        meta.setForeground(UIColors.NEUTRAL_600);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeRow.add(new StatusBadge(statusText, tone));

        body.add(meta);
        body.add(Box.createVerticalStrut(6));
        body.add(badgeRow);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void loadPayslips(String period) {
        currentPayslips = payslipController.getPayslips(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (Payslip p : currentPayslips) {
            String status = (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "Generated" : "Draft";
            tableModel.addRow(new Object[]{ no++, p.getId(), p.getEmployeeName(), p.getPosition(),
                                           UIHelper.formatCurrency(p.getNetSalary()), status });
        }
        statusLabel.setText("Data Slip: " + period + " (" + currentPayslips.size() + " data)");
    }

    // ============================================================
    // Actions
    // ============================================================
    private Payslip getSelectedPayslip() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Pilih slip gaji terlebih dahulu.");
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 1);
        return payslipController.getPayslipById(id);
    }

    private void previewSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;
        try {
            if (payslip.getPdfPath() == null || payslip.getPdfPath().isEmpty()) {
                payslipController.generatePdf(payslip);
            }
            if (payslip.getPdfPath() != null) {
                PayslipPreviewDialog dialog = new PayslipPreviewDialog(SwingUtilities.getWindowAncestor(this), payslip);
                dialog.setVisible(true);
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Gagal generate PDF: " + ex.getMessage());
        }
    }

    private void generateSelectedPdf() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;
        try {
            payslipController.generatePdf(payslip);
            UIHelper.showSuccess(this, "PDF berhasil digenerate");
            loadPayslips(currentPeriod);
        } catch (Exception ex) {
            UIHelper.showError(this, "Gagal generate PDF: " + ex.getMessage());
        }
    }

    private void editSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;
        EditPayslipDialog dialog = new EditPayslipDialog(SwingUtilities.getWindowAncestor(this), payslip);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadPayslips(currentPeriod);
    }

    private void deleteSelected() {
        Payslip payslip = popupSelectedPayslip;
        if (payslip == null) payslip = getSelectedPayslip();
        if (payslip == null) return;
        if (UIHelper.showConfirm(this, "Hapus data slip gaji untuk\n" + payslip.getEmployeeName() + "?")) {
            if (payslipController.deletePayslip(payslip.getId())) {
                popupSelectedPayslip = null;
                refresh();
                UIHelper.showSuccess(this, "Data slip gaji berhasil dihapus");
            } else {
                UIHelper.showError(this, "Gagal menghapus data slip gaji");
            }
        }
    }

    private void sendSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;
        if (!UIHelper.showConfirm(this, "Kirim slip gaji ke " + payslip.getEmployeeEmail() + "?")) return;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() throws Exception {
                emailController.sendSingle(payslip);
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    UIHelper.showSuccess(PayslipPanel.this, "Email berhasil dikirim");
                    loadPayslips(currentPeriod);
                } catch (Exception ex) {
                    UIHelper.showError(PayslipPanel.this, "Gagal mengirim email:\n" + ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private void generateAllPdfs() {
        if (currentPayslips == null || currentPayslips.isEmpty()) {
            UIHelper.showError(this, "Tidak ada slip gaji.");
            return;
        }
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setMaximum(currentPayslips.size());
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() throws Exception {
                payslipController.generateAllPdfs(currentPayslips, (current, total, name) -> {
                    publish(current);
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Generating PDF: " + name + " (" + current + "/" + total + ")"));
                });
                return null;
            }
            @Override protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            @Override protected void done() {
                try {
                    get();
                    UIHelper.showSuccess(PayslipPanel.this, "Semua PDF berhasil digenerate!");
                    loadPayslips(currentPeriod);
                } catch (Exception ex) {
                    UIHelper.showError(PayslipPanel.this, "Error: " + ex.getMessage());
                }
                progressBar.setVisible(false);
                statusLabel.setText("Selesai.");
            }
        };
        worker.execute();
    }

    private void sendAllEmails() {
        if (currentPayslips == null || currentPayslips.isEmpty()) {
            UIHelper.showError(this, "Tidak ada slip gaji.");
            return;
        }
        if (!UIHelper.showConfirm(this, "Kirim " + currentPayslips.size()
                + " slip gaji ke email masing-masing karyawan?\n\nSetiap email hanya berisi slip masing-masing karyawan."))
            return;
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setMaximum(currentPayslips.size());

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                emailController.sendBatch(currentPayslips, new EmailController.BatchCallback() {
                    @Override public void onProgress(int current, int total, String name, String status, String error) {
                        publish(current);
                        SwingUtilities.invokeLater(() -> {
                            String icon = status.equals("SUCCESS") ? "OK" : "FAIL";
                            statusLabel.setText(icon + " " + name + " (" + current + "/" + total + ")");
                        });
                    }
                    @Override public void onComplete(int success, int failed, java.util.List<String> failedNames,
                                                     java.util.List<String> failedErrors) {
                        SwingUtilities.invokeLater(() -> {
                            String msg = "Pengiriman selesai!\nBerhasil: " + success + "\nGagal: " + failed;
                            if (failed > 0) {
                                StringBuilder detail = new StringBuilder("\n\nDaftar gagal:\n");
                                for (int i = 0; i < failedNames.size(); i++) {
                                    detail.append("- ").append(failedNames.get(i))
                                          .append(": ").append(failedErrors.get(i)).append("\n");
                                }
                                UIHelper.showError(PayslipPanel.this, msg + detail);
                            } else {
                                UIHelper.showSuccess(PayslipPanel.this, msg);
                            }
                        });
                    }
                });
                return null;
            }
            @Override protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            @Override protected void done() {
                progressBar.setVisible(false);
                statusLabel.setText("Pengiriman selesai.");
                loadPayslips(currentPeriod);
            }
        };
        worker.execute();
    }

    private void generateFromPresensi() {
        String period = new PeriodePickerDialog(SwingUtilities.getWindowAncestor(this)).showDialog();
        if (period == null) return;

        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Memproses data presensi ke slip gaji...");

        SwingWorker<GenerateResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GenerateResult doInBackground() {
                PresensiToPayslipController controller = new PresensiToPayslipController();
                return controller.generatePayslipsFromPresensi(period);
            }
            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                try {
                    GenerateResult result = get();
                    StringBuilder msg = new StringBuilder();
                    msg.append("Selesai! Berhasil: ").append(result.getSuccessCount())
                       .append(", Skip: ").append(result.getSkipCount()).append("\n\n");
                    for (String m : result.getMessages()) msg.append(m).append("\n");
                    currentPeriod = period;
                    refresh();
                    UIHelper.showSuccess(PayslipPanel.this, msg.toString());
                } catch (Exception ex) {
                    UIHelper.showError(PayslipPanel.this, "Error: " + ex.getMessage());
                }
                statusLabel.setText("Selesai.");
            }
        };
        worker.execute();
    }

    /** Renderer kolom Status untuk tabel slip gaji. */
    private static class StatusColRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(sel ? UIColors.PRIMARY_50 : UIColors.NEUTRAL_0);
            if (v == null) return wrap;
            String s = v.toString();
            StatusBadge.Tone tone = switch (s) {
                case "Draft" -> StatusBadge.Tone.NEUTRAL;
                case "Generated" -> StatusBadge.Tone.WARNING;
                default -> StatusBadge.Tone.SUCCESS;
            };
            wrap.add(new StatusBadge(s, tone));
            return wrap;
        }
    }
}
