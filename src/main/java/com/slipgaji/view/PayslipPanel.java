package com.slipgaji.view;

import com.slipgaji.controller.EmailController;
import com.slipgaji.controller.PayslipController;
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
    
    private CardLayout cardLayout;
    private JPanel mainContent;
    
    // Folder View
    private JPanel folderContainer;
    
    // Detail View
    private JPanel detailHeaderPanel;
    private JLabel detailTitleLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    private String currentPeriod;
    private List<Payslip> currentPayslips;

    public PayslipPanel() {
        this.payslipController = new PayslipController();
        this.emailController = new EmailController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel pageTitle = new JLabel("Slip Gaji Karyawan");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);
        
        initFolderView();
        initDetailView();
        
        mainContent.add(createFolderWrapper(), "folder");
        mainContent.add(createDetailWrapper(), "detail");
        
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createFolderWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        JButton refreshBtn = UIHelper.createStyledButton("Refresh", Constants.REFRESH_BTN);
        refreshBtn.addActionListener(e -> loadFolders());
        controlPanel.add(refreshBtn);
        
        wrapper.add(controlPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(folderContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createDetailWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        detailHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        detailHeaderPanel.setOpaque(false);
        
        JButton backBtn = UIHelper.createStyledButton("⬅ Kembali", Constants.REFRESH_BTN);
        backBtn.addActionListener(e -> showFolderView());
        
        detailTitleLabel = new JLabel("Detail Periode");
        detailTitleLabel.setFont(Constants.FONT_SUBTITLE);
        
        detailHeaderPanel.add(backBtn);
        detailHeaderPanel.add(Box.createHorizontalStrut(10));
        detailHeaderPanel.add(detailTitleLabel);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setOpaque(false);
        
        JButton genAllBtn = UIHelper.createStyledButton("Generate Semua Slip", Constants.PRIMARY);
        genAllBtn.addActionListener(e -> generateAllPdfs());

        JButton sendAllBtn = UIHelper.createStyledButton("Kirim Email Batch", Constants.ACCENT_WARN);
        sendAllBtn.addActionListener(e -> sendAllEmails());
        
        actionPanel.add(genAllBtn);
        actionPanel.add(sendAllBtn);
        
        topPanel.add(detailHeaderPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        JPanel progressPanel = new JPanel(new BorderLayout(8, 0));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(0, 24));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Constants.FONT_SMALL);
        statusLabel.setForeground(Constants.TEXT_SECONDARY);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);
        
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(topPanel, BorderLayout.NORTH);
        topWrapper.add(progressPanel, BorderLayout.SOUTH);
        
        wrapper.add(topWrapper, BorderLayout.NORTH);
        
        // Table container
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));

        JPanel tableCard = UIHelper.createCard("Daftar Slip Gaji (klik kanan untuk aksi, double-click untuk preview)");
        tableCard.add(scrollPane, BorderLayout.CENTER);
        
        wrapper.add(tableCard, BorderLayout.CENTER);
        
        return wrapper;
    }

    private void initFolderView() {
        // We use WrapLayout or a modified FlowLayout to wrap folders nicely
        folderContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        folderContainer.setOpaque(false);
    }
    
    private void initDetailView() {
        String[] columns = {"No", "ID", "ID Karyawan", "Nama", "Email", 
                "Gaji Pokok", "Lembur", "Tunjangan", "Potongan", "Total Gaji", "PDF"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    previewSelected();
                }
            }
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem previewItem = new JMenuItem("Lihat Slip (PDF)");
        previewItem.addActionListener(e -> previewSelected());
        JMenuItem genItem = new JMenuItem("Generate Slip");
        genItem.addActionListener(e -> generateSelectedPdf());
        JMenuItem sendItem = new JMenuItem("Kirim Email");
        sendItem.addActionListener(e -> sendSelected());
        JMenuItem deleteItem = new JMenuItem("Hapus Data");
        deleteItem.addActionListener(e -> deleteSelected());

        popup.add(previewItem);
        popup.add(genItem);
        popup.addSeparator();
        popup.add(sendItem);
        popup.addSeparator();
        popup.add(deleteItem);
        table.setComponentPopupMenu(popup);
    }

    public void refresh() {
        if (mainContent.getComponents()[0].isVisible()) {
            loadFolders();
        } else {
            loadPayslips(currentPeriod);
        }
    }
    
    private void loadFolders() {
        folderContainer.removeAll();
        List<PeriodSummary> summaries = DatabaseService.getInstance().getPayslipPeriodSummaries();
        
        for (PeriodSummary summary : summaries) {
            folderContainer.add(createFolderCard(summary));
        }
        
        folderContainer.revalidate();
        folderContainer.repaint();
    }
    
    private JPanel createFolderCard(PeriodSummary summary) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showDetailView(summary.getPeriod());
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int r = Constants.CARD_RADIUS * 2;

                // Shadow
                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, hovered ? 8 * (4 - i) : 4 * (4 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + (hovered?2:1), w - i * 2, h - i * 2, r, r));
                }

                // Background
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                
                // Top Color Bar (like a folder tab)
                g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                Color bannerColor;
                if ("Terkirim".equals(summary.getStatus())) bannerColor = new Color(16, 185, 129); // Green
                else if ("Generated".equals(summary.getStatus())) bannerColor = Constants.PRIMARY; // Blue
                else bannerColor = new Color(107, 114, 128); // Gray
                
                g2.setColor(bannerColor);
                g2.fill(new Rectangle(0, 0, w, 8));
                g2.setClip(null);

                // Border
                g2.setColor(hovered ? Constants.PRIMARY : Constants.BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, r, r));

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 160));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel periodLbl = new JLabel(summary.getFormattedPeriod());
        periodLbl.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        periodLbl.setForeground(Constants.TEXT_PRIMARY);
        
        JLabel countLbl = new JLabel(summary.getSlipCount() + " Slip Karyawan");
        countLbl.setFont(Constants.FONT_BODY);
        countLbl.setForeground(Constants.TEXT_SECONDARY);
        
        JLabel totalLbl = new JLabel("Total: " + UIHelper.formatCurrency(summary.getTotalSalary()));
        totalLbl.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        totalLbl.setForeground(Constants.PRIMARY_DARK);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        String statusText = summary.getStatus();
        Color statusColor = statusText.equals("Terkirim") ? new Color(16, 185, 129) :
                            (statusText.equals("Generated") ? Constants.PRIMARY : new Color(107, 114, 128));
        JLabel statusLbl = new JLabel("● " + statusText);
        statusLbl.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        statusLbl.setForeground(statusColor);
        statusPanel.add(statusLbl);
        
        card.add(periodLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(countLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(totalLbl);
        card.add(Box.createVerticalGlue());
        card.add(statusPanel);
        
        return card;
    }
    
    private void showFolderView() {
        cardLayout.show(mainContent, "folder");
        loadFolders();
    }
    
    private void showDetailView(String period) {
        this.currentPeriod = period;
        // Format period for title
        String[] parts = period.split("-");
        String[] months = {"", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        String formatted = period;
        try { formatted = months[Integer.parseInt(parts[1])] + " " + parts[0]; } catch(Exception ignored) {}
        
        detailTitleLabel.setText("Detail Slip: " + formatted);
        cardLayout.show(mainContent, "detail");
        loadPayslips(period);
    }

    private void loadPayslips(String period) {
        currentPayslips = payslipController.getPayslips(period);
        tableModel.setRowCount(0);
        int no = 1;
        for (Payslip p : currentPayslips) {
            double tunjangan = p.getAllowances() + p.getNightShiftIncentive();
            tableModel.addRow(new Object[]{
                    no++,
                    p.getId(),
                    p.getEmployeeIdCode(),
                    p.getEmployeeName(),
                    p.getEmployeeEmail(),
                    UIHelper.formatCurrency(p.getBaseSalary()),
                    UIHelper.formatCurrency(p.getOvertimePay()),
                    UIHelper.formatCurrency(tunjangan),
                    UIHelper.formatCurrency(p.getDeductions()),
                    UIHelper.formatCurrency(p.getNetSalary()),
                    (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "✅ Tersedia" : "—"
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
            loadPayslips(currentPeriod);
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
            loadPayslips(currentPeriod);
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
                + " slip gaji ke email masing-masing karyawan?\n\nSetiap email hanya berisi slip masing-masing karyawan.")) return;

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
                loadPayslips(currentPeriod);
            }
        };
        worker.execute();
    }
}
