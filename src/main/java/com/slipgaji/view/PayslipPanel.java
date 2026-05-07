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
<<<<<<< HEAD
    
    private String currentPeriod = null;
=======
<<<<<<< HEAD
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    private JProgressBar progressBar;
    private JLabel statusLabel;
=======
    
    private String currentPeriod = null;
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
    private List<Payslip> currentPayslips;
    
    private JPanel cardsWrapper;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    public PayslipPanel() {
        this.payslipController = new PayslipController();
        this.emailController = new EmailController();
        initUI();
    }

    private void initUI() {
<<<<<<< HEAD
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header Seminal Mungkin
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
=======
<<<<<<< HEAD
        setLayout(new BorderLayout());
        setBackground(Constants.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));
=======
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 32, 24, 32));
>>>>>>> e7da53e (update fitur dan db)

        // Header Seminal Mungkin
        JPanel headerPanel = new JPanel(new BorderLayout());
<<<<<<< HEAD
        headerPanel.setBackground(Constants.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
=======
        headerPanel.setOpaque(false);
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08

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

<<<<<<< HEAD
        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 24));
        centerPanel.setOpaque(false);
=======
<<<<<<< HEAD
        // Controls
        JPanel controlCard = UIHelper.createCard("");
        controlCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
>>>>>>> 0274c08

        cardsWrapper = new JPanel();
        cardsWrapper.setOpaque(false);
        centerPanel.add(cardsWrapper, BorderLayout.NORTH);

<<<<<<< HEAD
=======
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
=======
        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 24));
        centerPanel.setOpaque(false);

        cardsWrapper = new JPanel();
        cardsWrapper.setOpaque(false);
        centerPanel.add(cardsWrapper, BorderLayout.NORTH);
>>>>>>> e7da53e (update fitur dan db)

>>>>>>> 0274c08
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);
        
        JPanel tblOptions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tblOptions.setOpaque(false);
        JButton btnGen = UIHelper.createStyledButton("Generate Semua", Constants.PRIMARY);
        btnGen.addActionListener(e -> generateAllPdfs());
        JButton btnSend = UIHelper.createStyledButton("Kirim Mode Batch", Constants.ACCENT_WARN);
        btnSend.addActionListener(e -> sendAllEmails());
        tblOptions.add(btnGen);
        tblOptions.add(btnSend);
        
        statusLabel = new JLabel("Silahkan pilih periode");
        statusLabel.setForeground(Constants.TEXT_SECONDARY);
        
        JPanel tblTop = new JPanel(new BorderLayout());
        tblTop.setOpaque(false);
        tblTop.add(statusLabel, BorderLayout.WEST);
        tblTop.add(tblOptions, BorderLayout.EAST);
        tablePanel.add(tblTop, BorderLayout.NORTH);

<<<<<<< HEAD
        String[] cols = {"No", "ID", "Nama", "Jabatan", "Gaji", "Status", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
=======
<<<<<<< HEAD
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"No", "ID", "ID Karyawan", "Nama", "Email", "Periode", "Batch",
                "Gaji Pokok", "Lembur", "Potongan", "Tunjangan", "Gaji Bersih", "PDF"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
=======
        String[] cols = {"No", "ID", "Nama", "Jabatan", "Gaji", "Status", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

<<<<<<< HEAD
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) previewSelected(); }
        });
        
=======
<<<<<<< HEAD
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
=======
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) previewSelected(); }
        });
        
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
        JPopupMenu popup = new JPopupMenu();
        JMenuItem previewItem = new JMenuItem("Lihat Slip (PDF)");
        previewItem.addActionListener(e -> previewSelected());
        JMenuItem genItem = new JMenuItem("Generate Slip");
        genItem.addActionListener(e -> generateSelectedPdf());
        JMenuItem sendItem = new JMenuItem("Kirim Email");
        sendItem.addActionListener(e -> sendSelected());
        JMenuItem deleteItem = new JMenuItem("Hapus Data");
        deleteItem.addActionListener(e -> deleteSelected());
        popup.add(previewItem); popup.add(genItem); popup.addSeparator(); popup.add(sendItem); popup.add(deleteItem);
        table.setComponentPopupMenu(popup);

        JScrollPane scrollPane = new JScrollPane(table);
<<<<<<< HEAD
=======
<<<<<<< HEAD
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
>>>>>>> 0274c08
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        tablePanel.add(progressBar, BorderLayout.SOUTH);

<<<<<<< HEAD
        centerPanel.add(tablePanel, BorderLayout.CENTER);
=======
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

