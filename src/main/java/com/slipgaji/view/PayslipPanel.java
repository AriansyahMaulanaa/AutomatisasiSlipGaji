package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.controller.EmailController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.controller.PresensiToPayslipController;
import com.slipgaji.controller.PresensiToPayslipController.GenerateResult;
import com.slipgaji.model.Payslip;
import com.slipgaji.model.PeriodSummary;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

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
    
    // Menyimpan payslip yang terpilih via popup menu agar tidak bergantung pada table selection
    private Payslip popupSelectedPayslip;

    public PayslipPanel() {
        this.payslipController = new PayslipController();
        this.emailController = new EmailController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel pageTitle = new JLabel("Daftar Slip Gaji");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        JButton btnRefresh = UIHelper.createStyledButton("Refresh", Constants.REFRESH_BTN);
        btnRefresh.addActionListener(e -> refresh());
        actions.add(btnRefresh);
        headerPanel.add(actions, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 24));
        centerPanel.setOpaque(false);

        cardsWrapper = new JPanel();
        cardsWrapper.setOpaque(false);
        centerPanel.add(cardsWrapper, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);
        
        JPanel tblOptions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tblOptions.setOpaque(false);
        JButton btnGenPresensi = UIHelper.createStyledButton("Generate dari Presensi", new Color(79, 70, 229));
        btnGenPresensi.addActionListener(e -> generateFromPresensi());
        JButton btnGen = UIHelper.createStyledButton("Generate Semua", Constants.PRIMARY);
        btnGen.addActionListener(e -> generateAllPdfs());
        JButton btnSend = UIHelper.createStyledButton("Kirim Mode Batch", Constants.ACCENT_WARN);
        btnSend.addActionListener(e -> sendAllEmails());
        JButton btnDelete = UIHelper.createStyledButton("Hapus", Constants.ACCENT_DANGER);
        btnDelete.addActionListener(e -> deleteSelected());
        tblOptions.add(btnDelete);
        if (AuthController.isManager()) {
            tblOptions.add(btnGenPresensi);
        }
        tblOptions.add(btnGen);
        tblOptions.add(btnSend);
        
        statusLabel = new JLabel("Silahkan pilih periode");
        statusLabel.setForeground(Constants.TEXT_SECONDARY);
        
        JPanel tblTop = new JPanel(new BorderLayout());
        tblTop.setOpaque(false);
        tblTop.add(statusLabel, BorderLayout.WEST);
        tblTop.add(tblOptions, BorderLayout.EAST);
        tablePanel.add(tblTop, BorderLayout.NORTH);

        String[] cols = {"No", "ID", "Nama", "Jabatan", "Gaji", "Status", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem previewItem = new JMenuItem("Lihat Slip (PDF)");
        previewItem.addActionListener(e -> { popupSelectedPayslip = getSelectedPayslip(); previewSelected(); });
        JMenuItem genItem = new JMenuItem("Generate Slip");
        genItem.addActionListener(e -> { popupSelectedPayslip = getSelectedPayslip(); generateSelectedPdf(); });
        JMenuItem sendItem = new JMenuItem("Kirim Email");
        sendItem.addActionListener(e -> { popupSelectedPayslip = getSelectedPayslip(); sendSelected(); });
        JMenuItem editItem = new JMenuItem("Edit Data");
        editItem.addActionListener(e -> { popupSelectedPayslip = getSelectedPayslip(); editSelected(); });
        JMenuItem deleteItem = new JMenuItem("Hapus Data");
        deleteItem.addActionListener(e -> { popupSelectedPayslip = getSelectedPayslip(); deleteSelected(); });
        popup.add(previewItem); popup.add(genItem); popup.addSeparator(); popup.add(sendItem); popup.add(editItem); popup.addSeparator(); popup.add(deleteItem);

        // Fix: pastikan baris yang diklik kanan terpilih sebelum popup muncul
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) previewSelected();
            }
            @Override public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) handlePopup(e);
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) handlePopup(e);
            }
            private void handlePopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        tablePanel.add(progressBar, BorderLayout.SOUTH);

        centerPanel.add(tablePanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        List<PeriodSummary> summaries = DatabaseService.getInstance().getPayslipPeriodSummaries();
        
        cardsWrapper.removeAll();
        
        int cardCount = Math.max(summaries.size(), 1);
        cardsWrapper.setLayout(new GridLayout(1, cardCount, 16, 0));
        cardsWrapper.setPreferredSize(new Dimension(0, 100));
        
        if (summaries.isEmpty()) {
            cardsWrapper.add(createEmptyState());
        } else {
            for (PeriodSummary s : summaries) {
                cardsWrapper.add(createPeriodCard(s));
            }
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
                    @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { 
                        currentPeriod = summary.getPeriod();
                        loadPayslips(summary.getPeriod()); 
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = Constants.CARD_RADIUS * 2;
                g2.setColor(hovered ? new Color(243, 244, 246) : Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));
                
                boolean isActive = currentPeriod != null && currentPeriod.equals(summary.getPeriod());
                g2.setColor(isActive ? Constants.PRIMARY : Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(isActive ? 2f : 1f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, r, r));
                g2.dispose();
            }
        };
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel(summary.getPeriod());
        title.setFont(Constants.FONT_HEADING);
        title.setForeground(Constants.TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        int pending = summary.getSlipCount() - summary.getEmailSentCount();
        String body = "<html><span style='color:#6B7280;font-size:11px'>" + summary.getSlipCount() + " Slip Karyawan<br>" 
                      + UIHelper.formatCurrency(summary.getTotalSalary()) + "<br>"
                      + (pending == 0 && summary.getSlipCount() > 0 ? "<span style='color:#10B981'>Terkirim</span>" : "<span style='color:#F59E0B'>Pending: " + pending + "</span>")
                      + "</span></html>";
        JLabel detail = new JLabel(body);
        card.add(detail, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEmptyState() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = Constants.CARD_RADIUS * 2;
                g2.setColor(new Color(248, 249, 251));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6f}, 0.0f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, r, r));
                g2.dispose();
            }
        };
        card.setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("<html><center><span style='color:#9CA3AF;font-size:11px'>Belum ada data ekstra</span></center></html>");
        card.add(lbl);
        return card;
    }

    private void loadPayslips(String period) {
        currentPayslips = payslipController.getPayslips(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (Payslip p : currentPayslips) {
            String status = (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "Generated" : "Draft";
            tableModel.addRow(new Object[]{ no++, p.getId(), p.getEmployeeName(), p.getPosition(), UIHelper.formatCurrency(p.getNetSalary()), status, "Klik Kanan" });
        }
        statusLabel.setText("Data Slip: " + period + " (" + currentPayslips.size() + " data)");
        cardsWrapper.repaint();
    }

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
            String path = payslipController.generatePdf(payslip);
            UIHelper.showSuccess(this, "PDF berhasil digenerate:\n" + path);
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
        if (dialog.isSaved()) {
            loadPayslips(currentPeriod);
        }
    }

    private void deleteSelected() {
        Payslip payslip = popupSelectedPayslip;
        if (payslip == null) {
            payslip = getSelectedPayslip();
        }
        if (payslip == null) return;
        
        if (UIHelper.showConfirm(this, "Apakah Anda yakin ingin menghapus data slip gaji untuk \n" + payslip.getEmployeeName() + " ?")) {
            boolean deleted = payslipController.deletePayslip(payslip.getId());
            if (deleted) {
                popupSelectedPayslip = null;
                refresh();
                UIHelper.showSuccess(this, "Data slip gaji berhasil dihapus");
            } else {
                UIHelper.showError(this, "Gagal menghapus data slip gaji. Silakan cek database atau hubungi admin.");
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
                    UIHelper.showSuccess(PayslipPanel.this, "Email berhasil dikirim ke " + payslip.getEmployeeEmail());
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
        if (!UIHelper.showConfirm(this, "Kirim " + currentPayslips.size() + " slip gaji ke email masing-masing karyawan?\n\nSetiap email hanya berisi slip masing-masing karyawan.")) return;
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
                    @Override public void onComplete(int success, int failed, java.util.List<String> failedNames, java.util.List<String> failedErrors) {
                        SwingUtilities.invokeLater(() -> {
                            String msg = "Pengiriman selesai!\n✅ Berhasil: " + success + "\n❌ Gagal: " + failed;
                            if (failed > 0) {
                                StringBuilder detail = new StringBuilder("\n\nDaftar gagal:\n");
                                for (int i = 0; i < failedNames.size(); i++) {
                                    detail.append("• ").append(failedNames.get(i))
                                          .append(": ").append(failedErrors.get(i)).append("\n");
                                }
                                UIHelper.showError(PayslipPanel.this, msg + detail.toString());
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
        String period = javax.swing.JOptionPane.showInputDialog(this,
                "Masukkan periode (yyyy-MM):", "Generate dari Presensi",
                javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (period == null || period.trim().isEmpty()) return;
        if (!period.matches("\\d{4}-\\d{2}")) {
            UIHelper.showError(this, "Format periode tidak valid. Gunakan yyyy-MM (contoh: 2026-06)");
            return;
        }

        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Memproses data presensi ke slip gaji...");

        SwingWorker<GenerateResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GenerateResult doInBackground() {
                PresensiToPayslipController controller = new PresensiToPayslipController();
                return controller.generatePayslipsFromPresensi(period.trim());
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
                    for (String m : result.getMessages()) {
                        msg.append(m).append("\n");
                    }
                    UIHelper.showSuccess(PayslipPanel.this, msg.toString());
                    refresh();
                } catch (Exception ex) {
                    UIHelper.showError(PayslipPanel.this, "Error: " + ex.getMessage());
                }
                statusLabel.setText("Selesai.");
            }
        };
        worker.execute();
    }
}
