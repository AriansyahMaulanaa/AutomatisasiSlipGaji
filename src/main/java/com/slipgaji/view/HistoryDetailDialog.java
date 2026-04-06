package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.model.SendHistory;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * Dialog to show detail of a send history entry.
 * User can view the associated payslip PDF from here.
 */
public class HistoryDetailDialog extends JDialog {
    private final SendHistory history;
    private final Payslip payslip;

    public HistoryDetailDialog(Window owner, SendHistory history, Payslip payslip) {
        super(owner, "Detail Pengiriman — " + history.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.history = history;
        this.payslip = payslip;
        initUI();
    }

    private void initUI() {
        setSize(520, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Status banner
        boolean isSuccess = "SUCCESS".equals(history.getStatus());
        Color bannerColor = isSuccess ? Constants.ACCENT : Constants.ACCENT_DANGER;
        String statusText = isSuccess ? "✅ Email Berhasil Terkirim" : "❌ Email Gagal Terkirim";

        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bannerColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(16, 20, 16, 20));
        banner.setLayout(new BorderLayout());
        banner.setPreferredSize(new Dimension(0, 56));

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 15));
        statusLabel.setForeground(Color.WHITE);
        banner.add(statusLabel, BorderLayout.WEST);

        mainPanel.add(banner, BorderLayout.NORTH);

        // Detail card
        JPanel detailCard = UIHelper.createCard("");
        detailCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel detailContent = new JPanel();
        detailContent.setLayout(new BoxLayout(detailContent, BoxLayout.Y_AXIS));
        detailContent.setOpaque(false);

        detailContent.add(Box.createVerticalStrut(8));
        addDetailRow(detailContent, "Nama Karyawan", history.getEmployeeName());
        addDetailRow(detailContent, "Email", history.getEmployeeEmail());
        addDetailRow(detailContent, "Periode", history.getPeriod());
        addDetailRow(detailContent, "Waktu Kirim", history.getSentAt() != null ? history.getSentAt() : "-");
        addDetailRow(detailContent, "Dikirim Oleh", history.getSentBy() != null ? history.getSentBy() : "-");

        if (!isSuccess && history.getErrorMessage() != null && !history.getErrorMessage().isEmpty()) {
            addDetailRow(detailContent, "Error", history.getErrorMessage());
        }

        if (payslip != null) {
            detailContent.add(Box.createVerticalStrut(8));
            addDetailRow(detailContent, "Gaji Bersih", UIHelper.formatCurrency(payslip.getNetSalary()));
            String pdfStatus = (payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()
                    && new File(payslip.getPdfPath()).exists()) ? "✅ Tersedia" : "— Tidak tersedia";
            addDetailRow(detailContent, "File PDF", pdfStatus);
        }

        detailCard.add(detailContent, BorderLayout.CENTER);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(16, 0, 0, 0));
        cardWrapper.add(detailCard, BorderLayout.CENTER);

        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        if (payslip != null && payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            JButton openPdfBtn = UIHelper.createStyledButton("📂 Buka Slip PDF", Constants.PRIMARY);
            openPdfBtn.addActionListener(e -> openPdf());
            btnPanel.add(openPdfBtn);
        }

        JButton closeBtn = UIHelper.createStyledButton("Tutup", Constants.REFRESH_BTN);
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(Constants.FONT_BODY);
        lblLabel.setForeground(Constants.TEXT_SECONDARY);
        lblLabel.setPreferredSize(new Dimension(140, 20));

        JLabel lblValue = new JLabel(value != null ? value : "-");
        lblValue.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        lblValue.setForeground(Constants.TEXT_PRIMARY);

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.CENTER);
        parent.add(row);
    }

    private void openPdf() {
        if (payslip != null && payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            File pdfFile = new File(payslip.getPdfPath());
            if (pdfFile.exists()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (Exception ex) {
                    UIHelper.showError(this, "Gagal membuka PDF: " + ex.getMessage());
                }
            } else {
                UIHelper.showError(this, "File PDF tidak ditemukan:\n" + payslip.getPdfPath());
            }
        }
    }
}
