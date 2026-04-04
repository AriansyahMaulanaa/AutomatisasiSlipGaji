package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class PayslipPreviewDialog extends JDialog {
    private final Payslip payslip;

    public PayslipPreviewDialog(Window owner, Payslip payslip) {
        super(owner, "Preview Slip Gaji - " + payslip.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.payslip = payslip;
        initUI();
    }

    private void initUI() {
        setSize(600, 750);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Info panel at top
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Constants.BG_CARD);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR),
                new EmptyBorder(16, 16, 16, 16)));

        String html = buildPreviewHtml();
        JLabel previewLabel = new JLabel(html);
        previewLabel.setFont(Constants.FONT_BODY);
        previewLabel.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(previewLabel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(Constants.BG_DARK);
        btnPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton openPdfBtn = UIHelper.createStyledButton("📂 Buka File PDF", Constants.PRIMARY);
        openPdfBtn.addActionListener(e -> openPdf());

        JButton closeBtn = UIHelper.createStyledButton("✖ Tutup", Constants.BG_SURFACE);
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(openPdfBtn);
        btnPanel.add(closeBtn);

        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private String buildPreviewHtml() {
        return "<html><body style='font-family:Segoe UI;padding:20px;'>"
                + "<div style='background:#4f46e5;color:white;padding:16px;'>"
                + "<h2 style='margin:0'>SLIP GAJI KARYAWAN</h2>"
                + "<p style='margin:4px 0 0 0;color:#c7d2fe'>Preview Periode: " + payslip.getPeriod() + "</p>"
                + "</div>"
                + "<br>"
                + "<table style='width:100%'>"
                + row("Nama", payslip.getEmployeeName())
                + row("ID Karyawan", payslip.getEmployeeIdCode())
                + row("Posisi", payslip.getPosition())
                + row("Departemen", payslip.getDepartment())
                + "</table>"
                + "<hr>"
                + "<h3 style='color:#1e293b'>Kehadiran</h3>"
                + "<table style='width:100%'>"
                + row("Hari Hadir", payslip.getDaysPresent() + " hari")
                + row("Hari Absen", payslip.getDaysAbsent() + " hari")
                + row("Jam Lembur", payslip.getOvertimeHours() + " jam")
                + "</table>"
                + "<hr>"
                + "<h3 style='color:#1e293b'>Rincian Gaji</h3>"
                + "<table style='width:100%'>"
                + row("Gaji Pokok", UIHelper.formatCurrency(payslip.getBaseSalary()))
                + row("Uang Lembur", UIHelper.formatCurrency(payslip.getOvertimePay()))
                + row("Tunjangan", UIHelper.formatCurrency(payslip.getAllowances()))
                + rowRed("Potongan Absen", "- " + UIHelper.formatCurrency(payslip.getDeductions()))
                + "</table>"
                + "<hr>"
                + "<div style='background:#10b981;color:white;padding:12px;text-align:center;'>"
                + "<b style='font-size:14px'>GAJI BERSIH: " + UIHelper.formatCurrency(payslip.getNetSalary()) + "</b>"
                + "</div>"
                + "</body></html>";
    }

    private String row(String label, String value) {
        return "<tr><td style='padding:4px 8px;color:#64748b'>" + label
                + "</td><td style='padding:4px 8px;font-weight:bold'>" + (value != null ? value : "-") + "</td></tr>";
    }

    private String rowRed(String label, String value) {
        return "<tr><td style='padding:4px 8px;color:#64748b'>" + label
                + "</td><td style='padding:4px 8px;font-weight:bold;color:#ef4444'>" + value + "</td></tr>";
    }

    private void openPdf() {
        if (payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            try {
                Desktop.getDesktop().open(new File(payslip.getPdfPath()));
            } catch (Exception ex) {
                UIHelper.showError(this, "Gagal membuka PDF: " + ex.getMessage());
            }
        } else {
            UIHelper.showError(this, "File PDF belum digenerate.");
        }
    }
}
