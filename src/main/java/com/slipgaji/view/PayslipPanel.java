package com.slipgaji.view;

import com.slipgaji.controller.EmailController;
import com.slipgaji.controller.PayslipController;
import com.slipgaji.model.Payslip;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PayslipPanel extends JPanel {
    private final PayslipController payslipController;
    private final EmailController emailController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private List<Payslip> currentPayslips;

    public PayslipPanel() {
        this.payslipController = new PayslipController();
        this.emailController = new EmailController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel pageTitle = new JLabel("Slip Gaji Karyawan");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Controls
        JPanel controlCard = UIHelper.createCard("");
        controlCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        JLabel periodLabel = new JLabel("Periode:");
        periodLabel.setFont(Constants.FONT_BODY);
        periodLabel.setForeground(Constants.TEXT_SECONDARY);

        periodCombo = new JComboBox<>();
        periodCombo.setFont(Constants.FONT_BODY);
        periodCombo.setPreferredSize(new Dimension(150, 34));
        periodCombo.addActionListener(e -> loadPayslips());

        JButton refreshBtn = UIHelper.createStyledButton("Refresh", Constants.BG_SURFACE);
        refreshBtn.addActionListener(e -> refresh());

        JButton genAllBtn = UIHelper.createStyledButton("Generate Semua PDF", Constants.PRIMARY);
        genAllBtn.addActionListener(e -> generateAllPdfs());

        JButton sendAllBtn = UIHelper.createStyledButton("Kirim Semua Email", Constants.ACCENT);
        sendAllBtn.addActionListener(e -> sendAllEmails());

        controlCard.add(periodLabel);
        controlCard.add(periodCombo);
        controlCard.add(refreshBtn);
        controlCard.add(Box.createHorizontalStrut(20));
        controlCard.add(genAllBtn);
        controlCard.add(sendAllBtn);

        // Progress
        JPanel progressPanel = new JPanel(new BorderLayout(8, 0));
        progressPanel.setBackground(Constants.BG_DARK);
        progressPanel.setBorder(new EmptyBorder(8, 0, 8, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(0, 24));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Constants.FONT_SMALL);
        statusLabel.setForeground(Constants.TEXT_SECONDARY);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"No", "ID", "ID Karyawan", "Nama", "Email", "Periode", "Batch",
                "Gaji Pokok", "Lembur", "Potongan", "Tunjangan", "Gaji Bersih", "PDF"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        // Hide internal ID column
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

        // Double-click for preview
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    previewSelected();
                }
            }
        });

        // Context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem previewItem = new JMenuItem("Preview PDF");
        previewItem.addActionListener(e -> previewSelected());
        JMenuItem sendItem = new JMenuItem("Kirim Email");
        sendItem.addActionListener(e -> sendSelected());
        JMenuItem genItem = new JMenuItem("Generate PDF");
        genItem.addActionListener(e -> generateSelectedPdf());
        JMenuItem deleteItem = new JMenuItem("Hapus Data");
        deleteItem.addActionListener(e -> deleteSelected());

        popup.add(previewItem);
        popup.add(genItem);
        popup.addSeparator();
        popup.add(sendItem);
        popup.addSeparator();
        popup.add(deleteItem);
        table.setComponentPopupMenu(popup);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));

        JPanel tableCard = UIHelper.createCard("Daftar Slip Gaji (klik kanan untuk aksi, double-click untuk preview)");
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Assembly
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(Constants.BG_DARK);
        topWrapper.add(controlCard, BorderLayout.NORTH);
        topWrapper.add(progressPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Constants.BG_DARK);
        centerPanel.add(topWrapper, BorderLayout.NORTH);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        // Reload periods
        List<String> periods = payslipController.getPeriods();
        periodCombo.removeAllItems();
        periodCombo.addItem("-- Semua --");
        for (String p : periods) {
            periodCombo.addItem(p);
        }
        loadPayslips();
    }

    private void loadPayslips() {
        String selected = (String) periodCombo.getSelectedItem();
        String period = (selected != null && !selected.startsWith("--")) ? selected : null;

        currentPayslips = payslipController.getPayslips(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (Payslip p : currentPayslips) {
            String batch = p.getCreatedAt() != null && p.getCreatedAt().length() >= 10 ? p.getCreatedAt().substring(0, 10) : "-";
            tableModel.addRow(new Object[]{
                    no++,
                    p.getId(),
                    p.getEmployeeIdCode(),
                    p.getEmployeeName(),
                    p.getEmployeeEmail(),
                    p.getPeriod(),
                    batch,
                    UIHelper.formatCurrency(p.getBaseSalary()),
                    UIHelper.formatCurrency(p.getOvertimePay()),
                    UIHelper.formatCurrency(p.getDeductions()),
                    UIHelper.formatCurrency(p.getAllowances()),
                    UIHelper.formatCurrency(p.getNetSalary()),
                    (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "Tersedia" : "Belum"
            });
        }
        statusLabel.setText("Total: " + currentPayslips.size() + " slip gaji");
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
            // Generate PDF if not exists
            if (payslip.getPdfPath() == null || payslip.getPdfPath().isEmpty()) {
                payslipController.generatePdf(payslip);
            }

            if (payslip.getPdfPath() != null) {
                PayslipPreviewDialog dialog = new PayslipPreviewDialog(
                        SwingUtilities.getWindowAncestor(this), payslip);
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
            loadPayslips();
        } catch (Exception ex) {
            UIHelper.showError(this, "Gagal generate PDF: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;

        if (UIHelper.showConfirm(this, "Apakah Anda yakin ingin menghapus data slip gaji untuk \n" + payslip.getEmployeeName() + " ?")) {
            payslipController.deletePayslip(payslip.getId());
            UIHelper.showSuccess(this, "Data slip gaji berhasil dihapus");
            loadPayslips();
        }
    }

    private void sendSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;

        if (!UIHelper.showConfirm(this, "Kirim slip gaji ke " + payslip.getEmployeeEmail() + "?")) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                emailController.sendSingle(payslip);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    UIHelper.showSuccess(PayslipPanel.this,
                            "Email berhasil dikirim ke " + payslip.getEmployeeEmail());
                } catch (Exception ex) {
                    UIHelper.showError(PayslipPanel.this,
                            "Gagal mengirim email:\n" + ex.getCause().getMessage());
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
            @Override
            protected Void doInBackground() throws Exception {
                payslipController.generateAllPdfs(currentPayslips, (current, total, name) -> {
                    publish(current);
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Generating PDF: " + name + " (" + current + "/" + total + ")");
                    });
                });
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    get();
                    UIHelper.showSuccess(PayslipPanel.this,
                            "Semua PDF berhasil digenerate!");
                    loadPayslips();
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
                + " slip gaji ke email masing-masing karyawan?")) return;

        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setMaximum(currentPayslips.size());

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                emailController.sendBatch(currentPayslips, new EmailController.BatchCallback() {
                    @Override
                    public void onProgress(int current, int total, String name, String status, String error) {
                        publish(current);
                        SwingUtilities.invokeLater(() -> {
                            String icon = status.equals("SUCCESS") ? "✅" : "❌";
                            statusLabel.setText(icon + " " + name + " (" + current + "/" + total + ")");
                        });
                    }

                    @Override
                    public void onComplete(int success, int failed) {
                        SwingUtilities.invokeLater(() -> {
                            UIHelper.showSuccess(PayslipPanel.this,
                                    "Pengiriman selesai!\n✅ Berhasil: " + success + "\n❌ Gagal: " + failed);
                        });
                    }
                });
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                statusLabel.setText("Pengiriman selesai.");
                loadPayslips();
            }
        };
        worker.execute();
    }
}