=======
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        tablePanel.add(progressBar, BorderLayout.SOUTH);

        centerPanel.add(tablePanel, BorderLayout.CENTER);
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refresh() {
<<<<<<< HEAD
        List<PeriodSummary> summaries = DatabaseService.getInstance().getPayslipPeriodSummaries();
        
        cardsWrapper.removeAll();
        int MAX_CARDS = 3; 

        int cardsToShow = Math.min(summaries.size(), MAX_CARDS);
        int emptySlots = Math.max(1, MAX_CARDS - cardsToShow); 
        
        cardsWrapper.setLayout(new GridLayout(1, cardsToShow + emptySlots, 16, 0));
        cardsWrapper.setPreferredSize(new Dimension(0, 100)); // Minimalist height constraint
        
        for (int i = 0; i < cardsToShow; i++) {
            cardsWrapper.add(createPeriodCard(summaries.get(i)));
        }
        for (int i = 0; i < emptySlots; i++) {
            cardsWrapper.add(createEmptyState());
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
=======
<<<<<<< HEAD
        // Reload periods
        List<String> periods = payslipController.getPeriods();
        periodCombo.removeAllItems();
        periodCombo.addItem("-- Semua --");
        for (String p : periods) {
            periodCombo.addItem(p);
        }
        loadPayslips();
=======
        List<PeriodSummary> summaries = DatabaseService.getInstance().getPayslipPeriodSummaries();
        
        cardsWrapper.removeAll();
        int MAX_CARDS = 3; 

        int cardsToShow = Math.min(summaries.size(), MAX_CARDS);
        int emptySlots = Math.max(1, MAX_CARDS - cardsToShow); 
        
        cardsWrapper.setLayout(new GridLayout(1, cardsToShow + emptySlots, 16, 0));
        cardsWrapper.setPreferredSize(new Dimension(0, 100)); // Minimalist height constraint
        
        for (int i = 0; i < cardsToShow; i++) {
            cardsWrapper.add(createPeriodCard(summaries.get(i)));
        }
        for (int i = 0; i < emptySlots; i++) {
            cardsWrapper.add(createEmptyState());
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
                      + (pending == 0 && summary.getSlipCount() > 0 ? "<span style='color:#10B981'>✅ Semua Terkirim</span>" : "<span style='color:#F59E0B'>⧗ " + pending + " Pending</span>")
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
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
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
                      + (pending == 0 && summary.getSlipCount() > 0 ? "<span style='color:#10B981'>✅ Semua Terkirim</span>" : "<span style='color:#F59E0B'>⧗ " + pending + " Pending</span>")
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
<<<<<<< HEAD
            String status = (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "Generated" : "Draft";
            tableModel.addRow(new Object[]{ no++, p.getId(), p.getEmployeeName(), p.getPosition(), UIHelper.formatCurrency(p.getNetSalary()), status, "Klik Kanan" });
=======
<<<<<<< HEAD
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
=======
            String status = (p.getPdfPath() != null && !p.getPdfPath().isEmpty()) ? "Generated" : "Draft";
            tableModel.addRow(new Object[]{ no++, p.getId(), p.getEmployeeName(), p.getPosition(), UIHelper.formatCurrency(p.getNetSalary()), status, "Klik Kanan" });
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
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

    private void deleteSelected() {
        Payslip payslip = getSelectedPayslip();
        if (payslip == null) return;
        if (UIHelper.showConfirm(this, "Apakah Anda yakin ingin menghapus data slip gaji untuk \n" + payslip.getEmployeeName() + " ?")) {
            payslipController.deletePayslip(payslip.getId());
            UIHelper.showSuccess(this, "Data slip gaji berhasil dihapus");
<<<<<<< HEAD
            refresh();
=======
<<<<<<< HEAD
            loadPayslips();
=======
            refresh();
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
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
<<<<<<< HEAD
                    UIHelper.showSuccess(PayslipPanel.this, "Semua PDF berhasil digenerate!");
                    loadPayslips(currentPeriod);
=======
<<<<<<< HEAD
                    UIHelper.showSuccess(PayslipPanel.this,
                            "Semua PDF berhasil digenerate!");
                    loadPayslips();
=======
                    UIHelper.showSuccess(PayslipPanel.this, "Semua PDF berhasil digenerate!");
                    loadPayslips(currentPeriod);
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
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
<<<<<<< HEAD
        if (!UIHelper.showConfirm(this, "Kirim " + currentPayslips.size() + " slip gaji ke email masing-masing karyawan?\n\nSetiap email hanya berisi slip masing-masing karyawan.")) return;
=======
<<<<<<< HEAD

        if (!UIHelper.showConfirm(this, "Kirim " + currentPayslips.size()
                + " slip gaji ke email masing-masing karyawan?")) return;

=======
        if (!UIHelper.showConfirm(this, "Kirim " + currentPayslips.size() + " slip gaji ke email masing-masing karyawan?\n\nSetiap email hanya berisi slip masing-masing karyawan.")) return;
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setMaximum(currentPayslips.size());

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                emailController.sendBatch(currentPayslips, new EmailController.BatchCallback() {
                    @Override public void onProgress(int current, int total, String name, String status, String error) {
                        publish(current);
                        SwingUtilities.invokeLater(() -> {
                            String icon = status.equals("SUCCESS") ? "✅" : "❌";
                            statusLabel.setText(icon + " " + name + " (" + current + "/" + total + ")");
                        });
                    }
                    @Override public void onComplete(int success, int failed) {
                        SwingUtilities.invokeLater(() -> {
                            UIHelper.showSuccess(PayslipPanel.this, "Pengiriman selesai!\n✅ Berhasil: " + success + "\n❌ Gagal: " + failed);
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
}
